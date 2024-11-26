package sampleapp.model;

/**
 * Represents a MonsterCard.
 */
public class MonsterCard extends Card {

    public MonsterCard(String name, int damage, String elementType) {
        super(name, damage, elementType);
    }

    @Override
    public String getCardType() {
        return "Monster";
    }
}
