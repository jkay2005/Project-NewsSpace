package course.examples.newsspace; // Thay bằng package của bạn

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.HashMap;
import java.util.Map;

import course.examples.newsspace.api.ApiClient;
import course.examples.newsspace.databinding.FragmentForgotPasswordBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordFragment extends Fragment {

    private FragmentForgotPasswordBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.sendOtpButton.setOnClickListener(v -> handleSendOtp());
    }

    private void handleSendOtp() {
        String email = binding.emailEditText.getText().toString().trim();

        if (email.isEmpty()) {
            showErrorDialog("Lỗi", "Vui lòng nhập địa chỉ email của bạn.");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showErrorDialog("Lỗi", "Vui lòng nhập một địa chỉ email hợp lệ.");
            return;
        }

        showLoading(true);

        Map<String, String> emailBody = new HashMap<>();
        emailBody.put("email", email);

        ApiClient.getApiService(requireContext()).forgotPassword(emailBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Yêu cầu đã được gửi, vui lòng kiểm tra email.", Toast.LENGTH_SHORT).show();
                    ForgotPasswordFragmentDirections.ActionForgotPasswordFragmentToOtpFragment action =
                            ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToOtpFragment(email);
                    action.setOtpType("forgotPassword"); // Chỉ định luồng "quên mật khẩu"
                    NavHostFragment.findNavController(ForgotPasswordFragment.this).navigate(action);
                } else {
                    showErrorDialog("Lỗi", "Không thể gửi yêu cầu. Vui lòng thử lại.");
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
        binding = null;
    }
}
