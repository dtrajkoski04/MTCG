
import httpserver.server.Server;
import httpserver.utils.Router;
import sampleapp.controller.UserController;
import sampleapp.persistence.DatabaseInitializer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting application...");

        // Datenbank initialisieren
        DatabaseInitializer.initializeDatabase();

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
        router.addService("/users", new UserController());
        router.addService("/sessions", new UserController());

        return router;
    }
}
