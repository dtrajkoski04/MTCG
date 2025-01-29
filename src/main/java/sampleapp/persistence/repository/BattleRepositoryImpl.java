package sampleapp.persistence.repository;

import sampleapp.persistence.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BattleRepositoryImpl implements BattleRepository {
    private final UnitOfWork unitOfWork;

    public BattleRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public void updateStats(String username, boolean isWinner) throws SQLException {
        int eloChange = isWinner ? 3 : -5;
        int winChange = isWinner ? 1 : 0;
        int lossChange = isWinner ? 0 : 1;

        String sql = """
        UPDATE users
        SET
            games_played = games_played + 1,
            games_won = games_won + ?,
            games_lost = games_lost + ?,
            elo = elo + ?
        WHERE username = ?;
    """;

        try (PreparedStatement stmt = this.unitOfWork.prepareStatement(sql)) {
            stmt.setInt(1, winChange);
            stmt.setInt(2, lossChange);
            stmt.setInt(3, eloChange);
            stmt.setString(4, username);
            stmt.executeUpdate();
            this.unitOfWork.commitTransaction();
        } catch (SQLException e) {
            this.unitOfWork.rollbackTransaction();
            throw new SQLException("Error while updating battle stats", e);
        }
    }


    @Override
    public void updateCoins(String username) throws SQLException {
        String sql = "UPDATE users SET coins = coins + 1 WHERE username = ?";

        try (PreparedStatement stmt = this.unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
            this.unitOfWork.commitTransaction();
        } catch (SQLException e) {
            this.unitOfWork.rollbackTransaction();
            throw new SQLException("Error while updating the coins", e);
        }
    }

}
