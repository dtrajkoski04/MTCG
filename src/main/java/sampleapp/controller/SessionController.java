package sampleapp.controller;

import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.http.Method;
import httpserver.server.Request;
import httpserver.server.Response;
import httpserver.server.RestController;
import httpserver.utils.RequestHandler;
import sampleapp.service.SessionService;

public class SessionController extends Controller {
    private final SessionService sessionService;

    public SessionController() {
        this.sessionService = new SessionService();
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.POST &&
                request.getPathParts().size() > 1 &&
                request.getPathParts().get(1).equals("sessions")) {
            return this.sessionService.login(request);
        } else if (request.getMethod() == Method.POST &&
                request.getPathname().equals("/sessions")) { // Handle query params for login
            return this.sessionService.login(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{\"message\": \"Invalid endpoint or method\"}"
        );
    }
}
