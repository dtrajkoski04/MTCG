package sampleapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.server.Request;
import httpserver.server.Response;
import sampleapp.DTO.UserStatsDTO;
import sampleapp.model.User;
import sampleapp.service.UserService;
import sampleapp.service.UserStatsService;

import java.sql.SQLException;

public class UserStatsController extends Controller {
    private final UserStatsService userStatsService;

    public UserStatsController() {
        this.userStatsService = new UserStatsService();
    }

    @Override
    public Response handleRequest(Request request) throws JsonProcessingException {
        String path = request.getPathname();
        String method = String.valueOf(request.getMethod());

        if(path.equals("/stats") && method.equals("GET")) {
            return this.getUserStats(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{\"message\": \"Invalid endpoint or method\"}"
        );
    }

    public Response getUserStats(Request request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String username = token.split("-")[0];

            if (!UserService.checkAuth(username, "Bearer " + token)) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Access Token missing or invalid");
            }

            try {
                UserStatsDTO userStatsDTO = userStatsService.getUserStats(username);
                String jsonResponse = String.format(
                        "{ \"Elo\": \"%s\", \"Games Played\": \"%s\", \"Games Won\": \"%s\", \"Games Lost\": \"%s\" }",
                        userStatsDTO.getElo(),
                        userStatsDTO.getGames_played(),
                        userStatsDTO.getGames_won(),
                        userStatsDTO.getGames_lost()
                );
                return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Unauthorized");
    }
}
