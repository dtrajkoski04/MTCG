package sampleapp.controller;

import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.http.Method;
import httpserver.server.Request;
import httpserver.server.Response;
import httpserver.server.RestController;
import sampleapp.service.CardService;

public class CardController implements RestController {
    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.GET &&
                request.getPathParts().size() > 1 &&
                request.getPathParts().get(1).equals("cards")) {
            return this.getCards(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{\"message\": \"Invalid endpoint or method\"}"
        );
    }

    private Response getCards(Request request) {
        String authorizationHeader = request.getHeader("authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"error\": \"Invalid or missing token\"}");
        }

        String token = authorizationHeader.substring("Bearer ".length());
        String username = token.split("-")[0];

        try {
            var cards = cardService.getAllCardsByUsername(username);
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    this.cardService.convertCardsToJson(cards)
            );
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"error\": \"Failed to fetch cards\"}");
        }
    }
}
