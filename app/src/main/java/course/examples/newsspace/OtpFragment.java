package course.examples.newsspace; // Thay bằng package của bạn

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import java.util.Locale;

import course.examples.newsspace.api.ApiClient;
import course.examples.newsspace.databinding.FragmentOtpBinding;
import course.examples.newsspace.model.ResendOtpRequest;
import course.examples.newsspace.model.VerifyOtpRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpFragment extends Fragment {

    private FragmentOtpBinding binding;
    private CountDownTimer countDownTimer;

    private String userEmail;
    private String otpType;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOtpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            OtpFragmentArgs args = OtpFragmentArgs.fromBundle(getArguments());
            userEmail = args.getEmail();
            otpType = args.getOtpType();
            String description = "Chúng tôi đã gửi mã OTP đến email\n" + userEmail;
            binding.descriptionTextView.setText(description);
        } else {
            showErrorDialog("Lỗi", "Không nhận được thông tin cần thiết.");
            NavHostFragment.findNavController(this).navigateUp();
            return;
        }

        binding.confirmButton.setOnClickListener(v -> handleOtpVerification());
        binding.resendOtpTextView.setOnClickListener(v -> handleResendOtp());

        startCountdown();
    }

    private void handleOtpVerification() {
        String otp = binding.otpEditText.getText().toString().trim();

        if (otp.length() < 6) {
            showErrorDialog("Lỗi", "Vui lòng nhập đủ 6 ký tự của mã OTP.");
            return;
        }

        // Dựa trên mã backend, việc xác thực OTP cho 'quên mật khẩu'
        // xảy ra cùng lúc với việc đặt lại mật khẩu ở màn hình tiếp theo.
        // Vì vậy, nếu là luồng 'quên mật khẩu', chúng ta chỉ chuyển OTP
        // sang màn hình ResetPasswordFragment mà không gọi API xác thực ở đây.
        if ("forgotPassword".equals(otpType)) {
            OtpFragmentDirections.ActionOtpFragmentToResetPasswordFragment action =
                    OtpFragmentDirections.actionOtpFragmentToResetPasswordFragment(userEmail, otp);
            NavHostFragment.findNavController(OtpFragment.this).navigate(action);
            return; // Bỏ qua phần gọi API bên dưới
        }

        // Logic xác thực OTP cho quy trình đăng ký vẫn giữ nguyên
        showLoading(true);
        VerifyOtpRequest request = new VerifyOtpRequest(userEmail, otp);

        ApiClient.getApiService(requireContext()).verifyOtp(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Xác thực thành công!", Toast.LENGTH_SHORT).show();
                    // Vì đã xử lý 'forgotPassword' ở trên, ở đây chỉ còn trường hợp đăng ký
                    NavHostFragment.findNavController(OtpFragment.this)
                            .navigate(R.id.action_otpFragment_to_registerSuccessFragment);
                } else {
                    showErrorDialog("Thất bại", "Mã OTP không chính xác hoặc đã hết hạn.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                showLoading(false);
                Log.e("OtpFragment", "Verify OTP Failed: " + t.getMessage());
                showErrorDialog("Lỗi mạng", "Không thể kết nối đến máy chủ.");
            }
        });
    }

    private void handleResendOtp() {
        binding.resendOtpTextView.setEnabled(false);

        ResendOtpRequest request = new ResendOtpRequest(userEmail);

        ApiClient.getApiService(requireContext()).resendOtp(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã gửi lại mã OTP.", Toast.LENGTH_SHORT).show();
                    startCountdown();
                } else {
                    Toast.makeText(getContext(), "Gửi lại mã thất bại. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                    binding.resendOtpTextView.setEnabled(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("OtpFragment", "Resend OTP Failed: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi mạng. Không thể gửi lại mã.", Toast.LENGTH_SHORT).show();
                binding.resendOtpTextView.setEnabled(true);
            }
        });
    }

    private void startCountdown() {
        binding.resendOtpTextView.setEnabled(false);
        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String timeLeft = String.format(Locale.getDefault(), "Gửi lại mã sau (%ds)", millisUntilFinished / 1000);
                binding.resendOtpTextView.setText(timeLeft);
            }

            @Override
            public void onFinish() {
                binding.resendOtpTextView.setText("Gửi lại mã");
                binding.resendOtpTextView.setEnabled(true);
            }
        }.start();
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            binding.loadingProgressBar.setVisibility(View.VISIBLE);
            binding.confirmButton.setEnabled(false);
        } else {
            binding.loadingProgressBar.setVisibility(View.GONE);
            binding.confirmButton.setEnabled(true);
        }
    }

    private void showErrorDialog(String title, String message) {
        if (isAdded()) {
            new AlertDialog.Builder(requireContext())
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        binding = null;
    }
}
