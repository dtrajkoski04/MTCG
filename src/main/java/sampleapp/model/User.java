package sampleapp.model;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.UUID;

public class User {
    @JsonAlias({"username"})
    private String username;

    @JsonAlias({"password"})
    private String password;

    @JsonAlias({"token"})
    private String token;

    // Constructor for creating a user with a generated token
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.token = generateToken();
    }

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

    // Private method to generate a unique token
    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}

