package course.examples.newsspace;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import course.examples.newsspace.databinding.FragmentArticleDetailBinding;
public class ArticleDetailFragment extends Fragment {
    private FragmentArticleDetailBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentArticleDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Lấy URL từ arguments bằng Safe Args với tên đã sửa
        String articleUrl = ArticleDetailFragmentArgs.fromBundle(getArguments()).getArticleUrl();

        setupToolbar();
        setupWebView(articleUrl);
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v ->
                // Điều hướng quay lại Fragment trước đó
                NavHostFragment.findNavController(this).navigateUp()
        );
    }

    private void setupWebView(String url) {
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                binding.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                binding.progressBar.setVisibility(View.GONE);
                // Lấy tiêu đề trang web và đặt làm tiêu đề Toolbar
                binding.toolbar.setTitle(view.getTitle());
            }
        });
        binding.webView.loadUrl(url);
    }}