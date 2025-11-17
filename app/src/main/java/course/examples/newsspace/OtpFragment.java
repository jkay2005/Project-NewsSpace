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

    private String userEmail; // Biến để lưu email nhận từ màn hình trước

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOtpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Nhận và lưu email từ Fragment trước bằng Safe Args
        if (getArguments() != null) {
            userEmail = OtpFragmentArgs.fromBundle(getArguments()).getEmail();
            String description = "Chúng tôi đã gửi mã OTP đến email\n" + userEmail;
            binding.descriptionTextView.setText(description);
        } else {
            // Xử lý trường hợp không nhận được email (lỗi)
            showErrorDialog("Lỗi", "Không nhận được thông tin email.");
            NavHostFragment.findNavController(this).navigateUp();
            return;
        }

        binding.confirmButton.setOnClickListener(v -> handleOtpVerification());
        binding.resendOtpTextView.setOnClickListener(v -> handleResendOtp());

        startCountdown();
    }


    private void handleOtpVerification() {
        String otp = binding.otpEditText.getText().toString().trim();

        // Validation
        if (otp.length() < 6) {
            showErrorDialog("Lỗi", "Vui lòng nhập đủ 6 ký tự của mã OTP.");
            return;
        }

        showLoading(true);

        // 1. Tạo request body
        VerifyOtpRequest request = new VerifyOtpRequest(userEmail, otp);

        // 2. Gọi API xác thực OTP
        ApiClient.getApiService(requireContext()).verifyOtp(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    // XÁC THỰC THÀNH CÔNG
                    Toast.makeText(getContext(), "Xác thực thành công!", Toast.LENGTH_SHORT).show();

                    // TODO: Quyết định luồng đi tiếp theo
                    // Nếu đến từ Register -> Chuyển đến RegisterSuccess
                    // Nếu đến từ ForgotPassword -> Chuyển đến ResetPassword
                    // Tạm thời mặc định là luồng đăng ký
                    NavHostFragment.findNavController(OtpFragment.this)
                            .navigate(R.id.action_otpFragment_to_registerSuccessFragment);
                } else {
                    // XÁC THỰC THẤT BẠI (sai OTP)
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
        binding.resendOtpTextView.setEnabled(false); // Vô hiệu hóa nút

        // 1. Tạo request body
        ResendOtpRequest request = new ResendOtpRequest(userEmail);

        // 2. Gọi API gửi lại OTP
        ApiClient.getApiService(requireContext()).resendOtp(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã gửi lại mã OTP.", Toast.LENGTH_SHORT).show();
                    startCountdown(); // Bắt đầu lại bộ đếm ngược
                } else {
                    Toast.makeText(getContext(), "Gửi lại mã thất bại. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                    binding.resendOtpTextView.setEnabled(true); // Cho phép nhấn lại
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
        countDownTimer = new CountDownTimer(30000, 1000) { // Đếm ngược 30 giây
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

    // Các hàm Helper
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
        // Hủy bộ đếm ngược để tránh memory leak khi Fragment bị hủy
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        binding = null;
    }
}