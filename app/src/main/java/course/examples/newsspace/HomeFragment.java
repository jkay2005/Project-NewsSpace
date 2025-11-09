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
import course.examples.newsspace.model.HeaderData;
import course.examples.newsspace.model.RssItem;
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

        // ----- LẶP LẠI CHO CÁC CHUYÊN MỤC KHÁC TƯƠNG TỰ -----
        // Ví dụ: Chính trị, Thế giới, Đời sống...

        // 4. (Tùy chọn) Thêm Footer ở cuối danh sách
        // homeItems.add(new FooterData());

        // 5. Thông báo cho adapter rằng toàn bộ dữ liệu đã thay đổi và cần vẽ lại
        homeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}