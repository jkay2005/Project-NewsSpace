package course.examples.newsspace.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model này được dùng để gửi dữ liệu cập nhật thông tin cá nhân lên server.
 * Chỉ chứa các trường mà người dùng có thể thay đổi.
 */
public class UpdateProfileRequest {

    @SerializedName("name") // Tên tài khoản
    private String name;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("date_of_birth") // Định dạng nên là "YYYY-MM-DD" để server dễ xử lý
    private String dateOfBirth;

    @SerializedName("gender")
    private String gender;

    @SerializedName("country")
    private String country;

    @SerializedName("city")
    private String city;

    // Constructor để dễ dàng tạo đối tượng
    public UpdateProfileRequest(String name, String fullName, String dateOfBirth, String gender, String country, String city) {
        this.name = name;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.country = country;
        this.city = city;
    }

    // Getters không bắt buộc cho request body, nhưng nên có để code nhất quán
}