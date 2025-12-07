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

    @POST("api/auth/register")
    Call<User> registerUser(@Body RegisterRequest registerRequest);

    @POST("api/auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("api/auth/oauth/google")
    Call<LoginResponse> loginWithGoogle(@Body Map<String, String> idTokenBody);
    
    @POST("api/auth/logout")
    Call<Void> logout(@Body LogoutRequest logoutRequest);


    // ======================================================
    // Articles API - API Bài viết (do hệ thống tạo)
    // ======================================================

    @GET("api/articles")
    Call<List<Article>> getArticles();

    /**
     * Lấy danh sách bài viết theo một chủ đề cụ thể (topic).
     * @param topic Slug của chủ đề (ví dụ: "the-thao", "cong-nghe").
     */
    @GET("api/articles/topic/{topic}")
    Call<List<Article>> getArticlesByTopic(@Path("topic") String topic);

    @POST("api/articles")
    Call<Article> createArticle(@Body CreateArticleRequest articleRequest);

    @GET("api/articles/{id}")
    Call<Article> getArticleDetail(@Path("id") int articleId);
    
    // ======================================================
    // RSS API - API Quản lý và Lấy tin RSS
    // ======================================================

    @POST("api/rss/sources")
    Call<RssSource> createRssSource(@Body RssSource rssSource);

    @GET("api/rss/items")
    Call<List<RssItem>> getRssItems();


    // ======================================================
    // Blog API - API Blog do người dùng đăng
    // ======================================================

    @POST("api/blogs")
    Call<Blog> createBlogPost(@Body CreateBlogRequest createBlogRequest);

    @POST("api/blogs/{id}/comments")
    Call<Comment> createComment(@Path("id") int blogId, @Body CreateCommentRequest createCommentRequest);


    // ======================================================
    // Recommendations API - API Gợi ý
    // ======================================================

    @GET("api/recommendations")
    Call<RecommendationResponse> getRecommendations();

    @PUT("api/recommendations/preferences")
    Call<UpdatePreferencesRequest> updatePreferences(@Body UpdatePreferencesRequest preferencesRequest);

    @GET("api/recommendations/preferences")
    Call<UpdatePreferencesRequest> getUserPreferences();

    @GET("api/categories")
    Call<List<Category>> getAllCategories();

    @POST("api/recommendations/feedback")
    Call<Feedback> createFeedback(@Body FeedbackRequest feedbackRequest);


    // ======================================================
    // Settings API - API Cài đặt người dùng
    // ======================================================

    @PUT("api/settings/theme")
    Call<Map<String, String>> updateTheme(@Body Map<String, String> themeBody);

    @PUT("api/settings/notifications")
    Call<UpdateNotificationsRequest> updateNotifications(@Body UpdateNotificationsRequest notificationsRequest);


    // ======================================================
    // Admin API - API Quản trị
    // ======================================================

    @GET("api/admin/stats")
    Call<AdminStatsResponse> getAdminStats();

    @PUT("api/admin/users/{id}")
    Call<User> updateUser(@Path("id") int userId, @Body UpdateUserRequest updateUserRequest);

    @GET("api/bookmarks")
    Call<BookmarkResponse> getBookmarks();

    @POST("api/bookmarks/collections")
    Call<Void> createBookmarkCollection(@Body Map<String, String> nameBody);


    public class OtpRequest {
        private String email;
        private String otp;
        public OtpRequest(String email, String otp) { this.email = email; this.otp = otp; }
    }

    @POST("api/auth/verify-email")
    Call<Void> verifyOtp(@Body VerifyOtpRequest request);

    @POST("api/auth/resend-otp")
    Call<Void> resendOtp(@Body ResendOtpRequest request);

    @GET("notifications")
    Call<List<NotificationItem>> getNotifications();

    @GET("search")
    Call<List<RssItem>> searchArticles(@Query("q") String query);

    @GET("blogs")
    Call<List<RssItem>> getBlogs();

    @POST("posts")
    Call<Void> createPost(@Body CreatePostRequest requestBody);

    @Multipart
    @POST("upload/image")
    Call<ImageUploadResponse> uploadImage(@Part MultipartBody.Part image);

    @DELETE("users/me")
    Call<Void> deleteAccount();

    @PATCH("users/me/category-notifications")
    Call<Void> updateCategoryNotificationSetting(@Body UpdateCategoryPrefsRequest requestBody);

    @GET("me/posts")
    Call<List<RssItem>> getMyBlogs();

    @GET("me/saved-articles")
    Call<List<RssItem>> getSavedNews();

    @GET("me/history")
    Call<List<RssItem>> getViewHistory();

    @GET("users/me")
    Call<User> getMyProfile();

    @PATCH("users/me")
    Call<User> updateMyProfile(@Body UpdateProfileRequest requestBody);

    @Multipart
    @POST("users/me/avatar")
    Call<ImageUploadResponse> uploadAvatar(@Part MultipartBody.Part image);
}