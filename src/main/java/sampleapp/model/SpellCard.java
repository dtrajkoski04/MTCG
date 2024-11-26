package sampleapp.model;

/**
 * Represents a SpellCard.
 */
public class SpellCard extends Card {

    public SpellCard(String name, int damage, String elementType) {
        super(name, damage, elementType);
    }

    @Override
    public String getCardType() {
        return "Spell";
    }
}
