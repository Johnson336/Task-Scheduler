package wgu.c195.schedulingsystem.components;

/**
 * Class to hold data about a logged-in user.
 */
public class User {
    private int id;
    private String username;
    private String password;
    private Timestamp timestamp;
    public User() {

    }
    public User(int i, String username, String password, Timestamp s) {
        this.id = i;
        this.username = username;
        this.password = password;
        this.timestamp = new Timestamp();
    }

    // Setters
    public void setId(int i) {
        this.id = i;
    }
    public void setUsername(String s) {
        this.username = s;
    }
    public void setPassword(String s) {
        this.password = s;
    }

    // Getters
    public int getId() {
        return this.id;
    }
    public String getUsername() {
        return this.username;
    }
    public String getPassword() {
        return this.password;
    }
    public void setTimestamp(Timestamp s) {
        this.timestamp = s;
    }
    public Timestamp getTimestamp() {
        return this.timestamp;
    }
}
