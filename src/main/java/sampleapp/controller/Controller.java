package sampleapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import httpserver.server.Request;
import httpserver.server.Response;
import httpserver.server.RestController;

public abstract class Controller {
    public abstract Response handleRequest(Request request) throws JsonProcessingException;

    private final ObjectMapper mapper;

    public Controller() {
        this.mapper = new ObjectMapper();
        this.mapper.configure(
                MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true
        );
    }

    protected JsonNode toJsonNode(String body) throws JsonProcessingException {
        return this.mapper.readTree(body);
    }


}
