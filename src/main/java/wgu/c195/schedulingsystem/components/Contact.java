package wgu.c195.schedulingsystem.components;

/**
 * Class to hold data about a Contact
 */
public class Contact {
    private int id;
    private String name;
    private String email;
    private Timestamp timestamp;
    public Contact() {

    }
    public Contact(int i, String name, String email) {
        this.id = i;
        this.name = name;
        this.email = email;
        this.timestamp = new Timestamp();
    }
    // Setters
    public void setId(int i) {
        this.id = i;
    }
    public void setName(String s) {
        this.name = s;
    }
    public void setEmail(String s) {
        this.email = s;
    }
    // Getters
    public int getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getEmail() {
        return this.email;
    }
    public void setTimestamp(Timestamp s) {
        this.timestamp = s;
    }
    public Timestamp getTimestamp() {
        return this.timestamp;
    }
    @Override
    public String toString() {
        return name;
    }
}
