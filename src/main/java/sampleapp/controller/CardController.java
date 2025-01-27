package sampleapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.server.Request;
import httpserver.server.Response;
import httpserver.server.RestController;
import sampleapp.model.Card;
import sampleapp.service.CardService;
import sampleapp.service.PackageService;
import sampleapp.service.UserService;

import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.List;

public class CardController extends Controller {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private CardService cardService;

    public CardController() {
        this.cardService = new CardService();
    }

    @Override
    public Response handleRequest(Request request) {
        String path = request.getPathname();
        String method = String.valueOf(request.getMethod());

        try {
            if (path.equals("/cards") && method.equals("GET")) {
                return this.getUserCards(request);
            }
        }catch(JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{\"message\": \"Invalid endpoint or method\"}"
        );
    }

    public Response getUserCards(Request request) throws JsonProcessingException, SQLException {
        String header = request.getHeader("Authorization");
        if(header != null && header.startsWith("Bearer ")) {
            String token = header.substring("Bearer ".length());
            String username = token.split("-")[0];

            if(!UserService.checkAuth(username, "Bearer " + token)) {
                return new Response(HttpStatus.OK, ContentType.JSON, "{\"message\": \"Access Token missing or Invalid\"}");
            }

            List<Card> cards = cardService.getAllCardsByUsername(username);
            String jsonCards = objectMapper.writeValueAsString(cards);
            return new Response(HttpStatus.OK, ContentType.JSON, jsonCards);
        }
        return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\": \"Unauthorized\"}");
    }
}