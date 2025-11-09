package course.examples.newsspace.utils; // Thay bằng package của bạn

import android.content.Context;
import android.content.SharedPreferences;

import course.examples.newsspace.model.User;

public class SessionManager {
    private static final String PREF_NAME = "AuthPrefs";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Lưu thông tin session sau khi đăng nhập thành công
     */
    public void saveAuthToken(String token) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.apply();
    }

    public void saveUser(User user) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.apply();
    }

    /**
     * Lấy token đã lưu
     */
    public String getAuthToken() {
        return prefs.getString(KEY_AUTH_TOKEN, null);
    }

    /**
     * Kiểm tra xem người dùng đã đăng nhập hay chưa
     */
    public boolean isLoggedIn() {
        return getAuthToken() != null;
    }

    /**
     * Xóa session khi đăng xuất
     */
    public void logout() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}