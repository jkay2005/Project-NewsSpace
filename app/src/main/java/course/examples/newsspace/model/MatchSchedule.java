package course.examples.newsspace.model;import com.google.gson.annotations.SerializedName; import java.util.List;public class MatchSchedule {
    @SerializedName("league")
    private League league;

    @SerializedName("response")
    private List<FootballMatch> matches;

    /**
     * Constructor mới cho MatchSchedule.
     * Được sử dụng bởi FakeDataGenerator.
     */
    public MatchSchedule(League league, List<FootballMatch> matches) {
        this.league = league;
        this.matches = matches;
    }

    // Lớp nội cho thông tin giải đấu
    public static class League {
        @SerializedName("name")
        private String name; // "Premier League"

        @SerializedName("round")
        private String round; // "Regular Season - 8"

        /**
         * Constructor mới cho lớp nội League.
         */
        public League(String name, String round) {
            this.name = name;
            this.round = round;
        }

        // Getters
        public String getName() {
            return name;
        }

        public String getRound() {
            return round;
        }
    }

    // --- Getters ---
    public League getLeague() {
        return league;
    }

    public List<FootballMatch> getMatches() {
        return matches;
    }
}