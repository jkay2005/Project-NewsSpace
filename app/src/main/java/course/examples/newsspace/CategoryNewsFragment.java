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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import course.examples.newsspace.api.ApiClient;
import course.examples.newsspace.databinding.FragmentCategoryNewsBinding;
import course.examples.newsspace.model.Article;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryNewsFragment extends Fragment {

    private FragmentCategoryNewsBinding binding;
    private ArticleListAdapter adapter;
    private final List<Article> articleList = new ArrayList<>();
    private String categoryApiKey;
    private String categoryDisplayName;

    // Tạo lại bản đồ đối chiếu để tìm tên hiển thị từ khóa API
    private final Map<String, String> categoryMap = new LinkedHashMap<>();

    private void initializeCategoryMap() {
        categoryMap.put("Mới nhất", "breaking-news");
        categoryMap.put("Thời sự", "nation");
        categoryMap.put("Chính trị", "nation");
        categoryMap.put("Thế giới", "world");
        categoryMap.put("Kinh tế", "business");
        categoryMap.put("Giải trí", "entertainment");
        categoryMap.put("Thể thao", "sports");
        categoryMap.put("Sức khỏe", "health");
        categoryMap.put("Công nghệ", "technology");
        categoryMap.put("Khoa học", "science");
    }

    /**
     * Tìm tên hiển thị (FE) dựa trên khóa API (BE).
     * @param apiKey Khóa API, ví dụ: "nation".
     * @return Tên hiển thị, ví dụ: "Thời sự".
     */
    private String findDisplayName(String apiKey) {
        if (apiKey == null) return "Tin tức"; // Fallback
        for (Map.Entry<String, String> entry : categoryMap.entrySet()) {
            if (apiKey.equals(entry.getValue())) {
                return entry.getKey(); // Trả về displayName đầu tiên tìm thấy
            }
        }
        return "Tin tức"; // Fallback nếu không tìm thấy
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeCategoryMap();

        // **BƯỚC 1: THIẾT LẬP GIÁ TRỊ MẶC ĐỊNH**
        // Mặc định là tin nổi bật nếu không có tham số nào được truyền.
        categoryApiKey = "breaking-news";
        categoryDisplayName = "Tin nổi bật";

        // **BƯỚC 2: KIỂM TRA VÀ GHI ĐÈ BẰNG THAM SỐ (NẾU CÓ)**
        if (getArguments() != null) {
            String passedApiKey = CategoryNewsFragmentArgs.fromBundle(getArguments()).getCategoryName();
            if (passedApiKey != null && !passedApiKey.isEmpty()) {
                categoryApiKey = passedApiKey;
                // Dùng khóa API vừa nhận để tìm lại tên hiển thị cho tiêu đề
                categoryDisplayName = findDisplayName(categoryApiKey);
            }
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

        // Bây giờ các biến luôn có giá trị hợp lệ (hoặc mặc định hoặc từ tham số)
        setupToolbar(categoryDisplayName);
        setupRecyclerView();
        loadNewsData(categoryApiKey);
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

    private void loadNewsData(String apiKey) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.newsRecyclerView.setVisibility(View.GONE);

        // Sử dụng API mới để lấy bài báo theo chuyên mục
        ApiClient.getApiService(requireContext()).getArticlesByCategory(apiKey).enqueue(new Callback<List<Article>>() {
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
                Log.e("CategoryNewsFragment", "API Call Failed: " + t.getMessage());
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
