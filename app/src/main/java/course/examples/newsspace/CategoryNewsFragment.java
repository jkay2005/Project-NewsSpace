package course.examples.newsspace; // Thay bằng package của bạn

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

import java.util.ArrayList;
import java.util.List;

import course.examples.newsspace.databinding.FragmentCategoryNewsBinding;
import course.examples.newsspace.model.Article;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import course.examples.newsspace.api.GNewsApiClient;
import course.examples.newsspace.model.gnews.GNewsArticle;
 import course.examples.newsspace.model.gnews.GNewsResponse;

public class CategoryNewsFragment extends Fragment {

    private FragmentCategoryNewsBinding binding;
    private ArticleListAdapter adapter;
    private final List<Article> articleList = new ArrayList<>();
    private static final String TAG = "CategoryNewsFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoryNewsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String categoryName = "Mới nhất";
        if (getArguments() != null) {
            categoryName = CategoryNewsFragmentArgs.fromBundle(getArguments()).getCategoryName();
        }

        setupToolbar(categoryName);
        setupRecyclerView(); // <-- CHÚNG TA SẼ SỬA PHƯƠNG THỨC NÀY
        loadNewsData(categoryName);
    }

    private void setupToolbar(String title) {
        binding.toolbar.setTitle(title);
        binding.toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
    }

    // ===================================================================================
// == PHẦN CẬP NHẬT CHÍNH NẰM Ở ĐÂY
// ===================================================================================
    private void setupRecyclerView() {
        // 1. TẠO RA MỘT LISTENER ĐỂ XỬ LÝ SỰ KIỆN CLICK
        ArticleListAdapter.OnArticleClickListener clickListener = article -> {
            if (article.getUrl() != null && !article.getUrl().isEmpty()) {
                // Tạo action điều hướng bằng Safe Args, khớp với ID trong nav_graph.xml
                CategoryNewsFragmentDirections.ActionCategoryNewsFragmentToArticleDetailFragment action =
                        CategoryNewsFragmentDirections.actionCategoryNewsFragmentToArticleDetailFragment(article.getUrl());

                // Thực hiện điều hướng
                NavHostFragment.findNavController(CategoryNewsFragment.this).navigate(action);
            } else {
                Toast.makeText(getContext(), "Bài viết này không có đường dẫn.", Toast.LENGTH_SHORT).show();
            }
        };

        // 2. KHỞI TẠO ADAPTER VÀ TRUYỀN LISTENER VÀO
        adapter = new ArticleListAdapter(articleList, clickListener);
        binding.newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.newsRecyclerView.setAdapter(adapter);
    }

    // Phương thức loadNewsData giữ nguyên như cũ...
    private void loadNewsData(String categoryNameToFilter) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.newsRecyclerView.setVisibility(View.GONE);
        Log.d(TAG, "Đang tìm kiếm tin tức cho chuyên mục: " + categoryNameToFilter);

        String apiKey = "ec4a35d60e28736506770fac7add6e82";

        Call<GNewsResponse> apiCall;
        if ("Mới nhất".equalsIgnoreCase(categoryNameToFilter)) {
            apiCall = GNewsApiClient.getApiService().getTopHeadlines(apiKey, "vi", "vn");
        } else {
            apiCall = GNewsApiClient.getApiService().searchArticles(categoryNameToFilter, apiKey, "vi", "publishedAt");
        }

        apiCall.enqueue(new Callback<GNewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<GNewsResponse> call, @NonNull Response<GNewsResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.newsRecyclerView.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    List<GNewsArticle> gnewsArticles = response.body().getArticles();
                    Log.d(TAG, "Tìm thấy " + gnewsArticles.size() + " bài báo cho '" + categoryNameToFilter + "'");

                    articleList.clear();

                    if (gnewsArticles.isEmpty()) {
                        Toast.makeText(getContext(), "Không có tin tức nào cho chuyên mục này", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i = 0; i < gnewsArticles.size(); i++) {
                            GNewsArticle gnewsArticle = gnewsArticles.get(i);
                            boolean isFeatured = i < 2;

                            if (isFeatured) {
                                articleList.add(Article.createFeaturedArticle(
                                        gnewsArticle.getTitle(),
                                        gnewsArticle.getDescription(),
                                        gnewsArticle.getImage(),
                                        gnewsArticle.getUrl()));
                            } else {
                                articleList.add(Article.createStandardArticle(
                                        gnewsArticle.getTitle(),
                                        gnewsArticle.getPublishedAt(),
                                        gnewsArticle.getImage(),
                                        gnewsArticle.getUrl()));
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();

                } else {
                    Log.e(TAG, "API call thất bại. Mã lỗi: " + response.code());
                    Toast.makeText(getContext(), "Không thể tải tin tức. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GNewsResponse> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Lỗi mạng: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi mạng. Vui lòng kiểm tra kết nối và thử lại.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
