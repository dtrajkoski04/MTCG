package sampleapp.controller;

import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.http.Method;
import httpserver.server.Request;
import httpserver.server.Response;
import httpserver.server.RestController;
import sampleapp.service.UserService;

public class UserController implements RestController {
    private final UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.POST &&
                request.getPathParts().size() > 1 &&
                request.getPathParts().get(1).equals("register")) {
            return this.userService.register(request);
        } else if (request.getMethod() == Method.POST &&
                request.getPathParts().size() > 1 &&
                request.getPathParts().get(1).equals("login")) {
            return this.userService.login(request);
        } else if (request.getMethod() == Method.POST &&
                request.getPathname().equals("/register")) { // Handle query params for register
            return this.userService.register(request);
        } else if (request.getMethod() == Method.POST &&
                request.getPathname().equals("/login")) { // Handle query params for login
            return this.userService.login(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{\"message\": \"Invalid endpoint or method\"}"
        );
    }
}
