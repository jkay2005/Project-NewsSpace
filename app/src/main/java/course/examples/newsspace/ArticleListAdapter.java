package course.examples.newsspace; // Thay bằng package của bạn

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// Import các lớp ViewBinding được tự động tạo ra
import course.examples.newsspace.databinding.ItemFeaturedNewsCardBinding;
import course.examples.newsspace.databinding.ItemStandardNewsCardBinding;

/**
 * Adapter này chịu trách nhiệm hiển thị danh sách các bài báo (Article).
 * Nó có khả năng xử lý hai loại giao diện khác nhau:
 * 1. Thẻ tin nổi bật (Featured) - Dùng layout item_featured_news_card.xml
 * 2. Thẻ tin tiêu chuẩn (Standard) - Dùng layout item_standard_news_card.xml
 */
public class ArticleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 1. Hằng số để định danh các loại View. Giúp code dễ đọc hơn là dùng số 1, 2.
    private static final int VIEW_TYPE_FEATURED = 1;
    private static final int VIEW_TYPE_STANDARD = 2;

    // 2. Nguồn dữ liệu mà Adapter sẽ sử dụng
    private List<Article> articles;

    // 3. Constructor để nhận dữ liệu từ Fragment
    public ArticleListAdapter(List<Article> articles) {
        this.articles = articles;
    }

    /**
     * 4. Quyết định loại View nào sẽ được sử dụng cho một vị trí (position) cụ thể.
     * Đây là phương thức cốt lõi cho việc hiển thị nhiều loại layout.
     */
    @Override
    public int getItemViewType(int position) {
        // Lấy bài báo tại vị trí hiện tại
        Article article = articles.get(position);
        // Kiểm tra cờ isFeatured của bài báo
        if (article.isFeatured()) {
            return VIEW_TYPE_FEATURED; // Nếu là tin nổi bật, trả về loại FEATURED
        } else {
            return VIEW_TYPE_STANDARD; // Nếu không, trả về loại STANDARD
        }
    }

    /**
     * 5. Tạo ra ViewHolder tương ứng với ViewType đã được quyết định ở trên.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Dựa vào viewType để "thổi phồng" (inflate) layout XML chính xác
        if (viewType == VIEW_TYPE_FEATURED) {
            ItemFeaturedNewsCardBinding binding = ItemFeaturedNewsCardBinding.inflate(inflater, parent, false);
            return new FeaturedNewsViewHolder(binding);
        } else { // viewType == VIEW_TYPE_STANDARD
            ItemStandardNewsCardBinding binding = ItemStandardNewsCardBinding.inflate(inflater, parent, false);
            return new StandardNewsViewHolder(binding);
        }
    }

    /**
     * 6. Gắn dữ liệu (bind data) từ đối tượng Article vào các View bên trong ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Article article = articles.get(position);

        // Kiểm tra xem holder thuộc loại nào để ép kiểu và gán dữ liệu cho đúng
        if (holder.getItemViewType() == VIEW_TYPE_FEATURED) {
            FeaturedNewsViewHolder featuredHolder = (FeaturedNewsViewHolder) holder;
            featuredHolder.binding.newsTitleTextView.setText(article.getTitle());
            featuredHolder.binding.newsDescriptionTextView.setText(article.getDescription());
            featuredHolder.binding.dateTextView.setText(article.getDate());
            // TODO: Dùng thư viện Glide/Picasso để tải ảnh từ article.getImageUrl() vào featuredHolder.binding.newsImageView
        } else { // holder.getItemViewType() == VIEW_TYPE_STANDARD
            StandardNewsViewHolder standardHolder = (StandardNewsViewHolder) holder;
            standardHolder.binding.newsTitleTextView.setText(article.getTitle());
            standardHolder.binding.dateTextView.setText(article.getDate());
            // TODO: Dùng thư viện Glide/Picasso để tải ảnh từ article.getImageUrl() vào standardHolder.binding.newsImageView
        }
    }

    /**
     * 7. Trả về tổng số item trong danh sách.
     */
    @Override
    public int getItemCount() {
        return articles.size();
    }

    // ===================================================================================
    // CÁC LỚP VIEWHOLDER
    // Mỗi ViewHolder chịu trách nhiệm cho một loại layout XML cụ thể.
    // ===================================================================================

    /**
     * 8. ViewHolder cho thẻ tin nổi bật (lớn).
     */
    public static class FeaturedNewsViewHolder extends RecyclerView.ViewHolder {
        // Chứa tham chiếu đến đối tượng binding của layout item_featured_news_card.xml
        ItemFeaturedNewsCardBinding binding;
        public FeaturedNewsViewHolder(ItemFeaturedNewsCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    /**
     * 9. ViewHolder cho thẻ tin tiêu chuẩn (nhỏ).
     */
    public static class StandardNewsViewHolder extends RecyclerView.ViewHolder {
        // Chứa tham chiếu đến đối tượng binding của layout item_standard_news_card.xml
        ItemStandardNewsCardBinding binding;
        public StandardNewsViewHolder(ItemStandardNewsCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}