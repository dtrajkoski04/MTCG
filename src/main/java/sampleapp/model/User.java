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

    private int elo = 100;

    private int games_played = 0;

    private int wins = 0;

    private int losses = 0;

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

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public int getGames_played() {
        return games_played;
    }

    public void setGames_played(int games_played) {
        this.games_played = games_played;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }
}
