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

import com.bumptech.glide.Glide;

import course.examples.newsspace.databinding.FragmentSettingsBinding;
import course.examples.newsspace.databinding.ItemSettingBinding;
import course.examples.newsspace.model.User;
import course.examples.newsspace.utils.SessionManager;
import course.examples.newsspace.utils.CredentialsManager;

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

            // LỖI 1 ĐÃ SỬA:
            // Tạm thời vô hiệu hóa việc tải ảnh từ URL vì model User chưa có getAvatarUrl().
            Glide.with(this)
                    .load((String) null) // Truyền vào null để Glide bỏ qua việc tải ảnh
                    .placeholder(R.drawable.ic_avatar_placeholder)
                    .error(R.drawable.ic_avatar_placeholder)
                    .circleCrop()
                    .into(binding.userAvatarImageView);
        }
    }

    private void setupSettingItems() {
        // LỖI 2 ĐÃ SỬA (TẠM THỜI):
        // TODO: Thay lại bằng các icon đúng sau khi đã import từ Figma.
        configureSettingItem(binding.generalSettingsLayout, R.drawable.ic_settings_general,
                "Cài đặt chung", "Thay đổi các tùy chỉnh cơ bản", true, v -> showToast("Chức năng Cài đặt chung"));

        configureSettingItem(binding.notificationSettingsLayout, R.drawable.ic_settings_notification,
                "Thông báo", "Tùy chỉnh liên quan đến thông báo", true, v -> showToast("Chức năng Thông báo"));

        configureSettingItem(binding.versionLayout, R.drawable.ic_settings_version,
                "Phiên bản", "v1.1", false, null);

        configureSettingItem(binding.termsLayout, R.drawable.ic_settings_terms,
                "Điều khoản sử dụng", null, true, v -> showToast("Chức năng Điều khoản sử dụng"));

        configureSettingItem(binding.policyLayout, R.drawable.ic_settings_policy,
                "Chính sách bảo mật", null, true, v -> showToast("Chức năng Chính sách bảo mật"));
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
        sessionManager.clearSession();
        // THÊM DÒNG NÀY: Xóa thông tin đăng nhập đã lưu
        new CredentialsManager(requireContext()).clearCredentials();
        Intent intent = new Intent(requireActivity(), SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void configureSettingItem(ItemSettingBinding itemBinding, int iconResId, String title, @Nullable String subtitle, boolean showChevron, @Nullable View.OnClickListener listener) {
        itemBinding.settingIcon.setImageResource(iconResId);
        itemBinding.settingTitle.setText(title);

        if (subtitle != null && !subtitle.isEmpty()) {
            itemBinding.settingSubtitle.setVisibility(View.VISIBLE);
            itemBinding.settingSubtitle.setText(subtitle);
        } else {
            itemBinding.settingSubtitle.setVisibility(View.GONE);
        }

        itemBinding.settingChevron.setVisibility(showChevron ? View.VISIBLE : View.GONE);

        if (listener != null) {
            itemBinding.getRoot().setOnClickListener(listener);
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