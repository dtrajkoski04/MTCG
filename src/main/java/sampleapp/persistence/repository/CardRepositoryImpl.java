package sampleapp.persistence.repository;

import sampleapp.model.Card;
import sampleapp.persistence.DataAccessException;
import sampleapp.persistence.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CardRepositoryImpl implements CardRepository {
    private UnitOfWork unitOfWork;

    @Override
    public void save(Card card) {
        String cardSql = "INSERT INTO cards (id, name, damage, element_type, card_type) VALUES (?, ?, ?, ?, ?)";

        try(PreparedStatement stmt = unitOfWork.prepareStatement(cardSql)) {
            stmt.setString(1, card.getId());
            stmt.setString(2, card.getName());
            stmt.setInt(3, card.getDamage());
            stmt.setString(4, card.getElementType());
            stmt.setString(5, card.getCardType());
            stmt.executeQuery();
            unitOfWork.commitTransaction();
        } catch(SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to create Card");
        }
    }
}