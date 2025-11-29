package course.examples.newsspace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import course.examples.newsspace.databinding.FragmentNotificationSettingsBinding;
import course.examples.newsspace.utils.NotificationPrefsManager;

public class NotificationSettingsFragment extends Fragment {

    private FragmentNotificationSettingsBinding binding;
    private NotificationPrefsManager prefsManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefsManager = new NotificationPrefsManager(requireContext());

        // Thiết lập nút quay lại
        binding.toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());

        setupInitialState();
        setupListeners();
    }

    /**
     * Đọc trạng thái đã lưu và cập nhật giao diện (các nút switch)
     */
    private void setupInitialState() {
        // Mục "Thông báo tin tức"
        binding.newsNotificationsLayout.settingTitle.setText("Thông báo tin tức");
        binding.newsNotificationsLayout.settingSwitch.setChecked(prefsManager.isNewsNotificationsEnabled());

        // Mục "Thông báo liên quan đến blog"
        binding.blogNotificationsLayout.settingTitle.setText("Thông báo liên quan đến blog");
        binding.blogNotificationsLayout.settingSwitch.setChecked(prefsManager.isBlogNotificationsEnabled());

        // Mục "Thông báo về chuyên mục"
        binding.categoryNotificationsLayout.settingTitle.setText("Thông báo về chuyên mục");
        binding.categoryNotificationsLayout.settingIcon.setVisibility(View.GONE); // Ẩn icon tròn không cần thiết
        binding.categoryNotificationsLayout.settingSubtitle.setVisibility(View.GONE);

        // Mục "Thông báo thời tiết"
        binding.weatherNotificationsLayout.settingTitle.setText("Thông báo thời tiết hàng ngày");
        binding.weatherNotificationsLayout.settingSwitch.setChecked(prefsManager.isWeatherNotificationsEnabled());
    }

    /**
     * Lắng nghe sự kiện người dùng thay đổi cài đặt
     */
    private void setupListeners() {
        binding.newsNotificationsLayout.settingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefsManager.setNewsNotificationsEnabled(isChecked);
        });

        binding.blogNotificationsLayout.settingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefsManager.setBlogNotificationsEnabled(isChecked);
        });

        binding.weatherNotificationsLayout.settingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefsManager.setWeatherNotificationsEnabled(isChecked);
        });

        // Xử lý click cho mục "Thông báo về chuyên mục"
        binding.categoryNotificationsLayout.getRoot().setOnClickListener(v -> {
            // TODO: Sẽ điều hướng đến màn hình mới ở phần sau
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_notificationSettingsFragment_to_categoryNotificationSettingsFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}