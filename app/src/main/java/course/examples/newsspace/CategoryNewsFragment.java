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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import course.examples.newsspace.api.ApiClient;
import course.examples.newsspace.databinding.FragmentCategoryNewsBinding;
import course.examples.newsspace.model.Article;
import course.examples.newsspace.utils.TopicHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryNewsFragment extends Fragment {

    private FragmentCategoryNewsBinding binding;
    private ArticleListAdapter adapter;
    private final List<Article> articleList = new ArrayList<>();
    private String topicSlug;
    private String categoryDisplayName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the arguments passed from HomeFragment
        if (getArguments() != null) {
            topicSlug = CategoryNewsFragmentArgs.fromBundle(getArguments()).getCategoryName();
            // Capitalize the first letter for the title
            categoryDisplayName = topicSlug.substring(0, 1).toUpperCase() + topicSlug.substring(1);
        } else {
            topicSlug = "general"; // Fallback topic
            categoryDisplayName = "General";
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoryNewsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar(categoryDisplayName);
        setupRecyclerView();
        loadNewsData(topicSlug);
    }

    private void setupToolbar(String title) {
        binding.toolbar.setTitle(title);
        binding.toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
    }

    private void setupRecyclerView() {
        adapter = new ArticleListAdapter(articleList);
        binding.newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.newsRecyclerView.setAdapter(adapter);
    }

    private void loadNewsData(String slug) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.newsRecyclerView.setVisibility(View.GONE);

        // Get the correct API key from the mapping
        String apiTopicKey = TopicHelper.getApiTopicKey(slug);

        // Use the new, more efficient API endpoint
        ApiClient.getApiService(requireContext()).getArticlesByTopic(apiTopicKey).enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(@NonNull Call<List<Article>> call, @NonNull Response<List<Article>> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.newsRecyclerView.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Article> articlesFromServer = response.body();
                    articleList.clear();

                    if (articlesFromServer.isEmpty()) {
                        Toast.makeText(getContext(), "Không có tin tức nào cho chuyên mục này", Toast.LENGTH_SHORT).show();
                    } else {
                        articleList.addAll(articlesFromServer);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Không thể tải tin tức. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Article>> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Log.e("CategoryNewsFragment", "API Call Failed for topic " + slug + ": " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi mạng. Vui lòng kiểm tra kết nối.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}