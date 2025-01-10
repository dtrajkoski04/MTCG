package sampleapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.server.Request;
import httpserver.server.Response;
import sampleapp.persistence.UnitOfWork;
import sampleapp.persistence.repository.CardRepository;
import sampleapp.persistence.repository.CardRepositoryImpl;
import sampleapp.persistence.repository.UserRepository;
import sampleapp.persistence.repository.UserRepositoryImpl;

public class CardService extends AbstractService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public CardService() {
        this.cardRepository = new CardRepositoryImpl(new UnitOfWork());
        this.userRepository = new UserRepositoryImpl(new UnitOfWork());
    }

    public Response getAllCards(Request request) {
        try {
            String authorizationHeader = request.getHeader("authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\": \"Invalid or missing token\"}");
            }

            String token = authorizationHeader.substring("Bearer ".length());
            String username = token.split("-")[0];

            var user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            var cards = cardRepository.findAllByUserId(user.getId());

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(cards);

            return new Response(HttpStatus.OK, ContentType.JSON, jsonResponse);
        } catch (JsonProcessingException e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"Error serializing cards\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, "{\"message\": \"An error occurred while fetching cards\"}");
        }
    }
}
