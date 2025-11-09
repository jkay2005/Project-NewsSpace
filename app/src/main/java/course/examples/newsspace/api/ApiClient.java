package course.examples.newsspace.api; // Thay bằng package của bạn

import android.content.Context;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // URL gốc của API. Thay đổi nếu cần.
    private static final String BASE_URL = "http://your.api.base.url/"; // <-- THAY ĐỔI URL NÀY

    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            // 1. Tạo Logging Interceptor để xem log
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Tạo AuthInterceptor
            AuthInterceptor authInterceptor = new AuthInterceptor(context);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(authInterceptor) // THÊM AUTH INTERCEPTOR VÀO ĐÂY
                    .build();
            // 3. Xây dựng đối tượng Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL) // Đặt URL gốc
                    .client(okHttpClient) // Sử dụng OkHttpClient đã tùy chỉnh
                    .addConverterFactory(GsonConverterFactory.create()) // Thêm Gson converter
                    .build();

        }
        return retrofit;
    }

    /**
     * Phương thức tiện ích để lấy trực tiếp ApiService
     */
    public static ApiService getApiService(Context context) {
        return getClient(context).create(ApiService.class);
    }
}