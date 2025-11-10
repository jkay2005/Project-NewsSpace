package course.examples.newsspace;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import course.examples.newsspace.model.Article;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import course.examples.newsspace.databinding.FragmentExploreBinding;


public class ExploreFragment extends Fragment {

    private FragmentExploreBinding binding;
    private ArticleListAdapter newsAdapter;
    private List<Article> articleList = new ArrayList<>();

    // ... onCreateView, onDestroyView ...

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupNewsRecyclerView();
        loadNewsData();
        setupTopicChips();

        // THAY ĐỔI 1: Gán sự kiện click cho nút "Tùy chỉnh" mới trong header
        // Truy cập thông qua binding của layout cha -> binding của layout được include -> id của nút
        binding.headerLayout.customizeButtonInHeader.setOnClickListener(v -> showCustomizeView());

        // Nút "Xác nhận" không thay đổi
        binding.confirmButton.setOnClickListener(v -> showNewsView());
    }

    private void setupNewsRecyclerView() {
        newsAdapter = new ArticleListAdapter(articleList);
        binding.newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.newsRecyclerView.setAdapter(newsAdapter);
    }

    private void setupTopicChips() {
        // Giả lập danh sách các chủ đề
        String[] topics = {"Thời sự", "Chính trị", "Thế giới", "Tiêu dùng", "Đời sống", "Du lịch",
                "Văn hóa", "Giải trí", "Giáo dục", "Thể thao", "Sức khỏe", "Công nghệ", "Thời trang", "Xe", "Kinh tế"};

        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (String topic : topics) {
            Chip chip = (Chip) inflater.inflate(R.layout.chip_choice, binding.topicChipGroup, false);
            chip.setText(topic);
            binding.topicChipGroup.addView(chip);
        }
    }

    // Hiển thị màn hình tùy chỉnh, ẩn màn hình tin tức
    private void showCustomizeView() {
        binding.newsRecyclerView.setVisibility(View.GONE);
        binding.customizeLayout.setVisibility(View.VISIBLE);
        // THAY ĐỔI 2: Xóa dòng code liên quan đến nút "Tùy chỉnh" cũ
        // binding.customizeButton.setVisibility(View.GONE);
    }

    // Hiển thị màn hình tin tức, ẩn màn hình tùy chỉnh
    // Hiển thị màn hình tin tức, ẩn màn hình tùy chỉnh
    private void showNewsView() {
        binding.customizeLayout.setVisibility(View.GONE);
        binding.newsRecyclerView.setVisibility(View.VISIBLE);
        // THAY ĐỔI 3: Xóa dòng code liên quan đến nút "Tùy chỉnh" cũ
        // binding.customizeButton.setVisibility(View.VISIBLE);

        // TODO: Dựa vào các chip đã chọn để tải lại danh sách tin tức
        // loadNewsData();
    }

    private void loadNewsData() {
        // Tái sử dụng logic tải dữ liệu tin tức đã có
        articleList.clear();
        for (int i = 0; i < 15; i++) {
            //articleList.add(new Article("Bé trai trôi giữa dòng nước...", "4/10/2025", "url", false));
        }
        newsAdapter.notifyDataSetChanged();
    }
}