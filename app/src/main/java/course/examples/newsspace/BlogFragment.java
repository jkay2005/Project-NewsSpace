package course.examples.newsspace;import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import java.util.List;
import course.examples.newsspace.api.ApiClient;
import course.examples.newsspace.databinding.FragmentBlogBinding;
import course.examples.newsspace.model.Article;
import course.examples.newsspace.model.RssItem; //Giả sử API blog trả về RssItem
import retrofit2.Call; import retrofit2.Callback;
import retrofit2.Response;public class BlogFragment extends Fragment {private FragmentBlogBinding binding;
private ArticleListAdapter adapter; // Tái sử dụng ArticleListAdapter
private final List<Article> blogList = new ArrayList<>();

@Nullable
@Override
public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    binding = FragmentBlogBinding.inflate(inflater, container, false);
    return binding.getRoot();
}

@Override
public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupRecyclerView();
    loadBlogs();
}

private void setupRecyclerView() {
    adapter = new ArticleListAdapter(blogList);
    binding.blogRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    binding.blogRecyclerView.setAdapter(adapter);
}

private void loadBlogs() {
    binding.progressBar.setVisibility(View.VISIBLE);
    // TODO: Thay "getBlogs" bằng endpoint API thực tế của bạn
    ApiClient.getApiService(requireContext()).getBlogs().enqueue(new Callback<List<RssItem>>() {
        @Override
        public void onResponse(@NonNull Call<List<RssItem>> call, @NonNull Response<List<RssItem>> response) {
            binding.progressBar.setVisibility(View.GONE);
            if (response.isSuccessful() && response.body() != null) {
                blogList.clear();
                for (RssItem item : response.body()) {
                    blogList.add(Article.createStandardArticle(
                            item.getTitle(),
                            item.getPublishedAt(),
                            item.getImageUrl()
                    ));
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Không thể tải danh sách blog", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(@NonNull Call<List<RssItem>> call, @NonNull Throwable t) {
            binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
        }
    });
}}