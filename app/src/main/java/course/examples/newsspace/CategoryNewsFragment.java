package course.examples.newsspace; // Thay bằng package của bạn

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import course.examples.newsspace.databinding.FragmentCategoryNewsBinding; // Thay bằng package của bạn

public class CategoryNewsFragment extends Fragment {

    private FragmentCategoryNewsBinding binding;
    private ArticleListAdapter adapter; // Sẽ tạo Adapter này ở bước sau
    private List<Article> articleList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoryNewsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Lấy tên chuyên mục được truyền từ HomeFragment (sẽ làm ở bước sau)
        // String categoryName = CategoryNewsFragmentArgs.fromBundle(getArguments()).getCategoryName();
        // binding.toolbar.setTitle(categoryName);
        binding.toolbar.setTitle("Mới nhất"); // Tạm thời gán cứng

        // Xử lý sự kiện click nút Back trên Toolbar
        binding.toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());

        setupRecyclerView();
        loadNewsData();
    }

    private void setupRecyclerView() {
        adapter = new ArticleListAdapter(articleList);
        binding.newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.newsRecyclerView.setAdapter(adapter);
    }

    private void loadNewsData() {
        // *** GIẢ LẬP TẢI DỮ LIỆU TỪ API ***
        articleList.clear();

        // Thêm tin nổi bật đầu tiên (dùng layout to)
        articleList.add(new Article("Đại hồng thủy...", "Nước sông Thu Bồn...", "url_to_image_1", true));
        articleList.add(new Article("Hơn 6.300 cán bộ...", "Từ đầu năm 2025...", "url_to_image_2", true));

        // Thêm các tin thường (dùng layout nhỏ)
        for (int i = 0; i < 10; i++) {
            articleList.add(new Article("Bé trai trôi giữa dòng nước...", "4/10/2025", "url_to_image_" + i, false));
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}