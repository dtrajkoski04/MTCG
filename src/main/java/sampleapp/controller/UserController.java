package sampleapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.http.Method;
import httpserver.server.Request;
import httpserver.server.Response;
import httpserver.server.RestController;
import sampleapp.DTO.UserDTO;
import sampleapp.exception.DataConflictException;
import sampleapp.service.UserService;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

public class UserController extends Controller {
    private final UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    @Override
    public Response handleRequest(Request request) {
        String path = request.getPathname();
        String method = String.valueOf(request.getMethod());
        if (path.equals("/users") && method.equals("POST")) {
            return this.registerUser(request);
        } else if(path.startsWith("/users") && method.equals("GET")) {
            return this.getUser(request);
        } else if(path.startsWith("/users") && method.equals("PUT")) {
            return this.updateUser(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{\"message\": \"Invalid endpoint or method\"}"
        );
    }

    public Response registerUser(Request request) {
        ObjectMapper objectMapper = new ObjectMapper();
        String username = null;
        String password = null;

        try {
            // Parse JSON body
            Map<String, String> body = objectMapper.readValue(request.getBody(), Map.class);
            username = body.get("Username");
            password = body.get("Password");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Invalid JSON format\"}");
        }

        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Username and password are required\"}");
        }

        try {
            String result = userService.register(username, password);
            return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\": \"" + result + "\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"" + e.getMessage() + "\"}");
        }
    }


    public Response getUser(Request request) {
        String[] pathSegments = request.getPathname().split("/");

        if(pathSegments.length != 3) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Invalid endpoint or method\"}");
        }

        String username = pathSegments[2];

        if(username.isBlank()) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Username is required\"}");
        }

        String token = request.getHeader("Authorization");

        if(!UserService.checkAuth(username, token)) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\": \"Invalid token\"}");
        }

        Optional<UserDTO> userDTO = null;
        try {
            userDTO = userService.getUser(username);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if(userDTO.isPresent()) {
            String jsonResponse = String.format(
                    "{ \"Name\": \"%s\", \"Bio\": \"%s\", \"Image\": \"%s\" }",
                    userDTO.get().getName(),
                    userDTO.get().getBio(),
                    userDTO.get().getImage()
            );

            return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);
        }
        return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"message\": \"User not found\"}");
    }

    public Response updateUser(Request request) {
        String[] pathSegments = request.getPathname().split("/");
        if(pathSegments.length != 3) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Invalid endpoint or method\"}");
        }

        String username = pathSegments[2];

        if(username.isEmpty()) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Username is required\"}");
        }

        String token = request.getHeader("Authorization");

        if(!UserService.checkAuth(username, token)) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\": \"Invalid token\"}");
        }

        // Den Body extrahieren und UserDTO erstellen
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserDTO userDTO = objectMapper.readValue(request.getBody(), UserDTO.class);

            boolean updateSuccess = this.userService.updateUser(username, userDTO);

            if(updateSuccess) {
                return new Response(HttpStatus.OK, ContentType.JSON, "{\"message\": \"User updated successfully\"}");
            }

            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"message\": \"User not found\"}");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Invalid JSON format\"}");
        }


    }

}
