package course.examples.newsspace.model;
import com.google.gson.annotations.SerializedName;
public class ResendOtpRequest {
    @SerializedName("email")
    private final String email;

    public ResendOtpRequest(String email) {
        this.email = email;
    }
}