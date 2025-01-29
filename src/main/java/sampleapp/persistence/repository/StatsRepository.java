package sampleapp.persistence.repository;

import sampleapp.DTO.UserStatsDTO;

import java.sql.SQLException;

public interface StatsRepository {
    UserStatsDTO getUserStats(String username) throws SQLException;
}
