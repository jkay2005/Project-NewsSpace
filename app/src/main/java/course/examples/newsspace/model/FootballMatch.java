package course.examples.newsspace.model;
import com.google.gson.annotations.SerializedName;
public class FootballMatch {@SerializedName("time")
private String time;

    @SerializedName("teams")
    private Teams teams;

    @SerializedName("goals")
    private Goals goals;

    /**
     * Constructor mới cho FootballMatch.
     * Được sử dụng bởi FakeDataGenerator.
     */
    public FootballMatch(String time, Teams teams, Goals goals) {
        this.time = time;
        this.teams = teams;
        this.goals = goals;
    }

    // Lớp nội (inner class) để khớp với cấu trúc JSON lồng nhau
    public static class Teams {
        @SerializedName("home")
        private Team homeTeam;

        @SerializedName("away")
        private Team awayTeam;

        /**
         * Constructor mới cho lớp nội Teams.
         */
        public Teams(Team homeTeam, Team awayTeam) {
            this.homeTeam = homeTeam;
            this.awayTeam = awayTeam;
        }

        // Getters
        public Team getHomeTeam() {
            return homeTeam;
        }

        public Team getAwayTeam() {
            return awayTeam;
        }
    }

    // Lớp nội cho tỉ số
    public static class Goals {
        @SerializedName("home")
        private int homeTeamScore;

        @SerializedName("away")
        private int awayTeamScore;

        /**
         * Constructor mới cho lớp nội Goals.
         */
        public Goals(int homeTeamScore, int awayTeamScore) {
            this.homeTeamScore = homeTeamScore;
            this.awayTeamScore = awayTeamScore;
        }

        // Getters
        public int getHomeTeamScore() {
            return homeTeamScore;
        }

        public int getAwayTeamScore() {
            return awayTeamScore;
        }
    }

    // --- Getters ---
    public String getTime() {
        return time;
    }

    public Teams getTeams() {
        return teams;
    }

    public Goals getGoals() {
        return goals;
    }}