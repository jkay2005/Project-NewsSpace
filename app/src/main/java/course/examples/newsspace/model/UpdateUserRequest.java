package course.examples.newsspace.model;

import com.google.gson.annotations.SerializedName;

public class UpdateUserRequest {
    @SerializedName("role")
    private final String role;

    @SerializedName("emailVerified")
    private final boolean emailVerified;

    public UpdateUserRequest(String role, boolean emailVerified) {
        this.role = role;
        this.emailVerified = emailVerified;
    }
}