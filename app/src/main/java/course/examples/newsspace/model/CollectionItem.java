package course.examples.newsspace.model;

import com.google.gson.annotations.SerializedName;

/**
 * Lớp này đại diện cho MỘT bộ sưu tập bookmark duy nhất.
 */
public class CollectionItem {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
}