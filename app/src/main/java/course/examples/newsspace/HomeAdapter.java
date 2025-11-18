package course.examples.newsspace; // Thay bằng package của bạn

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Thư viện tải ảnh, cần thêm vào build.gradle
import com.google.android.material.chip.Chip;
import android.content.res.ColorStateList; // Import thêm ColorStateList
import java.util.List;

// Import tất cả các lớp ViewBinding và Model cần thiết
import course.examples.newsspace.databinding.ItemAdBannerBinding; // Giả sử có layout này
import course.examples.newsspace.databinding.ItemCategoryTabsContainerBinding;
import course.examples.newsspace.databinding.ItemFeaturedNewsCardBinding;
import course.examples.newsspace.databinding.ItemHomeHeaderBinding;
import course.examples.newsspace.databinding.ItemSectionHeaderBinding;
import course.examples.newsspace.databinding.ItemStandardNewsCardBinding;
import course.examples.newsspace.model.Article;
import course.examples.newsspace.model.HeaderData;
import course.examples.newsspace.model.SectionHeader;
import course.examples.newsspace.model.TabData;
import course.examples.newsspace.databinding.ItemHomeFooterBinding;
import course.examples.newsspace.model.FooterData;

/**
 * Adapter đa năng cho màn hình Trang chủ (HomeFragment).
 * Chịu trách nhiệm hiển thị nhiều loại nội dung khác nhau như Header,
 * Thanh chuyên mục, Tin nổi bật, và các mục tin tức tiêu chuẩn.
 */
public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 1. Hằng số để định danh các loại ViewType
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_TABS = 1;
    private static final int TYPE_SECTION_HEADER = 2;
    private static final int TYPE_FEATURED_NEWS = 3;
    private static final int TYPE_STANDARD_NEWS = 4;
    private static final int TYPE_FOOTER = 6;

    // 2. Nguồn dữ liệu
    private final List<Object> items;
    private final OnArticleClickListener articleClickListener;
    public interface OnArticleClickListener {
        void onArticleClick(Article article);
    }
    // 3. Constructor
    public HomeAdapter(List<Object> items, OnArticleClickListener listener) {
        this.items = items;
        this.articleClickListener = listener;
    }

    /**
     * 4. Quyết định loại ViewType cho một vị trí cụ thể trong danh sách.
     */
    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof HeaderData) return TYPE_HEADER;
        if (item instanceof TabData) return TYPE_TABS;
        if (item instanceof SectionHeader) return TYPE_SECTION_HEADER;
        if (item instanceof Article && ((Article) item).isFeatured()) return TYPE_FEATURED_NEWS;
        if (item instanceof Article) return TYPE_STANDARD_NEWS;
        if (item instanceof FooterData) return TYPE_FOOTER;
        return -1;
    }


    /**
     * 5. Tạo ra ViewHolder tương ứng với ViewType.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_HEADER:
                return new HeaderViewHolder(ItemHomeHeaderBinding.inflate(inflater, parent, false));
            case TYPE_TABS:
                return new TabsViewHolder(ItemCategoryTabsContainerBinding.inflate(inflater, parent, false));
            case TYPE_SECTION_HEADER:
                return new SectionHeaderViewHolder(ItemSectionHeaderBinding.inflate(inflater, parent, false));
            case TYPE_FEATURED_NEWS:
                return new FeaturedNewsViewHolder(ItemFeaturedNewsCardBinding.inflate(inflater, parent, false));
            case TYPE_STANDARD_NEWS:
                return new StandardNewsViewHolder(ItemStandardNewsCardBinding.inflate(inflater, parent, false));
            case TYPE_FOOTER:
                return new FooterViewHolder(ItemHomeFooterBinding.inflate(inflater, parent, false));
            default:
                return new EmptyViewHolder(new View(parent.getContext()));
        }
    }

    /**
     * 6. Gán dữ liệu (bind data) vào các View bên trong ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object currentItem = items.get(position);

        switch (holder.getItemViewType()) {
            case TYPE_TABS:
                populateCategoryChips((TabsViewHolder) holder);
                break;
            case TYPE_SECTION_HEADER:
                SectionHeader sectionHeader = (SectionHeader) currentItem;
                ((SectionHeaderViewHolder) holder).binding.sectionTitleTextView.setText(sectionHeader.getTitle());
                break;
            case TYPE_FEATURED_NEWS:
                // 4. TRUYỀN LISTENER VÀO VIEWHOLDER KHI BIND
                ((FeaturedNewsViewHolder) holder).bind((Article) currentItem, articleClickListener);
                break;
            case TYPE_STANDARD_NEWS:
                // 4. TRUYỀN LISTENER VÀO VIEWHOLDER KHI BIND
                ((StandardNewsViewHolder) holder).bind((Article) currentItem, articleClickListener);
                break;
        }
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {
        ItemHomeFooterBinding binding;
        FooterViewHolder(ItemHomeFooterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
    /**
     * 7. Trả về tổng số item trong danh sách.
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Hàm helper để tạo và thêm các Chip vào ChipGroup
     */
    private void populateCategoryChips(TabsViewHolder holder) {
        // Danh sách các chuyên mục (có thể lấy từ API hoặc định nghĩa cứng)
        String[] categories = {
                "Mới nhất", "Thời sự", "Chính trị", "Thế giới", "Kinh tế", "Đời sống",
                "Du lịch", "Văn hóa", "Giải trí", "Giới trẻ", "Giáo dục", "Thể thao",
                "Sức khỏe", "Công nghệ", "Thời trang", "Xe", "Tiêu dùng"};

        holder.binding.categoryChipGroup.removeAllViews(); // Xóa chip cũ
        LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());


        for (String categoryName : categories) {
            Chip chip = (Chip) inflater.inflate(R.layout.chip_choice, holder.binding.categoryChipGroup, false);
            chip.setText(categoryName);
            chip.setOnClickListener(v -> {
                HomeFragmentDirections.ActionHomeFragmentToCategoryNewsFragment action =
                        HomeFragmentDirections.actionHomeFragmentToCategoryNewsFragment(categoryName);
                Navigation.findNavController(v).navigate(action);
            });
            holder.binding.categoryChipGroup.addView(chip);
        }
    }

    // ===================================================================================
    // CÁC LỚP VIEWHOLDER (Inner Classes)
    // ===================================================================================

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        HeaderViewHolder(ItemHomeHeaderBinding binding) { super(binding.getRoot()); }
    }

    static class TabsViewHolder extends RecyclerView.ViewHolder {
        ItemCategoryTabsContainerBinding binding;
        TabsViewHolder(ItemCategoryTabsContainerBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }

    static class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
        ItemSectionHeaderBinding binding;
        SectionHeaderViewHolder(ItemSectionHeaderBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }


    static class EmptyViewHolder extends RecyclerView.ViewHolder {
        EmptyViewHolder(View itemView) { super(itemView); }
    }


    // 5. CẬP NHẬT VIEWHOLDER ĐỂ NHẬN LISTENER VÀ XỬ LÝ CLICK
    static class FeaturedNewsViewHolder extends RecyclerView.ViewHolder {
        ItemFeaturedNewsCardBinding binding;

        FeaturedNewsViewHolder(ItemFeaturedNewsCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(final Article article, final OnArticleClickListener listener) {
            binding.newsTitleTextView.setText(article.getTitle());
            binding.newsDescriptionTextView.setText(article.getDescription());
            binding.dateTextView.setText(article.getDate());

            Glide.with(itemView.getContext())
                    .load(article.getImageUrl())
                    .centerCrop()
                    .placeholder(R.color.grey_200)
                    .into(binding.newsImageView);

            // Gán sự kiện click cho toàn bộ thẻ tin
            itemView.setOnClickListener(v -> {
                // Thay vì tự điều hướng, nó sẽ gọi ra bên ngoài (Fragment) để xử lý
                if (listener != null) {
                    listener.onArticleClick(article);
                }
            });
        }
    }

    static class StandardNewsViewHolder extends RecyclerView.ViewHolder {
        ItemStandardNewsCardBinding binding;

        StandardNewsViewHolder(ItemStandardNewsCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(final Article article, final OnArticleClickListener listener) {
            binding.newsTitleTextView.setText(article.getTitle());
            binding.dateTextView.setText(article.getDate());

            Glide.with(itemView.getContext())
                    .load(article.getImageUrl())
                    .centerCrop()
                    .placeholder(R.color.grey_200)
                    .into(binding.newsImageView);

            // Gán sự kiện click cho toàn bộ thẻ tin
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onArticleClick(article);
                }
            });
        }
    }
}