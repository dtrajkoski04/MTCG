package sampleapp.service;

import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.server.Request;
import httpserver.server.Response;
import sampleapp.persistence.DataAccessException;
import sampleapp.persistence.UnitOfWork;
import sampleapp.persistence.repository.UserRepository;
import sampleapp.persistence.repository.UserRepositoryImpl;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class UserService {
    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepositoryImpl(new UnitOfWork());
    }

    public String register(String username, String password) {
        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            throw new IllegalArgumentException("Username and password are required");
        }

        try {
            userRepository.registerUser(username, password);
            return "User registered successfully";
        } catch (IllegalArgumentException e) {
            // Weiterwerfen für spezifische Fehler wie "User existiert bereits"
            throw e;
        } catch (DataAccessException | SQLException e) {
            // Allgemeiner Datenbankfehler
            throw new RuntimeException("An error occurred during registration", e);
        }
    }


    public static boolean checkAuth(String username, String token) {
        System.out.println(username);
        System.out.println(token);
        if (token == null || !token.startsWith("Bearer ")) {
            return false;
        }
        return token.equals("Bearer %s-mtcgToken".formatted(username));
    }
}
