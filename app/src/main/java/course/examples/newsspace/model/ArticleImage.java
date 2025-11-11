package course.examples.newsspace.model;

public class ArticleImage {
    private final String imageUrl;
    private final String caption;

    public ArticleImage(String imageUrl, String caption) {
        this.imageUrl = imageUrl;
        this.caption = caption;
    }

    // Getters
    public String getImageUrl() { return imageUrl; }
    public String getCaption() { return caption; }
}