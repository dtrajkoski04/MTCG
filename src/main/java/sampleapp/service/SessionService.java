package sampleapp.service;

import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.server.Request;
import httpserver.server.Response;
import sampleapp.persistence.UnitOfWork;
import sampleapp.persistence.repository.UserRepository;
import sampleapp.persistence.repository.UserRepositoryImpl;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class SessionService extends AbstractService {
    private UserRepository userRepository;

    public SessionService() {
        userRepository = new UserRepositoryImpl(new UnitOfWork());
    }

    // POST /login or GET /login
    public Response login(Request request) {
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

            // Attempt login and generate token
            String token = userRepository.loginUser(username, password);

            if (token != null) {
                return new Response(HttpStatus.OK, ContentType.JSON, "{\"message\": \"Login successful\", \"token\": \"" + token + "\"}");
            } else {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\": \"Invalid credentials\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"An error occurred during login\"}");
        }
    }
}
