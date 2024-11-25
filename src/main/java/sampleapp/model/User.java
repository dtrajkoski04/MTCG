public class User {
    private String username;
    private String password;
    private String token;

    // Constructor for creating a user with no token (e.g., during registration)
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Constructor for creating a user with an existing token (e.g., fetched from the database)
    public User(String username, String password, String token) {
        this.username = username;
        this.password = password;
        this.token = token;
    }

    // Getters and setters for all fields
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

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
