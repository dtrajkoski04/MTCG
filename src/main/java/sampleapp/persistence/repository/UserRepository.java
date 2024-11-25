package sampleapp.persistence.repository;

import java.sql.SQLException;

public interface UserRepository {
    String registerUserAndReturnToken(String username, String password) throws SQLException;
    boolean loginUser(String username, String password, String token) throws SQLException;
}



