package course.examples.newsspace.api;

import java.util.List;
import java.util.Map;

import course.examples.newsspace.model.UpdateProfileRequest;
import course.examples.newsspace.model.ImageUploadResponse;
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
import course.examples.newsspace.model.*;

public interface ApiService {

    // ======================================================
    // Auth API - API Xác thực
    // ======================================================

    @POST("api/auth/register")
    Call<User> registerUser(@Body RegisterRequest registerRequest);

    @POST("api/auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("api/auth/oauth/google")
    Call<LoginResponse> loginWithGoogle(@Body Map<String, String> idTokenBody);

    @POST("api/auth/logout")
    Call<Void> logout(@Body LogoutRequest logoutRequest);

    @POST("api/auth/forgot-password")
    Call<Void> forgotPassword(@Body Map<String, String> emailBody);

    @POST("api/auth/reset-password")
    Call<Void> resetPassword(@Body Map<String, String> resetPasswordBody);

    @POST("api/auth/verify-email")
    Call<Void> verifyOtp(@Body VerifyOtpRequest request);

    @POST("api/auth/resend-otp")
    Call<Void> resendOtp(@Body ResendOtpRequest request);

    // ======================================================
    // User Profile API - API Hồ sơ người dùng
    // ======================================================

    @GET("api/users/me")
    Call<User> getMyProfile();

    @PATCH("api/users/me")
    Call<User> updateMyProfile(@Body UpdateProfileRequest requestBody);

    @Multipart
    @POST("api/users/me/avatar")
    Call<ImageUploadResponse> uploadAvatar(@Part MultipartBody.Part image);

    @DELETE("api/users/me")
    Call<Void> deleteAccount();

    @PATCH("api/users/me/category-notifications")
    Call<Void> updateCategoryNotificationSetting(@Body UpdateCategoryPrefsRequest requestBody);

    @GET("api/me/posts")
    Call<List<RssItem>> getMyBlogs();

    @GET("api/me/saved-articles")
    Call<List<RssItem>> getSavedNews();

    @GET("api/me/history")
    Call<List<RssItem>> getViewHistory();

    // ======================================================
    // Articles & Blogs API - API Bài viết và Blog
    // ======================================================

    @GET("api/articles")
    Call<List<Article>> getArticles();

    @GET("api/articles")
    Call<List<Article>> getArticlesByCategory(@Query("category") String categoryApiKey);

    @GET("api/articles/topic/{topic}")
    Call<List<Article>> getArticlesByTopic(@Path("topic") String topic);

    @GET("api/articles/{id}")
    Call<Article> getArticleDetail(@Path("id") int articleId);
    
    @POST("api/articles")
    Call<Article> createArticle(@Body CreateArticleRequest articleRequest); // Admin or specific roles

    @GET("api/blogs")
    Call<List<RssItem>> getBlogs();

    @POST("api/blogs")
    Call<Blog> createBlogPost(@Body CreateBlogRequest createBlogRequest);

    @POST("api/blogs/{id}/comments")
    Call<Comment> createComment(@Path("id") int blogId, @Body CreateCommentRequest createCommentRequest);

    // ======================================================
    // RSS API - API Quản lý và Lấy tin RSS
    // ======================================================

    @POST("api/rss/sources")
    Call<RssSource> createRssSource(@Body RssSource rssSource);

    @GET("api/rss/items")
    Call<List<RssItem>> getRssItems();

    // ======================================================
    // Recommendations API - API Gợi ý
    // ======================================================

    @GET("api/recommendations")
    Call<RecommendationResponse> getRecommendations();

    @GET("api/recommendations/preferences")
    Call<UpdatePreferencesRequest> getUserPreferences();

    @PUT("api/recommendations/preferences")
    Call<UpdatePreferencesRequest> updatePreferences(@Body UpdatePreferencesRequest preferencesRequest);

    @POST("api/recommendations/feedback")
    Call<Feedback> createFeedback(@Body FeedbackRequest feedbackRequest);

    // ======================================================
    // Bookmarks API - API Đánh dấu
    // ======================================================
    
    @GET("api/bookmarks")
    Call<BookmarkResponse> getBookmarks();

    @POST("api/bookmarks/collections")
    Call<Void> createBookmarkCollection(@Body Map<String, String> nameBody);

    // ======================================================
    // General & Misc API - API Chung & Khác
    // ======================================================

    @GET("api/categories")
    Call<List<Category>> getAllCategories();

    @GET("api/notifications")
    Call<List<NotificationItem>> getNotifications();

    @GET("api/search")
    Call<List<RssItem>> searchArticles(@Query("q") String query);

    @POST("api/posts")
    Call<Void> createPost(@Body CreatePostRequest requestBody);

    @Multipart
    @POST("api/upload/image")
    Call<ImageUploadResponse> uploadImage(@Part MultipartBody.Part image);

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
}
