package course.examples.newsspace;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import java.util.List;
import course.examples.newsspace.api.ApiClient;
import course.examples.newsspace.model.CreatePostRequest;
import course.examples.newsspace.databinding.ActivityCreatePostBinding;
import course.examples.newsspace.model.BlogContentBlock;
import course.examples.newsspace.model.ImageUploadResponse;
import course.examples.newsspace.utils.FileUtils;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class CreatePostActivity extends AppCompatActivity {
    // --- Biến thành viên ---
    private ActivityCreatePostBinding binding;
    private CreatePostAdapter adapter;
    private List<Object> contentList = new ArrayList<>();
    private boolean isToolbarOpen = false;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

// =========================================================================================
// Vòng đời Activity (Lifecycle)
// =========================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Gọi các phương thức thiết lập ban đầu
        setupToolbar();
        setupRecyclerView();
        setupFabToolbar();
        setupImagePicker();

        // Chỉ khởi tạo nội dung nếu Activity được tạo lần đầu, tránh mất dữ liệu khi xoay màn hình
        if (savedInstanceState == null) {
            initializeContent();
        }
    }

// =========================================================================================
// Thiết lập Giao diện (UI Setup)
// =========================================================================================

    /**
     * Thiết lập Toolbar chính, bao gồm nút quay lại và nút "Đăng".
     */
    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.postButton.setOnClickListener(v -> handlePostSubmission());
    }

    /**
     * Khởi tạo RecyclerView và Adapter, đồng thời định nghĩa logic cho các sự kiện thêm/xóa.
     */
    private void setupRecyclerView() {
        adapter = new CreatePostAdapter(contentList, new AdapterListener() {
            @Override
            public void onAddBlock(int position) {
                // Thêm một cặp Đề mục + Nội dung mới ngay sau khối hiện tại
                int insertPosition = position + 2;
                contentList.add(insertPosition, new BlogContentBlock(BlogContentBlock.BlockType.SUBTITLE, ""));
                contentList.add(insertPosition + 1, new BlogContentBlock(BlogContentBlock.BlockType.PARAGRAPH, ""));
                adapter.notifyItemRangeInserted(insertPosition, 2);
                adapter.notifyItemChanged(position); // Cập nhật item cũ để có thể ẩn/hiện nút xóa
            }

            @Override
            public void onRemoveBlock(int position) {
                // Xóa cặp Đề mục + Nội dung
                if (contentList.size() > 3 && position > 0) { // Đảm bảo không xóa khối đầu tiên
                    contentList.remove(position); // Xóa subtitle
                    contentList.remove(position); // Vị trí paragraph đã dịch lên, nên vẫn là `position`
                    adapter.notifyItemRangeRemoved(position, 2);

                    // Cập nhật lại item phía trước nó để hiện lại nút xóa nếu cần
                    if (position > 1) {
                        adapter.notifyItemChanged(position - 2);
                    }
                }
            }
        });

        binding.contentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.contentRecyclerView.setAdapter(adapter);
    }

    /**
     * Thiết lập logic cho thanh công cụ nổi (Floating Action Buttons).
     */
    private void setupFabToolbar() {
        binding.fabWrench.setOnClickListener(v -> {
            if (isToolbarOpen) {
                hideToolbar();
            } else {
                showToolbar();
            }
        });

        binding.fabImage.setOnClickListener(v -> {
            // Mở thư viện ảnh để người dùng chọn
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
            hideToolbar(); // Ẩn thanh công cụ sau khi chọn
        });

        // TODO: Thêm logic cho nút định dạng chữ (fabStyle) và căn lề (fabAlign)
    }

    /**
     * Khởi tạo nội dung ban đầu cho trình soạn thảo (1 tiêu đề, 1 đề mục, 1 nội dung).
     */
    private void initializeContent() {
        contentList.clear();
        contentList.add(""); // Item 0: String rỗng cho Tiêu đề
        contentList.add(new BlogContentBlock(BlogContentBlock.BlockType.SUBTITLE, ""));
        contentList.add(new BlogContentBlock(BlogContentBlock.BlockType.PARAGRAPH, ""));
        adapter.notifyDataSetChanged();
    }

// =========================================================================================
// Xử lý Ảnh (Image Handling)
// =========================================================================================

    /**
     * Thiết lập ActivityResultLauncher để nhận kết quả trả về từ thư viện ảnh.
     */
    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            // Bắt đầu quá trình tải ảnh lên server
                            uploadImageToServer(imageUri);
                        }
                    }
                }
        );
    }

    /**
     * Thực hiện gọi API để tải file ảnh lên server.
     * @param imageUri URI của ảnh người dùng đã chọn.
     */
    private void uploadImageToServer(Uri imageUri) {
        // TODO: Hiển thị một ProgressBar hoặc loading dialog chuyên nghiệp hơn
        Toast.makeText(this, "Đang tải ảnh lên...", Toast.LENGTH_SHORT).show();

        MultipartBody.Part imagePart = FileUtils.uriToMultipartBodyPart(this, imageUri, "image");
        if (imagePart == null) {
            Toast.makeText(this, "Không thể xử lý file ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi API để upload ảnh
        ApiClient.getApiService(this).uploadImage(imagePart).enqueue(new Callback<ImageUploadResponse>() {
            @Override
            public void onResponse(@NonNull Call<ImageUploadResponse> call, @NonNull Response<ImageUploadResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Upload thành công, lấy URL từ server
                    String imageUrlFromServer = response.body().getImageUrl();

                    // Thêm khối ảnh vào RecyclerView với URL thật từ server
                    contentList.add(new BlogContentBlock(BlogContentBlock.BlockType.IMAGE, imageUrlFromServer));
                    adapter.notifyItemInserted(contentList.size() - 1);
                    binding.contentRecyclerView.smoothScrollToPosition(contentList.size() - 1);
                } else {
                    Toast.makeText(CreatePostActivity.this, "Tải ảnh lên thất bại. Mã lỗi: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ImageUploadResponse> call, @NonNull Throwable t) {
                Toast.makeText(CreatePostActivity.this, "Lỗi mạng khi tải ảnh: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


// =========================================================================================
// Logic Gửi bài viết (Submission Logic)
// =========================================================================================

    /**
     * Thu thập dữ liệu, tạo request và gọi API để đăng bài.
     */
    private void handlePostSubmission() {
        // 1. Lấy và kiểm tra tiêu đề
        String title = ((String) contentList.get(0)).trim();
        if (title.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề bài viết", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Tách các khối nội dung ra khỏi danh sách chính
        List<BlogContentBlock> blocks = new ArrayList<>();
        for (int i = 1; i < contentList.size(); i++) {
            // Đảm bảo chỉ thêm các đối tượng BlogContentBlock
            if (contentList.get(i) instanceof BlogContentBlock) {
                blocks.add((BlogContentBlock) contentList.get(i));
            }
        }

        if (blocks.isEmpty()) {
            Toast.makeText(this, "Vui lòng thêm nội dung cho bài viết", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hiển thị trạng thái loading
        showLoading(true);

        // 3. Tạo đối tượng request body
        CreatePostRequest requestBody = new CreatePostRequest(title, blocks);

        // 4. Gọi API
        ApiClient.getApiService(this).createPost(requestBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                showLoading(false); // Ẩn loading dù thành công hay thất bại

                if (response.isSuccessful()) {
                    // Đăng bài thành công!
                    Toast.makeText(CreatePostActivity.this, "Đăng bài viết thành công!", Toast.LENGTH_LONG).show();
                    // Quay về màn hình trước đó
                    finish();
                } else {
                    // Server báo lỗi (ví dụ: nội dung không hợp lệ, lỗi xác thực, ...)
                    Toast.makeText(CreatePostActivity.this, "Đã xảy ra lỗi: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                showLoading(false); // Ẩn loading
                // Lỗi mạng hoặc lỗi kết nối
                Log.e("CreatePostAPI", "API call failed: ", t);
                Toast.makeText(CreatePostActivity.this, "Lỗi mạng, vui lòng thử lại.", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Hàm helper để quản lý trạng thái loading.
     * @param isLoading true để hiển thị ProgressBar, false để ẩn.
     */
    private void showLoading(boolean isLoading) {
        binding.loadingProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        // Vô hiệu hóa các nút để người dùng không thao tác khi đang gửi
        binding.postButton.setEnabled(!isLoading);
        binding.toolbar.setNavigationIcon(isLoading ? null : getDrawable(R.drawable.ic_arrow_back)); // Thay ic_arrow_back bằng icon của bạn
    }

// =========================================================================================
// Hiệu ứng & Animation
// =========================================================================================

    private void showToolbar() {
        isToolbarOpen = true;
        binding.fabWrench.animate().rotation(45f).setDuration(200);
        showFab(binding.fabImage, 0);
        showFab(binding.fabAlign, 50);
        showFab(binding.fabStyle, 100);
    }

    private void hideToolbar() {
        isToolbarOpen = false;
        binding.fabWrench.animate().rotation(0f).setDuration(200);
        hideFab(binding.fabStyle, 0);
        hideFab(binding.fabAlign, 50);
        hideFab(binding.fabImage, 100);
    }

    private void showFab(View fab, long startDelay) {
        fab.setVisibility(View.VISIBLE);
        fab.setAlpha(0f);
        fab.setScaleX(0f);
        fab.setScaleY(0f);
        fab.animate().alpha(1f).scaleX(1f).scaleY(1f)
                .setDuration(200).setStartDelay(startDelay).setListener(null);
    }

    private void hideFab(View fab, long startDelay) {
        fab.animate().alpha(0f).scaleX(0f).scaleY(0f)
                .setDuration(200).setStartDelay(startDelay)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fab.setVisibility(View.INVISIBLE);
                    }
                });
    }}