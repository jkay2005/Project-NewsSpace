package course.examples.newsspace; // Thay bằng package của bạn

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

// Import các lớp ViewBinding và Model cần thiết
import course.examples.newsspace.databinding.ItemArticleHeaderBinding;
import course.examples.newsspace.databinding.ItemArticleImageBinding;
import course.examples.newsspace.databinding.ItemArticleParagraphBinding;
import course.examples.newsspace.databinding.ItemCommentSectionBinding;
import course.examples.newsspace.databinding.ItemSectionHeaderBinding;
import course.examples.newsspace.databinding.ItemStandardNewsCardBinding;
import course.examples.newsspace.model.Article;
import course.examples.newsspace.model.ArticleHeader;
import course.examples.newsspace.model.ArticleImage;
import course.examples.newsspace.model.ArticleParagraph;
import course.examples.newsspace.model.CommentSection;
import course.examples.newsspace.model.RelatedNewsHeader;

public class ArticleDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Hằng số ViewType
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_PARAGRAPH = 1;
    private static final int TYPE_IMAGE = 2;
    private static final int TYPE_COMMENT_SECTION = 3;
    private static final int TYPE_RELATED_NEWS_HEADER = 4;
    private static final int TYPE_RELATED_NEWS_ITEM = 5;

    private final List<Object> contentList;
    private final HeaderClickListener headerClickListener;
    private String currentFontSize;

    // Interface callback để Fragment gán sự kiện click cho Header
    public interface HeaderClickListener {
        void onHeaderCreated(HeaderViewHolder holder);
    }

    // Constructor mới, nhận vào 3 tham số
    public ArticleDetailAdapter(List<Object> contentList, HeaderClickListener listener, String initialFontSize) {
        this.contentList = contentList;
        this.headerClickListener = listener;
        this.currentFontSize = initialFontSize;
    }

    /**
     * Phương thức công khai để Fragment có thể cập nhật cỡ chữ.
     */
    public void updateFontSize(String newSize) {
        this.currentFontSize = newSize;
        notifyDataSetChanged(); // Yêu cầu RecyclerView vẽ lại toàn bộ
    }

    @Override
    public int getItemViewType(int position) {
        Object item = contentList.get(position);
        if (item instanceof ArticleHeader) return TYPE_HEADER;
        if (item instanceof ArticleParagraph) return TYPE_PARAGRAPH;
        if (item instanceof ArticleImage) return TYPE_IMAGE;
        if (item instanceof CommentSection) return TYPE_COMMENT_SECTION;
        if (item instanceof RelatedNewsHeader) return TYPE_RELATED_NEWS_HEADER;
        if (item instanceof Article) return TYPE_RELATED_NEWS_ITEM;
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_HEADER:
                return new HeaderViewHolder(ItemArticleHeaderBinding.inflate(inflater, parent, false));
            case TYPE_PARAGRAPH:
                return new ParagraphViewHolder(ItemArticleParagraphBinding.inflate(inflater, parent, false));
            case TYPE_IMAGE:
                return new ImageViewHolder(ItemArticleImageBinding.inflate(inflater, parent, false));
            case TYPE_COMMENT_SECTION:
                return new CommentSectionViewHolder(ItemCommentSectionBinding.inflate(inflater, parent, false));
            case TYPE_RELATED_NEWS_HEADER:
                return new SectionHeaderViewHolder(ItemSectionHeaderBinding.inflate(inflater, parent, false));
            case TYPE_RELATED_NEWS_ITEM:
                return new StandardNewsViewHolder(ItemStandardNewsCardBinding.inflate(inflater, parent, false));
            default:
                return new EmptyViewHolder(new View(parent.getContext()));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object currentItem = contentList.get(position);
        switch (holder.getItemViewType()) {
            case TYPE_HEADER:
                HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
                headerHolder.bind((ArticleHeader) currentItem);
                // Gọi callback để Fragment gán sự kiện click
                if (headerClickListener != null) {
                    headerClickListener.onHeaderCreated(headerHolder);
                }
                break;
            case TYPE_PARAGRAPH:
                ParagraphViewHolder pvh = (ParagraphViewHolder) holder;
                pvh.bind((ArticleParagraph) currentItem, currentFontSize);
                break;
            case TYPE_IMAGE:
                ImageViewHolder ivh = (ImageViewHolder) holder;
                ivh.bind((ArticleImage) currentItem);
                break;
            case TYPE_RELATED_NEWS_HEADER:
                ((SectionHeaderViewHolder) holder).binding.sectionTitleTextView.setText("Tin liên quan");
                break;
            case TYPE_RELATED_NEWS_ITEM:
                ((StandardNewsViewHolder) holder).bind((Article) currentItem);
                break;
            // CommentSection không cần bind dữ liệu động ở đây
        }
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    // --- CÁC LỚP VIEWHOLDER ---

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        ItemArticleHeaderBinding binding;
        HeaderViewHolder(ItemArticleHeaderBinding binding) { super(binding.getRoot()); this.binding = binding; }
        void bind(ArticleHeader header) {
            binding.categoryTextView.setText(header.getCategory());
            binding.titleTextView.setText(header.getTitle());
            binding.authorNameTextView.setText(header.getAuthorName());
            binding.dateTextView.setText(header.getDate());
        }
    }

    static class ParagraphViewHolder extends RecyclerView.ViewHolder {
        ItemArticleParagraphBinding binding;
        ParagraphViewHolder(ItemArticleParagraphBinding binding) { super(binding.getRoot()); this.binding = binding; }
        void bind(ArticleParagraph paragraph, String fontSize) {
            binding.paragraphTextView.setText(paragraph.getText());
            // Áp dụng cỡ chữ
            float sizeMultiplier = 1.0f;
            if ("small".equals(fontSize)) sizeMultiplier = 0.85f;
            if ("large".equals(fontSize)) sizeMultiplier = 1.15f;
            // Lấy cỡ chữ gốc từ dimens và nhân với hệ số
            float baseSize = itemView.getContext().getResources().getDimension(R.dimen.text_size_body);
            binding.paragraphTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, baseSize * sizeMultiplier);
        }
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ItemArticleImageBinding binding;
        ImageViewHolder(ItemArticleImageBinding binding) { super(binding.getRoot()); this.binding = binding; }
        void bind(ArticleImage image) {
            binding.captionTextView.setText(image.getCaption());
            Glide.with(itemView.getContext()).load(image.getImageUrl()).into(binding.contentImageView);
        }
    }

    static class CommentSectionViewHolder extends RecyclerView.ViewHolder {
        ItemCommentSectionBinding binding;
        CommentSectionViewHolder(ItemCommentSectionBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }

    static class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
        ItemSectionHeaderBinding binding;
        SectionHeaderViewHolder(ItemSectionHeaderBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }

    static class StandardNewsViewHolder extends RecyclerView.ViewHolder {
        ItemStandardNewsCardBinding binding;
        StandardNewsViewHolder(ItemStandardNewsCardBinding binding) { super(binding.getRoot()); this.binding = binding; }
        void bind(Article article) { /* Logic gán dữ liệu và click cho tin liên quan */ }
    }

    static class EmptyViewHolder extends RecyclerView.ViewHolder {
        EmptyViewHolder(View itemView) { super(itemView); }
    }
}