package sampleapp.persistence.repository;

import sampleapp.model.Card;
import sampleapp.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public interface UserRepository {
    void registerUser(String username, String password) throws SQLException;
    String loginUser(String username, String password) throws SQLException;
    Optional<User> getUserByUsername(String username) throws SQLException;
    void updateUser(User user) throws SQLException;
    User mapResultSetToUser(ResultSet resultSet) throws SQLException;
    void addCardToUser(String username, String card) throws SQLException;
}
