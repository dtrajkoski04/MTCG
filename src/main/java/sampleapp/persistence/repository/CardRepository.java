package sampleapp.persistence.repository;

import sampleapp.model.Card;

public interface CardRepository {
    void save(Card card);
}