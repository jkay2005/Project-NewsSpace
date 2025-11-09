package course.examples.newsspace;


public class Article {
    private String title;
    private String description;
    private String imageUrl;
    private String date; // Thêm trường date
    private boolean isFeatured; // Cờ để xác định đây là tin nổi bật (thẻ to) hay tin thường

    // Constructor cho tin nổi bật (có description)
    // Constructor cho tin thường (chỉ có date)
    public Article(String title, String date, String imageUrl, boolean isFeatured) {
        this.title = title;
        this.description =(isFeatured )? description:  ""; // Gán rỗng
        this.date = date;
        this.imageUrl = imageUrl;
        this.isFeatured = isFeatured;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getDate() { return date; }
    public boolean isFeatured() { return isFeatured; }
    // Các getter khác...
}
