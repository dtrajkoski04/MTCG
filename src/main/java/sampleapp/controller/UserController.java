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
import sampleapp.service.UserService;

import java.sql.SQLException;
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
        String username = request.getParams("username");
        String password = request.getParams("password");

        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Username and password are required\"}");
        }

        try {
            String result = userService.register(username, password);
            return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\": \"" + result + "\"}");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("already exists")) {
                return new Response(HttpStatus.CONFLICT, ContentType.JSON, "{\"message\": \"User already exists\"}");
            }
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"An error occurred during registration\"}");
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
        return null;
    }

}
