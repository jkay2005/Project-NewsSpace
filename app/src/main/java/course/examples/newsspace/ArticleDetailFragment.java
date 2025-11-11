package course.examples.newsspace; // Thay bằng package của bạn

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

// Import lớp ViewBinding tương ứng với file layout
import course.examples.newsspace.databinding.FragmentArticleDetailBinding;
import course.examples.newsspace.databinding.ItemArticleHeaderBinding;
import course.examples.newsspace.model.Article;
import course.examples.newsspace.model.ArticleHeader;
import course.examples.newsspace.model.ArticleImage;
import course.examples.newsspace.model.ArticleParagraph;
import course.examples.newsspace.model.CommentSection;
import course.examples.newsspace.model.RelatedNewsHeader;
import course.examples.newsspace.api.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Implement các interface để lắng nghe kết quả từ các BottomSheet
public class ArticleDetailFragment extends Fragment
        implements FontSizePickerBottomSheet.FontSizeChangeListener,
        SaveToCollectionBottomSheet.OnCollectionSelectedListener {

    private FragmentArticleDetailBinding binding; // Sửa lỗi 'binding'
    private ArticleDetailAdapter adapter; // Sửa lỗi 'ArticleDetailAdapter'
    private final List<Object> contentList = new ArrayList<>();

    private String currentFontSize;
    private SharedPreferences sharedPreferences;
    private int articleId = -1;

    // --- CÁC PHƯƠNG THỨC VÒNG ĐỜI CỦA FRAGMENT ---

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        currentFontSize = sharedPreferences.getString("font_size", "medium");
        if (getArguments() != null) {
            articleId = (int) ArticleDetailFragmentArgs.fromBundle(getArguments()).getArticleId();
        }
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
            loadArticleContent();
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


    // --- CÁC HÀM SETUP (SỬA LẠI ĐỂ DÙNG BINDING) ---

    private void setupToolbar() {
        // 4. SỬ DỤNG BINDING ĐỂ TRUY CẬP TOOLBAR
        binding.toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
    }

    private void setupRecyclerView() {
        adapter = new ArticleDetailAdapter(contentList, this::setupHeaderClickListeners, currentFontSize);
        // 5. SỬ DỤNG BINDING ĐỂ TRUY CẬP RECYCLERVIEW
        binding.articleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.articleRecyclerView.setAdapter(adapter);
    }


    private void setupHeaderClickListeners(ArticleDetailAdapter.HeaderViewHolder holder) {
        holder.binding.fontSizeImageView.setOnClickListener(v -> {
            FontSizePickerBottomSheet bottomSheet = FontSizePickerBottomSheet.newInstance(currentFontSize);
            bottomSheet.setFontSizeChangeListener(this);
            bottomSheet.show(getParentFragmentManager(), "FontSizePicker");
        });
        holder.binding.bookmarkImageView.setOnClickListener(v -> {
            SaveToCollectionBottomSheet bottomSheet = new SaveToCollectionBottomSheet();
            bottomSheet.setOnCollectionSelectedListener(this); // Sửa lỗi 'setOnCollectionSelectedListener'
            bottomSheet.show(getParentFragmentManager(), "SaveToCollection");
        });
    }


    // --- CÁC PHƯƠNG THỨC XỬ LÝ DỮ LIỆU VÀ API ---

    private void loadArticleContent() {
        ApiClient.getApiService(requireContext()).getArticleDetail(articleId).enqueue(new Callback<Article>() {
            @Override
            public void onResponse(@NonNull Call<Article> call, @NonNull Response<Article> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Article article = response.body();
                    buildDisplayList(article);
                }
            }
            @Override
            public void onFailure(@NonNull Call<Article> call, @NonNull Throwable t) {
                Log.e("ArticleDetailFragment", "API Call Failed: " + t.getMessage());
            }
        });
    }

    private void buildDisplayList(Article article) {
        contentList.clear();
        String category = "Tin tức";
        if (article.getCategory() != null) { // Sửa lỗi 'getCategory'
            category = article.getCategory().getName();
        }
        binding.toolbar.setTitle("Some Category");
        contentList.add(new ArticleHeader(category, article.getTitle(), article.getAuthor().getName(), article.getDate())); // Sửa lỗi 'getName'
        if (article.getContent() != null) { // Sửa lỗi 'getContent'
            contentList.add(new ArticleParagraph(article.getContent()));
        }
        contentList.add(new CommentSection());
        contentList.add(new RelatedNewsHeader());
        adapter.notifyDataSetChanged(); // Sửa lỗi 'notifyDataSetChanged'
    }

    // --- IMPLEMENT CÁC PHƯƠNG THỨC CALLBACK ---

    @Override
    public void onFontSizeSelected(String size) {
        currentFontSize = size;
        sharedPreferences.edit().putString("font_size", size).apply();
        if (adapter != null) {
            adapter.updateFontSize(size); // Sửa lỗi 'updateFontSize'
        }
    }

    @Override
    public void onCollectionSelected(String collectionName) { // Sửa lỗi 'OnCollectionSelectedListener'
        Toast.makeText(getContext(), "Đã lưu vào '" + collectionName + "'", Toast.LENGTH_SHORT).show();
    }
}