package course.examples.newsspace.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Lớp này đại diện cho đối tượng JSON trả về từ API gợi ý.
 * Nó chứa một danh sách các tin RSS và một danh sách các bài viết.
 */
public class RecommendationResponse {

    @SerializedName("rss")
    private List<RssItem> rss;

    @SerializedName("articles")
    private List<Article> articles;

    // Getters
    public List<RssItem> getRss() {
        return rss;
    }

    public List<Article> getArticles() {
        return articles;
    }
}