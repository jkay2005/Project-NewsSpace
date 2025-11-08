package course.examples.newsspace; // Thay bằng package của bạn

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import course.examples.newsspace.databinding.FragmentForgotPasswordBinding; // Thay bằng package của bạn

public class ForgotPasswordFragment extends Fragment {

    // 1. Khai báo ViewBinding
    private FragmentForgotPasswordBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 2. Khởi tạo ViewBinding
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 3. Gán sự kiện cho nút "Gửi mã OTP"
        binding.sendOtpButton.setOnClickListener(v -> handleSendOtp());
    }

    private void handleSendOtp() {
        String email = binding.emailEditText.getText().toString().trim();

        // Kiểm tra tính hợp lệ của email
        if (email.isEmpty()) {
            showErrorDialog("Lỗi", "Vui lòng nhập địa chỉ email của bạn.");
            return;
        }

        // Sử dụng một Pattern có sẵn của Android để kiểm tra định dạng email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showErrorDialog("Lỗi", "Vui lòng nhập một địa chỉ email hợp lệ.");
            return;
        }

        // Bắt đầu xử lý
        showLoading(true);

        // *** GIẢ LẬP GỌI API GỬI MÃ OTP ***
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            showLoading(false);

            // Điều hướng sang OtpFragment và truyền email theo bằng Safe Args
            // Lớp Directions này được tự động tạo ra sau khi bạn cấu hình Safe Args
            ForgotPasswordFragmentDirections.ActionForgotPasswordFragmentToOtpFragment action =
                    ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToOtpFragment(email);

            NavHostFragment.findNavController(ForgotPasswordFragment.this).navigate(action);

        }, 1500); // Giả lập độ trễ 1.5 giây
    }

    // 4. Các hàm Helper
    private void showLoading(boolean isLoading) {
        if (isLoading) {
            binding.loadingProgressBar.setVisibility(View.VISIBLE);
            binding.sendOtpButton.setEnabled(false);
        } else {
            binding.loadingProgressBar.setVisibility(View.GONE);
            binding.sendOtpButton.setEnabled(true);
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
        // 5. Dọn dẹp ViewBinding
        binding = null;
    }
}