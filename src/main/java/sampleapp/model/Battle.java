package sampleapp.model;

import java.time.LocalDateTime;

public class Battle {
    private int id; // Primärschlüssel
    private Long player1Id; // ID von Spieler 1
    private Long player2Id; // ID von Spieler 2
    private Long winnerId; // ID des Gewinners (kann null sein, wenn unentschieden)
    private String log; // Kampfprotokoll
    private LocalDateTime createdAt; // Zeitstempel der Erstellung

    // Standardkonstruktor
    public Battle() {}

    // Konstruktor mit allen Attributen
    public Battle(int id, Long player1Id, Long player2Id, Long winnerId, String log, LocalDateTime createdAt) {
        this.id = id;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.winnerId = winnerId;
        this.log = log;
        this.createdAt = createdAt;
    }

    // Getter und Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(Long player1Id) {
        this.player1Id = player1Id;
    }

    public Long getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(Long player2Id) {
        this.player2Id = player2Id;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Long winnerId) {
        this.winnerId = winnerId;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Battle{" +
                "id=" + id +
                ", player1Id=" + player1Id +
                ", player2Id=" + player2Id +
                ", winnerId=" + winnerId +
                ", log='" + log + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
