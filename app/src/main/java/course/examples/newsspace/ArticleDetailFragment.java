package course.examples.newsspace; // Thay bằng package của bạn

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import course.examples.newsspace.databinding.FragmentArticleDetailBinding; // Thay bằng package của bạn

// Implement các interface để lắng nghe kết quả từ BottomSheet
public class ArticleDetailFragment extends Fragment
        implements FontSizePickerBottomSheet.FontSizeChangeListener,
        SaveToCollectionBottomSheet.OnCollectionSelectedListener {

    private FragmentArticleDetailBinding binding;
    private ArticleDetailAdapter adapter;
    private final List<Object> contentList = new ArrayList<>();

    // Biến để lưu trữ cỡ chữ hiện tại
    private String currentFontSize;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentArticleDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo SharedPreferences để lưu cỡ chữ
        sharedPreferences = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        // Đọc cỡ chữ đã lưu, nếu không có thì dùng "medium" làm mặc định
        currentFontSize = sharedPreferences.getString("font_size", "medium");

        setupToolbar();
        setupRecyclerView();
        loadDummyArticleContent();
    }

    private void setupToolbar() {
        binding.toolbar.setTitle("Thời sự");
        binding.toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
    }

    private void setupRecyclerView() {
        // Truyền cỡ chữ hiện tại vào Adapter
        adapter = new ArticleDetailAdapter(contentList, currentFontSize);
        binding.articleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.articleRecyclerView.setAdapter(adapter);
    }

    /**
     * Gán sự kiện click cho các icon chức năng trong header.
     * Hàm này sẽ được gọi từ bên trong onBindViewHolder của Adapter
     * khi HeaderViewHolder được tạo.
     */
    private void setupHeaderClickListeners(ArticleDetailAdapter.HeaderViewHolder holder) {
        // Mở dialog chọn cỡ chữ
        holder.binding.fontSizeImageView.setOnClickListener(v -> {
            FontSizePickerBottomSheet bottomSheet = new FontSizePickerBottomSheet();
            // Đặt Fragment này làm listener
            bottomSheet.setFontSizeChangeListener(this);
            // Có thể truyền cỡ chữ hiện tại vào bottomSheet để nó highlight đúng item
            bottomSheet.show(getParentFragmentManager(), "FontSizePicker");
        });

        // Mở dialog chọn bộ sưu tập bookmark
        holder.binding.bookmarkImageView.setOnClickListener(v -> {
            SaveToCollectionBottomSheet bottomSheet = new SaveToCollectionBottomSheet();
            // Đặt Fragment này làm listener
            bottomSheet.setOnCollectionSelectedListener(this);
            bottomSheet.show(getParentFragmentManager(), "SaveToCollection");
        });
    }

    private void loadDummyArticleContent() {
        // Logic tải dữ liệu không thay đổi
        contentList.clear();
        contentList.add(new ArticleHeader(/* ... */));
        contentList.add(new ArticleParagraph("Đoạn văn 1..."));
        contentList.add(new ArticleImage("url_1", "Chú thích 1"));
        contentList.add(new ArticleParagraph("Đoạn văn 2..."));
        // ...
        adapter.notifyDataSetChanged();
    }

    // ===================================================================
    // == IMPLEMENT CÁC PHƯƠNG THỨC CỦA INTERFACE CALLBACK            ==
    // ===================================================================

    @Override
    public void onFontSizeSelected(String size) {
        // 1. Cập nhật biến cỡ chữ hiện tại
        currentFontSize = size;

        // 2. Lưu lựa chọn mới vào SharedPreferences để ghi nhớ cho lần sau
        sharedPreferences.edit().putString("font_size", size).apply();

        // 3. Cập nhật cỡ chữ cho Adapter
        if (adapter != null) {
            adapter.updateFontSize(size);
        }

        Toast.makeText(getContext(), "Đã đổi cỡ chữ thành " + size, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCollectionSelected(String collectionName) {
        // Xử lý logic khi người dùng chọn một bộ sưu tập
        // Ví dụ: Gọi API để lưu bài báo vào bộ sưu tập đó
        Toast.makeText(getContext(), "Đã lưu vào '" + collectionName + "'", Toast.LENGTH_SHORT).show();

        // Cập nhật lại icon bookmark thành trạng thái đã lưu
        // (cần truy cập ViewHolder của header, việc này hơi phức tạp,
        // cách tốt hơn là dùng ViewModel hoặc cập nhật lại item)
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Cập nhật Adapter để có thể nhận sự kiện click từ Header
    // và cập nhật cỡ chữ
    public class ArticleDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        // ... các biến và phương thức khác ...
        private String fontSize;

        public ArticleDetailAdapter(List<Object> contentList, String initialFontSize) {
            this.contentList = contentList;
            this.fontSize = initialFontSize;
        }

        public void updateFontSize(String newSize) {
            this.fontSize = newSize;
            notifyDataSetChanged(); // Yêu cầu RecyclerView vẽ lại tất cả item với cỡ chữ mới
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case TYPE_HEADER:
                    // Khi bind header, hãy gán luôn sự kiện click
                    setupHeaderClickListeners((HeaderViewHolder) holder);
                    break;
                case TYPE_PARAGRAPH:
                    ParagraphViewHolder pvh = (ParagraphViewHolder) holder;
                    pvh.binding.paragraphTextView.setText(((ArticleParagraph) contentList.get(position)).getText());
                    // Áp dụng cỡ chữ
                    float sizeMultiplier = 1.0f;
                    if (fontSize.equals("small")) sizeMultiplier = 0.8f;
                    if (fontSize.equals("large")) sizeMultiplier = 1.2f;
                    pvh.binding.paragraphTextView.setTextSize(16 * sizeMultiplier); // 16sp là cỡ chữ gốc
                    break;
                // ... các case khác ...
            }
        }
        // ...
    }
}