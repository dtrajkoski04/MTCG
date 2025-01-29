package sampleapp.service;

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

    public List<Card> getAllCardsByUsername(String username) throws SQLException {
        var user = userRepository.getUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not Found"));
        return cardRepository.findAllByUsername(user.getUsername());
    }

    public void addCard(Card card) throws SQLException {
        cardRepository.save(card);
    }

}