package httpserver.server;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface RestController {
    Response handleRequest(Request request) throws JsonProcessingException;
}
