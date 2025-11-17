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

    private void loadArticleContent(int articleId) {
        // 1. Hiển thị trạng thái loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.articleRecyclerView.setVisibility(View.INVISIBLE); // Dùng INVISIBLE để layout không bị nhảy
        //2.gọi api
        ApiClient.getApiService(requireContext()).getArticleDetail(this.articleId).enqueue(new Callback<Article>() {
            @Override
            public void onResponse(@NonNull Call<Article> call, @NonNull Response<Article> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.articleRecyclerView.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    Article article = response.body();
                    buildDisplayList(article);
                } else {
                    // 5. API trả về lỗi (404, 500...)
                    Toast.makeText(getContext(), "Không thể tải nội dung bài báo. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Article> call, @NonNull Throwable t) {
                // 6. Lỗi mạng
                binding.progressBar.setVisibility(View.GONE);
                Log.e("ArticleDetailFragment", "API Call Failed: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi mạng. Vui lòng kiểm tra kết nối.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buildDisplayList(Article article) {
        contentList.clear();

        // Giả sử API trả về chuyên mục, nếu không, bạn có thể gán cứng
        String category = "Tin tức";
        if (article.getCategory() != null && article.getCategory().getName() != null) {
            category = article.getCategory().getName();
        }
        binding.toolbar.setTitle(category); // Cập nhật tiêu đề Toolbar

        // 1. Thêm Header
        contentList.add(new ArticleHeader(category, article.getTitle(), article.getAuthor().getName(), article.getDate()));

        // 2. Phân tích nội dung (ví dụ đơn giản)
        if (article.getContent() != null && !article.getContent().isEmpty()) {
            // TODO: Triển khai một trình phân tích HTML/Markdown tốt hơn
            String[] parts = article.getContent().split("<img_separator>");
            for (String part : parts) {
                if (part.startsWith("http")) {
                    contentList.add(new ArticleImage(part.trim(), ""));
                } else if (!part.trim().isEmpty()){
                    contentList.add(new ArticleParagraph(part.trim()));
                }
            }
        }

        // 3. Thêm phần bình luận và tin liên quan
        contentList.add(new CommentSection());
        contentList.add(new RelatedNewsHeader());
        // TODO: Thêm danh sách tin liên quan nếu API trả về

        // 4. Thông báo cho adapter
        adapter.notifyDataSetChanged();
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