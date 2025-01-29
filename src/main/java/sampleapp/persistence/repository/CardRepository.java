package sampleapp.persistence.repository;

import sampleapp.model.Card;

import java.sql.SQLException;
import java.util.List;

public interface CardRepository {
    void save(Card card) throws SQLException;
    List<Card> findAllByUsername(String username);
    boolean isCardOwned(String username, String cardId);
}