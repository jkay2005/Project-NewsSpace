package course.examples.newsspace.adapter;import android.content.Context; import android.view.LayoutInflater; import android.view.View; import android.view.ViewGroup; import androidx.annotation.NonNull; import androidx.recyclerview.widget.RecyclerView; import com.bumptech.glide.Glide; import java.util.List; import course.examples.newsspace.R; import course.examples.newsspace.databinding.ItemArticleBinding; import course.examples.newsspace.model.gnews.GNewsArticle;public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {private final Context context;
    private final List<GNewsArticle> articleList;
    private OnItemClickListener listener;

    /**
     * Interface để Fragment có thể lắng nghe sự kiện click từ Adapter.
     */
    public interface OnItemClickListener {
        void onItemClick(GNewsArticle article);
        void onBookmarkClick(GNewsArticle article);
    }

    public NewsAdapter(Context context, List<GNewsArticle> articleList, OnItemClickListener listener) {
        this.context = context;
        this.articleList = articleList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // "Thổi phồng" layout item_article.xml thành một View object
        ItemArticleBinding binding = ItemArticleBinding.inflate(
                LayoutInflater.from(context), parent, false
        );
        return new NewsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        // Lấy dữ liệu của item tại vị trí `position`
        GNewsArticle currentArticle = articleList.get(position);
        // Gán dữ liệu đó cho ViewHolder để hiển thị lên màn hình
        holder.bind(currentArticle, listener);
    }

    @Override
    public int getItemCount() {
        // Trả về tổng số item trong danh sách
        return articleList.size();
    }

    /**
     * Lớp ViewHolder đại diện cho MỘT HÀNG trong RecyclerView.
     * Nó giữ các tham chiếu đến các View bên trong (ImageView, TextViews...).
     */
    static class NewsViewHolder extends RecyclerView.ViewHolder {
        private final ItemArticleBinding binding;

        public NewsViewHolder(ItemArticleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        // Phương thức này gán dữ liệu từ một đối tượng GNewsArticle vào các View
        public void bind(final GNewsArticle article, final OnItemClickListener listener) {
            binding.articleTitleTextView.setText(article.getTitle());

            if (article.getSource() != null) {
                binding.articleSourceTextView.setText(article.getSource().getName());
            } else {
                binding.articleSourceTextView.setText("N/A"); // Hiển thị khi không có nguồn
            }

            // Dùng thư viện Glide để tải ảnh từ URL vào ImageView
            Glide.with(itemView.getContext())
                    .load(article.getImage())
                    .placeholder(R.drawable.ic_avatar_placeholder) // Ảnh hiển thị trong lúc chờ tải
                    .error(R.drawable.ic_avatar_placeholder)       // Ảnh hiển thị nếu tải lỗi
                    .into(binding.articleImageView);

            // Thiết lập sự kiện click
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(article);
                }
            });

            binding.bookmarkButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBookmarkClick(article);
                }
            });
        }
    }}