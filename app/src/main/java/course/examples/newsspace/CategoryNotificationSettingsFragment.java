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
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import course.examples.newsspace.api.ApiClient;
import course.examples.newsspace.databinding.FragmentCategoryNotificationSettingsBinding;
import course.examples.newsspace.model.UpdateCategoryPrefsRequest;
import course.examples.newsspace.utils.NotificationPrefsManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryNotificationSettingsFragment extends Fragment {

    private FragmentCategoryNotificationSettingsBinding binding;
    private NotificationPrefsManager prefsManager;
    private CategoryNotificationAdapter adapter;
    private final List<String> allCategories = Arrays.asList(
            "Mới nhất", "Thời sự", "Chính trị", "Thế giới", "Kinh tế", "Đời sống",
            "Du lịch", "Văn hóa", "Giải trí", "Giới trẻ", "Giáo dục", "Thể thao",
            "Sức khỏe", "Công nghệ", "Thời trang", "Xe", "Tiêu dùng"
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoryNotificationSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefsManager = new NotificationPrefsManager(requireContext());
        binding.toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        Map<String, Boolean> currentStates = new HashMap<>();
        for (String category : allCategories) {
            currentStates.put(category, prefsManager.isCategoryEnabled(category));
        }

        adapter = new CategoryNotificationAdapter(allCategories, currentStates, (category, isEnabled, callback) -> {
            // Khi người dùng nhấn toggle, gọi hàm để xử lý API
            updateCategoryPreference(category, isEnabled, callback);
        });

        binding.categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.categoriesRecyclerView.setAdapter(adapter);
    }

    /**
     * Gọi API để cập nhật sở thích chuyên mục lên server.
     */
    private void updateCategoryPreference(String category, boolean isEnabled, CategoryNotificationAdapter.ApiCallback callback) {
        UpdateCategoryPrefsRequest request = new UpdateCategoryPrefsRequest(category, isEnabled);

        ApiClient.getApiService(requireContext()).updateCategoryNotificationSetting(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    // API Thành công: Lưu vào SharedPreferences và báo lại cho Adapter
                    prefsManager.setCategoryEnabled(category, isEnabled);
                    adapter.updateCategoryState(category, isEnabled);
                    callback.onComplete(true); // Báo thành công
                    Toast.makeText(getContext(), "Đã lưu cài đặt cho: " + category, Toast.LENGTH_SHORT).show();
                } else {
                    // API Thất bại: Báo lại cho Adapter để nó hoàn tác UI
                    callback.onComplete(false); // Báo thất bại
                    Toast.makeText(getContext(), "Lỗi, không thể lưu cài đặt", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                // API Thất bại: Báo lại cho Adapter để nó hoàn tác UI
                callback.onComplete(false); // Báo thất bại
                Toast.makeText(getContext(), "Lỗi mạng, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}