package sampleapp.service;

import sampleapp.exception.ResourceNotFoundException;
import sampleapp.model.Card;
import sampleapp.persistence.UnitOfWork;
import sampleapp.persistence.repository.CardRepository;
import sampleapp.persistence.repository.CardRepositoryImpl;
import sampleapp.persistence.repository.UserRepository;
import sampleapp.persistence.repository.UserRepositoryImpl;

import java.sql.SQLException;
import java.util.List;

public class CardService {
    private CardRepository cardRepository;
    private UserRepository userRepository;

    public CardService() {
        this.cardRepository = new CardRepositoryImpl(new UnitOfWork());
        this.userRepository = new UserRepositoryImpl(new UnitOfWork());
    }

    public CardService(CardRepository cardRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    public List<Card> getAllCardsByUsername(String username) throws SQLException, ResourceNotFoundException {
        var user = userRepository.getUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Card> cards = cardRepository.findAllByUsername(user.getUsername());

        if (cards.isEmpty()) {
            throw new ResourceNotFoundException("No cards found for user");
        }

        return cards;
    }


    public void addCard(Card card) throws SQLException {
        cardRepository.save(card);
    }

}