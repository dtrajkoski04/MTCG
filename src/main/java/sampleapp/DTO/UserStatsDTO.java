package sampleapp.DTO;

public class UserStatsDTO {
    private String username;
    private int elo;
    private int games_played;
    private int games_won;
    private int games_lost;

    public UserStatsDTO(String username, int elo, int games_played, int games_won, int games_lost) {
        this.username = username;
        this.elo = elo;
        this.games_played = games_played;
        this.games_won = games_won;
        this.games_lost = games_lost;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public int getGames_played() {
        return games_played;
    }

    public void setGames_played(int games_played) {
        this.games_played = games_played;
    }

    public int getGames_won() {
        return games_won;
    }

    public void setGames_won(int games_won) {
        this.games_won = games_won;
    }

    public int getGames_lost() {
        return games_lost;
    }

    public void setGames_lost(int games_lost) {
        this.games_lost = games_lost;
    }
}
