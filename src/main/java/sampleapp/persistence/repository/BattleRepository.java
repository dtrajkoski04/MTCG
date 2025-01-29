package sampleapp.persistence.repository;

import java.sql.SQLException;

public interface BattleRepository {
    void updateStats(String username, String result) throws SQLException;
    void updateCoins(String username) throws SQLException;
}
