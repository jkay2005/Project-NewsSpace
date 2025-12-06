// File: /app/src/main/java/course/examples/newsspace/utils/SessionManager.java

package course.examples.newsspace.utils; // Thay bằng package của bạn

import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;

import course.examples.newsspace.model.User;

public class SessionManager {
    private static final String PREF_NAME = "AuthPrefs";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_DETAILS = "user_details_json";


    private final SharedPreferences prefs;
    private final Gson gson;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    /**
     * Lưu thông tin session sau khi đăng nhập thành công
     */
    public void saveTokens(String authToken, String refreshToken) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_AUTH_TOKEN, authToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.apply();
    }

    /**
     * Chuyển đối tượng User thành chuỗi JSON và lưu vào SharedPreferences.
     */
    public void saveUser(User user) {
        SharedPreferences.Editor editor = prefs.edit();
        String userJson = gson.toJson(user);
        editor.putString(KEY_USER_DETAILS, userJson);
        editor.apply();
    }

    /**
     * Lấy token đã lưu
     */
    public String getAuthToken() {
        return prefs.getString(KEY_AUTH_TOKEN, null);
    }
    
    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }

    /**
     * Đọc chuỗi JSON từ SharedPreferences và chuyển ngược lại thành đối tượng User.
     */
    public User getUser() {
        String userJson = prefs.getString(KEY_USER_DETAILS, null);
        if (userJson == null) {
            return null;
        }
        return gson.fromJson(userJson, User.class);
    }

    public boolean isLoggedIn() {
        return getAuthToken() != null;
    }

    /**
     * Xóa session khi đăng xuất.
     */
    public void clearSession() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}