package sampleapp.service;

import sampleapp.exception.ResourceNotFoundException;
import sampleapp.model.Card;
import sampleapp.persistence.DataAccessException;
import sampleapp.persistence.UnitOfWork;
import sampleapp.persistence.repository.*;

import java.sql.SQLException;
import java.util.List;

public class DeckService {
    private final DeckRepository deckRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;

    public DeckService() {
        this.deckRepository = new DeckRepositoryImpl(new UnitOfWork());
        this.userRepository = new UserRepositoryImpl(new UnitOfWork());
        this.cardRepository = new CardRepositoryImpl(new UnitOfWork());
    }

    public DeckService(DeckRepository deckRepository, UserRepository userRepository, CardRepository cardRepository) {
        this.deckRepository = deckRepository;
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
    }

    public List<Card> getDeck(String username) throws SQLException, ResourceNotFoundException {
        var user = userRepository.getUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return deckRepository.getDeck(user.getUsername()); // Will return an empty list if no deck cards exist
    }



    public void configureDeck(String username, List<String> ids) throws SQLException, IllegalArgumentException, DataAccessException {
        if (ids.size() != 4) {
            throw new IllegalArgumentException("Deck must contain exactly 4 cards");
        }

        var user = userRepository.getUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Card> currDeck = this.getDeck(username);

        for (String id : ids) {
            if (!cardRepository.isCardOwned(username, id)) {
                throw new IllegalArgumentException("Card " + id + " is not owned by " + username);
            }
            deckRepository.addToDeck(username, id);
        }

        for (Card card : currDeck) {
            deckRepository.removeFromDeck(username, card.getId());
        }
    }

}
