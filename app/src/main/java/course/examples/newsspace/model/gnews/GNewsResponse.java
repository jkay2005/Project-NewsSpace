// File: /app/src/main/java/course/examples/newsspace/model/gnews/GNewsResponse.java
package course.examples.newsspace.model.gnews;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GNewsResponse {
    @SerializedName("totalArticles")
    private int totalArticles;
    @SerializedName("articles")
    private List<GNewsArticle> articles;

    public int getTotalArticles() { return totalArticles; }
    public List<GNewsArticle> getArticles() { return articles; }
}
        