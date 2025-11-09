package course.examples.newsspace.model;

import com.google.gson.annotations.SerializedName;

public class CreateCommentRequest {
    @SerializedName("content")
    private final String content;

    public CreateCommentRequest(String content) {
        this.content = content;
    }
}