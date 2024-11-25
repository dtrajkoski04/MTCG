
import httpserver.server.Server;
import httpserver.utils.Router;
import sampleapp.controller.EchoController;
import sampleapp.controller.WeatherController;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(10001, configureRouter());
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Router configureRouter()
    {
        Router router = new Router();
        router.addService("/weather", new WeatherController());
        router.addService("/echo", new EchoController());

        return router;
    }
}
