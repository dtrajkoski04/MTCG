package sampleapp.controller;

import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.server.Request;
import httpserver.server.Response;
import sampleapp.service.BattleService;
import sampleapp.service.UserService;

public class BattleController extends Controller {
    private final BattleService battleService;

    public BattleController() {
        this.battleService = new BattleService();
    }

    @Override
    public Response handleRequest(Request request) {
        String path = request.getPathname();
        String method = String.valueOf(request.getMethod());

        if (path.equals("/battles") && method.equals("POST")) {
            return this.startBattle(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{\"message\": \"Invalid endpoint or method\"}"
        );
    }

    private Response startBattle(Request request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\": \"Authorization header missing or invalid\"}");
        }

        String username = token.substring("Bearer ".length()).split("-")[0];
        if (!UserService.checkAuth(username, token)) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\": \"Access token is missing or invalid\"}");
        }

        try {
            String log = battleService.startBattle(username);
            return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, log);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Failed to start battle\"}");
        }
    }
}
