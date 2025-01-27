package sampleapp.persistence.repository;

import sampleapp.DTO.UserStatsDTO;

import java.util.List;

public interface ScoreboardRepository {
    List<UserStatsDTO> getUserStats();
}
