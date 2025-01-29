package sampleapp.persistence.repository;

import sampleapp.DTO.UserStatsDTO;
import sampleapp.exception.ResourceNotFoundException;
import sampleapp.persistence.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatsRepositoryImpl implements StatsRepository {
    private final UnitOfWork unitOfWork;

    public StatsRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }


    @Override
    public UserStatsDTO getUserStats(String username) throws SQLException, ResourceNotFoundException {
        String sql = "SELECT u.elo, u.games_played, u.games_won, u.games_lost " +
                "FROM users u " +
                "WHERE u.username = ?";

        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserStatsDTO(
                            username,
                            rs.getInt("elo"),
                            rs.getInt("games_played"),
                            rs.getInt("games_won"),
                            rs.getInt("games_lost")
                    );
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to fetch user stats", e);
        }

        throw new ResourceNotFoundException("User stats not found");
    }

}
