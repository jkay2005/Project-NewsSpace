package course.examples.newsspace; // Thay bằng package của bạn

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import course.examples.newsspace.databinding.FragmentLoginBinding;
import course.examples.newsspace.model.LoginRequest;
import course.examples.newsspace.model.LoginResponse;
import course.examples.newsspace.api.ApiClient;
import course.examples.newsspace.utils.SessionManager; // Import lớp SessionManager
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private SessionManager sessionManager; // Khai báo SessionManager

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo SessionManager
        sessionManager = new SessionManager(requireContext());

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.loginButton.setOnClickListener(v -> handleLogin());
        binding.registerTextView.setOnClickListener(v -> NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.action_loginFragment_to_registerFragment));
        binding.forgotPasswordTextView.setOnClickListener(v -> NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.action_loginFragment_to_forgotPasswordFragment));
    }

    private void handleLogin() {
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showValidationDialog("Thông tin không hợp lệ", "Vui lòng nhập đầy đủ email và mật khẩu.");
            return;
        }

        showLoading(true);

        // 1. Tạo đối tượng Request Body
        LoginRequest loginRequest = new LoginRequest(email, password);

        // 2. Thực hiện gọi API bất đồng bộ
        ApiClient.getApiService(requireContext()).loginUser(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                showLoading(false);

                // 3. Xử lý phản hồi từ server
                if (response.isSuccessful() && response.body() != null) {
                    // ĐĂNG NHẬP THÀNH CÔNG
                    LoginResponse loginResponse = response.body();

                    // Lưu token và thông tin người dùng
                    sessionManager.saveAuthToken(loginResponse.getToken());
                    sessionManager.saveUser(loginResponse.getUser());

                    Toast.makeText(getContext(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    // Chuyển sang MainActivity
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    // Các cờ này đảm bảo người dùng không thể quay lại màn hình đăng nhập
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else {
                    // ĐĂNG NHẬP THẤT BẠI (sai mật khẩu, tài khoản không tồn tại...)
                    // Hiển thị dialog lỗi tùy chỉnh
                    new LoginFailDialogFragment().show(getParentFragmentManager(), "LoginFailDialog");
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                // 4. XỬ LÝ LỖI MẠNG
                showLoading(false);
                Log.e("LoginFragment", "API Call Failed: " + t.getMessage()); // Ghi log lỗi để debug
                showValidationDialog("Lỗi kết nối", "Không thể kết nối đến máy chủ. Vui lòng kiểm tra lại kết nối internet của bạn.");
            }
        });

        // ==========================================================
        // == THÊM PHẦN GIẢ LẬP ĐĂNG NHẬP THÀNH CÔNG             ==
        // ==========================================================
        // Dùng Handler để giả lập độ trễ mạng, tạo cảm giác thật hơn
//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//            showLoading(false);
//
//            // Giả vờ như đã nhận được token và thông tin người dùng
//            // Bạn không cần lưu token thật vào SessionManager lúc này
//
//            Toast.makeText(getContext(), "Đăng nhập thành công! (Chế độ Demo)", Toast.LENGTH_SHORT).show();
//
//            // Chuyển sang MainActivity
//            Intent intent = new Intent(getActivity(), MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//
//        }, 1000); // Giả lập chờ 1 giây
    }

    private void showLoading(boolean isLoading) {
        binding.loadingProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.loginButton.setEnabled(!isLoading);
        binding.registerTextView.setEnabled(!isLoading);
        binding.forgotPasswordTextView.setEnabled(!isLoading);
    }

    private void showValidationDialog(String title, String message) {
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