package sampleapp.persistence.repository;

import java.sql.SQLException;
import java.util.Optional;

public interface UserRepository {
    void registerUser(String username, String password) throws SQLException;

    String loginUser(String username, String password) throws SQLException;
}
