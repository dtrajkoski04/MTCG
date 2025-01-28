package sampleapp.persistence.repository;

import sampleapp.model.Card;
import sampleapp.model.User;
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
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (PreparedStatement pstmt = unitOfWork.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return username + "-mtcgToken";
            }
            return null; // Benutzername oder Passwort falsch
        } catch (SQLException e) {
            throw new DataAccessException("Failed to log in user", e);
        }
    }

    @Override
    public Optional<User> getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = unitOfWork.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            unitOfWork.commitTransaction();
            if (rs.next()) {
                User user = mapResultSetToUser(rs);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get user", e);
        }
        return Optional.empty();
    }

    @Override
    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET coins = ?, name = ?, bio = ?, image = ?, elo = ?, games_played = ?, games_won = ?, games_lost = ? WHERE username = ?";
        try (PreparedStatement pstmt = unitOfWork.prepareStatement(sql)) {
            pstmt.setInt(1, user.getCoins());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getBio());
            pstmt.setString(4, user.getImage());
            pstmt.setInt(5, user.getElo());
            pstmt.setInt(6, user.getGames_played());
            pstmt.setInt(7, user.getGames_won());
            pstmt.setInt(8, user.getGames_lost());
            pstmt.setString(9, user.getUsername());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                unitOfWork.commitTransaction();
                System.out.println("User updated successfully: " + user.getUsername());
            } else {
                System.out.println("No user found with username: " + user.getUsername());
            }
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction(); // Sicherstellen, dass die Transaktion zurückgesetzt wird
            System.err.println("Error updating user: " + e.getMessage());
            throw e; // Fehler weitergeben
        }
    }


    @Override
    public User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setCoins(rs.getInt("coins"));
        user.setName(rs.getString("name"));
        user.setBio(rs.getString("bio"));
        user.setImage(rs.getString("image"));
        return user;

    }

    @Override
    public void addCardToUser(String username, String cardId) throws SQLException {
        String sql = "INSERT INTO user_cards (user_username, card_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = unitOfWork.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, cardId);
            pstmt.executeUpdate();
            unitOfWork.commitTransaction();
        }catch(SQLException e) {
            unitOfWork.rollbackTransaction();
        }
    }

}
