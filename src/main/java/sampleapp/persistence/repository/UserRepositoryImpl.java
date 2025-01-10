package sampleapp.persistence.repository;

import sampleapp.persistence.UnitOfWork;
import sampleapp.persistence.DataAccessException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {
    private final UnitOfWork unitOfWork;

    public UserRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public String registerUser(String username, String password) throws SQLException {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (PreparedStatement pstmt = unitOfWork.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            int rowsAffected = pstmt.executeUpdate();
            unitOfWork.commitTransaction();

            return rowsAffected > 0 ? "User registered successfully" : null;
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            if ("23505".equals(e.getSQLState())) { // Unique constraint violation
                return null;
            }
            throw new DataAccessException("Failed to register user", e);
        }
    }

    @Override
    public String loginUser(String username, String password) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = unitOfWork.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return username + "-mtcgtoken";
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to log in user", e);
        }
    }

    @Override
    public Optional<Long> findUserIdByUsername(String username) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement pstmt = unitOfWork.prepareStatement(sql)) {
            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(rs.getLong("id"));
            }
            return Optional.empty();
        }
    }

    @Override
    public boolean updateUserCoins(long userId, int coins) throws SQLException {
        String sql = "UPDATE users SET coins = ? WHERE id = ?";
        try (PreparedStatement pstmt = unitOfWork.prepareStatement(sql)) {
            pstmt.setInt(1, coins);
            pstmt.setLong(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            unitOfWork.commitTransaction();

            return rowsAffected > 0;
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to update user coins", e);
        }
    }
}
