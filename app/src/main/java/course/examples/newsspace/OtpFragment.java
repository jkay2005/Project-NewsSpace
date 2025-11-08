package course.examples.newsspace;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.fragment.NavHostFragment;
import java.util.Locale;
import course.examples.newsspace.databinding.FragmentOtpBinding; // Thay bằng package của bạn

public class OtpFragment extends Fragment {

    private FragmentOtpBinding binding;
    private CountDownTimer countDownTimer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOtpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Nhận dữ liệu email từ Fragment trước bằng Safe Args
        if (getArguments() != null) {
            String email = OtpFragmentArgs.fromBundle(getArguments()).getEmail();
            String description = "Chúng tôi đã gửi mã OTP đến email\n" + email;
            binding.descriptionTextView.setText(description);
        }

        binding.confirmButton.setOnClickListener(v -> handleOtpVerification());
        binding.resendOtpTextView.setOnClickListener(v -> handleResendOtp());

        // Bắt đầu đếm ngược để cho phép gửi lại mã
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

        // *** GIẢ LẬP GỌI API XÁC THỰC OTP ***
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            showLoading(false);

            if (otp.equals("123456")) { // Giả sử mã OTP đúng
                // Nếu xác thực thành công, chuyển sang màn hình Thành công
                // Bạn cần tạo action này trong auth_nav_graph.xml
                NavHostFragment.findNavController(OtpFragment.this)
                        .navigate(R.id.action_otpFragment_to_registerSuccessFragment);
            } else {
                showErrorDialog("Thất bại", "Mã OTP không chính xác.");
            }
        }, 2000);
    }

    private void handleResendOtp() {
        // Vô hiệu hóa nút để tránh spam click
        binding.resendOtpTextView.setEnabled(false);

        // *** GIẢ LẬP GỌI API GỬI LẠI MÃ ***
        // Toast.makeText(getContext(), "Đã gửi lại mã.", Toast.LENGTH_SHORT).show();

        // Bắt đầu lại bộ đếm ngược
        startCountdown();
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