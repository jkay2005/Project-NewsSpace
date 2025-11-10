package course.examples.newsspace;

public class Article {
    // Fields based on database schema and existing code
    private String id;
    private String title;
    private String description; // Mapped from 'content' in the database
    private String imageUrl;
    private String date; // Mapped from 'createdAt' in the database
    private String authorId;
    private boolean isFeatured;

    /**
     * Constructor cho tin nổi bật (có description).
     * isFeatured sẽ được tự động đặt thành true.
     */
    public Article(String id, String title, String description, String imageUrl, String date, String authorId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.date = date;
        this.authorId = authorId;
        this.isFeatured = true;
    }

    /**
     * Constructor cho tin thường (không có description).
     * isFeatured sẽ được tự động đặt thành false và description là chuỗi rỗng.
     */
    public Article(String id, String title, String imageUrl, String date, String authorId) {
        this.id = id;
        this.title = title;
        this.description = ""; // Regular articles have empty description
        this.imageUrl = imageUrl;
        this.date = date;
        this.authorId = authorId;
        this.isFeatured = false;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getDate() { return date; }
    public String getAuthorId() { return authorId; }
    public boolean isFeatured() { return isFeatured; }
}
