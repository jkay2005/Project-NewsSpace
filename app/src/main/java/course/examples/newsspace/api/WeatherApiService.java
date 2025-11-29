package course.examples.newsspace.api;
import course.examples.newsspace.model.WeatherData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface WeatherApiService {
    /** * Lấy thông tin thời tiết hiện tại cho một địa điểm. * Ví dụ URL: https://api.weatherapi.com/v1/current.json?key=YOUR_API_KEY&q=Ho Chi Minh *
     * @param apiKey Khóa API của bạn.
     * @param location Tên thành phố.
     * @return Một đối tượng Call chứa WeatherData. */
    @GET("v1/current.json")
    Call<WeatherData> getCurrentWeather(
            @Query("key") String apiKey, @Query("q") String location );
}