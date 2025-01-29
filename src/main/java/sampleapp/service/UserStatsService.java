package sampleapp.service;

import sampleapp.DTO.UserStatsDTO;
import sampleapp.exception.ResourceNotFoundException;
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

    public UserStatsDTO getUserStats(String username) throws SQLException, ResourceNotFoundException {
        var user = userRepository.getUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return statsRepository.getUserStats(user.getUsername());
    }

}
