package course.examples.newsspace.model;

import com.google.gson.annotations.SerializedName;

// ... imports
public class Blog {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    // ... thêm tất cả các trường khác từ API
}