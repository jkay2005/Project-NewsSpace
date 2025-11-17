package course.examples.newsspace.model;

public class ArticleHeader {
    private final String category;
    private final String title;
    private final String authorName;
    private final String date;

    public ArticleHeader(String category, String title, String authorName, String date) {
        this.category = category;
        this.title = title;
        this.authorName = authorName;
        this.date = date;
    }

    // Getters
    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public String getAuthorName() { return authorName; }
    public String getDate() { return date; }
}