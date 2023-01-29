package wgu.c195.schedulingsystem.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Class to hold data about a Customer
 */
public class Customer extends Timestamp {
    private int id;
    private String name;
    private String address;
    private String postalCode;
    private String phone;
    private int divisionId;
    private Division division;
    private ObservableList<Appointment> appointments;
    private Timestamp timestamp;
    public Customer() {

    }

    public Customer(String name, String address, String postalCode, String phone, String createdBy, Division division) {
        appointments = FXCollections.observableArrayList();
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.setDivision(division);
        this.timestamp = new Timestamp(createdBy);
    }
    // Setters
    public void setId(int i) {
        this.id = i;
    }
    public void setName(String s) {
        this.name = s;
    }
    public void setAddress(String s) {
        this.address = s;
    }
    public void setPostalCode(String s) {
        this.postalCode = s;
    }
    public void setPhone(String s) {
        this.phone = s;
    }
    public void setDivisionId(int i) {
        this.divisionId = i;
    }

    public void setDivision(Division d) {
        this.setDivisionId(d.getId());
        this.division = d;
    }
    // Getters
    public int getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getAddress() {
        return this.address;
    }
    public String getPostalCode() {
        return this.postalCode;
    }
    public String getPhone() {
        return this.phone;
    }
    public int getDivisionId() {
        return this.divisionId;
    }
    public Division getDivision() {
        return division;
    }
    public ObservableList<Appointment> getAppointments() {
        return this.appointments;
    }

    // Appointments
    public void addAppointment(Appointment a) {
        appointments.add(a);
    }

    public boolean removeAppointment(Appointment a) {
        return appointments.remove(a);
    }
    public void setTimestamp(Timestamp s) {
        this.timestamp = s;
    }
    public Timestamp getTimestamp() {
        return this.timestamp;
    }
}
