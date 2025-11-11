package course.examples.newsspace.model;

import com.google.gson.annotations.SerializedName;

/**
 * Lớp Model đại diện cho thông tin tác giả.
 */
public class Author {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    // Constructor rỗng cho Gson
    public Author() {}

    // --- GETTERS ---

    public int getId() {
        return id;
    }

    public String getName() {
        // Luôn kiểm tra null để trả về một giá trị an toàn
        return name != null ? name : "Tác giả không xác định";
    }
}