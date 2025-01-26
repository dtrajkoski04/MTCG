package sampleapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    @JsonProperty("Username")
    private String username;

    @JsonProperty("Password")
    private String password;

    private String token;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Bio")
    private String bio;

    @JsonProperty("Image")
    private String image;

    private int coins = 20;

    public User(){

    }

    // Konstruktor für Registrierung
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Konstruktor für Benutzer mit Token
    public User(String username, String password, String token) {
        this.username = username;
        this.password = password;
        this.token = token;
    }

    // Getter und Setter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getCoins() {
        return this.coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    @Override
    public String toString() {
        return String.format(
                "User{username='%s', token='%s', coins=%d, elo=%d, name='%s', bio='%s', image='%s'}",
                username, token, name, bio, image
        );
    }
}
