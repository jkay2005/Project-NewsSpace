package course.examples.newsspace.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Lớp này đại diện cho đối tượng JSON trả về từ API lấy danh sách bookmark.
 * Nó chứa một danh sách tên các bộ sưu tập và một danh sách các bài báo đã lưu.
 */
public class BookmarkResponse {

    @SerializedName("collections")
    private List<String> collectionNames;

    @SerializedName("articles")
    private List<Article> savedArticles;

    // Getters
    public List<String> getCollectionNames() {
        return collectionNames;
    }

    public List<Article> getSavedArticles() {
        return savedArticles;
    }
}
