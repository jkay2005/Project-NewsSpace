package course.examples.newsspace.model;

import com.google.gson.annotations.SerializedName;

/**
 * Lớp này đại diện cho đối tượng JSON chứa các số liệu thống kê
 * từ API quản trị.
 */
public class AdminStatsResponse {

    @SerializedName("users")
    private int users;

    @SerializedName("activities")
    private int activities;

    @SerializedName("rssItems")
    private int rssItems;

    @SerializedName("blogs")
    private int blogs;

    @SerializedName("articles")
    private int articles;

    // Getters
    public int getUsers() {
        return users;
    }

    public int getActivities() {
        return activities;
    }

    public int getRssItems() {
        return rssItems;
    }

    public int getBlogs() {
        return blogs;
    }

    public int getArticles() {
        return articles;
    }
}