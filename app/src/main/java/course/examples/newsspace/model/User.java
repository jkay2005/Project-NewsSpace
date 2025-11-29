package course.examples.newsspace.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    // THÊM CÁC TRƯỜNG MỚI (tên @SerializedName phải khớp chính xác với key trong JSON từ API)
    @SerializedName("full_name")
    private String fullName;

    @SerializedName("avatar_url")
    private String avatarUrl;

    @SerializedName("date_of_birth")
    private String dateOfBirth; // Ví dụ: "1990-12-31"

    @SerializedName("gender")
    private String gender; // Ví dụ: "Nam", "Nữ", "Khác"

    @SerializedName("country")
    private String country;

    @SerializedName("city")
    private String city;

    // --- Constructor và Getters ---
    // (Đảm bảo bạn có đủ các getter cho tất cả các trường)

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }
}