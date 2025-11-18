// File: /app/src/main/java/course/examples/newsspace/model/BookmarkRequest.java
package course.examples.newsspace.model;

public class BookmarkRequest {
    private String title;
    private String url;
    private String imageUrl;
    private String publishedAt;

    public BookmarkRequest(String title, String url, String imageUrl, String publishedAt) {
        this.title = title;
        this.url = url;
        this.imageUrl = imageUrl;
        this.publishedAt = publishedAt;
    }
}
    