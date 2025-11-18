package course.examples.newsspace;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import course.examples.newsspace.databinding.FragmentExploreBinding;
import course.examples.newsspace.model.Article;
import course.examples.newsspace.model.gnews.GNewsArticle;
import course.examples.newsspace.model.gnews.GNewsResponse;
import course.examples.newsspace.api.GNewsApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import course.examples.newsspace.model.RssItem; // Dùng để hứng dữ liệu từ RecommendationResponse
import course.examples.newsspace.model.RecommendationResponse;
import course.examples.newsspace.api.ApiClient;

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
        Log.d("FRAGMENT_LIFECYCLE", "ExploreFragment onViewCreated ĐÃ ĐƯỢC GỌI!");
        setupNewsRecyclerView();
        setupTopicChips();
        setupClickListeners();


        // THAY ĐỔI 2: Ban đầu, hiển thị màn hình chọn chuyên mục thay vì tải tin tức ngay
        showCustomizeView();
    }

    private void setupClickListeners() {
        binding.headerLayout.customizeButtonInHeader.setOnClickListener(v -> showCustomizeView());
        binding.confirmButton.setOnClickListener(v -> handleConfirmSelection());
    }

    private void setupNewsRecyclerView() {
        ArticleListAdapter.OnArticleClickListener clickListener = article -> {
            if (article.getUrl() != null && !article.getUrl().isEmpty()) {
                // Sửa tên action và phương thức để khớp với ID trong nav_graph.xml
                ExploreFragmentDirections.ActionNavExploreToArticleDetailFragment action =
                        ExploreFragmentDirections.actionNavExploreToArticleDetailFragment(article.getUrl());

                NavHostFragment.findNavController(ExploreFragment.this).navigate(action);
            }  else {
                Toast.makeText(getContext(), "Bài viết này không có đường dẫn.", Toast.LENGTH_SHORT).show();
            }
        };

        // Khởi tạo adapter và truyền listener vào
        newsAdapter = new ArticleListAdapter(articleList, clickListener);
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
        List<String> selectedTopics = getSelectedTopics();

        if (selectedTopics.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng chọn ít nhất một chủ đề", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo một chuỗi truy vấn để tìm kiếm tất cả các chủ đề
        // Ví dụ: ["Kinh tế", "Thể thao"] -> "Kinh tế OR Thể thao"
        String searchQuery = selectedTopics.stream()
                .map(topic -> "\"" + topic + "\"") // Đặt trong ngoặc kép để tìm chính xác hơn
                .collect(Collectors.joining(" OR "));

        // Gọi hàm tải tin tức từ GNews với chuỗi truy vấn vừa tạo
        loadNewsFromGNews(searchQuery);
    }

    /**
     * THAY ĐỔI 4: Hàm mới để tải tin tức từ GNews dựa trên truy vấn tìm kiếm.
     */
    private void loadNewsFromGNews(String query) {
        showLoading(true);
        Log.d(TAG, "Đang tìm kiếm với truy vấn: " + query);

        String apiKey = "ec4a35d60e28736506770fac7add6e82";

        GNewsApiClient.getApiService()
                .searchArticles(query, apiKey, "vi", "publishedAt")
                .enqueue(new Callback<GNewsResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GNewsResponse> call, @NonNull Response<GNewsResponse> response) {
                        showLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            // Chuyển sang màn hình hiển thị tin tức
                            showNewsView();

                            List<GNewsArticle> gnewsArticles = response.body().getArticles();
                            Log.d(TAG, "Tìm thấy " + gnewsArticles.size() + " bài báo cho truy vấn '" + query + "'");

                            if (gnewsArticles.isEmpty()) {
                                Toast.makeText(getContext(), "Không tìm thấy bài báo nào cho các chủ đề này.", Toast.LENGTH_LONG).show();
                                // TODO: Hiển thị một TextView thông báo lỗi trên màn hình
                                // binding.emptyViewTextView.setVisibility(View.VISIBLE);
                                articleList.clear();
                                newsAdapter.notifyDataSetChanged();
                                return;
                            }

                            articleList.clear();
                            for (GNewsArticle gnewsArticle : gnewsArticles) {
                                articleList.add(Article.createStandardArticle(
                                        gnewsArticle.getTitle(),
                                        gnewsArticle.getPublishedAt(),
                                        gnewsArticle.getImage(),
                                        gnewsArticle.getUrl()
                                ));
                            }
                            newsAdapter.notifyDataSetChanged();

                        } else {
                            Log.e(TAG, "Tìm kiếm thất bại. Mã lỗi: " + response.code());
                            Toast.makeText(getContext(), "Không thể tải tin tức (Lỗi: " + response.code() + ")", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GNewsResponse> call, @NonNull Throwable t) {
                        showLoading(false);
                        Log.e(TAG, "Lỗi mạng khi tìm kiếm: " + t.getMessage());
                        Toast.makeText(getContext(), "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Tải danh sách tin tức được gợi ý từ API.
     */
    private void loadRecommendedNews() {
        showLoading(true);

        ApiClient.getApiService(requireContext()).getRecommendations().enqueue(new Callback<RecommendationResponse>() {
            @Override
            public void onResponse(@NonNull Call<RecommendationResponse> call, @NonNull Response<RecommendationResponse> response) {
                showLoading(false);
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
                                    item.getImageUrl(),
                                    item.getUrl()));
                        }
                    }
                    newsAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(getContext(), "Không thể tải tin tức gợi ý", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RecommendationResponse> call, @NonNull Throwable t) {
                showLoading(false);
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
    private void showLoading(boolean isLoading) {
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