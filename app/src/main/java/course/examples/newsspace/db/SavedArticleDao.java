// File: db/SavedArticleDao.java
package course.examples.newsspace.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface SavedArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SavedArticle article);

    @Delete
    void delete(SavedArticle article);

    @Query("SELECT * FROM saved_articles ORDER BY savedTimestamp DESC")
    LiveData<List<SavedArticle>> getAllSavedArticles(); // Dùng LiveData để tự động cập nhật UI

    @Query("SELECT * FROM saved_articles WHERE url = :url")
    SavedArticle getArticleByUrl(String url);
}