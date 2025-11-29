package course.examples.newsspace.model;
import com.google.gson.annotations.SerializedName;
public class ImageUploadResponse {
    @SerializedName("url")
    private String imageUrl;
    public String getImageUrl() {
    return imageUrl;
}
}