package course.examples.newsspace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import course.examples.newsspace.api.ApiClient;
import course.examples.newsspace.databinding.FragmentResetPasswordBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordFragment extends Fragment {

    private FragmentResetPasswordBinding binding;
    private String userEmail;
    private String otp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ResetPasswordFragmentArgs args = ResetPasswordFragmentArgs.fromBundle(getArguments());
            userEmail = args.getEmail();
            otp = args.getOtp();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.confirmButton.setOnClickListener(v -> handleResetPassword());
    }

    private void handleResetPassword() {
        // Clear previous errors
        binding.newPasswordInputLayout.setError(null);
        binding.confirmPasswordInputLayout.setError(null);

        String newPassword = Objects.requireNonNull(binding.newPasswordEditText.getText()).toString();
        String confirmPassword = Objects.requireNonNull(binding.confirmPasswordEditText.getText()).toString();

        if (newPassword.isEmpty() || newPassword.length() < 6) {
            binding.newPasswordInputLayout.setError("Mật khẩu phải có ít nhất 6 ký tự.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            binding.confirmPasswordInputLayout.setError("Mật khẩu xác nhận không khớp.");
            return;
        }

        showLoading(true);

        Map<String, String> resetPasswordBody = new LinkedHashMap<>();
        resetPasswordBody.put("otp", otp);
        resetPasswordBody.put("newPassword", newPassword);
        resetPasswordBody.put("email", userEmail);

        ApiClient.getApiService(requireContext()).resetPassword(resetPasswordBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đặt lại mật khẩu thành công.", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(ResetPasswordFragment.this)
                            .navigate(ResetPasswordFragmentDirections.actionResetPasswordFragmentToLoginFragment());
                } else {
                    showErrorDialog("Lỗi", "Không thể đặt lại mật khẩu. Vui lòng thử lại.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                showLoading(false);
                showErrorDialog("Lỗi mạng", "Không thể kết nối đến máy chủ.");
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (binding == null) return;
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
        binding = null;
    }
}