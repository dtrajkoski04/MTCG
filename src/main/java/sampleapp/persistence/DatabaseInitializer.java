package sampleapp.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseInitializer {
    private static final String URL = "jdbc:postgresql://localhost:5432/MTCG";
    private static final String USER = "MTCG";
    private static final String PASSWORD = "MTCG";

    public static void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            // Tabellen zurücksetzen und neu erstellen
            statement.execute("DROP TABLE IF EXISTS users CASCADE;");
            statement.execute("DROP TABLE IF EXISTS cards CASCADE;");

            statement.execute("""
                CREATE TABLE users (
                    username VARCHAR(255) PRIMARY KEY,
                    password VARCHAR(255) NOT NULL,
                    coins INT DEFAULT 20,
                    elo INT DEFAULT 100,
                    wins INT DEFAULT 0,
                    losses INT DEFAULT 0
                );
            """);

            statement.execute("""
                CREATE TABLE cards (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    damage INT NOT NULL,
                    element VARCHAR(50) NOT NULL,
                    type VARCHAR(50) NOT NULL,
                    owner_username VARCHAR(255),
                    FOREIGN KEY (owner_username) REFERENCES users(username)
                );
            """);

            System.out.println("Database initialized successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to initialize the database.");
        }
    }
}
