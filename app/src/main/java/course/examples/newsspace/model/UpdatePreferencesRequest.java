package course.examples.newsspace.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UpdatePreferencesRequest {
    @SerializedName("preferences")
    private final List<String> preferences;

    public UpdatePreferencesRequest(List<String> preferences) {
        this.preferences = preferences;
    }

    // Getter (có thể cần cho response)
    public List<String> getPreferences() {
        return preferences;
    }
}