package course.examples.newsspace; // Thay bằng package của bạn

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Thư viện tải ảnh, cần thêm vào build.gradle
import com.google.android.material.chip.Chip;
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

    // 2. Nguồn dữ liệu
    private final List<Object> items;

    // 3. Constructor
    public HomeAdapter(List<Object> items) {
        this.items = items;
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
        if (item instanceof Article) return TYPE_STANDARD_NEWS; // Mặc định là tin thường
        return -1; // Trả về -1 cho các trường hợp không xác định để tránh lỗi
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
            default:
                // Trả về một ViewHolder trống để ứng dụng không bị crash nếu gặp viewType lạ
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
                // Xử lý việc tạo các Chip chuyên mục
                TabsViewHolder tabsHolder = (TabsViewHolder) holder;
                populateCategoryChips(tabsHolder);
                break;
            case TYPE_SECTION_HEADER:
                SectionHeaderViewHolder sectionHolder = (SectionHeaderViewHolder) holder;
                SectionHeader sectionHeader = (SectionHeader) currentItem;
                sectionHolder.binding.sectionTitleTextView.setText(sectionHeader.getTitle());
                break;
            case TYPE_FEATURED_NEWS:
                FeaturedNewsViewHolder featuredHolder = (FeaturedNewsViewHolder) holder;
                Article featuredArticle = (Article) currentItem;
                featuredHolder.bind(featuredArticle);
                break;
            case TYPE_STANDARD_NEWS:
                StandardNewsViewHolder standardHolder = (StandardNewsViewHolder) holder;
                Article standardArticle = (Article) currentItem;
                standardHolder.bind(standardArticle);
                break;
            // Các case Header thường không cần gán dữ liệu động
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
        String[] categories = {"Mới nhất", "Thời sự", "Chính trị", "Thế giới", "Kinh tế", "Đời sống"};

        holder.binding.categoryChipGroup.removeAllViews(); // Xóa chip cũ
        LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());

        for (String categoryName : categories) {
            Chip chip = (Chip) inflater.inflate(R.layout.chip_choice, holder.binding.categoryChipGroup, false);
            chip.setText(categoryName);
            // Gán sự kiện click cho từng Chip
            chip.setOnClickListener(v -> {
                // Điều hướng sang CategoryNewsFragment và truyền tên chuyên mục
                HomeFragmentDirections.ActionHomeFragmentToCategoryNewsFragment action = HomeFragmentDirections.actionHomeFragmentToCategoryNewsFragment(categoryName);
                Navigation.findNavController(v).navigate(action);
            });
            holder.binding.categoryChipGroup.addView(chip);
        }
    }

    // ===================================================================================
    // CÁC LỚP VIEWHOLDER (Inner Classes)
    // ===================================================================================

    // ViewHolder cho Header
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        ItemHomeHeaderBinding binding;
        HeaderViewHolder(ItemHomeHeaderBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }

    // ViewHolder cho thanh chuyên mục
    static class TabsViewHolder extends RecyclerView.ViewHolder {
        ItemCategoryTabsContainerBinding binding;
        TabsViewHolder(ItemCategoryTabsContainerBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }

    // ViewHolder cho tiêu đề mục
    static class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
        ItemSectionHeaderBinding binding;
        SectionHeaderViewHolder(ItemSectionHeaderBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }

    // ViewHolder cho thẻ tin nổi bật (lớn)
    static class FeaturedNewsViewHolder extends RecyclerView.ViewHolder {
        ItemFeaturedNewsCardBinding binding;
        FeaturedNewsViewHolder(ItemFeaturedNewsCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Article article) {
            binding.newsTitleTextView.setText(article.getTitle());
            binding.newsDescriptionTextView.setText(article.getDescription());
            binding.dateTextView.setText(article.getDate());

            // Sử dụng Glide để tải ảnh
            Glide.with(itemView.getContext())
                    .load(article.getImageUrl())
                    .centerCrop()
                    .placeholder(R.color.grey_200) // Màu nền tạm thời khi đang tải
                    .into(binding.newsImageView);

            // Gán sự kiện click cho cả thẻ tin
            itemView.setOnClickListener(v -> {
                // Điều hướng sang ArticleDetailFragment
            });
        }
    }

    // ViewHolder cho thẻ tin tiêu chuẩn (nhỏ)
    static class StandardNewsViewHolder extends RecyclerView.ViewHolder {
        ItemStandardNewsCardBinding binding;
        StandardNewsViewHolder(ItemStandardNewsCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Article article) {
            binding.newsTitleTextView.setText(article.getTitle());
            binding.dateTextView.setText(article.getDate());

            // Sử dụng Glide để tải ảnh
            Glide.with(itemView.getContext())
                    .load(article.getImageUrl())
                    .centerCrop()
                    .placeholder(R.color.grey_200)
                    .into(binding.newsImageView);

            // Gán sự kiện click
            itemView.setOnClickListener(v -> {
                // Điều hướng sang ArticleDetailFragment
            });
        }
    }

    // ViewHolder trống để xử lý các viewtype không mong muốn
    static class EmptyViewHolder extends RecyclerView.ViewHolder {
        EmptyViewHolder(View itemView) { super(itemView); }
    }
}