// File: /app/src/main/java/course/examples/newsspace/utils/SessionManager.java

package course.examples.newsspace.utils; // Thay bằng package của bạn

import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;

import course.examples.newsspace.model.User;

public class SessionManager {
    private static final String PREF_NAME = "AuthPrefs";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    // THAY ĐỔI 1: Gộp các key user thành một key duy nhất
    private static final String KEY_USER_DETAILS = "user_details_json";


    private final SharedPreferences prefs;
    private final Gson gson; // THÊM BIẾN GSON

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson(); // Khởi tạo Gson
    }

    /**
     * Lưu thông tin session sau khi đăng nhập thành công
     */
    public void saveAuthToken(String token) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.apply();
    }

    /**
     * Chuyển đối tượng User thành chuỗi JSON và lưu vào SharedPreferences.
     */
    public void saveUser(User user) {
        SharedPreferences.Editor editor = prefs.edit();
        String userJson = gson.toJson(user); // Dùng Gson để chuyển đổi
        editor.putString(KEY_USER_DETAILS, userJson);
        editor.apply();
    }

    /**
     * Lấy token đã lưu
     */
    public String getAuthToken() {
        return prefs.getString(KEY_AUTH_TOKEN, null);
    }

    // ===================================================================
    // == PHẦN MÃ MỚI ĐƯỢC THÊM VÀO ĐỂ SỬA LỖI TRONG SETTINGSFRAGMENT ==
    // ===================================================================

    /**
     * Lấy thông tin người dùng đã lưu và tạo lại đối tượng User.
     * Phương thức này sẽ sửa lỗi "Cannot resolve method 'getUser' in 'SessionManager'".
     *
     * @return Đối tượng User nếu đã đăng nhập, ngược lại trả về null.
     */
    /**
     * Đọc chuỗi JSON từ SharedPreferences và chuyển ngược lại thành đối tượng User.
     * Phương thức này sẽ sửa lỗi "Cannot resolve method 'getUser' in 'SessionManager'".
     */
    public User getUser() {
        String userJson = prefs.getString(KEY_USER_DETAILS, null);
        if (userJson == null) {
            return null;
        }
        // Dùng Gson để chuyển đổi ngược lại
        return gson.fromJson(userJson, User.class);
    }
    public boolean isLoggedIn() {
        return getAuthToken() != null;
    }

    /**
     * Xóa session khi đăng xuất.
     * Sửa lỗi "Cannot resolve method 'clearSession' in 'SessionManager'".
     */
    public void clearSession() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Xóa session khi đăng xuất.
     * Phương thức này được đổi tên từ 'logout' thành 'clearSession' để khớp với
     * lời gọi trong SettingsFragment, sửa lỗi "Cannot resolve method 'clearSession' in 'SessionManager'".
     */
    // Phương thức logout() cũ của bạn cũng làm điều tương tự,
    // chúng ta giữ lại clearSession() để tương thích với SettingsFragment.
}