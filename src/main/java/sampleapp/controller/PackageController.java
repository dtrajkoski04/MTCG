package sampleapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import httpserver.http.ContentType;
import httpserver.http.HttpStatus;
import httpserver.server.Request;
import httpserver.server.Response;
import httpserver.server.RestController;
import sampleapp.model.Card;
import sampleapp.model.CardInfo;
import sampleapp.model.Package;
import sampleapp.service.CardService;
import sampleapp.service.PackageService;
import sampleapp.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class PackageController extends Controller {

    private PackageService packageService;
    private CardService cardService;

    public PackageController() {
        this.packageService = new PackageService();
        this.cardService = new CardService();
    }


    @Override
    public Response handleRequest(Request request) throws JsonProcessingException {
        String path = request.getPathname();
        String method = String.valueOf(request.getMethod());

        if (path.equals("/packages") && method.equals("POST")) {
            return this.createPackage(request);
        } else if (path.equals("/transactions/packages") && method.equals("POST")) {
            return this.acquirePackages(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{\"message\": \"Invalid endpoint or method\"}"
        );
    }

    private Response acquirePackages(Request request) throws JsonProcessingException {
        String header = request.getHeader("Authorization");
        if(header != null && header.startsWith("Bearer ")) {
            String token = header.substring("Bearer ".length());
            String username = token.split("-")[0];
            if(!UserService.checkAuth(username, "Bearer " + token)){
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\": \"Authorization denied\"}");
            }

            try {
                packageService.acquirePackages(username);
            } catch(Exception e){
                return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.JSON, e.getMessage());
            }
            return new Response(HttpStatus.OK, ContentType.JSON, "{\"message\": \"Package acquired successfully\"}");
        } else {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\": \"Invalid token\"}");
        }
    }

    private Response createPackage(Request request) throws JsonProcessingException {
        String token = request.getHeader("Authorization");
        if (!UserService.checkAuth("admin", token)) {
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "{\"message\": \"Unauthorized access\"}");
        }

        Package pkg = new Package(0);
        List<String> cards = new ArrayList<>();
        JsonNode jsonNode = toJsonNode(request.getBody());
        if (jsonNode.isArray()) {
            for (int i = 0; i < jsonNode.size(); i++) {
                JsonNode card = jsonNode.get(i);
                Card cardObj = new Card();
                cardObj.setId(card.get("Id").asText());
                cardObj.setDamage(card.get("Damage").asInt());
                cardObj.setInfo(CardInfo.fromDisplayName(card.get("Name").asText()));
                System.out.println("Saving card: " + cardObj.getId() + ", " + cardObj.getName() + ", " + cardObj.getDamage());

                cardService.addCard(cardObj);

                cards.add(cardObj.getId());
            }
        }
        this.packageService.save(pkg, cards);
        return new Response(HttpStatus.CREATED, ContentType.JSON, "{\"message\": \"Package created\"}");
    }


}