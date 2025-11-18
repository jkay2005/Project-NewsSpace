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
import java.util.List;

// Import các lớp cần thiết cho GNews
import course.examples.newsspace.api.GNewsApiClient;
import course.examples.newsspace.model.gnews.GNewsArticle;
import course.examples.newsspace.model.gnews.GNewsResponse;

// Import các lớp Model gốc của bạn
import course.examples.newsspace.databinding.FragmentHomeBinding;
import course.examples.newsspace.model.Article;
import course.examples.newsspace.model.FooterData;
import course.examples.newsspace.model.HeaderData;
import course.examples.newsspace.model.SectionHeader;
import course.examples.newsspace.model.TabData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeAdapter homeAdapter;
    private final List<Object> homeItems = new ArrayList<>();
    private static final String TAG = "HomeFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        loadHomePageData();
    }

    private void setupRecyclerView() {
        // 1. TẠO LISTENER
        HomeAdapter.OnArticleClickListener clickListener = article -> {
            if (article.getUrl() != null && !article.getUrl().isEmpty()) {
                // Tạo action điều hướng bằng Safe Args
                HomeFragmentDirections.ActionHomeFragmentToArticleDetailFragment action =
                        HomeFragmentDirections.actionHomeFragmentToArticleDetailFragment(article.getUrl());

                // Thực hiện điều hướng
                NavHostFragment.findNavController(HomeFragment.this).navigate(action);
            } else {
                Toast.makeText(getContext(), "Bài viết này không có đường dẫn.", Toast.LENGTH_SHORT).show();
            }
        };

        // 2. KHỞI TẠO ADAPTER VÀ TRUYỀN LISTENER
        homeAdapter = new HomeAdapter(homeItems, clickListener);
        binding.homeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.homeRecyclerView.setAdapter(homeAdapter);
    }

    private void loadHomePageData() {
        // TODO: Hiển thị trạng thái loading (ví dụ: Shimmer effect hoặc ProgressBar)
        String apiKey = "ec4a35d60e28736506770fac7add6e82";
        Log.d(TAG, "Đang gọi GNews API với key: " + apiKey);
        GNewsApiClient.getApiService().getTopHeadlines(apiKey, "vi", "vn").enqueue(new Callback<GNewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<GNewsResponse> call, @NonNull Response<GNewsResponse> response) {
                // TODO: Ẩn loading
                if (response.isSuccessful() && response.body() != null) {
                    List<GNewsArticle> gnewsArticles = response.body().getArticles();
                    Log.d(TAG, "API call thành công, nhận được " + gnewsArticles.size() + " bài báo.");


                    // Chuyển đổi từ GNewsArticle sang Article của bạn
                    List<Article> displayArticles = new ArrayList<>();
                    for (GNewsArticle gnewsArticle : gnewsArticles) {
                        displayArticles.add(
                                Article.createStandardArticle(
                                        gnewsArticle.getTitle(),
                                        gnewsArticle.getPublishedAt(),
                                        gnewsArticle.getImage(), // Dùng getImage()
                                        gnewsArticle.getUrl())
                        );
                    }

                            // Gọi buildDisplayList với danh sách Article đã được chuyển đổi
                            buildDisplayList(gnewsArticles);

                } else {
                    Log.e(TAG, "API call thất bại. Mã lỗi: " + response.code() + ", Thông báo: " + response.message());
                    Toast.makeText(getContext(), "Không thể tải dữ liệu trang chủ", Toast.LENGTH_SHORT).show();
                }
            }
                    @Override
                    public void onFailure(@NonNull Call<GNewsResponse> call, @NonNull Throwable t) {
                        // TODO: Ẩn trạng thái loading
                        // binding.progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "API Call Failed on network: " + t.getMessage(), t);
                        Toast.makeText(getContext(), "Lỗi mạng, không thể tải dữ liệu", Toast.LENGTH_SHORT).show();
                    }
        });

    }


    /**
     * THAY ĐỔI: Phương thức này giờ nhận `List<Article>` và xây dựng giao diện.
     * Logic lọc theo tag đã được loại bỏ vì GNews không cung cấp thông tin đó.
     * @param @gnewsArticles Danh sách tin tức đã được chuyển đổi từ GNews.
     */
    private void buildDisplayList(List<GNewsArticle> gnewsArticles) {
        homeItems.clear(); // Luôn xóa dữ liệu cũ trước khi thêm mới

        // 1. Thêm các item tĩnh không thay đổi
        homeItems.add(new HeaderData());
        homeItems.add(new TabData());

        if (gnewsArticles == null || gnewsArticles.isEmpty()) {
            // Xử lý trường hợp không có bài báo nào
            homeAdapter.notifyDataSetChanged();
            return;
        }

        // 2. Thêm tiêu đề cho mục "Tin nổi bật"
        homeItems.add(new SectionHeader("Tin nổi bật"));


        // Lấy 3 tin đầu tiên làm tin nổi bật (featured)
        int featuredCount = Math.min(gnewsArticles.size(), 3);
        for (int i = 0; i < featuredCount; i++) {
            GNewsArticle item = gnewsArticles.get(i);
            // Dùng lại model Article của bạn. Giả sử bạn có thể tạo featured article từ standard article.
            // Nếu không, bạn có thể cần thêm thông tin description.
            homeItems.add(Article.createFeaturedArticle(
                    item.getTitle(),
                    item.getDescription(),
                    item.getImage(),
                    item.getUrl()
            ));
        }

        // 3. Thêm các mục tin tức theo từng chuyên mục

        // ----- MỤC MỚI NHẤT-----
        homeItems.add(new SectionHeader("Mới Nhất"));
        int remainingCount = Math.min(gnewsArticles.size(), 5); // Lấy tối đa 10 tin mới
        if (gnewsArticles.size() > 3) {
            for (int i = 3; i < remainingCount; i++) {
                homeItems.add(gnewsArticles.get(i)); // Thêm trực tiếp đối tượng Article
            }
        }

        // ----- MỤC THỜI SỰ -----
        homeItems.add(new SectionHeader("Thời sự"));
        remainingCount = Math.min(gnewsArticles.size(), 5); // Lấy tối đa 10 tin mới
        if (gnewsArticles.size() > 3) {
            for (int i = 3; i < remainingCount; i++) {
                homeItems.add(gnewsArticles.get(i)); // Thêm trực tiếp đối tượng Article
            }
        }

        // ----- MỤC KINH TẾ -----
        homeItems.add(new SectionHeader("Kinh tế"));
        remainingCount = Math.min(gnewsArticles.size(), 5); // Lấy tối đa 10 tin mới
        if (gnewsArticles.size() > 3) {
            for (int i = 3; i < remainingCount; i++) {
                homeItems.add(gnewsArticles.get(i)); // Thêm trực tiếp đối tượng Article
            }
        }


        // ----- MỤC chính trị -----
        homeItems.add(new SectionHeader("Chính trị"));
        remainingCount = Math.min(gnewsArticles.size(), 5); // Lấy tối đa 10 tin mới
        if (gnewsArticles.size() > 3) {
            for (int i = 3; i < remainingCount; i++) {
                homeItems.add(gnewsArticles.get(i)); // Thêm trực tiếp đối tượng Article
            }
        }

        // ----- MỤC thế giới-----
        homeItems.add(new SectionHeader("Thế giới"));
        remainingCount = Math.min(gnewsArticles.size(), 5); // Lấy tối đa 10 tin mới
        if (gnewsArticles.size() > 3) {
            for (int i = 3; i < remainingCount; i++) {
                homeItems.add(gnewsArticles.get(i)); // Thêm trực tiếp đối tượng Article
            }
        }

        // ----- MỤC Đời sống -----
        homeItems.add(new SectionHeader("Đời sống"));
        remainingCount = Math.min(gnewsArticles.size(), 5); // Lấy tối đa 10 tin mới
        if (gnewsArticles.size() > 3) {
            for (int i = 3; i < remainingCount; i++) {
                homeItems.add(gnewsArticles.get(i)); // Thêm trực tiếp đối tượng Article
            }
        }

        // ----- MỤC Du lịch -----
        homeItems.add(new SectionHeader("Du lịch"));
        remainingCount = Math.min(gnewsArticles.size(), 5); // Lấy tối đa 10 tin mới
        if (gnewsArticles.size() > 3) {
            for (int i = 3; i < remainingCount; i++) {
                homeItems.add(gnewsArticles.get(i)); // Thêm trực tiếp đối tượng Article
            }
        }

        // ----- MỤC văn hóa -----
        homeItems.add(new SectionHeader("Văn hóa"));
        remainingCount = Math.min(gnewsArticles.size(), 5); // Lấy tối đa 10 tin mới
        if (gnewsArticles.size() > 3) {
            for (int i = 3; i < remainingCount; i++) {
                homeItems.add(gnewsArticles.get(i)); // Thêm trực tiếp đối tượng Article
            }
        }

        // ----- MỤC giải trí -----
        homeItems.add(new SectionHeader("Giải trí"));
        remainingCount = Math.min(gnewsArticles.size(), 5); // Lấy tối đa 10 tin mới
        if (gnewsArticles.size() > 3) {
            for (int i = 3; i < remainingCount; i++) {
                homeItems.add(gnewsArticles.get(i)); // Thêm trực tiếp đối tượng Article
            }
        }

        // ----- MỤC giới trẻ -----
        homeItems.add(new SectionHeader("Giới trẻ"));
        remainingCount = Math.min(gnewsArticles.size(), 5); // Lấy tối đa 10 tin mới
        if (gnewsArticles.size() > 3) {
            for (int i = 3; i < remainingCount; i++) {
                homeItems.add(gnewsArticles.get(i)); // Thêm trực tiếp đối tượng Article
            }
        }

        // ----- MỤC giáo dục -----
        homeItems.add(new SectionHeader("Giáo dục"));
        remainingCount = Math.min(gnewsArticles.size(), 5); // Lấy tối đa 10 tin mới
        if (gnewsArticles.size() > 3) {
            for (int i = 3; i < remainingCount; i++) {
                homeItems.add(gnewsArticles.get(i)); // Thêm trực tiếp đối tượng Article
            }
        }

        // ----- MỤC thể thao -----
        homeItems.add(new SectionHeader("Thể thao"));
        remainingCount = Math.min(gnewsArticles.size(), 5); // Lấy tối đa 10 tin mới
        if (gnewsArticles.size() > 3) {
            for (int i = 3; i < remainingCount; i++) {
                homeItems.add(gnewsArticles.get(i)); // Thêm trực tiếp đối tượng Article
            }
        }

        // ----- MỤC sức khỏe-----
        homeItems.add(new SectionHeader("Sức khỏe"));
        remainingCount = Math.min(gnewsArticles.size(), 5); // Lấy tối đa 10 tin mới
        if (gnewsArticles.size() > 3) {
            for (int i = 3; i < remainingCount; i++) {
                homeItems.add(gnewsArticles.get(i)); // Thêm trực tiếp đối tượng Article
            }
        }

        // ----- MỤC công nghệ -----
        homeItems.add(new SectionHeader("Công nghệ"));
        remainingCount = Math.min(gnewsArticles.size(), 5); // Lấy tối đa 10 tin mới
        if (gnewsArticles.size() > 3) {
            for (int i = 3; i < remainingCount; i++) {
                homeItems.add(gnewsArticles.get(i)); // Thêm trực tiếp đối tượng Article
            }
        }

        // ----- MỤC thời trang -----
        homeItems.add(new SectionHeader("Thời trang"));
        remainingCount = Math.min(gnewsArticles.size(), 5); // Lấy tối đa 10 tin mới
        if (gnewsArticles.size() > 3) {
            for (int i = 3; i < remainingCount; i++) {
                homeItems.add(gnewsArticles.get(i)); // Thêm trực tiếp đối tượng Article
            }
        }

        // ----- MỤC Xe -----
        homeItems.add(new SectionHeader("Xe"));
        remainingCount = Math.min(gnewsArticles.size(), 5); // Lấy tối đa 10 tin mới
        if (gnewsArticles.size() > 3) {
            for (int i = 3; i < remainingCount; i++) {
                homeItems.add(gnewsArticles.get(i)); // Thêm trực tiếp đối tượng Article
            }
        }

        // ----- MỤC Tiêu dùng -----
        homeItems.add(new SectionHeader("Tiêu dùng"));
        remainingCount = Math.min(gnewsArticles.size(), 5); // Lấy tối đa 10 tin mới
        if (gnewsArticles.size() > 3) {
            for (int i = 3; i < remainingCount; i++) {
                homeItems.add(gnewsArticles.get(i)); // Thêm trực tiếp đối tượng Article
            }
        }


        // 4. (Tùy chọn) Thêm Footer ở cuối danh sách
        homeItems.add(new FooterData());

        // 5. Thông báo cho adapter rằng toàn bộ dữ liệu đã thay đổi và cần vẽ lại
        homeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}