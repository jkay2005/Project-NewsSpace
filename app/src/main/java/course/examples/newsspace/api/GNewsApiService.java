// File: /app/src/main/java/course/examples/newsspace/api/GNewsApiService.java
package course.examples.newsspace.api;

import course.examples.newsspace.model.gnews.GNewsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GNewsApiService {

    @GET("top-headlines")
    Call<GNewsResponse> getTopHeadlines(
            @Query("apikey") String apiKey,
            @Query("lang") String lang,
            @Query("country") String country
    );

    @GET("search")
    Call<GNewsResponse> searchArticles(
            @Query("q") String query,
            @Query("apikey") String apiKey,  // <-- Sửa thành "apikey"
            @Query("lang") String lang,
            @Query("sortby") String sortBy
    );
}
    