package sampleapp.service;

import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.server.Request;
import httpserver.server.Response;
import sampleapp.exception.AuthenticationException;
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

    public SessionService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String login(String username, String password) throws AuthenticationException, SQLException {
        return userRepository.loginUser(username, password);
    }


}
