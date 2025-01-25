package sampleapp.controller;

import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.http.Method;
import httpserver.server.Request;
import httpserver.server.Response;
import httpserver.server.RestController;
import sampleapp.service.UserService;

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

}
