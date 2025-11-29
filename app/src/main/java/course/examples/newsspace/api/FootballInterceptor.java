package course.examples.newsspace.api;

import androidx.annotation.NonNull;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class FootballInterceptor implements Interceptor {

    // DÁN API KEY CỦA BẠN VÀO ĐÂY
    private final String apiKey = "9c9a72c3b0msh1c3b050466507a5p1074dajsn8990783fb1b9";

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Request newRequest = originalRequest.newBuilder()
                .addHeader("X-RapidAPI-Key", apiKey)
                .addHeader("X-RapidAPI-Host", "v3.football.api-sports.io")
                .build();

        return chain.proceed(newRequest);
    }
}