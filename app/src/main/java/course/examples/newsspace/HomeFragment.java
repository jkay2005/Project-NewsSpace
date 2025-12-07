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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import course.examples.newsspace.api.ApiClient;
import course.examples.newsspace.databinding.FragmentHomeBinding;
import course.examples.newsspace.model.Article;
import course.examples.newsspace.model.FooterData;
import course.examples.newsspace.model.HeaderData;
import course.examples.newsspace.model.RssItem;
import course.examples.newsspace.model.SectionHeader;
import course.examples.newsspace.model.TabData;
import course.examples.newsspace.utils.TopicHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeAdapter homeAdapter;
    private final List<Object> homeItems = new ArrayList<>();
    // Sửa lỗi: Loại bỏ "Chính trị" vì có thể nó không có endpoint riêng
    private final List<String> categories = Arrays.asList(
            "Mới nhất", "Thời sự", "Thế giới", "Kinh tế", "Giải trí",
            "Thể thao", "Sức khỏe", "Công nghệ", "Khoa học"
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        loadInitialData();
    }

    private void setupRecyclerView() {
        homeAdapter = new HomeAdapter(homeItems);
        binding.homeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.homeRecyclerView.setAdapter(homeAdapter);
    }

    private void loadInitialData() {
        binding.progressBar.setVisibility(View.VISIBLE);
        // Step 1: Fetch featured articles first
        ApiClient.getApiService(requireContext()).getRssItems().enqueue(new Callback<List<RssItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<RssItem>> call, @NonNull Response<List<RssItem>> response) {
                if (binding == null) return; // Add null check here
                if (response.isSuccessful() && response.body() != null) {
                    List<RssItem> featuredItems = response.body();

                    // Clear old data and setup static items
                    homeItems.clear();
                    homeItems.add(new HeaderData());
                    homeItems.add(new TabData());

                    // Add featured section header and content
                    homeItems.add(new SectionHeader("Tin nổi bật"));
                    int count = 0;
                    for (RssItem item : featuredItems) {
                        if (count < 3) {
                            homeItems.add(Article.createFeaturedArticle(
                                    item.getId(),
                                    item.getTitle(),
                                    item.getContent(),
                                    item.getImageUrl()
                            ));
                            count++;
                        } else break;
                    }
                    // Notify adapter with initial data
                    homeAdapter.notifyDataSetChanged();

                    // Step 2: Now, load all other categories
                    loadAllCategorySections();

                } else {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Không thể tải tin nổi bật", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<RssItem>> call, @NonNull Throwable t) {
                if (binding == null) return; // Add null check here
                binding.progressBar.setVisibility(View.GONE);
                Log.e("HomeFragment", "API Call Failed for featured items: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi mạng, không thể tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAllCategorySections() {
        // Use AtomicInteger to track completion of all API calls
        AtomicInteger remainingCalls = new AtomicInteger(categories.size());

        for (String categoryName : categories) {
            // Use TopicHelper to get the correct API key from the mapping
            String apiTopicKey = TopicHelper.getApiTopicKey(categoryName);

            ApiClient.getApiService(requireContext()).getArticlesByTopic(apiTopicKey).enqueue(new Callback<List<Article>>() {
                @Override
                public void onResponse(@NonNull Call<List<Article>> call, @NonNull Response<List<Article>> response) {
                    if (binding == null) return; // Add null check here
                    if (response.isSuccessful() && response.body() != null) {
                        List<Article> articles = response.body();
                        if (!articles.isEmpty()) {
                            // Add section header with the original display name
                            homeItems.add(new SectionHeader(categoryName));
                            // Add articles to the list (limit to 5 for display)
                            int count = 0;
                            for(Article article : articles) {
                                // Important: convert Article from API to a standard Article for the adapter
                                if(count < 5) {
                                     homeItems.add(Article.createStandardArticle(
                                        article.getId(),
                                        article.getTitle(),
                                        article.getDate(), // Assuming API Article has a date
                                        article.getImageUrl()
                                    ));
                                    count++;
                                } else break;
                            }
                        }
                    }
                    // Check if this is the last call to finish
                    if (remainingCalls.decrementAndGet() == 0) {
                        onAllCategoriesLoaded();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Article>> call, @NonNull Throwable t) {
                    if (binding == null) return; // Add null check here
                    Log.e("HomeFragment", "API Call Failed for topic " + categoryName + ": " + t.getMessage());
                    // Check if this is the last call to finish
                    if (remainingCalls.decrementAndGet() == 0) {
                        onAllCategoriesLoaded();
                    }
                }
            });
        }
    }

    private void onAllCategoriesLoaded() {
        // This is called when the last category API call has finished
        if (binding == null) return; // Add null check here
        binding.progressBar.setVisibility(View.GONE);
        homeItems.add(new FooterData());
        homeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
