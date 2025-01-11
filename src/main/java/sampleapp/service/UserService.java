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
    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepositoryImpl(new UnitOfWork());
    }

    public Response register(Request request) {
        try {
            String username = null;
            String password = null;

            if (request.getParams() != null && !request.getParams().isEmpty()) {
                // Query-Parameter verarbeiten
                Map<String, String> queryParams = Arrays.stream(request.getParams().split("&"))
                        .map(param -> param.split("="))
                        .collect(Collectors.toMap(param -> param[0], param -> param[1]));
                username = queryParams.get("username");
                password = queryParams.get("password");
            } else if (request.getBody() != null && !request.getBody().isEmpty()) {
                // JSON-Body verarbeiten
                Map<String, String> requestData = this.getObjectMapper().readValue(request.getBody(), Map.class);
                username = requestData.get("username");
                password = requestData.get("password");
            }

            if (username == null || password == null) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Username and password are required\"}");
            }

            String result = userRepository.registerUser(username, password);

            if (result != null) {
                return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\": \"" + result + "\"}");
            } else {
                return new Response(HttpStatus.CONFLICT, ContentType.JSON, "{\"message\": \"User already exists\"}");
            }
        } catch (JsonProcessingException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Invalid JSON format\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"An error occurred during registration\"}");
        }
    }

    public static boolean checkAuth(String username, String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return false;
        }
        return token.equals("Bearer %s-mtcgtoken".formatted(username));
    }
}
