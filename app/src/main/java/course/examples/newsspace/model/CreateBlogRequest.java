package course.examples.newsspace.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CreateBlogRequest {
    @SerializedName("title")
    private final String title;

    @SerializedName("content")
    private final String content;

    @SerializedName("excerpt")
    private final String excerpt;

    @SerializedName("categoryId")
    private final int categoryId;

    @SerializedName("tags")
    private final List<String> tags;

    public CreateBlogRequest(String title, String content, String excerpt, int categoryId, List<String> tags) {
        this.title = title;
        this.content = content;
        this.excerpt = excerpt;
        this.categoryId = categoryId;
        this.tags = tags;
    }
}