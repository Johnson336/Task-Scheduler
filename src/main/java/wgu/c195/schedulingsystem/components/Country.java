package wgu.c195.schedulingsystem.components;

/**
 * Class to hold data about a Country
 */
public class Country {
    private int id;
    private String country;
    private Timestamp timestamp;
    public Country() {

    }
    public Country(int i, String s) {
        this.id = i;
        this.country = s;
        this.timestamp = new Timestamp();
    }
    // Setters
    public void setId(int i) {
        this.id = i;
    }
    public void setCountry(String s) {
        this.country = s;
    }
    // Getters
    public int getId() {
        return this.id;
    }
    public String getCountry() {
        return this.country;
    }

    public void setTimestamp(Timestamp s) {
        this.timestamp = s;
    }
    public Timestamp getTimestamp() {
        return this.timestamp;
    }
    public String toString() {
        return this.country;
    }
}
