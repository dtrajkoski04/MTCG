package sampleapp.persistence.repository;

import sampleapp.model.Battle;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BattleRepository {
    void createBattle(Battle battle) throws SQLException;

    Optional<Battle> getBattleById(int battleId) throws SQLException;

    List<Battle> getBattlesByUsername(String username) throws SQLException;

    List<Battle> getAllBattles() throws SQLException;
}
