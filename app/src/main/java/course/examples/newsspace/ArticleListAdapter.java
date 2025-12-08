package course.examples.newsspace;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import course.examples.newsspace.databinding.ItemFeaturedNewsCardBinding;
import course.examples.newsspace.databinding.ItemStandardNewsCardBinding;
import course.examples.newsspace.model.AppDatabase;
import course.examples.newsspace.model.Article;
import course.examples.newsspace.model.ArticleDao;

public class ArticleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_FEATURED = 1;
    private static final int VIEW_TYPE_STANDARD = 2;

    private final List<Article> articles;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    public ArticleListAdapter(List<Article> articles) {
        this.articles = articles;
    }

    @Override
    public int getItemViewType(int position) {
        return articles.get(position).isFeatured() ? VIEW_TYPE_FEATURED : VIEW_TYPE_STANDARD;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_FEATURED) {
            return new FeaturedNewsViewHolder(ItemFeaturedNewsCardBinding.inflate(inflater, parent, false));
        } else {
            return new StandardNewsViewHolder(ItemStandardNewsCardBinding.inflate(inflater, parent, false), databaseExecutor);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Article article = articles.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_FEATURED) {
            ((FeaturedNewsViewHolder) holder).bind(article);
        } else {
            ((StandardNewsViewHolder) holder).bind(article);
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public static class FeaturedNewsViewHolder extends RecyclerView.ViewHolder {
        ItemFeaturedNewsCardBinding binding;
        public FeaturedNewsViewHolder(ItemFeaturedNewsCardBinding binding) { super(binding.getRoot()); this.binding = binding; }
        void bind(Article article) { /* ... */ }
    }

    public static class StandardNewsViewHolder extends RecyclerView.ViewHolder {
        ItemStandardNewsCardBinding binding;
        private final ExecutorService databaseExecutor;
        private final ArticleDao articleDao;

        public StandardNewsViewHolder(ItemStandardNewsCardBinding binding, ExecutorService executor) {
            super(binding.getRoot());
            this.binding = binding;
            this.databaseExecutor = executor;
            this.articleDao = AppDatabase.getDatabase(itemView.getContext()).articleDao();
        }

        void bind(Article article) {
            binding.newsTitleTextView.setText(article.getTitle());
            binding.dateTextView.setText(article.getDate());

            if (article.getImageUrl() != null && !article.getImageUrl().isEmpty()) {
                binding.newsImageView.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext()).load(article.getImageUrl()).centerCrop().placeholder(R.color.grey_200).into(binding.newsImageView);
            } else {
                binding.newsImageView.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putInt("articleId", article.getId());
                Navigation.findNavController(v).navigate(R.id.articleDetailFragment, bundle);
            });

            databaseExecutor.execute(() -> {
                boolean isBookmarked = articleDao.isArticleBookmarked(article.getId()) > 0;
                article.setBookmarked(isBookmarked);
                itemView.post(() -> updateBookmarkUi(binding.bookmarkImageView, isBookmarked));
            });

            binding.bookmarkImageView.setOnClickListener(v -> {
                boolean newBookmarkState = !article.isBookmarked();
                article.setBookmarked(newBookmarkState);
                updateBookmarkUi((ImageView) v, newBookmarkState);

                if (newBookmarkState) {
                    databaseExecutor.execute(() -> articleDao.insertArticle(article));
                    Toast.makeText(v.getContext(), "Đã lưu bài viết", Toast.LENGTH_SHORT).show();
                } else {
                    databaseExecutor.execute(() -> articleDao.deleteArticle(article));
                    Toast.makeText(v.getContext(), "Đã xóa khỏi danh sách đã lưu", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void updateBookmarkUi(ImageView bookmarkIcon, boolean isBookmarked) {
            if (isBookmarked) {
                bookmarkIcon.setImageResource(R.drawable.ic_nav_bookmark);
                bookmarkIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.primary_blue)));
            } else {
                bookmarkIcon.setImageResource(R.drawable.ic_bookmark_border);
                bookmarkIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), R.color.text_secondary)));
            }
            bookmarkIcon.setBackgroundResource(android.R.color.transparent);
        }
    }
}