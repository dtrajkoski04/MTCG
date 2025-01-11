package httpserver.utils;

import httpserver.server.RestController;
import sampleapp.controller.Controller;

import java.util.HashMap;
import java.util.Map;

public class Router {
    private Map<String, Controller> serviceRegistry = new HashMap<>();

    public void addService(String route, Controller service)
    {
        this.serviceRegistry.put(route, service);
    }

    public void removeService(String route)
    {
        this.serviceRegistry.remove(route);
    }

    public Controller resolve(String route)
    {
        return this.serviceRegistry.get(route);
    }
}
