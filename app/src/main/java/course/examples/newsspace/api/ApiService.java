package course.examples.newsspace.api; // Thay bằng package của bạn

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

// Import tất cả các lớp Model, Request, Response cần thiết
import course.examples.newsspace.model.*; // Giả sử tất cả model nằm trong package này

public interface ApiService {

    // ======================================================
    // Auth Endpoints - API Xác thực
    // ======================================================

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

}