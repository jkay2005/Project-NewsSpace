package course.examples.newsspace.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class NotificationPrefsManager {
    private static final String PREF_NAME = "NotificationPrefs";
    private final SharedPreferences prefs;

    // Khóa để lưu trữ các cài đặt
    private static final String KEY_NEWS_NOTIFICATIONS = "key_news_notifications";
    private static final String KEY_BLOG_NOTIFICATIONS = "key_blog_notifications";
    private static final String KEY_WEATHER_NOTIFICATIONS = "key_weather_notifications";

    private static final String KEY_CATEGORY_PREFIX = "category_";

    public boolean isCategoryEnabled(String category) {
        // Mặc định tất cả chuyên mục đều được bật
        return prefs.getBoolean(KEY_CATEGORY_PREFIX + category, true);
    }

    public void setCategoryEnabled(String category, boolean isEnabled) {
        prefs.edit().putBoolean(KEY_CATEGORY_PREFIX + category, isEnabled).apply();
    }
    public NotificationPrefsManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // --- Getters ---
    public boolean isNewsNotificationsEnabled() {
        return prefs.getBoolean(KEY_NEWS_NOTIFICATIONS, true); // Mặc định là bật
    }

    public boolean isBlogNotificationsEnabled() {
        return prefs.getBoolean(KEY_BLOG_NOTIFICATIONS, true); // Mặc định là bật
    }

    public boolean isWeatherNotificationsEnabled() {
        return prefs.getBoolean(KEY_WEATHER_NOTIFICATIONS, true); // Mặc định là bật
    }

    // --- Setters ---
    public void setNewsNotificationsEnabled(boolean isEnabled) {
        prefs.edit().putBoolean(KEY_NEWS_NOTIFICATIONS, isEnabled).apply();
    }

    public void setBlogNotificationsEnabled(boolean isEnabled) {
        prefs.edit().putBoolean(KEY_BLOG_NOTIFICATIONS, isEnabled).apply();
    }

    public void setWeatherNotificationsEnabled(boolean isEnabled) {
        prefs.edit().putBoolean(KEY_WEATHER_NOTIFICATIONS, isEnabled).apply();
    }
}