package sampleapp.persistence.repository;

import sampleapp.DTO.UserStatsDTO;

import java.sql.SQLException;
import java.util.List;

public interface ScoreboardRepository {
    List<UserStatsDTO> getUserStats() throws SQLException;
}
