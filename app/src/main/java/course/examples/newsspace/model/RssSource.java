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

    //test

    // Constructor rỗng cho Gson
    public RssSource() {}

    // Constructor tiện lợi để tạo dữ liệu giả
    public RssSource(String name, String tag) {
        this.name = name;
        this.tag = tag;
    }
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setTag(String tag) { this.tag = tag; }

}

    // Getters...