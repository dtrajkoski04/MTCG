package sampleapp.model;

public class SpellCard extends Card {

    public SpellCard(String name, double damage, ElementType elementType) {
        super(name, damage, elementType);
    }

    @Override
    public String getCardType() {
        return "Spell";
    }
}
