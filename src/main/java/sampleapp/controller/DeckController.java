package sampleapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.server.Request;
import httpserver.server.Response;
import sampleapp.exception.ResourceNotFoundException;
import sampleapp.model.Card;
import sampleapp.persistence.DataAccessException;
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

        if (header == null || !header.startsWith("Bearer ")) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\": \"Unauthorized\"}");
        }

        String token = header.substring("Bearer ".length());
        String username = token.split("-")[0];

        if (!UserService.checkAuth(username, "Bearer " + token)) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\": \"Access Token missing or invalid\"}");
        }

        List<String> cardIds = new ArrayList<>();
        JsonNode jsonNode = toJsonNode(request.getBody());

        // Validate JSON input
        if (!jsonNode.isArray()) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Expected an array of card IDs\"}");
        }

        for (JsonNode card : jsonNode) {
            if (card.isTextual()) {
                cardIds.add(card.asText());
            } else {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"Invalid card format\"}");
            }
        }

        try {
            deckService.configureDeck(username, cardIds);
            return new Response(HttpStatus.OK, ContentType.JSON, "{\"message\": \"Deck configured successfully\"}");
        } catch (IllegalArgumentException e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"message\": \"" + e.getMessage() + "\"}");
        } catch (DataAccessException e) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"message\": \"" + e.getMessage() + "\"}");
        } catch (SQLException e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Database error while configuring deck\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"An unexpected error occurred\"}");
        }
    }



    private Response getDeck(Request request) {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\": \"Unauthorized\"}");
        }

        String token = header.substring("Bearer ".length());
        String username = token.split("-")[0];

        if (!UserService.checkAuth(username, "Bearer " + token)) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\": \"Access Token missing or invalid\"}");
        }

        try {
            List<Card> deck = deckService.getDeck(username);
            String jsonCards = mapper.writeValueAsString(deck);
            return new Response(HttpStatus.OK, ContentType.JSON, jsonCards);
        } catch (ResourceNotFoundException e) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"message\": \"" + e.getMessage() + "\"}");
        } catch (SQLException e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Database error while retrieving user deck\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"An unexpected error occurred\"}");
        }
    }


    private Response getDeckPlain(Request request) {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\": \"Unauthorized\"}");
        }

        String token = header.substring("Bearer ".length());
        String username = token.split("-")[0];

        if (!UserService.checkAuth(username, "Bearer " + token)) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\": \"Access Token missing or invalid\"}");
        }

        try {
            List<Card> deck = deckService.getDeck(username);

            StringBuilder builder = new StringBuilder();
            for (Card card : deck) {
                builder.append(card.toString()).append("\n"); // Ensure proper formatting for plain text output
            }

            return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, builder.toString().trim()); // Remove trailing newline
        } catch (ResourceNotFoundException e) {
            return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{\"message\": \"User not found\"}");
        } catch (SQLException e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Database error while retrieving user deck\"}");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"An unexpected error occurred\"}");
        }
    }

}
