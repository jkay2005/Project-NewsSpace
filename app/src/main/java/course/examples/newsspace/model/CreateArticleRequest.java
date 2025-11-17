package course.examples.newsspace.model;

import com.google.gson.annotations.SerializedName;

public class CreateArticleRequest {
    @SerializedName("title")
    private final String title;

    @SerializedName("content")
    private final String content;

    public CreateArticleRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
}