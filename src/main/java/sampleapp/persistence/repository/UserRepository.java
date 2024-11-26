package sampleapp.persistence.repository;

import java.sql.SQLException;

public interface UserRepository {
    String registerUser(String username, String password) throws SQLException;
    String loginUser(String username, String password) throws SQLException;
}



