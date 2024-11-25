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
    public boolean registerUser(String username, String password) throws SQLException {
        String sql = "INSERT INTO users (username, password, token) VALUES (?, ?, ?)";
        String token = generateToken();

        try (PreparedStatement pstmt = unitOfWork.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, token);

            int rowsAffected = pstmt.executeUpdate();
            unitOfWork.commitTransaction(); // Commit the transaction
            return rowsAffected > 0;
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction(); // Rollback the transaction on failure
            throw new DataAccessException("Failed to register user", e);
        }
    }

    @Override
    public boolean loginUser(String username, String password, String token) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND token = ?";

        try (PreparedStatement pstmt = unitOfWork.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, token);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                unitOfWork.commitTransaction(); // Commit the transaction if successful
                return true;
            }
            return false; // No matching record found
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
