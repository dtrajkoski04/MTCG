package sampleapp.model;

/**
 * Abstract base class for all cards.
 */
public abstract class Card {
    private String name;
    private int damage;
    private String elementType;

    // Constructor
    public Card(String name, int damage, String elementType) {
        this.name = name;
        this.damage = damage;
        this.elementType = elementType;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    // Abstract method to describe the card type
    public abstract String getCardType();

    @Override
    public String toString() {
        return "Card{" +
                "name='" + name + '\'' +
                ", damage=" + damage +
                ", elementType='" + elementType + '\'' +
                ", cardType='" + getCardType() + '\'' +
                '}';
    }
}

