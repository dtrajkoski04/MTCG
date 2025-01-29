package sampleapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.server.Request;
import httpserver.server.Response;
import sampleapp.DTO.UserStatsDTO;
import sampleapp.service.ScoreboardService;
import sampleapp.service.UserService;

import java.sql.SQLException;
import java.util.List;

public class ScoreboardController extends Controller {
    private final ScoreboardService scoreboardService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ScoreboardController() {
        this.scoreboardService = new ScoreboardService();
    }

    @Override
    public Response handleRequest(Request request) throws JsonProcessingException {
        String path = request.getPathname();
        String method = String.valueOf(request.getMethod());

        if (path.equals("/scoreboard") && method.equals("GET")) {
            return this.getScoreboard(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{\"message\": \"Invalid endpoint or method\"}"
        );
    }

    private Response getScoreboard(Request request) {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\": \"Access Token missing\"}");
        }

        String token = header.substring("Bearer ".length());
        String username = token.split("-")[0];

        if (!UserService.checkAuth(username, "Bearer " + token)) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\": \"Access Token missing or invalid\"}");
        }

        try {
            List<UserStatsDTO> scoreboard = scoreboardService.getScoreboard();
            return new Response(HttpStatus.OK, ContentType.JSON, objectMapper.writeValueAsString(scoreboard));
        } catch (SQLException e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Database error while fetching scoreboard\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"An unexpected error occurred\"}");
        }
    }

}
