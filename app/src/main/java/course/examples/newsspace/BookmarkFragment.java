package course.examples.newsspace;

import android.os.Bundle;
import android.view.View;

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
    private BookmarkAdapter adapter; // Sẽ tạo adapter này
    private List<Object> bookmarkItems = new ArrayList<>();

    // ... onCreateView, onDestroyView ...

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        loadBookmarkData();
    }

    private void setupRecyclerView() {
        binding.bookmarkRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Adapter này sẽ cần xử lý nhiều ViewType: Header, ChipGroup, và Article
        adapter = new BookmarkAdapter(bookmarkItems, () -> {
            // Callback này được gọi khi người dùng nhấn nút "+"
            new CreateBookmarkCollectionDialogFragment().show(getParentFragmentManager(), "CreateBookmarkDialog");
        });
        binding.bookmarkRecyclerView.setAdapter(adapter);
    }

    private void loadBookmarkData() {
        // *** GIẢ LẬP TẢI DỮ LIỆU BOOKMARK ***
        bookmarkItems.clear();

        // 1. Thêm Header
        bookmarkItems.add(new HeaderData());

        // 2. Thêm danh sách các bộ sưu tập bookmark
        List<String> collections = new ArrayList<>();
        collections.add("Đọc sau");
        collections.add("Đã lưu");
        // ...
        bookmarkItems.add(new BookmarkCollections(collections));

        // 3. Thêm danh sách các bài báo đã lưu
        for (int i = 0; i < 10; i++) {
            bookmarkItems.add(new Article("Bé trai trôi giữa dòng nước...", "4/10/2025", "url", false));
        }

        adapter.notifyDataSetChanged();
    }
}