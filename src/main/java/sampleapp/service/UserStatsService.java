package sampleapp.service;

import sampleapp.DTO.UserStatsDTO;
import sampleapp.persistence.UnitOfWork;
import sampleapp.persistence.repository.StatsRepository;
import sampleapp.persistence.repository.StatsRepositoryImpl;
import sampleapp.persistence.repository.UserRepository;
import sampleapp.persistence.repository.UserRepositoryImpl;

import java.sql.SQLException;

public class UserStatsService {
    private final UserRepository userRepository;
    private final StatsRepository statsRepository;

    public UserStatsService() {
        this.userRepository = new UserRepositoryImpl(new UnitOfWork());
        this.statsRepository = new StatsRepositoryImpl(new UnitOfWork());
    }

    public UserStatsDTO getUserStats(String username) throws SQLException {
        var user = this.userRepository.getUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return statsRepository.getUserStats(username);
    }
}
