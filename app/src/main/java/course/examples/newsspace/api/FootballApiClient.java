package course.examples.newsspace.api;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class FootballApiClient {
    // Base URL của dịch vụ bóng đá (ví dụ: API-FOOTBALL)
    private static final String BASE_URL = "https://v3.football.api-sports.io/";
    private static Retrofit retrofit = null;

    public static FootballApiService getApiService() {
        if (retrofit == null) {

            // 1. Tạo một OkHttpClient.Builder
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            // 2. Thêm Interceptor của bạn vào
            httpClient.addInterceptor(new FootballInterceptor());

            // 3. Build Retrofit với client đã được cấu hình
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build()) // <-- THÊM DÒNG NÀY
                    .build();
        }
        return retrofit.create(FootballApiService.class);
    }}