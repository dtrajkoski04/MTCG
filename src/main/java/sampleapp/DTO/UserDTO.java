package sampleapp.DTO;

import sampleapp.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDTO {
    @JsonProperty("Username")
    private String username;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Bio")
    private String bio;

    @JsonProperty("Image")
    private String image;

    public UserDTO() {}

    public UserDTO(String username, User user) {
        this.username = username;
        this.name = user.getName();
        this.bio = user.getBio();
        this.image = user.getImage();
    }

    public UserDTO(String username, String name, String bio, String image) {
        this.username = username;
        this.name = name;
        this.bio = bio;
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    public String getImage() {
        return image;
    }
}