// File: /app/src/main/java/course/examples/newsspace/model/gnews/GNewsArticle.java
package course.examples.newsspace.model.gnews;

import com.google.gson.annotations.SerializedName;

public class GNewsArticle {
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("content")
    private String content;
    @SerializedName("url")
    private String url;
    @SerializedName("image")
    private String image;
    @SerializedName("publishedAt")
    private String publishedAt;
    @SerializedName("source")
    private GNewsSource source;

    // Getters...
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getContent() { return content; }
    public String getUrl() { return url; }
    public String getImage() { return image; }
    public String getPublishedAt() { return publishedAt; }
    public GNewsSource getSource() { return source; }
}
        