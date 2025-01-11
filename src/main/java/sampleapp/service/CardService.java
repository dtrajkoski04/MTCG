package sampleapp.service;

import sampleapp.model.Card;
import sampleapp.persistence.UnitOfWork;
import sampleapp.persistence.repository.CardRepository;
import sampleapp.persistence.repository.CardRepositoryImpl;

public class CardService {
    private CardRepository cardRepository;

    public CardService() {
        this.cardRepository = new CardRepositoryImpl(new UnitOfWork());
    }

    public void addCard(Card card) {
        cardRepository.save(card);
    }
}