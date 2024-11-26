package sampleapp.persistence.repository;

import sampleapp.persistence.UnitOfWork;
import sampleapp.persistence.DataAccessException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepositoryImpl implements UserRepository {

    private UnitOfWork unitOfWork;

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
            unitOfWork.commitTransaction(); // Commit the transaction

            return rowsAffected > 0 ? "User registered successfully" : null;
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction(); // Rollback the transaction on failure
            throw new DataAccessException("Failed to register user", e);
        }
    }



    @Override
    public String loginUser(String username, String password) throws SQLException {
        String selectUserSql = "SELECT * FROM users WHERE username = ? AND password = ?";
        String upsertTokenSql = "INSERT INTO user_tokens (username, token) VALUES (?, ?) " +
                "ON CONFLICT (username) DO UPDATE SET token = EXCLUDED.token";

        try (PreparedStatement selectStmt = unitOfWork.prepareStatement(selectUserSql)) {
            selectStmt.setString(1, username);
            selectStmt.setString(2, password);

            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                // User authenticated, generate a token
                String token = username + "-mtcgtoken";

                try (PreparedStatement tokenStmt = unitOfWork.prepareStatement(upsertTokenSql)) {
                    tokenStmt.setString(1, username);
                    tokenStmt.setString(2, token);

                    tokenStmt.executeUpdate();
                    unitOfWork.commitTransaction(); // Commit the transaction after token update

                    return token;
                }
            }
            return null; // Invalid credentials
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction(); // Rollback the transaction on failure
            throw new DataAccessException("Failed to log in user", e);
        }
    }




    // Private method to generate a unique token
    private String generateToken() {
        return java.util.UUID.randomUUID().toString();
    }
}
