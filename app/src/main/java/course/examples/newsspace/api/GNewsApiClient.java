// File: /app/src/main/java/course/examples/newsspace/network/GNewsApiClient.java
package course.examples.newsspace.api;

import course.examples.newsspace.api.GNewsApiService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GNewsApiClient {

    private static final String GNEWS_BASE_URL = "https://gnews.io/api/v4/";
    private static Retrofit retrofit = null;

    private static Retrofit getClient() {
        if (retrofit == null) {
            // HttpLoggingInterceptor giúp bạn thấy log của các request/response trong Logcat
            // Rất hữu ích cho việc debug
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(GNEWS_BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * Phương thức tiện ích để lấy GNewsApiService trực tiếp.
     * Đây là phương thức bạn sẽ gọi từ ViewModel.
     */
    public static GNewsApiService getApiService() {
        return getClient().create(GNewsApiService.class);
    }
}
    