package sampleapp.service;

import sampleapp.DTO.UserStatsDTO;
import sampleapp.persistence.UnitOfWork;
import sampleapp.persistence.repository.ScoreboardRepository;
import sampleapp.persistence.repository.ScoreboardRepositoryImpl;

import java.util.List;

public class ScoreboardService {
    private final ScoreboardRepository scoreboardRepository;

    public ScoreboardService() {
        this.scoreboardRepository = new ScoreboardRepositoryImpl(new UnitOfWork());
    }

    public List<UserStatsDTO> getScoreboard() {
        return scoreboardRepository.getUserStats();
    }

}
