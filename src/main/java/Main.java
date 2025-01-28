
import httpserver.server.Server;
import httpserver.utils.Router;
import sampleapp.controller.*;
import sampleapp.persistence.DatabaseInitializer;
import sampleapp.persistence.repository.CardRepository;

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
        router.addService("/sessions", new SessionController());
        router.addService("/packages", new PackageController());
        router.addService("/transactions", new PackageController());
        router.addService("/cards", new CardController());
        router.addService("/deck", new DeckController());
        router.addService("/stats", new UserStatsController());
        router.addService("/scoreboard", new ScoreboardController());
        router.addService("/battles", new BattleController());

        return router;
    }
}
