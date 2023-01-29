package wgu.c195.schedulingsystem.components;

/**
 * Class to hold data about a Division
 */
public class Division {
    private int id;
    private String division;
    private int country_id;
    private Country country;
    private Timestamp timestamp;
    public Division() {
    }
    public Division(int i, String d, Country c) {
        this.id = i;
        this.division = d;
        this.country = c;
        this.timestamp = new Timestamp();
    }
    // Setters
    public void setId(int i) {
        this.id = i;
    }
    public void setDivision(String d) {
        this.division = d;
    }
    public void setCountryId(int i) {
        this.country_id = i;
    }
    public void setCountry(Country c) {
        this.setCountryId(c.getId());
        this.country = c;
    }

    // Getters
    public int getId() {
        return this.id;
    }
    public String getDivision() {
        return this.division;
    }
    public int getCountryId() {
        return this.country_id;
    }
    public Country getCountry() {
        return this.country;
    }
    public void setTimestamp(Timestamp s) {
        this.timestamp = s;
    }
    public Timestamp getTimestamp() {
        return this.timestamp;
    }
    public String toString() {
        return this.division;
    }
}
