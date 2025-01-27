package sampleapp.model;

import java.time.LocalDateTime;

public class Battle {
    private int id; // Primärschlüssel für Battles
    private String player1Username; // Username von Spieler 1
    private String player2Username; // Username von Spieler 2
    private String winnerUsername; // Username des Gewinners (kann null sein bei Unentschieden)
    private String log; // Protokoll des Battles
    private LocalDateTime createdAt; // Zeitpunkt der Erstellung

    public Battle() {}

    public Battle(int id, String player1Username, String player2Username, String winnerUsername, String log, LocalDateTime createdAt) {
        this.id = id;
        this.player1Username = player1Username;
        this.player2Username = player2Username;
        this.winnerUsername = winnerUsername;
        this.log = log;
        this.createdAt = createdAt;
    }

    // Getter und Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPlayer1Username() { return player1Username; }
    public void setPlayer1Username(String player1Username) { this.player1Username = player1Username; }

    public String getPlayer2Username() { return player2Username; }
    public void setPlayer2Username(String player2Username) { this.player2Username = player2Username; }

    public String getWinnerUsername() { return winnerUsername; }
    public void setWinnerUsername(String winnerUsername) { this.winnerUsername = winnerUsername; }

    public String getLog() { return log; }
    public void setLog(String log) { this.log = log; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
