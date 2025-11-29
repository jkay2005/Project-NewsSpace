package course.examples.newsspace.api; // Thay bằng package của bạn

import java.util.List;
import java.util.Map;

import course.examples.newsspace.model.UpdateProfileRequest;
import course.examples.newsspace.model.ImageUploadResponse; // Tái sử dụng model này
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.PATCH;

// Import tất cả các lớp Model, Request, Response cần thiết
import course.examples.newsspace.model.*; // Giả sử tất cả model nằm trong package này

public interface ApiService {
    // API Xác thực

    /**
     * Đăng ký một người dùng mới.
     */
    @POST("api/auth/register")
    Call<User> registerUser(@Body RegisterRequest registerRequest);

    /**
     * Đăng nhập người dùng bằng email và mật khẩu.
     */
    @POST("api/auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    /**
     * Đăng nhập bằng Google OAuth.
     * @param idTokenBody Một Map chứa "idToken".
     */
    @POST("api/auth/oauth/google")
    Call<LoginResponse> loginWithGoogle(@Body Map<String, String> idTokenBody);


    // ======================================================
    // Articles API - API Bài viết (do hệ thống tạo)
    // ======================================================

    /**
     * Lấy danh sách tất cả các bài viết.
     */
    @GET("api/articles")
    Call<List<Article>> getArticles();

    /**
     * Tạo một bài viết mới (chức năng này có thể dành cho Admin).
     */
    @POST("api/articles")
    Call<Article> createArticle(@Body CreateArticleRequest articleRequest); // Cần tạo lớp CreateArticleRequest

    /**
     * Lấy thông tin chi tiết của một bài báo dựa trên ID.
     * @param articleId ID của bài báo cần lấy.
     */
    @GET("api/articles/{id}")
    Call<Article> getArticleDetail(@Path("id") int articleId);
    // ======================================================
    // RSS API - API Quản lý và Lấy tin RSS
    // ======================================================

    /**
     * Thêm một nguồn RSS mới (chức năng Admin).
     */
    @POST("api/rss/sources")
    Call<RssSource> createRssSource(@Body RssSource rssSource);

    /**
     * Lấy danh sách các tin tức đã được tổng hợp từ các nguồn RSS.
     */
    @GET("api/rss/items")
    Call<List<RssItem>> getRssItems();


    // ======================================================
    // Blog API - API Blog do người dùng đăng
    // ======================================================

    /**
     * Tạo một bài blog mới.
     */
    @POST("api/blogs")
    Call<Blog> createBlogPost(@Body CreateBlogRequest createBlogRequest);

    /**
     * Tạo một bình luận mới cho một bài blog.
     * @param blogId ID của bài blog cần bình luận.
     */
    @POST("api/blogs/{id}/comments")
    Call<Comment> createComment(@Path("id") int blogId, @Body CreateCommentRequest createCommentRequest);


    // ======================================================
    // Recommendations API - API Gợi ý
    // ======================================================

    /**
     * Lấy danh sách các tin tức và bài viết được gợi ý cho người dùng.
     */
    @GET("api/recommendations")
    Call<RecommendationResponse> getRecommendations();

    /**
     * Cập nhật sở thích (chủ đề) của người dùng.
     */
    @PUT("api/recommendations/preferences")
    Call<UpdatePreferencesRequest> updatePreferences(@Body UpdatePreferencesRequest preferencesRequest);

    @GET("api/recommendations/preferences")
    Call<UpdatePreferencesRequest> getUserPreferences(); // Tái sử dụng model

    @GET("api/categories")
    Call<List<Category>> getAllCategories();

    /**
     * Gửi phản hồi về một item được gợi ý.
     */
    @POST("api/recommendations/feedback")
    Call<Feedback> createFeedback(@Body FeedbackRequest feedbackRequest); // Cần tạo lớp Feedback


    // ======================================================
    // Settings API - API Cài đặt người dùng
    // ======================================================

    /**
     * Cập nhật theme (giao diện Sáng/Tối) của người dùng.
     * @param themeBody Một Map chứa "theme": "dark" hoặc "light".
     */
    @PUT("api/settings/theme")
    Call<Map<String, String>> updateTheme(@Body Map<String, String> themeBody);

    /**
     * Cập nhật cài đặt thông báo của người dùng.
     */
    @PUT("api/settings/notifications")
    Call<UpdateNotificationsRequest> updateNotifications(@Body UpdateNotificationsRequest notificationsRequest);


    // ======================================================
    // Admin API - API Quản trị
    // ======================================================

    /**
     * Lấy các số liệu thống kê chung.
     */
    @GET("api/admin/stats")
    Call<AdminStatsResponse> getAdminStats();

    /**
     * Cập nhật thông tin của một người dùng (vai trò, trạng thái email).
     * @param userId ID của người dùng cần cập nhật.
     */
    @PUT("api/admin/users/{id}")
    Call<User> updateUser(@Path("id") int userId, @Body UpdateUserRequest updateUserRequest);

    // Trong ApiService.java
    @GET("api/bookmarks")
    Call<BookmarkResponse> getBookmarks();

    @POST("api/bookmarks/collections")
    Call<Void> createBookmarkCollection(@Body Map<String, String> nameBody); // Gửi {"name": "Tên mới"}


    public class OtpRequest {
        private String email;
        private String otp;
        public OtpRequest(String email, String otp) { this.email = email; this.otp = otp; }
    }

    @POST("api/auth/verify-email") // Giả định endpoint
    Call<Void> verifyOtp(@Body VerifyOtpRequest request);

    /**
     * Yêu cầu gửi lại mã OTP.
     */
    @POST("api/auth/resend-otp") // Giả định endpoint
    Call<Void> resendOtp(@Body ResendOtpRequest request);

    @POST("auth/logout") // Hoặc đường dẫn API logout của bạn
    Call<Void> logoutUser(); // Không cần body trả về nên dùng Void

    @GET("notifications") // Endpoint để lấy danh sách thông báo
    Call<List<NotificationItem>> getNotifications();

    // Giả sử API tìm kiếm của bạn là /search?q=<từ khóa>
    @GET("search")
    Call<List<RssItem>> searchArticles(@Query("q") String query);

    @GET("blogs") // Giả sử endpoint là /blogs
    Call<List<RssItem>> getBlogs();

    /**
     * Gửi một bài viết mới lên server.
     * @Body sẽ tự động chuyển đối tượng CreatePostRequest thành JSON.
     * Call<Void> vì chúng ta không cần nhận lại dữ liệu gì đặc biệt,
     * chỉ cần biết request có thành công hay không (HTTP 200/201).
     */
    @POST("posts") // Thay "posts" bằng endpoint API thật của bạn
    Call<Void> createPost(@Body CreatePostRequest requestBody);

    /**
     * Endpoint để upload một file ảnh.
     * @Multipart cho Retrofit biết đây là một request dạng multipart.
     * @Part("image") định nghĩa một "phần" của request.
     *     - "image" là tên key mà backend sẽ dùng để lấy file.
     *     - MultipartBody.Part chứa dữ liệu nhị phân của file.
     */
    @Multipart
    @POST("upload/image") // Đây là URL endpoint ví dụ, bạn cần thay bằng URL của backend
    Call<ImageUploadResponse> uploadImage(@Part MultipartBody.Part image);

    /**
     * Gửi yêu cầu xóa tài khoản của người dùng đã được xác thực.
     * Call<Void> vì chúng ta không cần nhận lại dữ liệu, chỉ cần biết thành công hay không.
     */
    @DELETE("users/me") // Thay bằng endpoint thật của bạn
    Call<Void> deleteAccount();

    /**
     * Cập nhật trạng thái thông báo cho một chuyên mục cụ thể.
     */
    @PATCH("users/me/category-notifications") // Thay bằng endpoint thật
    Call<Void> updateCategoryNotificationSetting(@Body UpdateCategoryPrefsRequest requestBody);


    // Lấy danh sách bài blog của user
    @GET("me/posts")
    Call<List<RssItem>> getMyBlogs();

    // Lấy danh sách tin đã lưu
    @GET("me/saved-articles")
    Call<List<RssItem>> getSavedNews();

    // Lấy lịch sử xem
    @GET("me/history")
    Call<List<RssItem>> getViewHistory();

    /**
     * Lấy thông tin chi tiết của người dùng đang đăng nhập.
     * @return một đối tượng User chứa đầy đủ thông tin.
     */
    @GET("users/me") // Endpoint để lấy thông tin cá nhân
    Call<User> getMyProfile();

    /**
     * Cập nhật thông tin cá nhân của người dùng.
     * @param requestBody Đối tượng chứa các thông tin cần thay đổi.
     * @return đối tượng User với thông tin đã được cập nhật.
     */
    @PATCH("users/me") // Dùng PATCH vì chúng ta chỉ cập nhật một phần thông tin
    Call<User> updateMyProfile(@Body UpdateProfileRequest requestBody);

    /**
     * Tải lên một ảnh đại diện mới cho người dùng.
     * @param image File ảnh đã được đóng gói dưới dạng MultipartBody.Part.
     * @return một đối tượng chứa URL của ảnh mới.
     */
    @Multipart
    @POST("users/me/avatar") // Endpoint để upload avatar
    Call<ImageUploadResponse> uploadAvatar(@Part MultipartBody.Part image);
}