package sampleapp.service;

import sampleapp.DTO.UserStatsDTO;
import sampleapp.persistence.UnitOfWork;
import sampleapp.persistence.repository.ScoreboardRepository;
import sampleapp.persistence.repository.ScoreboardRepositoryImpl;

import java.sql.SQLException;
import java.util.List;

public class ScoreboardService {
    private final ScoreboardRepository scoreboardRepository;

    public ScoreboardService() {
        this.scoreboardRepository = new ScoreboardRepositoryImpl(new UnitOfWork());
    }

    public ScoreboardService(ScoreboardRepository scoreboardRepository) {
        this.scoreboardRepository = scoreboardRepository;
    }

    public List<UserStatsDTO> getScoreboard() throws SQLException {
        return scoreboardRepository.getUserStats();
    }

}
