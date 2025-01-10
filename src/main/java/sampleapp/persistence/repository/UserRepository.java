package sampleapp.persistence.repository;

import java.sql.SQLException;
import java.util.Optional;

public interface UserRepository {
    String registerUser(String username, String password) throws SQLException;

    String loginUser(String username, String password) throws SQLException;

    Optional<Long> findUserIdByUsername(String username) throws SQLException;

    boolean updateUserCoins(long userId, int coins) throws SQLException;
}
