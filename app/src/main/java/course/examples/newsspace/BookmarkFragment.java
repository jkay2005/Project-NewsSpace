package course.examples.newsspace;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import course.examples.newsspace.databinding.FragmentBookmarkBinding;


public abstract class BookmarkFragment extends Fragment implements CreateBookmarkCollectionDialogFragment.OnCollectionCreatedListener {

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

        adapter = new BookmarkAdapter(bookmarkItems, () -> {
            // Callback này được gọi khi người dùng nhấn nút "+" trong adapter
            CreateBookmarkCollectionDialogFragment dialog = new CreateBookmarkCollectionDialogFragment();
            // Đăng ký Fragment này làm listener để nhận kết quả
            dialog.setOnCollectionCreatedListener(this);
            dialog.show(getParentFragmentManager(), "CreateBookmarkDialog");
        });

        binding.bookmarkRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onCollectionCreated(String collectionName) {
        // Xử lý logic khi nhận được tên bộ sưu tập mới
        // Ví dụ: Gọi API để lưu, sau đó cập nhật lại giao diện
        Toast.makeText(getContext(), "Đã tạo bộ sưu tập: " + collectionName, Toast.LENGTH_SHORT).show();

        // Cập nhật lại danh sách bộ sưu tập và thông báo cho adapter
        // (Đây là phần logic bạn sẽ tự hoàn thiện)
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