package sampleapp.persistence.repository;

import sampleapp.model.Battle;
import sampleapp.persistence.DataAccessException;
import sampleapp.persistence.UnitOfWork;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BattleRepositoryImpl implements BattleRepository {
    private final UnitOfWork unitOfWork;

    public BattleRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public void createBattle(Battle battle) throws SQLException {
        String sql = "INSERT INTO battles (player1_username, player2_username, winner_username, log, created_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = unitOfWork.prepareStatement(sql)) {
            pstmt.setString(1, battle.getPlayer1Username());
            pstmt.setString(2, battle.getPlayer2Username());
            pstmt.setString(3, battle.getWinnerUsername());
            pstmt.setString(4, battle.getLog());
            pstmt.setTimestamp(5, Timestamp.valueOf(battle.getCreatedAt()));
            pstmt.executeUpdate();
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Error creating battle", e);
        }
    }

    @Override
    public Optional<Battle> getBattleById(int battleId) throws SQLException {
        String sql = "SELECT * FROM battles WHERE id = ?";
        try (PreparedStatement pstmt = unitOfWork.prepareStatement(sql)) {
            pstmt.setInt(1, battleId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToBattle(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching battle by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Battle> getBattlesByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM battles WHERE player1_username = ? OR player2_username = ? ORDER BY created_at DESC";
        List<Battle> battles = new ArrayList<>();
        try (PreparedStatement pstmt = unitOfWork.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                battles.add(mapResultSetToBattle(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching battles for user", e);
        }
        return battles;
    }

    @Override
    public List<Battle> getAllBattles() throws SQLException {
        String sql = "SELECT * FROM battles ORDER BY created_at DESC";
        List<Battle> battles = new ArrayList<>();
        try (PreparedStatement pstmt = unitOfWork.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                battles.add(mapResultSetToBattle(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching all battles", e);
        }
        return battles;
    }

    private Battle mapResultSetToBattle(ResultSet rs) throws SQLException {
        return new Battle(
                rs.getInt("id"),
                rs.getString("player1_username"),
                rs.getString("player2_username"),
                rs.getString("winner_username"),
                rs.getString("log"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
