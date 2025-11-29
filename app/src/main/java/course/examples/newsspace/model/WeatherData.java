package course.examples.newsspace.model;

import com.google.gson.annotations.SerializedName;

/**
 * Đại diện cho dữ liệu thời tiết nhận được từ API.
 * Các annotation @SerializedName được sử dụng để ánh xạ tên trường trong JSON
 * sang tên thuộc tính trong class Java, phòng trường hợp chúng không giống nhau.
 */
public class WeatherData {

    // Ví dụ: {"location_name": "Ho Chi Minh City"}
    @SerializedName("location")
    private String location; // Sẽ lưu "TP HCM"

    // Ví dụ: {"temp_c": 30}
    @SerializedName("temperature")
    private int temperature; // Sẽ lưu 30

    // Ví dụ: {"condition_text": "Partly cloudy"}
    @SerializedName("condition")
    private String condition; // Sẽ lưu "Nhiều mây"

    // Ví dụ: {"condition_icon": "//cdn.weatherapi.com/weather/64x64/day/116.png"}
    @SerializedName("iconUrl")
    private String iconUrl; // URL đến icon thời tiết

    // Ví dụ: {"temp_max_c": 32}
    @SerializedName("maxTemp")
    private int maxTemp; // Nhiệt độ cao nhất

    // Ví dụ: {"temp_min_c": 25}
    @SerializedName("minTemp")
    private int minTemp; // Nhiệt độ thấp nhất

    // Ví dụ: {"humidity": 82}
    @SerializedName("humidity")
    private int humidity; // Độ ẩm (%)

    // Ví dụ: {"chance_of_rain": 8}
    @SerializedName("rainChance")
    private int rainChance; // Khả năng mưa (%)

    // Constructor rỗng (cần thiết cho một số thư viện)
    public WeatherData() {
    }

    // Constructor đầy đủ để dễ dàng tạo đối tượng (ví dụ: cho dữ liệu giả)
    public WeatherData(String location, int temperature, String condition, String iconUrl, int maxTemp, int minTemp, int humidity, int rainChance) {
        this.location = location;
        this.temperature = temperature;
        this.condition = condition;
        this.iconUrl = iconUrl;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.humidity = humidity;
        this.rainChance = rainChance;
    }

    // --- Getters ---
    // (Bắt buộc phải có để Gson và các thư viện khác có thể truy cập dữ liệu)

    public String getLocation() {
        return location;
    }

    public int getTemperature() {
        return temperature;
    }

    public String getCondition() {
        return condition;
    }

    public String getIconUrl() {
        // API có thể trả về URL không có "https:", cần thêm vào
        if (iconUrl != null && iconUrl.startsWith("//")) {
            return "https:" + iconUrl;
        }
        return iconUrl;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getRainChance() {
        return rainChance;
    }
}