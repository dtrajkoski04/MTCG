package sampleapp.persistence.repository;

import sampleapp.model.Card;
import sampleapp.model.CardInfo;
import sampleapp.persistence.DataAccessException;
import sampleapp.persistence.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CardRepositoryImpl implements CardRepository {
    private UnitOfWork unitOfWork;

    public CardRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public void save(Card card) throws SQLException {
        String cardSql = "INSERT INTO cards (id, name, damage, element_type, card_type) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = unitOfWork.prepareStatement(cardSql)) {
            stmt.setString(1, card.getId());
            stmt.setString(2, card.getName());
            stmt.setInt(3, card.getDamage());
            stmt.setString(4, card.getElementType());
            stmt.setString(5, card.getCardType());

            System.out.println("Saving card: " + card.getId() + " " + card.getName() + " " + card.getDamage());

            stmt.executeUpdate();
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new SQLException("Failed to create Card", e);
        }
    }


    @Override
    public List<Card> findAllByUsername(String username) throws SQLException {
        String sql = "SELECT c.id, c.name, c.damage, c.element_type, c.card_type " +
                "FROM user_cards uc " +
                "JOIN cards c ON uc.card_id = c.id " +
                "WHERE uc.user_username = ?";
        List<Card> cards = new ArrayList<>();

        try (PreparedStatement pstmt = unitOfWork.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    cards.add(mapResultSetToCard(rs));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Failed to fetch user cards", e);
        }

        return cards;
    }


    public static Card mapResultSetToCard(ResultSet rs) throws SQLException {
        Card card = new Card();
        card.setId(rs.getString("id"));
        card.setDamage(rs.getInt("damage"));
        card.setInfo(CardInfo.fromDisplayName(rs.getString("name")));
        return card;
    }

    public boolean isCardOwned(String username, String cardId) {
        String sql = "SELECT COUNT(*) AS count FROM user_cards WHERE user_username = ? AND card_id = ?";

        try(PreparedStatement stmt = unitOfWork.prepareStatement(sql)){
            stmt.setString(1, username);
            stmt.setString(2, cardId);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch(SQLException e) {
            throw new RuntimeException("Error fetching card for user", e);
        }
        return false;
    }

}