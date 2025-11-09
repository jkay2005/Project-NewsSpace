package course.examples.newsspace.model;

import com.google.gson.annotations.SerializedName;

public class FeedbackRequest {
    @SerializedName("itemType")
    private final String itemType; // "rss" hoặc "article"

    @SerializedName("itemId")
    private final String itemId;

    @SerializedName("rating")
    private final int rating;

    @SerializedName("comment")
    private final String comment;

    public FeedbackRequest(String itemType, String itemId, int rating, String comment) {
        this.itemType = itemType;
        this.itemId = itemId;
        this.rating = rating;
        this.comment = comment;
    }
}