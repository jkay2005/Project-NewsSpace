package course.examples.newsspace; // Thay bằng package của bạn

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import java.util.List;

// Import các lớp ViewBinding cần thiết
import course.examples.newsspace.databinding.ItemBookmarkCollectionsBinding;
import course.examples.newsspace.databinding.ItemHomeHeaderBinding;
import course.examples.newsspace.databinding.ItemStandardNewsCardBinding;
import course.examples.newsspace.model.Article;
import course.examples.newsspace.model.BookmarkCollections;

public class BookmarkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 1. Hằng số để định danh các loại View
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_COLLECTIONS = 1;
    private static final int TYPE_ARTICLE = 2;

    // 2. Nguồn dữ liệu
    private final List<Object> items;
    // 3. Interface để giao tiếp ngược lại với Fragment (khi nhấn nút +)
    private final OnAddCollectionClickListener addListener;

    public interface OnAddCollectionClickListener {
        void onAddClick();
    }

    // 4. Constructor
    public BookmarkAdapter(List<Object> items, OnAddCollectionClickListener listener) {
        this.items = items;
        this.addListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof HeaderData) return TYPE_HEADER;
        if (item instanceof BookmarkCollections) return TYPE_COLLECTIONS;
        if (item instanceof Article) return TYPE_ARTICLE;
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_HEADER:
                return new HeaderViewHolder(ItemHomeHeaderBinding.inflate(inflater, parent, false));
            case TYPE_COLLECTIONS:
                return new CollectionsViewHolder(ItemBookmarkCollectionsBinding.inflate(inflater, parent, false));
            case TYPE_ARTICLE:
                return new ArticleViewHolder(ItemStandardNewsCardBinding.inflate(inflater, parent, false));
            default:
                return new HomeAdapter.EmptyViewHolder(new View(parent.getContext()));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_HEADER:
                // Header không có dữ liệu cần bind
                break;
            case TYPE_COLLECTIONS:
                CollectionsViewHolder collectionsHolder = (CollectionsViewHolder) holder;
                BookmarkCollections collections = (BookmarkCollections) items.get(position);
                // Xử lý việc hiển thị các chip
                populateChips(collectionsHolder, collections.getCollectionNames());
                // Gán sự kiện click cho nút "+"
                collectionsHolder.binding.addCollectionImageView.setOnClickListener(v -> {
                    if (addListener != null) {
                        addListener.onAddClick();
                    }
                });
                break;
            case TYPE_ARTICLE:
                // Chỉ cần gọi hàm bind, nó sẽ tự xử lý
                ((ArticleViewHolder) holder).bind((Article) items.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Hàm helper để tạo và thêm các Chip vào ChipGroup
     */
    private void populateChips(CollectionsViewHolder holder, List<String> collectionNames) {
        holder.binding.bookmarkChipGroup.removeAllViews(); // Xóa các chip cũ
        LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
        for (String name : collectionNames) {
            Chip chip = (Chip) inflater.inflate(R.layout.chip_choice, holder.binding.bookmarkChipGroup, false);
            chip.setText(name);
            holder.binding.bookmarkChipGroup.addView(chip);
        }
    }

    // 5. Các lớp ViewHolder
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        ItemHomeHeaderBinding binding;
        HeaderViewHolder(ItemHomeHeaderBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }
    static class CollectionsViewHolder extends RecyclerView.ViewHolder {
        ItemBookmarkCollectionsBinding binding;
        CollectionsViewHolder(ItemBookmarkCollectionsBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }
    static class ArticleViewHolder extends RecyclerView.ViewHolder {
        ItemStandardNewsCardBinding binding;

        ArticleViewHolder(ItemStandardNewsCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        // Phương thức này sẽ gán dữ liệu và xử lý sự kiện click
        void bind(Article article) {
            // Gán dữ liệu
            binding.newsTitleTextView.setText(article.getTitle());
            binding.dateTextView.setText(article.getDate());
            Glide.with(itemView.getContext())
                    .load(article.getImageUrl())
                    .centerCrop()
                    .placeholder(R.color.grey_200)
                    .into(binding.newsImageView);

            // Gán sự kiện click
            itemView.setOnClickListener(v -> {
                // Điều hướng sang ArticleDetailFragment
                BookmarkFragmentDirections.ActionBookmarkFragmentToArticleDetailFragment action =
                        BookmarkFragmentDirections.actionBookmarkFragmentToArticleDetailFragment(article.getId());
                Navigation.findNavController(v).navigate(action);
            });
        }
    }

    // Thêm ViewHolder trống để an toàn
    static class EmptyViewHolder extends RecyclerView.ViewHolder {
        EmptyViewHolder(View itemView) { super(itemView); }
    }
}