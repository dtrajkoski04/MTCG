package sampleapp.persistence.repository;

import sampleapp.model.Card;
import sampleapp.model.ElementType;
import sampleapp.model.MonsterCard;
import sampleapp.model.SpellCard;
import sampleapp.persistence.UnitOfWork;
import sampleapp.persistence.DataAccessException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CardRepositoryImpl implements CardRepository {
    private final UnitOfWork unitOfWork;

    public CardRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public void saveCard(Card card) throws SQLException {
        String sql = "INSERT INTO cards (id, name, damage, element_type, card_type) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = unitOfWork.prepareStatement(sql)) {
            pstmt.setString(1, card.getId());
            pstmt.setString(2, card.getName());
            pstmt.setDouble(3, card.getDamage());
            pstmt.setString(4, card.getElementType().toString());
            pstmt.setString(5, card.getCardType());

            pstmt.executeUpdate();
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to save card", e);
        }
    }

    @Override
    public Optional<Card> findCardById(String cardId) throws SQLException {
        String sql = "SELECT * FROM cards WHERE id = ?";
        try (PreparedStatement pstmt = unitOfWork.prepareStatement(sql)) {
            pstmt.setString(1, cardId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToCard(rs));
            }
            return Optional.empty();
        }
    }

    @Override
    public List<Card> findAllCardsByUserId(long userId) throws SQLException {
        String sql = "SELECT c.id, c.name, c.damage, c.element_type, c.card_type " +
                "FROM user_cards uc JOIN cards c ON uc.card_id = c.id " +
                "WHERE uc.user_id = ?";
        List<Card> cards = new ArrayList<>();
        try (PreparedStatement pstmt = unitOfWork.prepareStatement(sql)) {
            pstmt.setLong(1, userId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                cards.add(mapResultSetToCard(rs));
            }
        }
        return cards;
    }

    @Override
    public boolean assignCardToUser(long userId, String cardId) throws SQLException {
        String sql = "INSERT INTO user_cards (user_id, card_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = unitOfWork.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setString(2, cardId);

            int rowsAffected = pstmt.executeUpdate();
            unitOfWork.commitTransaction();

            return rowsAffected > 0;
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to assign card to user", e);
        }
    }

    private Card mapResultSetToCard(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String name = rs.getString("name");
        double damage = rs.getDouble("damage");
        String elementType = rs.getString("element_type");
        String cardType = rs.getString("card_type");

        if ("Monster".equals(cardType)) {
            return new MonsterCard(name, damage, ElementType.valueOf(elementType));
        } else {
            return new SpellCard(name, damage, ElementType.valueOf(elementType));
        }
    }
}
