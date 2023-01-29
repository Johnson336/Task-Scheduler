package wgu.c195.schedulingsystem.components;

import java.time.*;

/**
 * Class to hold data about an Appointment
 */
public class Appointment extends Timestamp {
    private int id;
    private String title;
    private String desc;
    private String location;
    private String type;
    private LocalDateTime start;
    private LocalDateTime end;
    private int customerId;
    private int userId;
    private int contactId;
    private Contact contact;
    private Timestamp timestamp;
    public Appointment() {

    }
    public Appointment(String title, String desc, String location, String type, LocalDate date, LocalTime startTime, LocalTime endTime, int customerId, int userId, Contact contact) {
        this.title = title;
        this.desc = desc;
        this.location = location;
        this.type = type;
        this.start = LocalDateTime.of(date, startTime);
        this.end = LocalDateTime.of(date, endTime);
        this.customerId = customerId;
        this.userId = userId;
        this.contact = contact;
        this.contactId = contact.getId();
        this.timestamp = new Timestamp();
    }

    // Setters
    public void setId(int i) {
        this.id = i;
    }
    public void setTitle(String t) {
        this.title = t;
    }
    public void setDesc(String d) {
        this.desc = d;
    }
    public void setLocation(String s) {
        this.location = s;
    }
    public void setType(String s) {
        this.type = s;
    }
    public void setStart(LocalDateTime d) {
        this.start = d;
    }
    public void setEnd(LocalDateTime d) {
        this.end = d;
    }
    public void setCustomerId(int i) {
        this.customerId = i;
    }
    public void setUserId(int i) {
        this.userId = i;
    }
    public void setContact(Contact c) {
        this.contactId = c.getId();
        this.contact = c;
    }
    public void setContactId(int i) {
        this.contactId = i;
    }

    // Getters
    public int getId() {
        return this.id;
    }
    public String getTitle() {
        return this.title;
    }
    public String getDesc() {
        return this.desc;
    }
    public String getLocation() {
        return this.location;
    }
    public String getType() {
        return this.type;
    }
    public LocalDateTime getStart() {
        return this.start;
    }
    public LocalDateTime getEnd() {
        return this.end;
    }
    public int getCustomerId() {
        return this.customerId;
    }
    public int getUserId() {
        return this.userId;
    }
    public Contact getContact() {
        return this.contact;
    }
    public int getContactId() {
        return this.contactId;
    }
    public void setTimestamp(Timestamp s) {
        this.timestamp = s;
    }
    public Timestamp getTimestamp() {
        return this.timestamp;
    }
}
