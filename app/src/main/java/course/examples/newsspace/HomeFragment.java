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
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import course.examples.newsspace.databinding.FragmentHomeBinding;
import course.examples.newsspace.model.Article; // Giả định RssItem có thể được biểu diễn bằng Article
import course.examples.newsspace.model.FooterData;
import course.examples.newsspace.model.HeaderData;
import course.examples.newsspace.model.RssItem;
import course.examples.newsspace.model.RssSource;
import course.examples.newsspace.model.SectionHeader;
import course.examples.newsspace.model.TabData;
import course.examples.newsspace.api.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeAdapter homeAdapter;
    private final List<Object> homeItems = new ArrayList<>();

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
        // Khởi tạo adapter với danh sách rỗng ban đầu
        homeAdapter = new HomeAdapter(homeItems);
        binding.homeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.homeRecyclerView.setAdapter(homeAdapter);
    }

    private void loadHomePageData() {
        // TODO: Hiển thị trạng thái loading (ví dụ: Shimmer effect hoặc ProgressBar)

        // Gọi API để lấy danh sách các tin tức từ RSS
        ApiClient.getApiService(requireContext()).getRssItems().enqueue(new Callback<List<RssItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<RssItem>> call, @NonNull Response<List<RssItem>> response) {
                // TODO: Ẩn trạng thái loading

                if (response.isSuccessful() && response.body() != null) {
                    // LẤY DỮ LIỆU THÀNH CÔNG
                    List<RssItem> rssItems = response.body();

                    // Xây dựng lại danh sách hiển thị
                    buildDisplayList(rssItems);

                } else {
                    Toast.makeText(getContext(), "Không thể tải dữ liệu trang chủ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<RssItem>> call, @NonNull Throwable t) {
                // TODO: Ẩn trạng thái loading
                Log.e("HomeFragment", "API Call Failed: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi mạng, không thể tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });

//        // 1. Tạo một danh sách RssItem giả
//        List<RssItem> fakeRssItems = new ArrayList<>();
//
//        // Tạo 3 tin nổi bật đầu tiên
//        for (int i = 1; i <= 3; i++) {
//            RssItem item = new RssItem();
//            item.setId(i);
//            item.setTitle("Tin nổi bật số " + i + ": Thế giới đối mặt với thách thức mới");
//            item.setContent("Đây là một đoạn mô tả ngắn gọn nhưng đầy đủ thông tin về nội dung của bài báo nổi bật, thu hút sự chú ý của người đọc ngay từ cái nhìn đầu tiên.");
//            item.setPublishedAt("11/12/2025");
//            item.setAuthor("BTV Thời sự");
//            item.setSource(new RssSource("VTV News", "thoisu")); // Gán chuyên mục "thoisu"
//            fakeRssItems.add(item);
//        }
//
//        // Tạo 5 tin cho chuyên mục "Thời sự"
//        for (int i = 4; i <= 8; i++) {
//            RssItem item = new RssItem();
//            item.setId(i);
//            item.setTitle("Tin thời sự " + (i - 3) + ": Cập nhật tình hình trong nước");
//            item.setPublishedAt("11/12/2025");
//            item.setSource(new RssSource("Báo Mới", "thoisu")); // Gán tag "thoisu"
//            fakeRssItems.add(item);
//        }
//
//        // Tạo 5 tin cho chuyên mục "Kinh tế"
//        for (int i = 9; i <= 13; i++) {
//            RssItem item = new RssItem();
//            item.setId(i);
//            item.setTitle("Bản tin kinh tế " + (i - 8) + ": Thị trường chứng khoán biến động");
//            item.setPublishedAt("11/12/2025");
//            item.setSource(new RssSource("CafeF", "kinhte")); // Gán tag "kinhte"
//            fakeRssItems.add(item);
//        }
//
//        // Tạo 5 tin cho chuyên mục "Công nghệ"
//        for (int i = 14; i <= 18; i++) {
//            RssItem item = new RssItem();
//            item.setId(i);
//            item.setTitle("Đánh giá sản phẩm công nghệ mới " + (i - 13));
//            item.setPublishedAt("11/12/2025");
//            item.setSource(new RssSource("Tinh Tế", "congnghe")); // Gán tag "congnghe"
//            fakeRssItems.add(item);
//        }
//
//        // 2. Gọi hàm buildDisplayList để xử lý và hiển thị dữ liệu giả này
//        buildDisplayList(fakeRssItems);
    }

    /**
     * Sắp xếp dữ liệu nhận từ API thành một danh sách duy nhất để Adapter hiển thị.
     * @param rssItems Danh sách tin tức gốc từ API.
     */
    private void buildDisplayList(List<RssItem> rssItems) {
        homeItems.clear(); // Luôn xóa dữ liệu cũ trước khi thêm mới

        // 1. Thêm các item tĩnh không thay đổi
        homeItems.add(new HeaderData());
        homeItems.add(new TabData());

        // 2. Thêm tiêu đề cho mục "Tin nổi bật"
        homeItems.add(new SectionHeader("Tin nổi bật"));

        // Lấy 3 tin đầu tiên làm tin nổi bật (featured)
        int featuredCount = 0;
        for (RssItem item : rssItems) {
            if (featuredCount < 3) {
                // Sử dụng phương thức tĩnh để tạo một Article nổi bật
                homeItems.add(Article.createFeaturedArticle(
                        item.getTitle(),
                        item.getContent(), // Giả định content là description ngắn
                        item.getImageUrl()
                ));
                featuredCount++;
            } else {
                break; // Dừng lại khi đã đủ 3 tin
            }
        }

        // 3. Thêm các mục tin tức theo từng chuyên mục

        // ----- MỤC MỚI NHẤT-----
        homeItems.add(new SectionHeader("Mới Nhất"));
        int moiNhatCount = 0;
        for (RssItem item : rssItems) {
            if (moiNhatCount < 5 && item.getSource() != null && "moinhat".equalsIgnoreCase(item.getSource().getTag())) {
                homeItems.add(Article.createStandardArticle(
                        item.getTitle(),
                        item.getPublishedAt(),
                        item.getImageUrl()
                ));
                moiNhatCount++;
            }
        }

        // ----- MỤC THỜI SỰ -----
        homeItems.add(new SectionHeader("Thời sự"));
        int thoiSuCount = 0;
        for (RssItem item : rssItems) {
            // Lọc các tin thuộc "Thời sự" và chỉ lấy tối đa 5 tin
            if (thoiSuCount < 5 && item.getSource() != null && "thoisu".equalsIgnoreCase(item.getSource().getTag())) {
                // Sử dụng phương thức tĩnh để tạo một Article tiêu chuẩn
                homeItems.add(Article.createStandardArticle(
                        item.getTitle(),
                        item.getPublishedAt(),
                        item.getImageUrl()
                ));
                thoiSuCount++;
            }
        }

        // ----- MỤC KINH TẾ -----
        homeItems.add(new SectionHeader("Kinh tế"));
        int kinhTeCount = 0;
        for (RssItem item : rssItems) {
            if (kinhTeCount < 5 && item.getSource() != null && "kinhte".equalsIgnoreCase(item.getSource().getTag())) {
                homeItems.add(Article.createStandardArticle(
                        item.getTitle(),
                        item.getPublishedAt(),
                        item.getImageUrl()
                ));
                kinhTeCount++;
            }
        }


        // ----- MỤC chính trị -----
        homeItems.add(new SectionHeader("Chính trị"));
        int chinhTriCount = 0;
        for (RssItem item : rssItems) {
            if (chinhTriCount < 5 && item.getSource() != null && "chinhtri".equalsIgnoreCase(item.getSource().getTag())) {
                homeItems.add(Article.createStandardArticle(
                        item.getTitle(),
                        item.getPublishedAt(),
                        item.getImageUrl()
                ));
                chinhTriCount++;
            }
        }

        // ----- MỤC thế giới-----
        homeItems.add(new SectionHeader("Thế giới"));
        int theGioiCount = 0;
        for (RssItem item : rssItems) {
            if (theGioiCount < 5 && item.getSource() != null && "thegioi".equalsIgnoreCase(item.getSource().getTag())) {
                homeItems.add(Article.createStandardArticle(
                        item.getTitle(),
                        item.getPublishedAt(),
                        item.getImageUrl()
                ));
                theGioiCount++;
            }
        }

        // ----- MỤC Đời sống -----
        homeItems.add(new SectionHeader("Đời sống"));
        int doiSongCount = 0;
        for (RssItem item : rssItems) {
            if (doiSongCount < 5 && item.getSource() != null && "doisong".equalsIgnoreCase(item.getSource().getTag())) {
                homeItems.add(Article.createStandardArticle(
                        item.getTitle(),
                        item.getPublishedAt(),
                        item.getImageUrl()
                ));
                doiSongCount++;
            }
        }

        // ----- MỤC Du lịch -----
        homeItems.add(new SectionHeader("Du lịch"));
        int duLichCount = 0;
        for (RssItem item : rssItems) {
            if (duLichCount < 5 && item.getSource() != null && "dulich".equalsIgnoreCase(item.getSource().getTag())) {
                homeItems.add(Article.createStandardArticle(
                        item.getTitle(),
                        item.getPublishedAt(),
                        item.getImageUrl()
                ));
                duLichCount++;
            }
        }

        // ----- MỤC văn hóa -----
        homeItems.add(new SectionHeader("Văn hóa"));
        int vanHoaCount = 0;
        for (RssItem item : rssItems) {
            if (vanHoaCount < 5 && item.getSource() != null && "vanhoa".equalsIgnoreCase(item.getSource().getTag())) {
                homeItems.add(Article.createStandardArticle(
                        item.getTitle(),
                        item.getPublishedAt(),
                        item.getImageUrl()
                ));
                vanHoaCount++;
            }
        }

        // ----- MỤC giải trí -----
        homeItems.add(new SectionHeader("Giải trí"));
        int giaiTriCount = 0;
        for (RssItem item : rssItems) {
            if (giaiTriCount < 5 && item.getSource() != null && "giaitri".equalsIgnoreCase(item.getSource().getTag())) {
                homeItems.add(Article.createStandardArticle(
                        item.getTitle(),
                        item.getPublishedAt(),
                        item.getImageUrl()
                ));
                giaiTriCount++;
            }
        }

        // ----- MỤC giới trẻ -----
        homeItems.add(new SectionHeader("Giới trẻ"));
        int gioiTreCount = 0;
        for (RssItem item : rssItems) {
            if (gioiTreCount < 5 && item.getSource() != null && "gioitre".equalsIgnoreCase(item.getSource().getTag())) {
                homeItems.add(Article.createStandardArticle(
                        item.getTitle(),
                        item.getPublishedAt(),
                        item.getImageUrl()
                ));
                gioiTreCount++;
            }
        }

        // ----- MỤC giáo dục -----
        homeItems.add(new SectionHeader("Giáo dục"));
        int giaoDucCount = 0;
        for (RssItem item : rssItems) {
            if (giaoDucCount < 5 && item.getSource() != null && "giaoduc".equalsIgnoreCase(item.getSource().getTag())) {
                homeItems.add(Article.createStandardArticle(
                        item.getTitle(),
                        item.getPublishedAt(),
                        item.getImageUrl()
                ));
                giaoDucCount++;
            }
        }

        // ----- MỤC thể thao -----
        homeItems.add(new SectionHeader("Thể thao"));
        int theThaoCount = 0;
        for (RssItem item : rssItems) {
            if (theThaoCount < 5 && item.getSource() != null && "thethao".equalsIgnoreCase(item.getSource().getTag())) {
                homeItems.add(Article.createStandardArticle(
                        item.getTitle(),
                        item.getPublishedAt(),
                        item.getImageUrl()
                ));
                theThaoCount++;
            }
        }

        // ----- MỤC sức khỏe-----
        homeItems.add(new SectionHeader("Sức khỏe"));
        int sucKhoeCount = 0;
        for (RssItem item : rssItems) {
            if (sucKhoeCount < 5 && item.getSource() != null && "suckhoe".equalsIgnoreCase(item.getSource().getTag())) {
                homeItems.add(Article.createStandardArticle(
                        item.getTitle(),
                        item.getPublishedAt(),
                        item.getImageUrl()
                ));
                sucKhoeCount++;
            }
        }

        // ----- MỤC công nghệ -----
        homeItems.add(new SectionHeader("Công nghệ"));
        int congNgheCount = 0;
        for (RssItem item : rssItems) {
            if (congNgheCount < 5 && item.getSource() != null && "congnghe".equalsIgnoreCase(item.getSource().getTag())) {
                homeItems.add(Article.createStandardArticle(
                        item.getTitle(),
                        item.getPublishedAt(),
                        item.getImageUrl()
                ));
                congNgheCount++;
            }
        }

        // ----- MỤC thời trang -----
        homeItems.add(new SectionHeader("Thời trang"));
        int thoiTrangCount = 0;
        for (RssItem item : rssItems) {
            if (thoiTrangCount < 5 && item.getSource() != null && "troitrang".equalsIgnoreCase(item.getSource().getTag())) {
                homeItems.add(Article.createStandardArticle(
                        item.getTitle(),
                        item.getPublishedAt(),
                        item.getImageUrl()
                ));
                thoiTrangCount++;
            }
        }

        // ----- MỤC Xe -----
        homeItems.add(new SectionHeader("Xe"));
        int xeCount = 0;
        for (RssItem item : rssItems) {
            if (xeCount < 5 && item.getSource() != null && "xe".equalsIgnoreCase(item.getSource().getTag())) {
                homeItems.add(Article.createStandardArticle(
                        item.getTitle(),
                        item.getPublishedAt(),
                        item.getImageUrl()
                ));
                xeCount++;
            }
        }

        // ----- MỤC Tiêu dùng -----
        homeItems.add(new SectionHeader("Tiêu dùng"));
        int tieuDungCount = 0;
        for (RssItem item : rssItems) {
            if (tieuDungCount < 5 && item.getSource() != null && "tieudung".equalsIgnoreCase(item.getSource().getTag())) {
                homeItems.add(Article.createStandardArticle(
                        item.getTitle(),
                        item.getPublishedAt(),
                        item.getImageUrl()
                ));
                tieuDungCount++;
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