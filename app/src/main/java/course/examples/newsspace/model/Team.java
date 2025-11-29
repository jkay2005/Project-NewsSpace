package course.examples.newsspace.model;import com.google.gson.annotations.SerializedName;public class Team {@SerializedName("name")
private String name;

    @SerializedName("logo")
    private String logoUrl;

    // --- Constructor Mới ---
// Được sử dụng bởi FakeDataGenerator để tạo đối tượng nhanh chóng.
    public Team(String name, String logoUrl) {
        this.name = name;
        this.logoUrl = logoUrl;
    }

    // --- Getters ---
    public String getName() {
        return name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }}