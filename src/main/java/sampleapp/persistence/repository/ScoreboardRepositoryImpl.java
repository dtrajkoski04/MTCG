package sampleapp.persistence.repository;

import sampleapp.DTO.UserStatsDTO;
import sampleapp.persistence.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ScoreboardRepositoryImpl implements ScoreboardRepository {
    private final UnitOfWork unitOfWork;

    public ScoreboardRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public List<UserStatsDTO> getUserStats() {
        String sql = "SELECT username, elo, games_played, games_won, games_lost " +
                "FROM users " +
                "ORDER BY elo DESC";
        List<UserStatsDTO> scoreboard = new ArrayList<>();

        try(PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    scoreboard.add(new UserStatsDTO(
                            rs.getString("username"),
                            rs.getInt("elo"),
                            rs.getInt("games_played"),
                            rs.getInt("games_won"),
                            rs.getInt("games_lost")
                    ));
                }
            }
        } catch(SQLException e) {
            throw new RuntimeException("Error fetching scoreboard", e);
        }
        return scoreboard;
    }
}
