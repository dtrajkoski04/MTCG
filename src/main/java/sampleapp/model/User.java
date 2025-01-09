package sampleapp.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private String token;
    private List<Card> stack;
    private List<Card> deck;

    // Constructor for creating a user with no token (e.g., during registration)
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.stack = new ArrayList<>();
        this.deck = new ArrayList<>();
    }

    // Constructor for creating a user with an existing token (e.g., fetched from the database)
    public User(String username, String password, String token) {
        this.username = username;
        this.password = password;
        this.token = token;
    }

    // Getters and setters for all fields
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Card> getStack() {
        return stack;
    }

    public void setStack(List<Card> stack) {
        this.stack = stack;
    }

    public List<Card> getDeck() {
        return deck;
    }

    public void setDeck(List<Card> deck) {
        this.deck = deck;
    }

    // Utility methods for cards
    public void addCardToStack(Card card) { stack.add(card); }

    public boolean addCardToDeck(Card card) {
        if (deck.size() < 4 && stack.contains(card)) {
            deck.add(card);
            return true;
        }
        return false;
    }

    public void removeCardFromDeck(Card card) { deck.remove(card); }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", token='" + token + '\'' +
                ", stack=" + stack +
                ", deck=" + deck +
                '}';
    }
}
