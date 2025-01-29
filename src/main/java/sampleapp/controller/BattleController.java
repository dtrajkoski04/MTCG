package sampleapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    public Response handleRequest(Request request) throws JsonProcessingException {
        String path = request.getPathname();
        String method = String.valueOf(request.getMethod());

        if(path.equals("/battles") && method.equals("POST")) {
            return this.battle(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{\"message\": \"Invalid endpoint or method\"}"
        );
    }

    public Response battle(Request request) throws JsonProcessingException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring("Bearer ".length());
            String username = token.split("-")[0];

            if (!UserService.checkAuth(username, "Bearer " + token)) {
                return new Response(HttpStatus.OK, ContentType.JSON, "{\"message\": \"Access Token missing or Invalid\"}");
            }

            String battleLog = this.battleService.startBattle(username);
            return new Response(HttpStatus.OK, ContentType.JSON, battleLog);
        }
        return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\": \"Unauthorized\"}");
    }

}
