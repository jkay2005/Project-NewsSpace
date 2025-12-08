package course.examples.newsspace.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * DAO (Data Access Object) cho Article entity.
 * Interface này định nghĩa các phương thức để tương tác với bảng "bookmarked_articles".
 */
@Dao
public interface ArticleDao {

    /**
     * Thêm một bài báo vào cơ sở dữ liệu.
     * Nếu bài báo đã tồn tại (dựa trên PrimaryKey là id), nó sẽ được thay thế.
     * @param article Bài báo cần thêm.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertArticle(Article article);

    /**
     * Xóa một bài báo khỏi cơ sở dữ liệu.
     * @param article Bài báo cần xóa.
     */
    @Delete
    void deleteArticle(Article article);

    /**
     * Lấy tất cả các bài báo đã được đánh dấu, sắp xếp theo thứ tự mới nhất (id giảm dần).
     * Trả về một LiveData, giúp giao diện tự động cập nhật khi dữ liệu thay đổi.
     * @return LiveData chứa danh sách các bài báo đã lưu.
     */
    @Query("SELECT * FROM bookmarked_articles ORDER BY id DESC")
    LiveData<List<Article>> getAllBookmarkedArticles();

    /**
     * Đếm số lượng bài báo trong cơ sở dữ liệu có id trùng khớp.
     * Dùng để kiểm tra nhanh xem một bài báo đã được lưu hay chưa.
     * @param articleId ID của bài báo cần kiểm tra.
     * @return 1 nếu đã tồn tại, 0 nếu chưa.
     */
    @Query("SELECT COUNT(*) FROM bookmarked_articles WHERE id = :articleId")
    int isArticleBookmarked(int articleId);
}