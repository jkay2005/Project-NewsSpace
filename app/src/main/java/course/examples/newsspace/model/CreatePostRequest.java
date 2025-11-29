package course.examples.newsspace.model;
import com.google.gson.annotations.SerializedName;
import java.util.List;
public class CreatePostRequest {
    @SerializedName("title")
private String title;

    @SerializedName("content_blocks")
    private List<BlogContentBlock> contentBlocks;

    public CreatePostRequest(String title, List<BlogContentBlock> contentBlocks) {
        this.title = title;
        this.contentBlocks = contentBlocks;
    }

    // Getters (cần thiết cho Gson)
    public String getTitle() {
        return title;
    }

    public List<BlogContentBlock> getContentBlocks() {
        return contentBlocks;
    }}