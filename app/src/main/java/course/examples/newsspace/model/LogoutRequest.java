package course.examples.newsspace.model;

import com.google.gson.annotations.SerializedName;

public class LogoutRequest {
    @SerializedName("refreshToken")
    private String refreshToken;

    public LogoutRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}