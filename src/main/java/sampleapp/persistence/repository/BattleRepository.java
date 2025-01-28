package sampleapp.persistence.repository;

import sampleapp.model.Battle;

import java.util.List;
import java.util.Optional;

public interface BattleRepository {
    void createBattle(Battle battle);
    Optional<Battle> findBattleById(int id);
    List<Battle> findAllBattles();
    List<Battle> findBattleByUsername(String username);
}
