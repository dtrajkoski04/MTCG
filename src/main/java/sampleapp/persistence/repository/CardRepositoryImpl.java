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
    public void save(Card card) {
        String cardSql = "INSERT INTO cards (id, name, damage, element_type, card_type) VALUES (?, ?, ?, ?, ?)";

        try(PreparedStatement stmt = unitOfWork.prepareStatement(cardSql)) {
            stmt.setString(1, card.getId());
            stmt.setString(2, card.getName());
            stmt.setInt(3, card.getDamage());
            stmt.setString(4, card.getElementType());
            stmt.setString(5, card.getCardType());
            System.out.println("Saving card: " + card.getId() + " " + card.getName() + " " + card.getDamage() + " " + card.getElementType() + " " + card.getCardType());

            stmt.executeUpdate();
            unitOfWork.commitTransaction();
        } catch(SQLException e) {
            unitOfWork.rollbackTransaction();
            e.printStackTrace();
            throw new DataAccessException("Failed to create Card");
        }
    }

    public List<Card> findAllByUsername(String username) {
        String sql = "SELECT c.id, c.name, c.damage, c.element_type, c.card_type " +
                "FROM user_cards uc " +
                "JOIN cards c ON uc.card_id = c.id " +
                "WHERE uc.user_username = ?";
        List<Card> cards = new ArrayList<>();

        try(PreparedStatement pstmt = unitOfWork.prepareStatement(sql)){
            pstmt.setString(1, username);

            try(ResultSet rs = pstmt.executeQuery()){
                while(rs.next()) {
                    cards.add(mapResultSetToCard(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Error fetching cards for user", e);
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

}