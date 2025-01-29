package sampleapp.persistence.repository;

import java.sql.SQLException;

public interface BattleRepository {
    void updateStats(String username, boolean isWinner) throws SQLException;
    void updateCoins(String username) throws SQLException;
}
