package sampleapp.persistence.repository;

import sampleapp.persistence.UnitOfWork;
import sampleapp.persistence.DataAccessException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {
    private final UnitOfWork unitOfWork;

    public UserRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public void registerUser(String username, String password) throws SQLException {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (PreparedStatement pstmt = unitOfWork.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            pstmt.executeUpdate();
            unitOfWork.commitTransaction();

        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            // Prüfen, ob der Fehler durch ein Duplikat in der Datenbank verursacht wurde
            if (e.getSQLState().equals("23505")) { // PostgreSQL-Code für Unique Constraint Violation
                throw new IllegalArgumentException("User with the given username already exists");
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
            return null; // Benutzername oder Passwort falsch
        } catch (SQLException e) {
            throw new DataAccessException("Failed to log in user", e);
        }
    }

}
