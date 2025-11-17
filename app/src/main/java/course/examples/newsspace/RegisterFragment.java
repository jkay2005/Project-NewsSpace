package course.examples.newsspace; // Thay bằng package của bạn

import android.os.Bundle;
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

// Import các lớp cần thiết cho việc gọi API
import course.examples.newsspace.databinding.FragmentRegisterBinding;
import course.examples.newsspace.model.RegisterRequest;
import course.examples.newsspace.model.User;
import course.examples.newsspace.api.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.registerButton.setOnClickListener(v -> handleRegister());
        binding.loginTextView.setOnClickListener(v -> NavHostFragment.findNavController(RegisterFragment.this).navigateUp());
    }

    private void handleRegister() {
        // Lấy dữ liệu từ các EditText
        String email = binding.emailEditText.getText().toString().trim();
        String username = binding.usernameEditText.getText().toString().trim(); // API của bạn dùng "name"
        String password = binding.passwordEditText.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordEditText.getText().toString().trim();

        // Kiểm tra tính hợp lệ của dữ liệu (Validation)
        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showErrorDialog("Thông tin không hợp lệ", "Vui lòng điền đầy đủ thông tin.");
            return;
        }

        if (password.length() < 6) {
            showErrorDialog("Mật khẩu không hợp lệ", "Mật khẩu phải có ít nhất 6 ký tự.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showErrorDialog("Mật khẩu không hợp lệ", "Mật khẩu xác nhận không khớp.");
            return;
        }

        showLoading(true);

        // 1. Tạo đối tượng Request Body
        RegisterRequest registerRequest = new RegisterRequest(username, email, password);

        // 2. Thực hiện gọi API đăng ký
        ApiClient.getApiService(requireContext()).registerUser(registerRequest).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                showLoading(false);

                // 3. Xử lý phản hồi
                if (response.isSuccessful() && response.body() != null) {
                    // ĐĂNG KÝ THÀNH CÔNG (HTTP code 201)
                    Toast.makeText(getContext(), "Đăng ký thành công! Vui lòng xác thực OTP.", Toast.LENGTH_LONG).show();

                    // Chuyển sang màn hình OTP và truyền email theo
                    // (Sử dụng Safe Args để truyền dữ liệu)
                    RegisterFragmentDirections.ActionRegisterFragmentToOtpFragment action =
                            RegisterFragmentDirections.actionRegisterFragmentToOtpFragment(email);
                    NavHostFragment.findNavController(RegisterFragment.this).navigate(action);

                } else {
                    // ĐĂNG KÝ THẤT BẠI (HTTP code 4xx, 5xx)
                    // Ví dụ: email đã tồn tại
                    // TODO: Phân tích response.errorBody() để có thông báo lỗi chính xác hơn
                    showErrorDialog("Đăng ký thất bại", "Email này có thể đã được sử dụng. Vui lòng thử lại.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                // 4. XỬ LÝ LỖI MẠNG
                showLoading(false);
                Log.e("RegisterFragment", "API Call Failed: " + t.getMessage());
                showErrorDialog("Lỗi kết nối", "Không thể kết nối đến máy chủ. Vui lòng kiểm tra lại kết nối internet.");
            }
        });
    }

    private void showLoading(boolean isLoading) {
        binding.loadingProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.registerButton.setEnabled(!isLoading);
        binding.loginTextView.setEnabled(!isLoading);
    }

    private void showErrorDialog(String title, String message) {
        if (isAdded()) {
            new AlertDialog.Builder(requireContext())
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}