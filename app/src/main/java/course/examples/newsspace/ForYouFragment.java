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
import course.examples.newsspace.api.ApiClient;
import course.examples.newsspace.databinding.FragmentForYouBinding; // <-- THAY ĐỔI BINDING
import course.examples.newsspace.model.Article;
import course.examples.newsspace.model.RecommendationResponse;
import course.examples.newsspace.model.RssItem;
import course.examples.newsspace.model.UpdatePreferencesRequest;
import retrofit2.Call; import retrofit2.Callback;
import retrofit2.Response;
public class ForYouFragment extends Fragment {
    private FragmentForYouBinding binding; // <-- THAY ĐỔI BINDING
    private ArticleListAdapter adapter;
private final List<Article> articleList = new ArrayList<>();
private final List<String> allTopics = Arrays.asList(
        "Thời sự", "Chính trị", "Thế giới", "Tiêu dùng", "Đời sống", "Du lịch",
        "Văn hóa", "Giải trí", "Giáo dục", "Thể thao", "Sức khỏe", "Công nghệ",
        "Thời trang", "Xe", "Kinh tế"
);
private ExploreFragment parentExploreFragment; // Tham chiếu đến Fragment cha

@Override
public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    binding = FragmentForYouBinding.inflate(inflater, container, false); // <-- THAY ĐỔI BINDING
    return binding.getRoot();
}

@Override
public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    // Lấy tham chiếu đến ExploreFragment cha
    if (getParentFragment() instanceof ExploreFragment) {
        parentExploreFragment = (ExploreFragment) getParentFragment(); }

    setupNewsRecyclerView();
    setupTopicChips();
    setupClickListeners();

    loadRecommendedNews();
}

private void setupClickListeners() {
    // Lấy nút "Tùy chỉnh" từ layout của Fragment cha
    if (parentExploreFragment != null) {
        parentExploreFragment.getBinding().headerLayout.customizeButton.setOnClickListener(v -> showCustomizeView()); }
}

private void setupNewsRecyclerView() {
    adapter = new ArticleListAdapter(articleList); // Đổi từ newsAdapter thành adapter
    binding.newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    binding.newsRecyclerView.setAdapter(adapter);
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
    binding.customizeLayout.setVisibility(View.VISIBLE);
    // Ẩn nút "Tùy chỉnh" trên header của cha
    if (parentExploreFragment != null) {
        parentExploreFragment.getBinding().headerLayout.customizeButton.setVisibility(View.INVISIBLE);
    }
    // TODO: Lấy sở thích hiện tại của người dùng và check vào các Chip tương ứng
}

private void showNewsView() {
    binding.customizeLayout.setVisibility(View.GONE);
    binding.newsRecyclerView.setVisibility(View.VISIBLE);
    // Hiển thị lại nút "Tùy chỉnh" trên header của cha
    if (parentExploreFragment != null) {
        parentExploreFragment.getBinding().headerLayout.customizeButton.setVisibility(View.VISIBLE);
    }
}

private void handleConfirmSelection() {
    // TODO: Hiển thị loading
    List<String> selectedTopics = getSelectedTopics();
    UpdatePreferencesRequest request = new UpdatePreferencesRequest(selectedTopics);

    ApiClient.getApiService(requireContext()).updatePreferences(request).enqueue(new Callback<UpdatePreferencesRequest>() {
        @Override
        public void onResponse(@NonNull Call<UpdatePreferencesRequest> call, @NonNull Response<UpdatePreferencesRequest> response) {
            // TODO: Ẩn loading
            if (response.isSuccessful()) {
                showNewsView();
                loadRecommendedNews();
            } else {
                Toast.makeText(getContext(), "Cập nhật sở thích thất bại", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(@NonNull Call<UpdatePreferencesRequest> call, @NonNull Throwable t) {
            // TODO: Ẩn loading
            Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
        }
    });
}

private void loadRecommendedNews() {
    // TODO: Hiển thị loading
    ApiClient.getApiService(requireContext()).getRecommendations().enqueue(new Callback<RecommendationResponse>() {
        @Override
        public void onResponse(@NonNull Call<RecommendationResponse> call, @NonNull Response<RecommendationResponse> response) {
            // TODO: Ẩn loading
            if (response.isSuccessful() && response.body() != null) {
                List<RssItem> recommendedItems = response.body().getRss();
                articleList.clear();
                if (recommendedItems != null) {
                    for (RssItem item : recommendedItems) {
                        articleList.add(Article.createStandardArticle(
                                item.getTitle(), item.getPublishedAt(), item.getImageUrl()
                        ));
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Không thể tải tin tức gợi ý", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(@NonNull Call<RecommendationResponse> call, @NonNull Throwable t) {
            // TODO: Ẩn loading
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

@Override
public void onDestroyView() {
    super.onDestroyView();
    binding = null;
}}