package course.examples.newsspace;
import java.util.ArrayList;
import java.util.List;
import course.examples.newsspace.model.FootballMatch;
import course.examples.newsspace.model.MatchSchedule;
import course.examples.newsspace.model.Team;
import course.examples.newsspace.model.WeatherData;
/**•Lớp tiện ích để tạo dữ liệu giả cho mục đích hiển thị giao diện mẫu. */
public class FakeDataGenerator {
    /**
     * •Tạo một đối tượng WeatherData giả với thông tin thời tiết cho TP.HCM.•@return Một đối tượng WeatherData.
     */
    public static WeatherData createFakeWeatherData() {
        return new WeatherData("TP HCM", 30, "Nhiều mây", "//cdn.weatherapi.com/weather/64x64/day/116.png", 32, 25, 82, 8);
    }

    /**
     * •Tạo một đối tượng MatchSchedule giả cho lịch thi đấu Ngoại Hạng Anh.
     * •@return Một đối tượng MatchSchedule.
     */
    public static MatchSchedule createFakeMatchSchedule() {
        // 1. Tạo thông tin giải đấu và vòng đấu
        MatchSchedule.League leagueInfo = new MatchSchedule.League("Ngoại Hạng Anh", "Vòng 8, ngày 18/10/2025");
        // 2. Tạo danh sách các trận đấu giả
        List<FootballMatch> fakeMatches = new ArrayList<>();
        fakeMatches.add(createFakeMatch("21:00", "Manchester City", "Everton", 3, 1));
        fakeMatches.add(createFakeMatch("21:00", "Arsenal", "Chelsea", 2, 2));
        fakeMatches.add(createFakeMatch("23:30", "Liverpool", "Tottenham", 1, 0));
        fakeMatches.add(createFakeMatch("23:30", "Manchester Utd", "Newcastle", 0, 1));
        fakeMatches.add(createFakeMatch("02:00", "Aston Villa", "Brighton", 2, 1));
        // 3. Tạo đối tượng MatchSchedule hoàn chỉnh
        return new MatchSchedule(leagueInfo, fakeMatches);
    }

    /**
     * •Phương thức helper để tạo một đối tượng FootballMatch đơn lẻ.
     */
    private static FootballMatch createFakeMatch(String time, String homeTeamName, String awayTeamName, int homeScore, int awayScore) {
        Team homeTeam = new Team(homeTeamName, null);
        // logoUrl có thể là null
        Team awayTeam = new Team(awayTeamName, null);
        FootballMatch.Teams teams = new FootballMatch.Teams(homeTeam, awayTeam);
        FootballMatch.Goals goals = new FootballMatch.Goals(homeScore, awayScore);
        return new FootballMatch(time, teams, goals);
    }
    // Lưu ý: Bạn cần thêm các constructor tương ứng vào các Model class để code trên hoạt động.
    // Ví dụ, trong MatchSchedule.java:
    // public MatchSchedule(League league, List<FootballMatch> matches) { this.league = league; this.matches = matches; } }

}