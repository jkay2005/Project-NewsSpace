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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import course.examples.newsspace.databinding.FragmentBookmarkBinding;
import course.examples.newsspace.model.Article;
import course.examples.newsspace.model.BookmarkCollections;
import course.examples.newsspace.model.BookmarkResponse;
import course.examples.newsspace.model.HeaderData;
import course.examples.newsspace.api.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment hiển thị các bài báo đã được người dùng đánh dấu (bookmark).
 * Cho phép người dùng xem, quản lý các bộ sưu tập và tạo mới.
 */
public class BookmarkFragment extends Fragment implements CreateBookmarkCollectionDialogFragment.OnCollectionCreatedListener {

    private FragmentBookmarkBinding binding;
    private BookmarkAdapter adapter;
    private final List<Object> displayItems = new ArrayList<>(); // Danh sách cuối cùng để hiển thị

    // Dùng để cache dữ liệu gốc từ API
    private List<String> allCollections = new ArrayList<>();
    private List<Article> allSavedArticles = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        // Khởi tạo Adapter và truyền vào một callback (dưới dạng lambda)
        // để xử lý sự kiện khi người dùng nhấn nút "+"
        adapter = new BookmarkAdapter(displayItems, () -> {
            CreateBookmarkCollectionDialogFragment dialog = new CreateBookmarkCollectionDialogFragment();
            // Đặt Fragment này làm listener để nhận kết quả từ Dialog
            dialog.setOnCollectionCreatedListener(this);
            dialog.show(getParentFragmentManager(), "CreateBookmarkDialog");
        });

        binding.bookmarkRecyclerView.setAdapter(adapter);
    }

    private void loadBookmarkData() {
        // TODO: Hiển thị trạng thái loading


        // === PHẦN GỌI API THẬT SỰ ===
        // TODO: Bỏ comment phần này khi backend có API cho bookmark

        ApiClient.getApiService(requireContext()).getBookmarks().enqueue(new Callback<BookmarkResponse>() {
            @Override
            public void onResponse(@NonNull Call<BookmarkResponse> call, @NonNull Response<BookmarkResponse> response) {
                // TODO: Ẩn trạng thái loading

                if (response.isSuccessful() && response.body() != null) {
                    BookmarkResponse data = response.body();

                    // Lưu dữ liệu gốc vào các biến cache
                    allCollections = data.getCollectionNames();
                    allSavedArticles = data.getSavedArticles();

                    // Xây dựng danh sách để hiển thị lên giao diện
                    buildDisplayList();

                } else {
                    Toast.makeText(getContext(), "Không thể tải danh sách bookmark", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookmarkResponse> call, @NonNull Throwable t) {
                // TODO: Ẩn trạng thái loading
                Log.e("BookmarkFragment", "API Call Failed: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi mạng, không thể tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });


//        // === PHẦN DÙNG DỮ LIỆU GIẢ (ĐỂ TEST GIAO DIỆN) ===
//        // TODO: Xóa phần này khi đã có API thật
//        allCollections.clear();
//        allCollections.add("Đọc sau");
//        allCollections.add("Đã lưu");
//        allCollections.add("Thời sự");
//
//        allSavedArticles.clear();
//        for (int i = 0; i < 8; i++) {
//            allSavedArticles.add(Article.createStandardArticle(
//                    "Bài báo đã lưu thứ " + (i + 1),
//                    "4/10/2025",
//                    "https://picsum.photos/200?random=" + i // Thêm random để ảnh khác nhau
//            ));
//        }
//        buildDisplayList(); // Xây dựng giao diện từ dữ liệu giả
    }

    /**
     * Sắp xếp dữ liệu từ các biến cache thành một danh sách duy nhất để Adapter hiển thị.
     */
    private void buildDisplayList() {
        displayItems.clear();

        // 1. Thêm Header
        displayItems.add(new HeaderData());

        // 2. Thêm danh sách các bộ sưu tập
        displayItems.add(new BookmarkCollections(allCollections));

        // 3. Thêm các bài báo đã lưu
        // TODO: Thêm logic lọc bài báo theo bộ sưu tập đang được chọn
        displayItems.addAll(allSavedArticles);

        // Thông báo cho adapter
        adapter.notifyDataSetChanged();
    }

    /**
     * Được gọi khi người dùng tạo thành công một bộ sưu tập mới từ Dialog.
     * @param collectionName Tên của bộ sưu tập mới.
     */
    @Override
    public void onCollectionCreated(String collectionName) {
        // TODO: Hiển thị loading


        // === PHẦN GỌI API THẬT SỰ  ===
        Map<String, String> body = new HashMap<>();
        body.put("name", collectionName);

        ApiClient.getApiService(requireContext()).createBookmarkCollection(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                // TODO: Ẩn loading
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã tạo bộ sưu tập: " + collectionName, Toast.LENGTH_SHORT).show();
                    loadBookmarkData(); // Tải lại toàn bộ dữ liệu để cập nhật danh sách chip
                } else {
                    Toast.makeText(getContext(), "Tạo bộ sưu tập thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                // TODO: Ẩn loading
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });


        // === PHẦN DÙNG DỮ LIỆU GIẢ (ĐỂ TEST GIAO DIỆN) ===
//        Toast.makeText(getContext(), "Đã tạo bộ sưu tập: " + collectionName, Toast.LENGTH_SHORT).show();
//        allCollections.add(collectionName); // Thêm vào danh sách giả
//        buildDisplayList(); // Cập nhật lại giao diện
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}