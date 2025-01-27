package sampleapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.server.Request;
import httpserver.server.Response;
import sampleapp.model.Card;
import sampleapp.service.DeckService;
import sampleapp.service.UserService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeckController extends Controller {
    private final ObjectMapper mapper = new ObjectMapper();
    private final DeckService deckService;

    public DeckController() {
        this.deckService = new DeckService();
    }

    @Override
    public Response handleRequest(Request request) throws JsonProcessingException {
        String path = request.getPathname();
        String method = String.valueOf(request.getMethod());
        String format = request.getParams("format"); // Query-Parameter "format" extrahieren

        if (path.equals("/deck") && method.equals("GET")) {
            if ("plain".equalsIgnoreCase(format)) {
                return this.getDeckPlain(request);
            } else {
                return this.getDeck(request);
            }
        } else if (path.equals("/deck") && method.equals("PUT")) {
            return this.configureDeck(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{\"message\": \"Invalid endpoint or method\"}"
        );
    }


    private Response configureDeck(Request request) throws JsonProcessingException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String username = token.split("-")[0];

            if (!UserService.checkAuth(username, "Bearer " + token)) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Access Token missing or invalid");
            }

            List<String> cardIds = new ArrayList<>();
            JsonNode jsonNode = toJsonNode(request.getBody());

            // Prüfen, ob das JSON ein Array ist
            if (jsonNode.isArray()) {
                for (JsonNode card : jsonNode) {
                    if (card.isTextual()) { // Überprüfen, ob der Knoten ein String ist
                        cardIds.add(card.asText());
                    } else {
                        return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "Invalid card format");
                    }
                }
            } else {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "Expected an array of card IDs");
            }

            // Deck konfigurieren
            try {
                deckService.configureDeck(username, cardIds);
            } catch (SQLException e) {
                return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, e.getMessage());
            }
            return new Response(HttpStatus.OK, ContentType.JSON, "Deck configured successfully");
        } else {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Unauthorized");
        }
    }


    private Response getDeck(Request request) throws JsonProcessingException {
        String header = request.getHeader("Authorization");
        if(header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String username = token.split("-")[0];
            if(!UserService.checkAuth(username,"Bearer " + token)){
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Access Token missing or invalid");
            }

            List<Card> deck = null;
            try {
                deck = deckService.getDeck(username);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            String jsonCards = mapper.writeValueAsString(deck);
            return new Response(HttpStatus.OK, ContentType.JSON, jsonCards);
        }
        return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Unauthorized");
    }

    private Response getDeckPlain(Request request) throws JsonProcessingException {
        String header = request.getHeader("Authorization");
        if(header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String username = token.split("-")[0];
            if(!UserService.checkAuth(username,"Bearer " + token)){
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Access Token missing or invalid");
            }

            List<Card> deck = null;
            try {
                deck = deckService.getDeck(username);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            StringBuilder builder = new StringBuilder();
            for (Card card : deck) {
                builder.append(card.toString());
            }
            return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, builder.toString());
        }
        return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Unauthorized");
    }
}
