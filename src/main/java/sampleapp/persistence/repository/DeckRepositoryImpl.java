package sampleapp.persistence.repository;

import sampleapp.model.Card;
import sampleapp.persistence.DataAccessException;
import sampleapp.persistence.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static sampleapp.persistence.repository.CardRepositoryImpl.mapResultSetToCard;

public class DeckRepositoryImpl implements DeckRepository {
    private final UnitOfWork unitOfWork;

    public DeckRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = new UnitOfWork();
    }


    @Override
    public List<Card> getDeck(String username) {
        String sql = "SELECT c.id, c.name, c.damage, c.element_type, c.card_type " +
                "FROM user_cards uc " +
                "JOIN cards c ON uc.card_id = c.id " +
                "WHERE uc.user_username = ? AND uc.is_in_deck = TRUE";
        List<Card> deck = new ArrayList<>();

        try(PreparedStatement stmt = unitOfWork.prepareStatement(sql)){
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                deck.add(mapResultSetToCard(rs));
            }

        }catch(SQLException e){
            throw new RuntimeException("Error while retrieving user deck", e);
        }
        return deck;
    }

    @Override
    public void addToDeck(String username, String id) {
        String sql = "UPDATE user_cards SET is_in_deck = TRUE WHERE user_username = ? AND card_id = ?";

        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, id);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new DataAccessException("Card not found for the given user or is already in the deck");
            }

            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to add card to deck", e);
        }
    }


    @Override
    public void removeFromDeck(String username, String id) {
        String sql = "UPDATE user_cards SET is_in_deck = FALSE WHERE user_username = ? AND card_id = ?";

        try (PreparedStatement stmt = unitOfWork.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, id);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new DataAccessException("Card not found for the given user or is not in the deck");
            }

            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to remove card from deck", e);
        }
    }

}
