package sampleapp.persistence.repository;

import sampleapp.DTO.UserStatsDTO;

public interface StatsRepository {
    UserStatsDTO getUserStats(String username);
}
