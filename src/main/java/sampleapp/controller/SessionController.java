package sampleapp.controller;

import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.http.Method;
import httpserver.server.Request;
import httpserver.server.Response;
import httpserver.server.RestController;
import httpserver.utils.RequestHandler;
import sampleapp.service.SessionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class SessionController extends Controller {
    private final SessionService sessionService;

    public SessionController() {
        this.sessionService = new SessionService();
    }

    public Response handleRequest(Request request) {
        String path = request.getPathname();
        String method = String.valueOf(request.getMethod());

        if (path.equals("/sessions") && method.equals("POST")) {
            return this.loginUser(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{\"message\": \"Invalid endpoint or method\"}"
        );
    }


    public Response loginUser(Request request) {
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
            String token = sessionService.login(username, password);
            return new Response(HttpStatus.OK, ContentType.JSON, "{\"message\": \"Login successful\", \"token\": \"" + token + "\"}");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Invalid credentials")) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\": \"Invalid credentials\"}");
            }
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"An error occurred during login\"}");
        }
    }


}
