// File: /app/src/main/java/course/examples/newsspace/model/NotificationItem.java
package course.examples.newsspace.model;

import androidx.annotation.Nullable;

public class NotificationItem {
    private long id;
    private String title;
    @Nullable // Mô tả có thể có hoặc không
    private String description;
    private String timestamp;
    private boolean isRead;

    public NotificationItem(long id, String title, @Nullable String description, String timestamp, boolean isRead) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    // Thêm các phương thức Getters
    public long getId() { return id; }
    public String getTitle() { return title; }
    @Nullable
    public String getDescription() { return description; }
    public String getTimestamp() { return timestamp; }
    public boolean isRead() { return isRead; }
}
    