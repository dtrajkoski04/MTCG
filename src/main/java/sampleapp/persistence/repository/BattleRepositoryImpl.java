package sampleapp.persistence.repository;

import sampleapp.model.Battle;
import sampleapp.persistence.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BattleRepositoryImpl implements BattleRepository {
    private final UnitOfWork unitOfWork;

    public BattleRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public void createBattle(Battle battle) {
        String sql = "INSERT INTO battles (player1_username, player2_username, winner_username, log) VALUES (?, ?, ?, ?)";

        try(PreparedStatement stmt = this.unitOfWork.prepareStatement(sql)){
            stmt.setString(1, battle.getPlayer1Username());
            stmt.setString(2, battle.getPlayer2Username());
            stmt.setString(3, battle.getWinnerUsername());
            stmt.setString(4, battle.getLog());
            stmt.executeUpdate();

            try(ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    battle.setId(rs.getInt(1));
                }
            }

            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating Battle", e);
        }
    }

    @Override
    public Optional<Battle> findBattleById(int id) {
        String sql = "SELECT * FROM battles WHERE id = ?";

        try(PreparedStatement stmt = this.unitOfWork.prepareStatement(sql)){
            stmt.setInt(1, id);
            try(ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBattle(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding Battle by ID", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Battle> findAllBattles() {
        String sql = "SELECT * FROM battles ORDER BY created_at DESC";
        List<Battle> battles = new ArrayList<>();

        try(PreparedStatement stmt = this.unitOfWork.prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                battles.add(mapResultSetToBattle(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all Battles", e);
        }

        return battles;
    }

    @Override
    public List<Battle> findBattleByUsername(String username) {
        String sql = "SELECT * FROM battles WHERE player1_username = ? OR player2_username = ?";
        List<Battle> battles = new ArrayList<>();

        try(PreparedStatement stmt = this.unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, username);
            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    battles.add(mapResultSetToBattle(rs));
                }
            }
        } catch(SQLException e){
            throw new RuntimeException("Error finding Battle by Username", e);
        }

        return List.of();
    }

    private Battle mapResultSetToBattle(ResultSet rs) throws SQLException {
        return new Battle(
                rs.getInt("id"),
                rs.getString("player1_username"),
                rs.getString("player2_username"),
                rs.getObject("winner_username") != null ? rs.getString("winner_username") : null,
                rs.getString("log"),
                rs.getTimestamp("created_at").toLocalDateTime()
                );
    }
}
