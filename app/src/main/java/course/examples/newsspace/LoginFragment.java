package course.examples.newsspace; // Thay thế bằng package chính xác của bạn

import android.content.Intent;
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

import course.examples.newsspace.databinding.FragmentLoginBinding; // Thay thế bằng package chính xác của bạn

/**
 * Fragment chịu trách nhiệm cho màn hình Đăng nhập.
 * Xử lý việc nhập liệu của người dùng, gọi API đăng nhập,
 * và điều hướng đến các luồng khác như Đăng ký hoặc Quên mật khẩu.
 */
public class LoginFragment extends Fragment {

    // 1. Khai báo ViewBinding để tương tác an toàn với các View trong layout
    private FragmentLoginBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 2. Khởi tạo đối tượng binding từ layout XML tương ứng
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 3. Gán các sự kiện click cho các nút và text
        setupClickListeners();
    }

    /**
     * Thiết lập tất cả các sự kiện OnClickListener cho các View trên màn hình.
     */
    private void setupClickListeners() {
        // Sự kiện khi nhấn nút "Đăng nhập"
        binding.loginButton.setOnClickListener(v -> handleLogin());

        // Sự kiện khi nhấn vào text "Đăng ký ngay"
        binding.registerTextView.setOnClickListener(v -> {
            // Sử dụng Navigation Component để chuyển sang RegisterFragment
            // Action này phải được định nghĩa trong file res/navigation/auth_nav_graph.xml
            NavHostFragment.findNavController(LoginFragment.this)
                    .navigate(R.id.action_loginFragment_to_registerFragment);
        });

        // Sự kiện khi nhấn vào text "Quên mật khẩu?"
        binding.forgotPasswordTextView.setOnClickListener(v -> {
            // Sử dụng Navigation Component để chuyển sang ForgotPasswordFragment
            // Action này cũng phải được định nghĩa trong auth_nav_graph.xml
            NavHostFragment.findNavController(LoginFragment.this)
                    .navigate(R.id.action_loginFragment_to_forgotPasswordFragment);
        });
    }

    /**
     * Xử lý logic chính khi người dùng nhấn nút Đăng nhập.
     */
    private void handleLogin() {
        // Lấy dữ liệu từ EditText và loại bỏ khoảng trắng thừa
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào (validation)
        if (email.isEmpty() || password.isEmpty()) {
            showValidationDialog("Thông tin không hợp lệ", "Vui lòng nhập đầy đủ email và mật khẩu.");
            return; // Dừng thực thi nếu thông tin không hợp lệ
        }

        // Bắt đầu quá trình đăng nhập, hiển thị trạng thái loading
        showLoading(true);

        // *** GIẢ LẬP GỌI API ĐĂNG NHẬP ***
        // Trong ứng dụng thực tế, đây là nơi bạn sẽ gọi đến server.
        // Sử dụng Handler để mô phỏng độ trễ mạng là 2 giây.
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Sau khi có kết quả từ server, ẩn trạng thái loading
            showLoading(false);

            // Xử lý kết quả trả về
            if (email.equals("test@gmail.com") && password.equals("123456")) {
                // ĐĂNG NHẬP THÀNH CÔNG
                // Chuyển sang MainActivity và kết thúc luồng xác thực
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                if (getActivity() != null) {
                    getActivity().finish();
                }
            } else {
                // ĐĂNG NHẬP THẤT BẠI
                // Hiển thị Dialog tùy chỉnh đã thiết kế
                new LoginFailDialogFragment().show(getParentFragmentManager(), "LoginFailDialog");
            }
        }, 2000);
    }

    /**
     * Hiển thị hoặc ẩn giao diện loading (ProgressBar).
     * Đồng thời vô hiệu hóa các nút để tránh người dùng click nhiều lần.
     * @param isLoading True nếu muốn hiển thị loading, False nếu muốn ẩn.
     */
    private void showLoading(boolean isLoading) {
        if (isLoading) {
            binding.loadingProgressBar.setVisibility(View.VISIBLE);
            binding.loginButton.setEnabled(false);
            binding.registerTextView.setEnabled(false);
        } else {
            binding.loadingProgressBar.setVisibility(View.GONE);
            binding.loginButton.setEnabled(true);
            binding.registerTextView.setEnabled(true);
        }
    }

    /**
     * Hiển thị một AlertDialog cơ bản cho các lỗi nhập liệu.
     * @param title Tiêu đề của dialog.
     * @param message Nội dung thông báo của dialog.
     */
    private void showValidationDialog(String title, String message) {
        // Luôn kiểm tra isAdded() trước khi hiển thị dialog từ Fragment
        // để tránh lỗi "Fragment not attached to a context".
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
        // 4. Dọn dẹp đối tượng binding khi View của Fragment bị hủy
        // Điều này rất quan trọng để tránh rò rỉ bộ nhớ (memory leaks).
        binding = null;
    }
}