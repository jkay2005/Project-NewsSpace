// File: db/SavedArticle.java
package course.examples.newsspace.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "saved_articles")
public class SavedArticle {

    @PrimaryKey
    @NonNull
    private String url; // Dùng URL làm khóa chính vì nó là duy nhất

    private String title;
    private String imageUrl;
    private String publishedAt;
    private long savedTimestamp; // Thời gian lưu để sắp xếp

    // Constructor, Getters, Setters
    public SavedArticle(@NonNull String url, String title, String imageUrl, String publishedAt) {
        this.url = url;
        this.title = title;
        this.imageUrl = imageUrl;
        this.publishedAt = publishedAt;
        this.savedTimestamp = System.currentTimeMillis();
    }

    @NonNull
    public String getUrl() { return url; }
    public void setUrl(@NonNull String url) { this.url = url; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getPublishedAt() { return publishedAt; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }
    public long getSavedTimestamp() { return savedTimestamp; }
    public void setSavedTimestamp(long savedTimestamp) { this.savedTimestamp = savedTimestamp; }
}