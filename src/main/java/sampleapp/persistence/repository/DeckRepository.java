package sampleapp.persistence.repository;

import sampleapp.model.Card;

import java.util.List;

public interface DeckRepository {
    List<Card> getDeck(String username);
    void addToDeck(String username, String id);
    void removeFromDeck(String username, String id);
}
