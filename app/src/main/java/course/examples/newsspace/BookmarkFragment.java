package course.examples.newsspace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import course.examples.newsspace.databinding.FragmentBookmarkBinding;
import course.examples.newsspace.model.Article;
import course.examples.newsspace.model.HeaderData;

public class BookmarkFragment extends Fragment {

    private FragmentBookmarkBinding binding;
    private BookmarkAdapter adapter;
    private final List<Object> bookmarkItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBookmarkBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        loadBookmarkData();
    }

    private void setupRecyclerView() {
        binding.bookmarkRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BookmarkAdapter(bookmarkItems, () -> {
            new CreateBookmarkCollectionDialogFragment().show(getParentFragmentManager(), "CreateBookmarkDialog");
        });
        binding.bookmarkRecyclerView.setAdapter(adapter);
    }

    private void loadBookmarkData() {
        bookmarkItems.clear();

        // 1. Thêm Header
        bookmarkItems.add(new HeaderData());

        // 2. Thêm danh sách các bộ sưu tập bookmark
        List<String> collections = new ArrayList<>();
        collections.add("Đọc sau");
        collections.add("Đã lưu");
        bookmarkItems.add(new BookmarkCollections(collections));

        // 3. Thêm danh sách các bài báo đã lưu - sử dụng factory method
        for (int i = 0; i < 10; i++) {
            bookmarkItems.add(Article.createStandardArticle("Bé trai trôi giữa dòng nước...", "4/10/2025", "url_to_image_" + i));
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}