package course.examples.newsspace.model;

import com.google.gson.annotations.SerializedName;

/**
 * Lớp này đại diện cho toàn bộ đối tượng JSON trả về khi đăng nhập thành công.
 * Nó chứa token xác thực và thông tin chi tiết của người dùng.
 */
public class LoginResponse {

    @SerializedName("token")
    private String token;

    @SerializedName("refreshToken")
    private String refreshToken;

    @SerializedName("user")
    private User user; // Đối tượng User lồng nhau

    // Getters
    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public User getUser() {
        return user;
    }
}