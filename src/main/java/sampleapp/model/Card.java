package sampleapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Card {
    private String id;

    private CardInfo info;

    private int damage;

    private String owner;

    private boolean usedInDeck = false;

    public Card() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return info.getDisplayName();
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public CardInfo getInfo() {
        return info;
    }

    public void setInfo(CardInfo info) {
        this.info = info;
    }

    public String getElementType() {
        return info.getElement();
    }

    public String getCardType() {
        return info.getType();
    }

    @Override
    public String toString() {
        return "Id: " + this.getId() + ", Name: " + info.getDisplayName() + ", Damage: " + this.getDamage() + "\n";
    }

}