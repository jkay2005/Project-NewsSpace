package course.examples.newsspace;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

// Import các thư viện cần thiết
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import course.examples.newsspace.databinding.FragmentRegisterBinding; // Thay bằng package của bạn

public class RegisterFragment extends Fragment {

    // 1. Khai báo ViewBinding
    private FragmentRegisterBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 2. Khởi tạo ViewBinding
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 3. Gán sự kiện cho các View
        binding.registerButton.setOnClickListener(v -> handleRegister());

        binding.loginTextView.setOnClickListener(v -> {
            // Quay lại màn hình trước đó (LoginFragment) trong backstack
            NavHostFragment.findNavController(RegisterFragment.this).navigateUp();
        });
    }

    private void handleRegister() {
        // Lấy dữ liệu từ các EditText
        String email = binding.emailEditText.getText().toString().trim();
        String username = binding.usernameEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordEditText.getText().toString().trim();

        // Kiểm tra tính hợp lệ của dữ liệu (Validation)
        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showErrorDialog("Lỗi", "Vui lòng điền đầy đủ thông tin.");
            return; // Dừng lại nếu có lỗi
        }

        if (password.length() < 6) {
            showErrorDialog("Lỗi", "Mật khẩu phải có ít nhất 6 ký tự.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showErrorDialog("Lỗi", "Mật khẩu xác nhận không khớp.");
            return;
        }

        // Nếu tất cả dữ liệu hợp lệ, bắt đầu quá trình đăng ký
        showLoading(true);

        // *** GIẢ LẬP GỌI API ĐĂNG KÝ ***
        // Sử dụng Handler để mô phỏng độ trễ mạng là 2 giây.
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Sau khi có kết quả từ API
            showLoading(false);

            // Giả sử đăng ký thành công, điều hướng sang màn hình OTP
            // Bạn cần tạo action này trong file auth_nav_graph.xml
            NavHostFragment.findNavController(RegisterFragment.this)
                    .navigate(R.id.action_registerFragment_to_otpFragment);

        }, 2000);
    }

    // 4. Các hàm Helper (Tái sử dụng từ LoginFragment)
    private void showLoading(boolean isLoading) {
        if (isLoading) {
            binding.loadingProgressBar.setVisibility(View.VISIBLE);
            binding.registerButton.setEnabled(false); // Vô hiệu hóa nút khi đang tải
        } else {
            binding.loadingProgressBar.setVisibility(View.GONE);
            binding.registerButton.setEnabled(true);
        }
    }

    private void showErrorDialog(String title, String message) {
        // Cần kiểm tra isAdded() để đảm bảo Fragment đã được gắn vào Activity
        // tránh lỗi "Fragment ... not attached to a context."
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
        // 5. Dọn dẹp ViewBinding để tránh rò rỉ bộ nhớ
        binding = null;
    }
}