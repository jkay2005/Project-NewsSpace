package course.examples.newsspace;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import course.examples.newsspace.databinding.FragmentArticleDetailBinding;
import course.examples.newsspace.model.AppDatabase;
import course.examples.newsspace.model.Article;
import course.examples.newsspace.model.ArticleDao;
import course.examples.newsspace.model.ArticleHeader;
import course.examples.newsspace.model.ArticleImage;
import course.examples.newsspace.model.ArticleParagraph;
import course.examples.newsspace.model.Author;
import course.examples.newsspace.model.CommentSection;
import course.examples.newsspace.model.RelatedNewsHeader;
import course.examples.newsspace.api.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleDetailFragment extends Fragment implements FontSizePickerBottomSheet.FontSizeChangeListener {

    private FragmentArticleDetailBinding binding;
    private ArticleDetailAdapter adapter;
    private final List<Object> contentList = new ArrayList<>();

    private String currentFontSize;
    private SharedPreferences sharedPreferences;
    private int articleId = -1;
    private Article currentArticle;

    private ArticleDao articleDao;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        currentFontSize = sharedPreferences.getString("font_size", "medium");
        if (getArguments() != null) {
            articleId = ArticleDetailFragmentArgs.fromBundle(getArguments()).getArticleId();
        }
        articleDao = AppDatabase.getDatabase(requireContext()).articleDao();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentArticleDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar();
        setupRecyclerView();
        if (articleId != -1) {
            loadArticleContent(articleId);
        } else {
            Toast.makeText(getContext(), "Lỗi: Không tìm thấy ID bài báo.", Toast.LENGTH_LONG).show();
            NavHostFragment.findNavController(this).navigateUp();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
    }

    private void setupRecyclerView() {
        adapter = new ArticleDetailAdapter(contentList, this::setupHeaderClickListeners, currentFontSize);
        binding.articleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.articleRecyclerView.setAdapter(adapter);
    }

    private void setupHeaderClickListeners(ArticleDetailAdapter.HeaderViewHolder holder) {
        holder.binding.fontSizeImageView.setOnClickListener(v -> {
            FontSizePickerBottomSheet bottomSheet = FontSizePickerBottomSheet.newInstance(currentFontSize);
            bottomSheet.setFontSizeChangeListener(this);
            bottomSheet.show(getParentFragmentManager(), "FontSizePicker");
        });

        if (currentArticle != null) {
            updateBookmarkUi(holder.binding.bookmarkImageView, currentArticle.isBookmarked());
        }

        holder.binding.bookmarkImageView.setOnClickListener(v -> {
            if (currentArticle == null) return;
            boolean newBookmarkState = !currentArticle.isBookmarked();
            currentArticle.setBookmarked(newBookmarkState);
            updateBookmarkUi((ImageView) v, newBookmarkState);

            if (newBookmarkState) {
                databaseExecutor.execute(() -> articleDao.insertArticle(currentArticle));
                Toast.makeText(getContext(), "Đã lưu bài viết", Toast.LENGTH_SHORT).show();
            } else {
                databaseExecutor.execute(() -> articleDao.deleteArticle(currentArticle));
                Toast.makeText(getContext(), "Đã xóa khỏi danh sách đã lưu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- HÀM CẬP NHẬT UI BOOKMARK (ĐÃ SỬA) ---
    private void updateBookmarkUi(ImageView bookmarkIcon, boolean isBookmarked) {
        if (isBookmarked) {
            // Trạng thái ĐÃ LƯU: icon đầy, màu xanh
            bookmarkIcon.setImageResource(R.drawable.ic_nav_bookmark);
            bookmarkIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.primary_blue)));
        } else {
            // Trạng thái CHƯA LƯU: icon viền, màu xám
            bookmarkIcon.setImageResource(R.drawable.ic_bookmark_border);
            bookmarkIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.text_secondary)));
        }
        // Bỏ đi phần nền để giao diện sạch hơn
        bookmarkIcon.setBackgroundResource(android.R.color.transparent);
    }

    private void loadArticleContent(int articleId) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.articleRecyclerView.setVisibility(View.INVISIBLE);

        ApiClient.getApiService(requireContext()).getArticleDetail(this.articleId).enqueue(new Callback<Article>() {
            @Override
            public void onResponse(@NonNull Call<Article> call, @NonNull Response<Article> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.articleRecyclerView.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    currentArticle = response.body();
                    databaseExecutor.execute(() -> {
                        boolean isBookmarked = articleDao.isArticleBookmarked(currentArticle.getId()) > 0;
                        currentArticle.setBookmarked(isBookmarked);
                        requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
                    });
                    buildDisplayList(currentArticle);
                } else {
                    Toast.makeText(getContext(), "Không thể tải nội dung bài báo.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Article> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Log.e("ArticleDetailFragment", "API Call Failed: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi mạng.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buildDisplayList(Article article) {
        contentList.clear();
        String category = "Tin tức";
        if (article.getCategory() != null && article.getCategory().getName() != null) {
            category = article.getCategory().getName();
        }
        binding.toolbar.setTitle(category);
        Author author = article.getAuthor();
        String authorName = (author != null && author.getName() != null) ? author.getName() : "Không rõ tác giả";
        contentList.add(new ArticleHeader(category, article.getTitle(), authorName, article.getDate()));
        if (article.getContent() != null && !article.getContent().isEmpty()) {
            Document doc = Jsoup.parse(article.getContent());
            for (Element element : doc.body().children()) {
                String tagName = element.tagName().toLowerCase();
                switch (tagName) {
                    case "p":
                        if (!element.text().trim().isEmpty()) { contentList.add(new ArticleParagraph(element.text())); }
                        break;
                    case "img":
                        String imageUrl = element.attr("src");
                        String caption = element.attr("alt");
                        if (!imageUrl.isEmpty()) { contentList.add(new ArticleImage(imageUrl, caption)); }
                        break;
                    default:
                        if (!element.text().trim().isEmpty()) { contentList.add(new ArticleParagraph(element.text())); }
                        break;
                }
            }
        }
        contentList.add(new CommentSection());
        contentList.add(new RelatedNewsHeader());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFontSizeSelected(String size) {
        currentFontSize = size;
        sharedPreferences.edit().putString("font_size", size).apply();
        if (adapter != null) {
            adapter.updateFontSize(size);
        }
    }
}