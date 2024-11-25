package sampleapp.persistence.repository;

import java.sql.SQLException;

public interface UserRepository {
    boolean registerUser(String username, String password) throws SQLException;
    boolean loginUser(String username, String password, String token) throws SQLException;
}


