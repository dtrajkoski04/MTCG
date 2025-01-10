package sampleapp.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CardDTO {
    @JsonProperty("Id")
    private String id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Damage")
    private int damage;

    public CardDTO(String id, String name, int damage) {
        this.id = id;
        this.name = name;
        this.damage = damage;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }
}
