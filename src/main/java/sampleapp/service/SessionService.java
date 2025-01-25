package sampleapp.service;

import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.server.Request;
import httpserver.server.Response;
import sampleapp.persistence.DataAccessException;
import sampleapp.persistence.UnitOfWork;
import sampleapp.persistence.repository.UserRepository;
import sampleapp.persistence.repository.UserRepositoryImpl;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class SessionService {
    private UserRepository userRepository;

    public SessionService() {
        userRepository = new UserRepositoryImpl(new UnitOfWork());
    }

    public String login(String username, String password) {
        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            throw new IllegalArgumentException("Username and password are required");
        }

        try {
            String token = userRepository.loginUser(username, password);
            if (token == null) {
                throw new IllegalArgumentException("Invalid credentials");
            }
            return token;
        } catch (IllegalArgumentException e) {
            // Weitergeben spezifischer Eingabefehler wie ungültige Anmeldedaten
            throw e;
        } catch (DataAccessException e) {
            // Allgemeiner Datenbankfehler
            throw new RuntimeException("An error occurred during login", e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
