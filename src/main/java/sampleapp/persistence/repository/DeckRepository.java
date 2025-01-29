package sampleapp.persistence.repository;

import sampleapp.model.Card;

import java.sql.SQLException;
import java.util.List;

public interface DeckRepository {
    List<Card> getDeck(String username) throws SQLException;
    void addToDeck(String username, String id) throws SQLException;
    void removeFromDeck(String username, String id) throws SQLException;
}
