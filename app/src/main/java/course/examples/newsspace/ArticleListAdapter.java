package course.examples.newsspace; // Thay bằng package của bạn

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

import course.examples.newsspace.databinding.ItemFeaturedNewsCardBinding;
import course.examples.newsspace.databinding.ItemStandardNewsCardBinding;
import course.examples.newsspace.model.Article;

public class ArticleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_FEATURED = 1;
    private static final int VIEW_TYPE_STANDARD = 2;

    private final List<Article> articles;

    public ArticleListAdapter(List<Article> articles) {
        this.articles = articles;
    }

    @Override
    public int getItemViewType(int position) {
        if (articles.get(position).isFeatured()) {
            return VIEW_TYPE_FEATURED;
        } else {
            return VIEW_TYPE_STANDARD;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_FEATURED) {
            return new FeaturedNewsViewHolder(ItemFeaturedNewsCardBinding.inflate(inflater, parent, false));
        } else {
            return new StandardNewsViewHolder(ItemStandardNewsCardBinding.inflate(inflater, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Article article = articles.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_FEATURED) {
            // Gọi phương thức bind của ViewHolder tương ứng
            ((FeaturedNewsViewHolder) holder).bind(article);
        } else {
            // Gọi phương thức bind của ViewHolder tương ứng
            ((StandardNewsViewHolder) holder).bind(article);
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    // ===================================================================================
    // CÁC LỚP VIEWHOLDER ĐÃ ĐƯỢC CẬP NHẬT
    // ===================================================================================

    public static class FeaturedNewsViewHolder extends RecyclerView.ViewHolder {
        ItemFeaturedNewsCardBinding binding;

        public FeaturedNewsViewHolder(ItemFeaturedNewsCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /**
         * Phương thức riêng để gán dữ liệu và xử lý sự kiện cho ViewHolder này.
         */
        void bind(Article article) {
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
                // Tạo action để điều hướng từ CategoryNewsFragment sang ArticleDetailFragment
                // và truyền vào ID của bài báo.
                CategoryNewsFragmentDirections.ActionCategoryNewsFragmentToArticleDetailFragment action =
                        CategoryNewsFragmentDirections.actionCategoryNewsFragmentToArticleDetailFragment(article.getId());

                // Tìm NavController và thực hiện điều hướng
                Navigation.findNavController(v).navigate(action);
            });
        }
    }

    public static class StandardNewsViewHolder extends RecyclerView.ViewHolder {
        ItemStandardNewsCardBinding binding;

        public StandardNewsViewHolder(ItemStandardNewsCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /**
         * Phương thức riêng để gán dữ liệu và xử lý sự kiện cho ViewHolder này.
         */
        void bind(Article article) {
            binding.newsTitleTextView.setText(article.getTitle());
            binding.dateTextView.setText(article.getDate());

            Glide.with(itemView.getContext())
                    .load(article.getImageUrl())
                    .centerCrop()
                    .placeholder(R.color.grey_200)
                    .into(binding.newsImageView);

            // Gán sự kiện click cho toàn bộ thẻ tin
            itemView.setOnClickListener(v -> {
                // Tạo action để điều hướng từ CategoryNewsFragment
                CategoryNewsFragmentDirections.ActionCategoryNewsFragmentToArticleDetailFragment action =
                        CategoryNewsFragmentDirections.actionCategoryNewsFragmentToArticleDetailFragment(article.getId());

                Navigation.findNavController(v).navigate(action);
            });
        }
    }
}