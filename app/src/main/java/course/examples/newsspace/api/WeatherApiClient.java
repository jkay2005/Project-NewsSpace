package course.examples.newsspace.api;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class WeatherApiClient { // Base URL của dịch vụ thời tiết (ví dụ: WeatherAPI.com)
     private static final String BASE_URL = "https://api.weatherapi.com/";
     private static Retrofit retrofit = null;

    public static WeatherApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(WeatherApiService.class);
    }}