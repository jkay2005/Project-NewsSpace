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
import java.util.ArrayList;
import java.util.List;
import course.examples.newsspace.api.ApiClient;
import course.examples.newsspace.databinding.FragmentUserActivityBinding;
import course.examples.newsspace.model.Article;
import course.examples.newsspace.model.RssItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivityFragment extends Fragment {

    private FragmentUserActivityBinding binding;
    private ArticleListAdapter adapter;
    private final List<Article> articleList = new ArrayList<>();
    private String activityType;
    private String screenTitle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Nhận tham số từ navigation graph
        if (getArguments() != null) {
            activityType = UserActivityFragmentArgs.fromBundle(getArguments()).getActivityType();
            screenTitle = UserActivityFragmentArgs.fromBundle(getArguments()).getScreenTitle();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserActivityBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar();
        setupRecyclerView();
        loadData();
    }

    private void setupToolbar() {
        binding.toolbar.setTitle(screenTitle);
        binding.toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
    }

    private void setupRecyclerView() {
        adapter = new ArticleListAdapter(articleList);
        binding.newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.newsRecyclerView.setAdapter(adapter);
    }

    private void loadData() {
        showLoading(true);

        // Chọn API call dựa trên activityType
        Call<List<RssItem>> apiCall = getApiCall();

        if (apiCall == null) {
            showLoading(false);
            Toast.makeText(getContext(), "Loại hoạt động không xác định", Toast.LENGTH_SHORT).show();
            return;
        }

        apiCall.enqueue(new Callback<List<RssItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<RssItem>> call, @NonNull Response<List<RssItem>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<RssItem> items = response.body();
                    if (items.isEmpty()) {
                        binding.emptyStateTextView.setVisibility(View.VISIBLE);
                        binding.newsRecyclerView.setVisibility(View.GONE);
                    } else {
                        articleList.clear();
                        for (RssItem item : items) {
                            // Chuyển đổi RssItem thành Article để Adapter hiển thị
                            articleList.add(Article.createStandardArticle(
                                    item.getTitle(),
                                    item.getPublishedAt(),
                                    item.getImageUrl()
                            ));
                        }
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getContext(), "Không thể tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<RssItem>> call, @NonNull Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Call<List<RssItem>> getApiCall() {
        if (getContext() == null) return null;

        switch (activityType) {
            case "MY_BLOGS":
                return ApiClient.getApiService(requireContext()).getMyBlogs();
            case "SAVED_NEWS":
                return ApiClient.getApiService(requireContext()).getSavedNews();
            case "HISTORY":
                return ApiClient.getApiService(requireContext()).getViewHistory();
            default:
                return null;
        }
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.newsRecyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        if (isLoading) {
            binding.emptyStateTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}