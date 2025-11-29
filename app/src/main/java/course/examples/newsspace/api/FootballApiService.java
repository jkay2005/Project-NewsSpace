package course.examples.newsspace.api;
import course.examples.newsspace.model.MatchSchedule;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
public interface FootballApiService {
    /** * Lấy lịch thi đấu cho một giải đấu cụ thể. * Ví dụ URL: https://v3.football.api-sports.io/fixtures?league=39&season=2024
     * * @param apiKey Khóa API của bạn, được gửi qua Header.
     * * @param leagueId ID của giải đấu (ví dụ: 39 cho Premier League).
     * * @param season Mùa giải (ví dụ: 2024).
     * * @return Một đối tượng Call chứa MatchSchedule. */
    @GET("fixtures")
    Call<MatchSchedule> getFixtures(
            @Query("league") int league,
            @Query("season") int season
    );
}