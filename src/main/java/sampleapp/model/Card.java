package sampleapp.model;

public abstract class Card {
    private String id;
    private String name;
    private final double damage;
    private final ElementType elementType;

    public Card(String name, double damage, ElementType elementType) {
        this.name = name;
        this.damage = damage;
        this.elementType = elementType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDamage() {
        return damage;
    }

    public ElementType getElementType() {
        return elementType;
    }

    public abstract String getCardType();
}
