package course.examples.newsspace.model;

import com.google.gson.annotations.SerializedName;

public class Comment {
    @SerializedName("id")
    private int id;

    @SerializedName("blogId")
    private int blogId;

    @SerializedName("userId")
    private int userId;

    @SerializedName("content")
    private String content;

    @SerializedName("createdAt")
    private String createdAt;

    // TODO: Bạn có thể thêm một đối tượng User ở đây để hiển thị thông tin người bình luận
    // @SerializedName("user")
    // private User user;

    // Getters
    public int getId() {
        return id;
    }

    public int getBlogId() {
        return blogId;
    }

    public int getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}