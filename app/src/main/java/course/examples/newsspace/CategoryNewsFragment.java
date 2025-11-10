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

import course.examples.newsspace.databinding.FragmentCategoryNewsBinding;
import course.examples.newsspace.model.Article;

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

        binding.toolbar.setTitle("Mới nhất"); // Tạm thời gán cứng

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
        articleList.clear();

        // Thêm tin nổi bật đầu tiên - sử dụng factory method
        articleList.add(Article.createFeaturedArticle("Đại hồng thủy...", "Nước sông Thu Bồn...", "url_to_image_1"));
        articleList.add(Article.createFeaturedArticle("Hơn 6.300 cán bộ...", "Từ đầu năm 2025...", "url_to_image_2"));

        // Thêm các tin thường - sử dụng factory method
        for (int i = 0; i < 10; i++) {
            articleList.add(Article.createStandardArticle("Bé trai trôi giữa dòng nước...", "4/10/2025", "url_to_image_" + i));
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
