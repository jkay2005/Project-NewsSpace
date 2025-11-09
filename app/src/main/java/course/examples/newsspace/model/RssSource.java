package course.examples.newsspace.model;

import com.google.gson.annotations.SerializedName;

public class RssSource {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("tag")
    private String tag;

    // Thêm cả url và active cho request body
    @SerializedName("url")
    private String url;

    @SerializedName("active")
    private boolean active;

    // Constructor để tạo request body
    public RssSource(String name, String url, String tag, boolean active) {
        this.name = name;
        this.url = url;
        this.tag = tag;
        this.active = active;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    // *** BỔ SUNG GETTER NÀY NẾU CHƯA CÓ ***
    public String getTag() {
        return tag;
    }

    public String getUrl() {
        return url;
    }

    public boolean isActive() {
        return active;
    }
}

    // Getters...