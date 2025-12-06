
package course.examples.newsspace;

import android.content.Intent;
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

import com.bumptech.glide.Glide;

import course.examples.newsspace.api.ApiClient;
import course.examples.newsspace.databinding.FragmentSettingsBinding;
import course.examples.newsspace.databinding.ItemSettingBinding;
import course.examples.newsspace.model.LogoutRequest;
import course.examples.newsspace.model.User;
import course.examples.newsspace.utils.CredentialsManager;
import course.examples.newsspace.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(requireContext());
        loadUserProfile();
        setupSettingItems();
        setupLogoutButton();
    }

    private void loadUserProfile() {
        User currentUser = sessionManager.getUser();
        if (currentUser != null) {
            binding.usernameTextView.setText(currentUser.getName());
            binding.emailTextView.setText(currentUser.getEmail());

            Glide.with(this)
                    .load(currentUser.getAvatarUrl())
                    .placeholder(R.drawable.ic_avatar_placeholder)
                    .error(R.drawable.ic_avatar_placeholder)
                    .circleCrop()
                    .into(binding.userAvatarImageView);
        }
    }

    private void setupSettingItems() {
        configureSettingItem(binding.generalSettingsLayout, R.drawable.ic_settings_general,
                "Cài đặt chung", "Thay đổi các tùy chỉnh cơ bản", true, true,
                v -> NavHostFragment.findNavController(this).navigate(R.id.action_settingsFragment_to_generalSettingsFragment));

        configureSettingItem(binding.notificationSettingsLayout, R.drawable.ic_settings_notification,
                "Thông báo", "Tùy chỉnh liên quan đến thông báo", true, true,
                v -> NavHostFragment.findNavController(this).navigate(R.id.action_settingsFragment_to_notificationSettingsFragment));

        configureSettingItem(binding.versionLayout, R.drawable.ic_settings_version,
                "Phiên bản", "v1.1", false, true, null);

        configureSettingItem(binding.termsLayout, R.drawable.ic_settings_terms,
                "Điều khoản sử dụng", null, true, true, v -> showToast("Chức năng Điều khoản sử dụng"));

        configureSettingItem(binding.policyLayout, R.drawable.ic_settings_policy,
                "Chính sách bảo mật", null, true, true, v -> showToast("Chức năng Chính sách bảo mật"));
    }

    private void configureSettingItem(ItemSettingBinding itemBinding, int iconResId, String title, @Nullable String subtitle, boolean showChevron, boolean hideDivider, @Nullable View.OnClickListener listener) {
        itemBinding.settingIcon.setImageResource(iconResId);
        itemBinding.settingTitle.setText(title);

        if (subtitle != null && !subtitle.isEmpty()) {
            itemBinding.settingSubtitle.setVisibility(View.VISIBLE);
            itemBinding.settingSubtitle.setText(subtitle);
        } else {
            itemBinding.settingSubtitle.setVisibility(View.GONE);
        }

        itemBinding.settingChevron.setVisibility(showChevron ? View.VISIBLE : View.GONE);
        itemBinding.settingDivider.setVisibility(hideDivider ? View.GONE : View.VISIBLE);

        if (listener != null) {
            itemBinding.getRoot().setOnClickListener(listener);
        }
    }

    private void setupLogoutButton() {
        binding.logoutLayout.setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> performLogout())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void performLogout() {
        String refreshToken = sessionManager.getRefreshToken();
        if (refreshToken == null) {
            // If there's no refresh token, just clean up locally.
            Toast.makeText(getContext(), "Không tìm thấy thông tin đăng nhập, đăng xuất cục bộ.", Toast.LENGTH_SHORT).show();
            cleanupAndNavigate();
            return;
        }

        binding.logoutProgressBar.setVisibility(View.VISIBLE);
        binding.logoutLayout.setEnabled(false);

        LogoutRequest logoutRequest = new LogoutRequest(refreshToken);

        ApiClient.getApiService(requireContext()).logout(logoutRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                // Backend request finished, proceed with client-side cleanup regardless of outcome
                cleanupAndNavigate();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                // Network error or other issue, still proceed with client-side cleanup
                Toast.makeText(getContext(), "Không thể kết nối đến máy chủ, đăng xuất cục bộ.", Toast.LENGTH_SHORT).show();
                cleanupAndNavigate();
            }
        });
    }

    private void cleanupAndNavigate() {
        // Clear local session and credentials
        sessionManager.clearSession();
        new CredentialsManager(requireContext()).clearCredentials();

        // Navigate to SplashActivity and clear back stack
        Intent intent = new Intent(requireActivity(), SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
