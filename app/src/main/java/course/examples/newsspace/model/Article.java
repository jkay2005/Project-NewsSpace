package course.examples.newsspace.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

/**
 * Lớp Model đa năng, đại diện cho một bài báo.
 * Giờ đây cũng là một Room Entity để lưu trữ các bài báo đã đánh dấu.
 */
@Entity(tableName = "bookmarked_articles") // Đánh dấu lớp này là một bảng trong cơ sở dữ liệu
public class Article {

    // --- CÁC TRƯỜNG DỮ LIỆU SẼ ĐƯỢC LƯU TRONG CSDL ---

    @PrimaryKey // Đánh dấu 'id' là khóa chính
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("authorId")
    private int authorId;

    @SerializedName("createdAt")
    private String createdAt;

    // --- CÁC TRƯỜNG BỔ SUNG CHO GIAO DIỆN (KHÔNG CÓ TRONG JSON) ---
    // Room cũng sẽ lưu các trường này

    private boolean isFeatured;
    private String imageUrl;
    private String date; // Trường date đã được xử lý
    private String description; // Trường description đã được xử lý
    private boolean isBookmarked; // Trường mới để theo dõi trạng thái bookmark

    // --- CÁC TRƯỜNG PHỨC TẠP SẼ BỊ BỎ QUA (KHÔNG LƯU VÀO CSDL) ---

    @Ignore // Yêu cầu Room bỏ qua trường này
    @SerializedName("author")
    private Author author;

    @Ignore // Yêu cầu Room bỏ qua trường này
    @SerializedName("category")
    private Category category;

    // --- CONSTRUCTORS ---

    // Constructor rỗng - Bắt buộc cho cả Gson và Room
    public Article() {}

    // Các factory method tĩnh vẫn hoạt động bình thường
    public static Article createFeaturedArticle(int id, String title, String description, String imageUrl) {
        Article article = new Article();
        article.id = id;
        article.title = title;
        article.description = description;
        article.imageUrl = imageUrl;
        article.isFeatured = true;
        return article;
    }

    public static Article createStandardArticle(int id, String title, String date, String imageUrl) {
        Article article = new Article();
        article.id = id;
        article.title = title;
        article.date = date;
        article.imageUrl = imageUrl;
        article.isFeatured = false;
        return article;
    }

    // --- GETTERS AND SETTERS ---
    // Room cần các getters và setters để truy cập các trường

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getAuthorId() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public boolean isFeatured() { return isFeatured; }
    public void setFeatured(boolean featured) { isFeatured = featured; }

    public String getImageUrl() {
        if (imageUrl != null && !imageUrl.isEmpty()) { return imageUrl; }
        // TODO: Thêm logic trích xuất ảnh từ 'content' nếu cần
        return "https://picsum.photos/400/200";
    }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDate() {
        if (date != null && !date.isEmpty()) { return date; }
        if (createdAt != null && createdAt.length() >= 10) { return createdAt.substring(0, 10); }
        return "";
    }
    public void setDate(String date) { this.date = date; }

    public String getDescription() {
        if (description != null && !description.isEmpty()) { return description; }
        if (content != null && content.length() > 150) { return content.substring(0, 150) + "..."; }
        return content != null ? content : "";
    }
    public void setDescription(String description) { this.description = description; }

    public boolean isBookmarked() { return isBookmarked; }
    public void setBookmarked(boolean bookmarked) { isBookmarked = bookmarked; }

    // --- Getters and Setters cho các trường bị @Ignore ---
    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}