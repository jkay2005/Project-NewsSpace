package course.examples.newsspace; // Thay bằng package của bạn

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import java.util.List;
import course.examples.newsspace.databinding.FragmentBookmarkBinding;
import course.examples.newsspace.model.AppDatabase;
import course.examples.newsspace.model.Article;
import course.examples.newsspace.model.ArticleDao;

/**
 * Fragment hiển thị các bài báo đã được người dùng đánh dấu (bookmark),
 * được lấy từ cơ sở dữ liệu Room.
 */
public class BookmarkFragment extends Fragment {

    private FragmentBookmarkBinding binding;
    private ArticleListAdapter adapter;
    private final List<Article> bookmarkedArticles = new ArrayList<>();
    private ArticleDao articleDao;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBookmarkBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Lấy DAO từ AppDatabase
        articleDao = AppDatabase.getDatabase(requireContext()).articleDao();

        setupRecyclerView();
        observeBookmarkedArticles();
    }

    private void setupRecyclerView() {
        binding.bookmarkRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Sử dụng ArticleListAdapter đơn giản để hiển thị danh sách bài báo
        adapter = new ArticleListAdapter(bookmarkedArticles);
        binding.bookmarkRecyclerView.setAdapter(adapter);
    }

    /**
     * Lắng nghe các thay đổi trong danh sách bài báo đã lưu từ Room.
     * Khi có thay đổi, danh sách sẽ tự động được cập nhật.
     */
    private void observeBookmarkedArticles() {
        // Sử dụng LiveData để tự động cập nhật UI khi CSDL thay đổi
        articleDao.getAllBookmarkedArticles().observe(getViewLifecycleOwner(), new Observer<List<Article>>() {
            @Override
            public void onChanged(List<Article> articles) {
                if (articles == null || articles.isEmpty()) {
                    // Hiển thị trạng thái trống
                    binding.emptyStateLayout.setVisibility(View.VISIBLE);
                    binding.bookmarkRecyclerView.setVisibility(View.GONE);
                } else {
                    // Ẩn trạng thái trống và hiển thị danh sách
                    binding.emptyStateLayout.setVisibility(View.GONE);
                    binding.bookmarkRecyclerView.setVisibility(View.VISIBLE);

                    // Cập nhật danh sách trong adapter và thông báo thay đổi
                    bookmarkedArticles.clear();
                    bookmarkedArticles.addAll(articles);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Tránh memory leak
    }
}