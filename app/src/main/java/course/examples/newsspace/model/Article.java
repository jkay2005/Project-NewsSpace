package course.examples.newsspace.model;

import com.google.gson.annotations.SerializedName;

/**
 * Lớp Model đa năng, đại diện cho một bài báo.
 * Chứa các trường được ánh xạ từ JSON response của API và các trường/phương thức
 * bổ sung để hỗ trợ hiển thị trên giao diện.
 */
public class Article {

    // --- CÁC TRƯỜNG DỮ LIỆU ÁNH XẠ TỪ API ---

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("authorId")
    private int authorId;

    @SerializedName("author")
    private Author author;

    @SerializedName("createdAt")
    private String createdAt;

    // --- CÁC TRƯỜNG BỔ SUNG CHO GIAO DIỆN (KHÔNG CÓ TRONG JSON) ---

    private boolean isFeatured;
    private String imageUrl;
    private String date; // Trường date đã được xử lý
    private String description; // Trường description đã được xử lý

    // --- CONSTRUCTORS (Dùng để tạo đối tượng từ code, không phải từ Gson) ---

    // Constructor rỗng - Bắt buộc phải có để Gson hoạt động chính xác
    public Article() {}

    public static Article createFeaturedArticle(String title, String description, String imageUrl) {
        Article article = new Article();
        article.title = title;
        article.description = description;
        article.imageUrl = imageUrl;
        article.isFeatured = true;
        return article;
    }

    public static Article createStandardArticle(String title, String date, String imageUrl) {
        Article article = new Article();
        article.title = title;
        article.date = date;
        article.imageUrl = imageUrl;
        article.isFeatured = false;
        return article;
    }

    // --- GETTERS (PHẦN QUAN TRỌNG NHẤT ĐỂ SỬA LỖI) ---

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getAuthorId() {
        return authorId;
    }

    public Author getAuthor() {
        return author;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    // --- CÁC GETTERS TÙY CHỈNH CHO GIAO DIỆN ---

    public boolean isFeatured() {
        return isFeatured;
    }

    public String getImageUrl() {
        // Ưu tiên URL đã được gán sẵn
        if (imageUrl != null && !imageUrl.isEmpty()) {
            return imageUrl;
        }
        // TODO: Thêm logic trích xuất ảnh từ 'content' nếu cần
        return "https://picsum.photos/400/200"; // Trả về ảnh mẫu nếu không có
    }

    public String getDate() {
        // Ưu tiên ngày đã được gán sẵn
        if (date != null && !date.isEmpty()) {
            return date;
        }
        // Nếu không, lấy từ 'createdAt' và định dạng lại
        if (createdAt != null && createdAt.length() >= 10) {
            return createdAt.substring(0, 10); // Lấy phần YYYY-MM-DD
        }
        return ""; // Trả về rỗng nếu không có
    }

    public String getDescription() {
        // Ưu tiên mô tả đã được gán sẵn
        if (description != null && !description.isEmpty()) {
            return description;
        }
        // Nếu không, có thể lấy một đoạn ngắn từ 'content'
        if (content != null && content.length() > 150) {
            return content.substring(0, 150) + "...";
        }
        return content != null ? content : "";
    }

    // (Tùy chọn) Thêm Getter cho Category, nếu API trả về
    // Ví dụ, nếu API trả về một đối tượng Category
     @SerializedName("category")
    private Category category;
    public Category getCategory() { return category; }
}