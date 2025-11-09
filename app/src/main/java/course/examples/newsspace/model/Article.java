package course.examples.newsspace.model;

import com.google.gson.annotations.SerializedName;

/**
 * Lớp Model đa năng, có thể biểu diễn cho một bài báo (Article),
 * một tin tức từ RSS (RssItem), hoặc một bài blog.
 * Lớp này được thiết kế để linh hoạt cho việc hiển thị trên giao diện.
 */
public class Article {

    // Các trường dữ liệu chính, ánh xạ từ JSON
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content; // Nội dung chi tiết, có thể chứa HTML

    @SerializedName("author")
    private Author author;

    @SerializedName("createdAt") // Dùng cho Article từ API
    private String createdAt;

    @SerializedName("publishedAt") // Dùng cho RssItem từ API
    private String publishedAt;

    // Trường bổ sung, không có trong JSON, dùng để điều khiển giao diện
    private boolean isFeatured;
    private String imageUrl; // Trường để lưu URL ảnh đã được xử lý


    // --- CONSTRUCTORS ---

    // Constructor rỗng - Rất hữu ích cho Gson và các thư viện khác
    public Article() {} // Constructor rỗng cho Gson

// --- STATIC FACTORY METHODS ---

    public static Article createFeaturedArticle(String title, String description, String imageUrl) {
        Article article = new Article();
        article.title = title;
        article.content = description;
        article.imageUrl = imageUrl;
        article.isFeatured = true;
        article.publishedAt = ""; // Gán mặc định
        return article;
    }

    public static Article createStandardArticle(String title, String date, String imageUrl) {
        Article article = new Article();
        article.title = title;
        article.publishedAt = date;
        article.imageUrl = imageUrl;
        article.isFeatured = false;
        article.content = ""; // Gán mặc định
        return article;
    }

    // --- GETTERS (Phần quan trọng để sửa lỗi) ---

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    /**
     * Trả về mô tả ngắn. Đối với thẻ featured, chúng ta dùng trường 'content' để lưu nó.
     */
    public String getDescription() {
        // Nếu content rỗng, trả về chuỗi rỗng thay vì null để tránh lỗi
        return content != null ? content : "";
    }

    /**
     * Trả về ngày đăng. Ưu tiên 'createdAt', nếu không có thì dùng 'publishedAt'.
     */
    public String getDate() {
        if (createdAt != null) {
            // TODO: Định dạng lại chuỗi ngày tháng cho đẹp hơn
            return createdAt.substring(0, 10); // Lấy YYYY-MM-DD
        }
        if (publishedAt != null) {
            return publishedAt;
        }
        return ""; // Trả về chuỗi rỗng nếu không có ngày nào
    }

    /**
     * Trả về URL của hình ảnh.
     */
    public String getImageUrl() {
        // Nếu trường imageUrl đã được gán (từ constructor), dùng nó.
        if (imageUrl != null) {
            return imageUrl;
        }
        // Nếu không, thử phân tích từ 'content' (logic này có thể được thêm vào sau)
        // ...
        // Trả về một ảnh mẫu nếu không có gì cả
        return "https://picsum.photos/400/200";
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public Author getAuthor() {
        return author;
    }
}