package sampleapp.service;

import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.server.Request;
import httpserver.server.Response;
import sampleapp.persistence.UnitOfWork;
import sampleapp.persistence.repository.UserRepository;
import sampleapp.persistence.repository.UserRepositoryImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class UserService extends AbstractService {
    private UserRepository userRepository;

    public UserService() {
        userRepository = new UserRepositoryImpl(new UnitOfWork());
    }

    // POST /register
    public Response register(Request request) {
        try {
            String username = null;
            String password = null;

            // Check for query parameters
            if (request.getParams() != null && !request.getParams().isEmpty()) {
                Map<String, String> queryParams = Arrays.stream(request.getParams().split("&"))
                        .map(s -> s.split("="))
                        .collect(Collectors.toMap(a -> a[0], a -> a[1]));
                username = queryParams.get("username");
                password = queryParams.get("password");
            } else if (request.getBody() != null && !request.getBody().isEmpty()) {
                // Parse JSON body
                Map<String, String> requestData = this.getObjectMapper().readValue(request.getBody(), Map.class);
                username = requestData.get("username");
                password = requestData.get("password");
            }

            // Validate required fields
            if (username == null || password == null) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Username and password are required\"}");
            }

            // Register the user in the repository
            boolean isRegistered = userRepository.registerUser(username, password);

            if (isRegistered) {
                return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\": \"User registered successfully\"}");
            } else {
                return new Response(HttpStatus.CONFLICT, ContentType.JSON, "{\"message\": \"User already exists\"}");
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Invalid JSON format\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"An error occurred during registration\"}");
        }
    }

    // POST /login
    public Response login(Request request) {
        try {
            Map<String, String> requestData = this.getObjectMapper().readValue(request.getBody(), Map.class);
            String username = requestData.get("username");
            String password = requestData.get("password");
            String token = requestData.get("token");

            if (username == null || password == null || token == null) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Username, password, and token are required\"}");
            }

            boolean isLoggedIn = userRepository.loginUser(username, password, token);

            if (isLoggedIn) {
                return new Response(HttpStatus.OK, ContentType.JSON, "{\"message\": \"Login successful\"}");
            } else {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\": \"Invalid credentials or token\"}");
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Invalid JSON format\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"An error occurred during login\"}");
        }
    }
}
