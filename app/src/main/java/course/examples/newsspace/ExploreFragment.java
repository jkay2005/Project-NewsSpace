package course.examples.newsspace;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import course.examples.newsspace.databinding.FragmentExploreBinding;
import course.examples.newsspace.model.Article;
import course.examples.newsspace.model.RssItem; // Dùng để hứng dữ liệu từ RecommendationResponse
import course.examples.newsspace.model.UpdatePreferencesRequest;
import course.examples.newsspace.model.RecommendationResponse;
import course.examples.newsspace.api.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreFragment extends Fragment {

    private FragmentExploreBinding binding;
    private ArticleListAdapter newsAdapter;
    private final List<Article> articleList = new ArrayList<>();
    private final List<String> allTopics = Arrays.asList(
            "Thời sự", "Chính trị", "Thế giới", "Tiêu dùng", "Đời sống", "Du lịch",
            "Văn hóa", "Giải trí", "Giáo dục", "Thể thao", "Sức khỏe", "Công nghệ",
            "Thời trang", "Xe", "Kinh tế"
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExploreBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupNewsRecyclerView();
        setupTopicChips();
        setupClickListeners();

        // Ban đầu, tải và hiển thị tin tức gợi ý
        loadRecommendedNews();
    }

    private void setupClickListeners() {
        binding.headerLayout.customizeButtonInHeader.setOnClickListener(v -> showCustomizeView());
        binding.confirmButton.setOnClickListener(v -> handleConfirmSelection());
    }

    private void setupNewsRecyclerView() {
        newsAdapter = new ArticleListAdapter(articleList);
        binding.newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.newsRecyclerView.setAdapter(newsAdapter);
    }

    private void setupTopicChips() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        binding.topicChipGroup.removeAllViews();
        for (String topic : allTopics) {
            Chip chip = (Chip) inflater.inflate(R.layout.chip_choice, binding.topicChipGroup, false);
            chip.setText(topic);
            binding.topicChipGroup.addView(chip);
        }
    }

    private void showCustomizeView() {
        binding.newsRecyclerView.setVisibility(View.GONE);
        binding.headerLayout.customizeButtonInHeader.setVisibility(View.INVISIBLE);
        binding.customizeLayout.setVisibility(View.VISIBLE);
        // TODO: Lấy sở thích hiện tại của người dùng và check vào các Chip tương ứng
    }

    private void showNewsView() {
        binding.customizeLayout.setVisibility(View.GONE);
        binding.newsRecyclerView.setVisibility(View.VISIBLE);
        binding.headerLayout.customizeButtonInHeader.setVisibility(View.VISIBLE);
    }

    /**
     * Lấy các chủ đề đã chọn và gửi lên server.
     */
    private void handleConfirmSelection() {
        showLoading(true, "Đang cập nhật sở thích...");

        List<String> selectedTopics = getSelectedTopics();
        UpdatePreferencesRequest request = new UpdatePreferencesRequest(selectedTopics);

        ApiClient.getApiService(requireContext()).updatePreferences(request).enqueue(new Callback<UpdatePreferencesRequest>() {
            @Override
            public void onResponse(@NonNull Call<UpdatePreferencesRequest> call, @NonNull Response<UpdatePreferencesRequest> response) {
                showLoading(false, null);
                if (response.isSuccessful()) {
                    // Cập nhật sở thích thành công, giờ tải lại tin tức
                    showNewsView();
                    loadRecommendedNews();
                } else {
                    Toast.makeText(getContext(), "Cập nhật sở thích thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UpdatePreferencesRequest> call, @NonNull Throwable t) {
                showLoading(false, null);
                Log.e("ExploreFragment", "Update Prefs Failed: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Tải danh sách tin tức được gợi ý từ API.
     */
    private void loadRecommendedNews() {
        showLoading(true, "Đang tải tin cho bạn...");

        ApiClient.getApiService(requireContext()).getRecommendations().enqueue(new Callback<RecommendationResponse>() {
            @Override
            public void onResponse(@NonNull Call<RecommendationResponse> call, @NonNull Response<RecommendationResponse> response) {
                showLoading(false, null);
                if (response.isSuccessful() && response.body() != null) {
                    RecommendationResponse data = response.body();

                    // Lấy danh sách tin RSS từ response (giả sử chỉ dùng tin RSS)
                    List<RssItem> recommendedItems = data.getRss();

                    // Chuyển đổi và cập nhật UI
                    articleList.clear();
                    if (recommendedItems != null) {
                        for (RssItem item : recommendedItems) {
                            articleList.add(Article.createStandardArticle(
                                    item.getTitle(),
                                    item.getPublishedAt(),
                                    item.getImageUrl()
                            ));
                        }
                    }
                    newsAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(getContext(), "Không thể tải tin tức gợi ý", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RecommendationResponse> call, @NonNull Throwable t) {
                showLoading(false, null);
                Log.e("ExploreFragment", "Get Recommendations Failed: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<String> getSelectedTopics() {
        List<String> selected = new ArrayList<>();
        for (int i = 0; i < binding.topicChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) binding.topicChipGroup.getChildAt(i);
            if (chip.isChecked()) {
                selected.add(chip.getText().toString());
            }
        }
        return selected;
    }

    // Hàm helper để quản lý trạng thái loading
    private void showLoading(boolean isLoading, @Nullable String message) {
        // TODO: Implement một giao diện loading tốt hơn
        if (isLoading) {
            // Hiển thị ProgressBar, có thể kèm theo text
        } else {
            // Ẩn ProgressBar
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}