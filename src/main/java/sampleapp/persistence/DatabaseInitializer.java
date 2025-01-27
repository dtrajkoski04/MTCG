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
            statement.execute("DROP TABLE IF EXISTS user_cards CASCADE;");
            statement.execute("DROP TABLE IF EXISTS package_cards CASCADE;");
            statement.execute("DROP TABLE IF EXISTS packages CASCADE;");
            statement.execute("DROP TABLE IF EXISTS cards CASCADE;");
            statement.execute("DROP TABLE IF EXISTS users CASCADE;");

            // Users Tabelle
            statement.execute("""
                CREATE TABLE users (
                    username VARCHAR(50) UNIQUE NOT NULL PRIMARY KEY,
                    password VARCHAR(255) NOT NULL,
                    name VARCHAR(100),
                    bio TEXT,
                    image TEXT,
                    coins INT DEFAULT 20,
                    elo INT DEFAULT 100,
                    games_played INT DEFAULT 0,
                    wins INT DEFAULT 0,
                    losses INT DEFAULT 0
                );
            """);

            // Cards Tabelle
            statement.execute("""
                CREATE TABLE cards (
                    id VARCHAR(255) PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    damage INT NOT NULL,
                    element_type VARCHAR(10) CHECK (element_type IN ('fire', 'water', 'regular')) NOT NULL,
                    card_type VARCHAR(10) CHECK (card_type IN ('spell', 'monster')) NOT NULL
                );
            """);

            // User Cards Tabelle
            statement.execute("""
                CREATE TABLE user_cards (
                    id SERIAL PRIMARY KEY,
                    user_username VARCHAR(255) REFERENCES users(username) ON DELETE CASCADE,
                    card_id VARCHAR(255) REFERENCES cards(id) ON DELETE CASCADE,
                    is_in_deck BOOLEAN DEFAULT FALSE
                );
            """);

            // Packages Tabelle
            statement.execute("""
                CREATE TABLE packages (
                    id SERIAL PRIMARY KEY,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
            """);

            // Package Cards Tabelle
            statement.execute("""
                CREATE TABLE package_cards (
                    package_id INT REFERENCES packages(id) ON DELETE CASCADE,
                    card_id VARCHAR(255) REFERENCES cards(id) ON DELETE CASCADE,
                    PRIMARY KEY (package_id, card_id)
                );
            """);

            System.out.println("Database initialized successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to initialize the database.");
        }
    }
}
