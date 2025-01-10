package sampleapp.persistence.repository;

import sampleapp.model.Card;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CardRepository {
    void saveCard(Card card) throws SQLException;

    Optional<Card> findCardById(String cardId) throws SQLException;

    List<Card> findAllCardsByUserId(long userId) throws SQLException;

    boolean assignCardToUser(long userId, String cardId) throws SQLException;
}
