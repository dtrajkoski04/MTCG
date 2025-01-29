package sampleapp.service;

import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.server.Request;
import httpserver.server.Response;
import sampleapp.DTO.UserDTO;
import sampleapp.exception.DataConflictException;
import sampleapp.model.User;
import sampleapp.persistence.DataAccessException;
import sampleapp.persistence.UnitOfWork;
import sampleapp.persistence.repository.UserRepository;
import sampleapp.persistence.repository.UserRepositoryImpl;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserService {
    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepositoryImpl(new UnitOfWork());
    }

    public String register(String username, String password) throws DataConflictException, SQLException {
        userRepository.registerUser(username, password);
        return "User registered successfully";
    }

    public Optional<UserDTO> getUser(String username) throws SQLException {
        Optional<User> user = userRepository.getUserByUsername(username);
        if (user.isPresent()) {
            return Optional.of(new UserDTO(username, user.get()));
        }
        return Optional.empty();
    }

    public boolean updateUser(String username, UserDTO newUser) {
        try {
            Optional<User> oldUser = this.userRepository.getUserByUsername(username);
            if (oldUser.isPresent()) {
                User user = oldUser.get();
                user.setName(newUser.getName());
                user.setBio(newUser.getBio());
                user.setImage(newUser.getImage());
                this.userRepository.updateUser(user);
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
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
