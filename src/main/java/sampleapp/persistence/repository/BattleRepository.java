package sampleapp.persistence.repository;

public interface BattleRepository {
    void updateStats(String username, boolean isWinner);
    void updateCoins(String username);
}
