package course.examples.newsspace; // Thay bằng package của bạn

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import java.util.stream.Collectors;

import course.examples.newsspace.api.ApiClient;
import course.examples.newsspace.databinding.FragmentCategoryNewsBinding;
import course.examples.newsspace.model.Article;
import course.examples.newsspace.model.RssItem;
import course.examples.newsspace.model.RssSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryNewsFragment extends Fragment {

    private FragmentCategoryNewsBinding binding;
    private ArticleListAdapter adapter;
    private final List<Article> articleList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoryNewsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // == "MỞ QUÀ": LẤY DỮ LIỆU TỪ ARGUMENTS BẰNG SAFE ARGS   ==
        // ==========================================================
        String categoryName = "Mới nhất"; // Giá trị mặc định
        if (getArguments() != null) {
            // Lấy tên chuyên mục đã được HomeAdapter gửi qua
            categoryName = CategoryNewsFragmentArgs.fromBundle(getArguments()).getCategoryName();
        }

        // ==========================================================
        // == SỬ DỤNG "MÓn QUÀ"                                  ==
        // ==========================================================
        // 1. Đặt tên chuyên mục làm tiêu đề cho Toolbar
        setupToolbar(categoryName);

        setupRecyclerView();

        // 2. Dùng tên chuyên mục để lọc dữ liệu từ API
        loadNewsData(categoryName);
    }

    private void setupToolbar(String title) {
        // Hàm này bây giờ sẽ nhận tiêu đề một cách linh hoạt
        binding.toolbar.setTitle(title);
        binding.toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
    }


    private void setupRecyclerView() {
        adapter = new ArticleListAdapter(articleList);
        binding.newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.newsRecyclerView.setAdapter(adapter);
    }

    // Trong file CategoryNewsFragment.java

    private void loadNewsData(String categoryNameToFilter) {
        // 1. Hiển thị trạng thái loading để người dùng biết ứng dụng đang làm việc
        // TODO: Thay thế ProgressBar bằng một hiệu ứng Shimmer đẹp hơn nếu có thời gian
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.newsRecyclerView.setVisibility(View.GONE);

        ApiClient.getApiService(requireContext()).getRssItems().enqueue(new Callback<List<RssItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<RssItem>> call, @NonNull Response<List<RssItem>> response) {
                // 2. Ẩn trạng thái loading sau khi có phản hồi
                binding.progressBar.setVisibility(View.GONE);
                binding.newsRecyclerView.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    List<RssItem> allItems = response.body();

                    // 3. Lọc danh sách theo chuyên mục đã nhận được
                    List<RssItem> filteredItems;

                    // Trường hợp đặc biệt: Nếu là "Mới nhất", hiển thị tất cả
                    if ("Mới nhất".equalsIgnoreCase(categoryNameToFilter)) {
                        filteredItems = allItems;
                    } else {
                        // Lọc theo tag của chuyên mục
                        filteredItems = allItems.stream()
                                .filter(item -> item.getSource() != null && categoryNameToFilter.equalsIgnoreCase(item.getSource().getTag()))
                                .collect(Collectors.toList());
                    }

                    // 4. Xóa dữ liệu cũ và chuyển đổi dữ liệu mới để Adapter hiển thị
                    articleList.clear();

                    if (filteredItems.isEmpty()) {
                        // TODO: Hiển thị một giao diện "Không có tin tức" cho người dùng
                        Toast.makeText(getContext(), "Không có tin tức nào cho chuyên mục này", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i = 0; i < filteredItems.size(); i++) {
                            RssItem item = filteredItems.get(i);
                            // Đánh dấu 2 tin đầu tiên là tin nổi bật (featured) để dùng layout lớn
                            boolean isFeatured = i < 2;

                            if (isFeatured) {
                                // Dùng phương thức tĩnh của Article để tạo
                                articleList.add(Article.createFeaturedArticle(
                                        item.getTitle(),
                                        item.getContent(), // Giả định content là description
                                        item.getImageUrl()
                                ));
                            } else {
                                articleList.add(Article.createStandardArticle(
                                        item.getTitle(),
                                        item.getPublishedAt(),
                                        item.getImageUrl()
                                ));
                            }
                        }
                    }

                    // 5. Thông báo cho Adapter rằng dữ liệu đã thay đổi và cần vẽ lại
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(getContext(), "Không thể tải tin tức. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<RssItem>> call, @NonNull Throwable t) {
                // 6. Ẩn trạng thái loading và xử lý lỗi mạng
                binding.progressBar.setVisibility(View.GONE);
                Log.e("CategoryNewsFragment", "API Call Failed: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi mạng. Vui lòng kiểm tra kết nối và thử lại.", Toast.LENGTH_SHORT).show();
            }
        });

        // == THÊM PHẦN TẠO DỮ LIỆU GIẢ (DUMMY DATA) ĐỂ TEST     ==
        // ==========================================================
        // Dùng Handler để giả lập độ trễ mạng, tạo cảm giác thật hơn
//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//            // 2. Ẩn trạng thái loading sau khi "tải" xong dữ liệu giả
//            binding.progressBar.setVisibility(View.GONE);
//            binding.newsRecyclerView.setVisibility(View.VISIBLE);
//
//            // 3. Tạo một danh sách RssItem giả đầy đủ
//            List<RssItem> allFakeItems = createAllFakeRssItems();
//
//            // 4. Lọc danh sách giả theo chuyên mục đã nhận được
//            List<RssItem> filteredFakeItems;
//            if ("Mới nhất".equalsIgnoreCase(categoryNameToFilter)) {
//                filteredFakeItems = allFakeItems;
//            } else {
//                filteredFakeItems = allFakeItems.stream()
//                        .filter(item -> item.getSource() != null && categoryNameToFilter.equalsIgnoreCase(item.getSource().getTag()))
//                        .collect(Collectors.toList());
//            }
//
//            // 5. Xóa dữ liệu cũ và chuyển đổi dữ liệu mới để Adapter hiển thị
//            articleList.clear();
//            if (filteredFakeItems.isEmpty()) {
//                Toast.makeText(getContext(), "Không có tin tức nào cho chuyên mục này (dữ liệu giả)", Toast.LENGTH_SHORT).show();
//            } else {
//                for (int i = 0; i < filteredFakeItems.size(); i++) {
//                    RssItem item = filteredFakeItems.get(i);
//                    boolean isFeatured = i < 2;
//                    if (isFeatured) {
//                        articleList.add(Article.createFeaturedArticle(item.getTitle(), item.getContent(), item.getImageUrl()));
//                    } else {
//                        articleList.add(Article.createStandardArticle(item.getTitle(), item.getPublishedAt(), item.getImageUrl()));
//                    }
//                }
//            }
//
//            // 6. Thông báo cho Adapter rằng dữ liệu đã thay đổi
//            adapter.notifyDataSetChanged();
//
//        }, 1000); // Giả lập chờ 1 giây
    }

    /**
     * Hàm helper để tạo ra một danh sách đầy đủ các tin tức giả cho tất cả các chuyên mục.
     * Tách ra hàm riêng để giữ cho loadNewsData() gọn gàng.
     * @return Danh sách các RssItem giả.
     */
//    private List<RssItem> createAllFakeRssItems() {
//        List<RssItem> fakeItems = new ArrayList<>();
//        String[] categories = {"Thời sự", "Kinh tế", "Công nghệ", "Thế giới"}; // Các chuyên mục có dữ liệu giả
//
//        int idCounter = 1;
//        for (String category : categories) {
//            // Tạo 7 tin cho mỗi chuyên mục
//            for (int i = 1; i <= 7; i++) {
//                RssItem item = new RssItem();
//                item.setId(idCounter++);
//                item.setTitle("Tin " + category + " số " + i + ": Tiêu đề bài viết mẫu");
//                item.setContent("Đây là nội dung mô tả ngắn cho bài báo thuộc chuyên mục " + category + ".");
//                item.setPublishedAt("11/12/2025");
//                item.setSource(new RssSource("Báo Giả Lập", category.toLowerCase())); // Gán tag là tên chuyên mục viết thường
//                fakeItems.add(item);
//            }
//        }
//        return fakeItems;
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
