package sampleapp.service;

import sampleapp.model.Card;
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

    public List<Card> getDeck(String username) throws SQLException {
        var user = userRepository.getUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return deckRepository.getDeck(username);
    }

    public void configureDeck(String username, List<String> ids) throws SQLException {
        if(ids.size() != 4){
            throw new IllegalArgumentException("Not enough ids");
        }
        var user = userRepository.getUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Card> currDeck = this.getDeck(username);

        for(String id : ids){
            if(!cardRepository.isCardOwned(username, id)){
                throw new IllegalArgumentException("Card" + id + " is not owned by " + username);
            }
            deckRepository.addToDeck(username, id);
        }

        for(Card card : currDeck){
            deckRepository.removeFromDeck(username, card.getId());
        }
    }
}
