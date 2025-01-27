package sampleapp.persistence.repository;

import sampleapp.model.Card;

import java.util.List;

public interface CardRepository {
    void save(Card card);
    List<Card> findAllByUsername(String username);
}