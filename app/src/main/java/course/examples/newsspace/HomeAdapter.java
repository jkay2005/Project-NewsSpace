package course.examples.newsspace; // Thay bằng package của bạn

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Import các lớp ViewBinding và Model cần thiết
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

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_TABS = 1;
    private static final int TYPE_SECTION_HEADER = 2;
    private static final int TYPE_FEATURED_NEWS = 3;
    private static final int TYPE_STANDARD_NEWS = 4;
    private static final int TYPE_FOOTER = 6;

    private final List<Object> items;
    private final Map<String, String> categoryMap;
    private boolean isCategoryChipsPopulated = false;

    public HomeAdapter(List<Object> items) {
        this.items = items;
        this.categoryMap = new LinkedHashMap<>();
        // Sửa lỗi: Loại bỏ "Chính trị" vì không có endpoint riêng
        categoryMap.put("Mới nhất", "Mới nhất");
        categoryMap.put("Thời sự", "Thời sự");
        categoryMap.put("Thế giới", "Thế giới");
        categoryMap.put("Kinh tế", "Kinh tế");
        categoryMap.put("Giải trí", "Giải trí");
        categoryMap.put("Thể thao", "Thể thao");
        categoryMap.put("Sức khỏe", "Sức khỏe");
        categoryMap.put("Công nghệ", "Công nghệ");
        categoryMap.put("Khoa học", "Khoa học");
    }

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

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object currentItem = items.get(position);
        switch (holder.getItemViewType()) {
            case TYPE_TABS:
                if (!isCategoryChipsPopulated) {
                    populateCategoryChips((TabsViewHolder) holder);
                    isCategoryChipsPopulated = true;
                }
                break;
            case TYPE_SECTION_HEADER:
                SectionHeader sectionHeader = (SectionHeader) currentItem;
                ((SectionHeaderViewHolder) holder).binding.sectionTitleTextView.setText(sectionHeader.getTitle());
                break;
            case TYPE_FEATURED_NEWS:
                ((FeaturedNewsViewHolder) holder).bind((Article) currentItem);
                break;
            case TYPE_STANDARD_NEWS:
                ((StandardNewsViewHolder) holder).bind((Article) currentItem);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void populateCategoryChips(TabsViewHolder holder) {
        holder.binding.categoryChipGroup.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());

        for (String displayName : categoryMap.keySet()) {
            Chip chip = (Chip) inflater.inflate(R.layout.chip_choice, holder.binding.categoryChipGroup, false);
            chip.setText(displayName);

            chip.setOnClickListener(v -> {
                // Lấy khóa API từ bản đồ (bây giờ đã chính xác)
                String apiKey = categoryMap.get(displayName);

                // Tạo action và truyền khóa API đi
                HomeFragmentDirections.ActionHomeFragmentToCategoryNewsFragment action =
                        HomeFragmentDirections.actionHomeFragmentToCategoryNewsFragment(apiKey);
                Navigation.findNavController(v).navigate(action);
            });

            holder.binding.categoryChipGroup.addView(chip);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        ItemHomeHeaderBinding binding;
        HeaderViewHolder(ItemHomeHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.notificationIcon.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_home_to_notification));
            binding.searchIcon.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_home_to_search));
        }
    }

    static class TabsViewHolder extends RecyclerView.ViewHolder {
        ItemCategoryTabsContainerBinding binding;
        TabsViewHolder(ItemCategoryTabsContainerBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }

    static class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
        ItemSectionHeaderBinding binding;
        SectionHeaderViewHolder(ItemSectionHeaderBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }

    static class FeaturedNewsViewHolder extends RecyclerView.ViewHolder {
        ItemFeaturedNewsCardBinding binding;
        FeaturedNewsViewHolder(ItemFeaturedNewsCardBinding binding) { super(binding.getRoot()); this.binding = binding; }

        void bind(Article article) {
            binding.newsTitleTextView.setText(article.getTitle());
            binding.newsDescriptionTextView.setText(article.getDescription());
            binding.dateTextView.setText(article.getDate());
            Glide.with(itemView.getContext()).load(article.getImageUrl()).centerCrop().placeholder(R.color.grey_200).into(binding.newsImageView);
            itemView.setOnClickListener(v -> {
                HomeFragmentDirections.ActionHomeFragmentToArticleDetailFragment action = HomeFragmentDirections.actionHomeFragmentToArticleDetailFragment(article.getId());
                Navigation.findNavController(v).navigate(action);
            });
        }
    }

    static class StandardNewsViewHolder extends RecyclerView.ViewHolder {
        ItemStandardNewsCardBinding binding;
        StandardNewsViewHolder(ItemStandardNewsCardBinding binding) { super(binding.getRoot()); this.binding = binding; }

        void bind(Article article) {
            binding.newsTitleTextView.setText(article.getTitle());
            binding.dateTextView.setText(article.getDate());
            Glide.with(itemView.getContext()).load(article.getImageUrl()).centerCrop().placeholder(R.color.grey_200).into(binding.newsImageView);
            itemView.setOnClickListener(v -> {
                HomeFragmentDirections.ActionHomeFragmentToArticleDetailFragment action = HomeFragmentDirections.actionHomeFragmentToArticleDetailFragment(article.getId());
                Navigation.findNavController(v).navigate(action);
            });
        }
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {
        ItemHomeFooterBinding binding;
        FooterViewHolder(ItemHomeFooterBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }

    static class EmptyViewHolder extends RecyclerView.ViewHolder {
        EmptyViewHolder(View itemView) { super(itemView); }
    }
}
