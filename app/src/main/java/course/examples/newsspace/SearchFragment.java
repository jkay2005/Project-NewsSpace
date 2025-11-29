package course.examples.newsspace;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import java.util.ArrayList;
import java.util.List;
import course.examples.newsspace.api.ApiClient;
import course.examples.newsspace.databinding.FragmentSearchBinding;
import course.examples.newsspace.model.Article;
import course.examples.newsspace.model.RssItem; // Giả sử API trả về RssItem
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;
private ArticleListAdapter adapter;
private final List<Article> articleList = new ArrayList<>();

@Override
public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentSearchBinding.inflate(inflater, container, false);
    return binding.getRoot();
}

@Override
public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    setupRecyclerView();
    setupClickListeners();
}

private void setupRecyclerView() {
    // Tái sử dụng ArticleListAdapter
    adapter = new ArticleListAdapter(articleList);
    binding.searchResultsRecyclerView.setAdapter(adapter);
}

private void setupClickListeners() {
    // Nút quay lại
    binding.backButton.setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());

    // Nút tìm kiếm (kính lúp)
    binding.searchButton.setOnClickListener(v -> performSearch());

    // Bắt sự kiện nhấn "Search/Enter" trên bàn phím
    binding.searchInputEditText.setOnEditorActionListener((v, actionId, event) -> {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            performSearch();
            return true;
        }
        return false;
    });
}

private void performSearch() {
    String query = binding.searchInputEditText.getText().toString().trim();
    if (query.isEmpty()) {
        Toast.makeText(getContext(), "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
        return;
    }

    showLoading(true);

    // TODO: Thay "searchArticles" bằng tên phương thức API của bạn
    ApiClient.getApiService(requireContext()).searchArticles(query).enqueue(new Callback<List<RssItem>>() {
        @Override
        public void onResponse(@NonNull Call<List<RssItem>> call, @NonNull Response<List<RssItem>> response) {
            showLoading(false);
            if (response.isSuccessful() && response.body() != null) {
                List<RssItem> results = response.body();
                if (results.isEmpty()) {
                    showEmptyState(true);
                } else {
                    displayResults(results);
                }
            } else {
                Toast.makeText(getContext(), "Tìm kiếm thất bại", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(@NonNull Call<List<RssItem>> call, @NonNull Throwable t) {
            showLoading(false);
            Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
        }
    });
}

private void displayResults(List<RssItem> results) {
    articleList.clear();
    for (RssItem item : results) {
        // Chuyển đổi RssItem thành Article (loại standard)
        articleList.add(Article.createStandardArticle(
                item.getTitle(),
                item.getPublishedAt(),
                item.getImageUrl()
        ));
    }
    adapter.notifyDataSetChanged();

    binding.resultsTitleTextView.setVisibility(View.VISIBLE);
    binding.searchResultsRecyclerView.setVisibility(View.VISIBLE);
    binding.emptyStateTextView.setVisibility(View.GONE);
}

private void showLoading(boolean isLoading) {
    binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    if (isLoading) {
        binding.resultsTitleTextView.setVisibility(View.GONE);
        binding.searchResultsRecyclerView.setVisibility(View.GONE);
        binding.emptyStateTextView.setVisibility(View.GONE);
    }
}

private void showEmptyState(boolean show) {
    binding.emptyStateTextView.setVisibility(show ? View.VISIBLE : View.GONE);
    binding.resultsTitleTextView.setVisibility(View.GONE);
    binding.searchResultsRecyclerView.setVisibility(View.GONE);
}}