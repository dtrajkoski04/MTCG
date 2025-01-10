package sampleapp.persistence.repository;

import sampleapp.persistence.UnitOfWork;
import sampleapp.persistence.DataAccessException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
            if (e.getSQLState().equals("23505")) { // Unique constraint violation
                return null;
            }
            throw new DataAccessException("Failed to register user", e);
        }
    }

    @Override
    public String loginUser(String username, String password) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ? AND password = ?";
        String updateTokenSql = "INSERT INTO user_tokens (username, token) VALUES (?, ?) " +
                "ON CONFLICT (username) DO UPDATE SET token = EXCLUDED.token";

        try (PreparedStatement pstmt = unitOfWork.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String token = generateToken(username);

                try (PreparedStatement tokenStmt = unitOfWork.prepareStatement(updateTokenSql)) {
                    tokenStmt.setString(1, username);
                    tokenStmt.setString(2, token);
                    tokenStmt.executeUpdate();

                    unitOfWork.commitTransaction();
                    return token;
                }
            }
            return null;
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to log in user", e);
        }
    }

    private String generateToken(String username) {
        return username + "-mtcgtoken";
    }
}
