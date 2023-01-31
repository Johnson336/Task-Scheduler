package wgu.c195.schedulingsystem.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import wgu.c195.schedulingsystem.components.*;
import wgu.c195.schedulingsystem.database.DBQuery;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * CRUDController. Create-Update-Delete Customers and Appointments.
 * Main form for the majority of tasks this project requires.
 * @author Cole Niermeyer
 */
public class CRUDController extends GenericController implements Initializable {
    /**
     * Customer Table
     */
    @FXML
    private TableView<Customer> table_Customer;
    @FXML
    private TableColumn<Customer, Integer> table_CustomerID;
    @FXML
    private TableColumn<Customer, String> table_CustomerName;
    @FXML
    private TableColumn<Customer, String> table_CustomerAddress;
    @FXML
    private TableColumn<Customer, String> table_CustomerPostal;
    @FXML
    private TableColumn<Customer, String> table_CustomerPhone;
    @FXML
    private TableColumn<Customer, String> table_CustomerDivision;
    @FXML
    private TableColumn<Customer, String> table_CustomerCountry;
    @FXML
    private TextField textField_CustomerName;
    @FXML
    private TextField textField_CustomerAddress;
    @FXML
    private TextField textField_CustomerPostal;
    @FXML
    private TextField textField_CustomerPhone;
    @FXML
    private Button button_CustomerAdd;
    @FXML
    private Button button_CustomerUpdate;
    @FXML
    private Button button_CustomerDelete;
    @FXML
    private TextField textField_customerID;
    @FXML
    private ComboBox<Division> combo_CustomerDivision;
    @FXML
    private ComboBox<Country> combo_CustomerCountry;

    /**
     * Appointment Table
     *
     */
    @FXML
    private TableView<Appointment> table_Appointment;
    @FXML
    private TableColumn<Appointment, Integer> table_AppID;
    @FXML
    private TableColumn<Appointment, String> table_AppTitle;
    @FXML
    private TableColumn<Appointment, String> table_AppDesc;
    @FXML
    private TableColumn<Appointment, String> table_AppLoc;
    @FXML
    private TableColumn<Appointment, String> table_AppContact;
    @FXML
    private TableColumn<Appointment, String> table_AppType;
    @FXML
    private TableColumn<Appointment, Date> table_AppStart;
    @FXML
    private TableColumn<Appointment, Date> table_AppEnd;
    @FXML
    private TableColumn<Appointment, Integer> table_AppCustID;
    @FXML
    private TableColumn<Appointment, Integer> table_AppUserID;
    @FXML
    private TextField textField_AppID;
    @FXML
    private TextField textField_AppTitle;
    @FXML
    private TextField textField_AppDesc;
    @FXML
    private TextField textField_AppLoc;
    @FXML
    private TextField textField_AppType;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField textField_timeStart;
    @FXML
    private TextField textField_timeEnd;
    @FXML
    private TextField textField_AppCustomerID;
    @FXML
    private TextField textField_AppUserID;
    @FXML
    private ComboBox<Contact> combo_AppContact;
    @FXML
    private Button button_AppAdd;
    @FXML
    private Button button_AppUpdate;
    @FXML
    private Button button_AppDelete;
    @FXML
    private Button button_Reports;
    @FXML
    private RadioButton radio_FilterAll;
    @FXML
    private RadioButton radio_FilterWeek;
    @FXML
    private RadioButton radio_FilterMonth;
    @FXML
    private Label label_filterType;
    private static Customer selectedCustomer;
    private static Appointment selectedAppointment;
    private static String user;

    /**
     * Main initialize function to populate everything upon logging in.
     * Takes callback parameters and launches automatically upon form open.
     * Also calls a function to display a 15 minute meeting warning upon logging in.
     *
     * lambda - This function uses lambda expressions to initialize selection listeners on the
     * TableView elements, updating a global variable with the currently selected item and
     * automatically populating the text fields with the selected element's information.
     *
     * @param url
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resources) {
        try {
            user = LoginController.getUser();
            initCustomers();
            initAppointments("all");


            // initialize combo boxes
            initDivisionCombo();
            initCountryCombo();
            initContactsCombo();

            // implement Customer selection listener functionality
            table_Customer.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectedCustomer = newSelection;
                    populateCustomerTextFields(selectedCustomer);
                }
            });

            // implement Appointment selection listener functionality
            table_Appointment.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectedAppointment = newSelection;
                    populateAppointmentTextFields(selectedAppointment);
                }
            });

            display15minAlert(user);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * lambda - This function uses lambda expressions to take a given CellValueFactory value and extract out an
     * appropriate string for display purposes. Converts a Customer Division code into the appropriate Division name,
     * and a Customer Country code into the appropriate Country.
     *
     * Initialize Customers TableView with database records.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public void initCustomers() throws SQLException {
        table_Customer.setItems(DBQuery.getDBCustomers());
        table_CustomerID.setCellValueFactory(new PropertyValueFactory<>("id"));
        table_CustomerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        table_CustomerAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        table_CustomerPostal.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        table_CustomerPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        table_CustomerDivision.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDivision().getDivision()));
        table_CustomerCountry.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDivision().getCountry().getCountry()));
    }

    /**
     * Initialize Appointments TableView with database records.
     * @param filter Takes a string ("all", "week", "month") and provides appointment filtering via SQL Statement modification.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public void initAppointments(String filter) throws SQLException {
        table_Appointment.setItems(DBQuery.getDBAppointments(filter));
        table_AppID.setCellValueFactory(new PropertyValueFactory<>("id"));
        table_AppTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        table_AppDesc.setCellValueFactory(new PropertyValueFactory<>("desc"));
        table_AppLoc.setCellValueFactory(new PropertyValueFactory<>("location"));
        table_AppType.setCellValueFactory(new PropertyValueFactory<>("type"));
        table_AppStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        table_AppEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        table_AppCustID.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        table_AppUserID.setCellValueFactory(new PropertyValueFactory<>("userId"));
        table_AppContact.setCellValueFactory(new PropertyValueFactory<>("contactId"));

    }

    /**
     * Initialize Division Combo Box using a StringConverter to change a Division into an appropriate String.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public void initDivisionCombo() throws SQLException {
        combo_CustomerDivision.setItems(DBQuery.getDBDivisions());
        combo_CustomerDivision.setPromptText("Select division");
        combo_CustomerDivision.setConverter(new StringConverter<Division>() {
            @Override
            public String toString(Division d) {
                return d.getDivision();
            }

            @Override
            public Division fromString(String d) {
                return null;
            }
        });
    }

    /**
     * Initialize Country Combo Box using a StringConverter to change a Country into an appropriate String.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public void initCountryCombo() throws SQLException {
        combo_CustomerCountry.setItems(DBQuery.getDBCountries());
        combo_CustomerCountry.setPromptText("Select country");
        combo_CustomerCountry.setConverter(new StringConverter<Country>() {
            @Override
            public String toString(Country c) {
                return c.getCountry();
            }

            @Override
            public Country fromString(String c) {
                return null;
            }
        });
    }

    /**
     * Initialize Contacts Combo Box using a StringConverter to change a Contact into an appropriate String.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public void initContactsCombo() throws SQLException {
        combo_AppContact.setItems(DBQuery.getDBContacts());
        combo_AppContact.setPromptText("Select contact");
        combo_AppContact.setConverter(new StringConverter<Contact>() {
            @Override
            public String toString(Contact c) {
                return c.getName();
            }
            @Override
            public Contact fromString(String c) {
                return null;
            }
        });
    }

    /**
     * Initialize the Appointments table and provide filtering based on which Radio button is currently selected.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public void doInitAppointments() throws SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");
        LocalDateTime startRange = LocalDateTime.now();
        LocalDateTime endRange = LocalDateTime.MAX;
        if (radio_FilterAll.isSelected()) {
            initAppointments("all");
        } else if (radio_FilterWeek.isSelected()) {
            endRange = startRange.plusWeeks(1);
            initAppointments("week");
        } else if (radio_FilterMonth.isSelected()) {
            endRange = startRange.plusMonths(1);
            initAppointments("month");
        } else {
            // Should never get here!
            alertDialog("Error", "Error reached", "Reached invalid condition in doInitAppointments()", Alert.AlertType.ERROR);
        }
        label_filterType.setText(String.format("%s to %s", formatter.format(startRange), formatter.format(endRange)));
    }

    /**
     * Radio button callback to provide single-radio button selection restriction.
     * @param event Callback method called when clicking a radio button.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public void selectRadio(ActionEvent event) throws SQLException {
        if (event.getSource().equals(radio_FilterAll)) {
            radio_FilterAll.setSelected(true);
            radio_FilterMonth.setSelected(false);
            radio_FilterWeek.setSelected(false);
        }
        if (event.getSource().equals(radio_FilterWeek)) {
            radio_FilterWeek.setSelected(true);
            radio_FilterMonth.setSelected(false);
            radio_FilterAll.setSelected(false);
        }
        if (event.getSource().equals(radio_FilterMonth)) {
            radio_FilterMonth.setSelected(true);
            radio_FilterAll.setSelected(false);
            radio_FilterWeek.setSelected(false);
        }
            doInitAppointments();
    }

    /**
     * Callback function upon making a selection in the Country Combo Box. Updates Division Combo Box accordingly.
     * @param event Callback method called when making a Country selection.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public void selectedCountry(ActionEvent event) throws SQLException {
        Country thisCountry = combo_CustomerCountry.getSelectionModel().getSelectedItem();
        combo_CustomerDivision.getSelectionModel().clearSelection();
        combo_CustomerDivision.getSelectionModel().select(null);
        combo_CustomerDivision.setItems(DBQuery.getDBCountryDivisions(thisCountry));
        combo_CustomerDivision.setPromptText("Select division");

    }

    /**
     * Callback method used for populating Customer Text Fields by the TableView event listener.
     * @param c Currently selected Customer in the TableView.
     */
    public void populateCustomerTextFields(Customer c) {
        textField_customerID.setText(String.valueOf(c.getId()));
        textField_CustomerName.setText(c.getName());
        textField_CustomerAddress.setText(c.getAddress());
        textField_CustomerPostal.setText(c.getPostalCode());
        textField_CustomerPhone.setText(c.getPhone());
        combo_CustomerCountry.getSelectionModel().select(c.getDivision().getCountry());
        combo_CustomerDivision.setValue(c.getDivision());
    }

    /**
     * Callback method used for populating Appointment Text Fields by the TableView event listener.
     * @param a Currently selected Appointment in the TableView.
     */
    public void populateAppointmentTextFields(Appointment a) {
        textField_AppID.setText(String.valueOf(a.getId()));
        textField_AppTitle.setText(a.getTitle());
        textField_AppDesc.setText(a.getDesc());
        textField_AppLoc.setText(a.getLocation());
        textField_AppType.setText(a.getType());
        datePicker.setValue(a.getStart().toLocalDate());
        textField_timeStart.setText(a.getStart().toLocalTime().toString());
        textField_timeEnd.setText(a.getEnd().toLocalTime().toString());
        textField_AppCustomerID.setText(String.valueOf(a.getCustomerId()));
        textField_AppUserID.setText(String.valueOf(a.getUserId()));
        combo_AppContact.setValue(a.getContact());

    }

    /**
     * Method to bring up the Reports Form.
     * @param event Callback method upon clicking the Reports button.
     * @throws IOException Thrown if the Reports_form.fxml couldn't be loaded.
     */
    public void gotoReports(ActionEvent event) throws IOException {
        moveToWindow(event, "reports_form.fxml", "Reports");
    }

    /**
     * Build a customer using data from the customer text fields and call DBQuery to perform the database action.
     * @param event Callback event upon clicking Add button.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public void doAddCustomer(ActionEvent event) throws SQLException {
        Customer newCustomer = new Customer(textField_CustomerName.getText(), textField_CustomerAddress.getText(), textField_CustomerPostal.getText(),
                textField_CustomerPhone.getText(), user, combo_CustomerDivision.getSelectionModel().getSelectedItem());

        if (!DBQuery.addDBCustomer(newCustomer)) {
            alertDialog("Error Adding", "Error Adding Customer", "Unable to add customer, please try again later.", Alert.AlertType.ERROR);
        }
        initCustomers();
    }

    /**
     * Update a customer using data in the customer text fields, then call DBQuery to update the database.
     * @param event Callback event upon clicking Update button.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public void doUpdateCustomer(ActionEvent event) throws SQLException {
        Customer newCustomer = new Customer(textField_CustomerName.getText(), textField_CustomerAddress.getText(), textField_CustomerPostal.getText(),
                textField_CustomerPhone.getText(), user, combo_CustomerDivision.getSelectionModel().getSelectedItem());
        newCustomer.setId(Integer.parseInt(textField_customerID.getText()));

        if (!DBQuery.updateDBCustomer(newCustomer)) {
            alertDialog("Error Updating", "Error Updating Customer", "Unable to update customer, please try again later.", Alert.AlertType.ERROR);
        }
        initCustomers();
    }

    /**
     * Called upon clicking Delete customer button. Finds the currently selected Customer and tries to delete it. Then
     * calls DBQuery to update the database.
     * @param event Callback event upon clicking Delete button.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public void doDeleteCustomer(ActionEvent event) throws SQLException {
        Customer highlighted = table_Customer.getSelectionModel().getSelectedItem();
        if (highlighted == null) {
            alertDialog("Error", "No customer selected", "Please select a customer to delete.", Alert.AlertType.ERROR);
            return;
        }
        String deletedName = highlighted.getName();
        DBQuery.deleteDBCustomer(highlighted);
        table_Customer.getItems().remove(highlighted);
        // Reinitialize appointments in case we had to delete some during Customer deletion.
        doInitAppointments();
        alertDialog("Deleted", "Customer was deleted", deletedName + " was deleted.", Alert.AlertType.INFORMATION);
    }

    /**
     * Build a new appointment using the Appointment Text Fields. Performs logical checks and validations before valling DBQuery to
     * perform the database action.
     * @param event Callback upon clicking add button.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public void doAddAppointment(ActionEvent event) throws SQLException {
        Appointment newAppt = new Appointment(
                textField_AppTitle.getText(),
                textField_AppDesc.getText(),
                textField_AppLoc.getText(),
                textField_AppType.getText(),
                datePicker.getValue(),
                LocalTime.parse(textField_timeStart.getText()),
                LocalTime.parse(textField_timeEnd.getText()),
                Integer.parseInt(textField_AppCustomerID.getText()),
                Integer.parseInt(textField_AppUserID.getText()),
                combo_AppContact.getValue()
        );
        if (!validate(newAppt)) {
            return;
        }
        if (!DBQuery.addDBAppointment(newAppt)) {
            alertDialog("Error Adding", "Error Adding Appointment", "Unable to add appointment, please try again later.", Alert.AlertType.ERROR);
        }
        doInitAppointments();
    }

    /**
     * Called upon clicking update Appointment button. Builds an Appointment from text boxes and then calls DBQuery to perform
     * database actions.
     * @param event Callback upon clicking update appointment.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public void doUpdateAppointment(ActionEvent event) throws SQLException {
        Appointment newAppt = new Appointment(
                textField_AppTitle.getText(),
                textField_AppDesc.getText(),
                textField_AppLoc.getText(),
                textField_AppType.getText(),
                datePicker.getValue(),
                LocalTime.parse(textField_timeStart.getText()),
                LocalTime.parse(textField_timeEnd.getText()),
                Integer.parseInt(textField_AppCustomerID.getText()),
                Integer.parseInt(textField_AppUserID.getText()),
                combo_AppContact.getValue()
        );
        newAppt.setId(Integer.parseInt(textField_AppID.getText()));
        if (!validate(newAppt)) {
            return;
        }
        if (!DBQuery.updateDBAppointment(newAppt)) {
            alertDialog("Error Adding", "Error Adding Appointment", "Unable to add appointment, please try again later.", Alert.AlertType.ERROR);
        }
        doInitAppointments();
    }

    /**
     * Helper function to consolidate logic validation boolean functions.
     * @param newAppt Appointment that we're checking logic against.
     * @return True or false based on success or fail condition.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public boolean validate(Appointment newAppt) throws SQLException {
        return !outsideBusinessHours(newAppt) && !overlapsExisting(newAppt);
    }

    /**
     * Validation function to check if the given appointment falls outside EST business hours.
     * @param a Appointment to be checked for validity.
     * @return True or false based on success condition.
     */
    public boolean outsideBusinessHours(Appointment a) {
        LocalDateTime estStartHours = LocalDateTime.of(datePicker.getValue(), LocalTime.of(8, 0, 0));
        LocalDateTime estEndHours = LocalDateTime.of(datePicker.getValue(), LocalTime.of(22, 0, 0));
        LocalDateTime localStartHours = DBQuery.convertTime(estStartHours, "America/New_York", "local");
        LocalDateTime localEndHours = DBQuery.convertTime(estEndHours, "America/New_York", "local");
        if (a.getStart().isBefore(localStartHours) || a.getStart().isAfter(localEndHours)) {
            alertDialog("Error", "Appointment Start Error", "Error creating appointment, start time is outside business hours.", Alert.AlertType.ERROR);
            return true;
        }
        if (a.getEnd().isBefore(localStartHours) || a.getEnd().isAfter(localEndHours)) {
            alertDialog("Error", "Appointment End Error", "Error creating appointment, end time is outside business hours.", Alert.AlertType.ERROR);
            return true;
        }
        return false;
    }

    /**
     * Validation function to check if the given appointment overlaps an existing appointment for this Customer.
     * @param a Appointment to be checked.
     * @return True or false based on success condition.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public boolean overlapsExisting(Appointment a) throws SQLException {
        ObservableList<Appointment> customerAppointments = DBQuery.getDBAppointmentsByCustomerID(a.getCustomerId());
        for (Appointment app : customerAppointments) {
            // Existing appointment shouldn't be checked against when updating
            if (app.getId() == a.getId()) {
                continue;
            }
            // System.out.printf("NewApp %d begin: %s end: %s\nCompared to app %d begin: %s end %s\n", a.getId(), a.getStart(), a.getEnd(), app.getId(), app.getStart(), app.getEnd());
            /**
             * (StartA <= EndB) and (EndA >= StartB)
             */
            // if ((a.getStart().isBefore(app.getEnd()) || a.getStart().isEqual(app.getEnd())) && (a.getEnd().isAfter(app.getStart()) || a.getEnd().isEqual(app.getStart()))) {
            if (timesOverlapping(a.getStart(), a.getEnd(), app.getStart(), app.getEnd())) {
                String strA = " ".repeat(a.getStart().getHour()) + "|" + "-".repeat((a.getEnd().getHour()-a.getStart().getHour())) + "|";
                String strB = " ".repeat(app.getStart().getHour()) + "|" + "-".repeat((app.getEnd().getHour()-app.getStart().getHour())) + "|";
                alertDialog("Error", "Error Adding Appointment", String.format("Error adding appointment, time overlaps existing appointment.\nAppointment %2d: %s\nAppointment %2d: %s", a.getId(), strA, app.getId(), strB), Alert.AlertType.ERROR);
                return true;
            }


        }
        return false;
    }

    /**
     * Helper function to consolidate logic regarding 2 overlapping periods of time.
     * @param a_start First period start time.
     * @param a_end First period end time.
     * @param b_start Second period start time.
     * @param b_end Second period end time.
     * @return True or false based on success condition.
     */
    public boolean timesOverlapping(LocalDateTime a_start, LocalDateTime a_end, LocalDateTime b_start, LocalDateTime b_end) {
        return ((a_start.isBefore(b_end) || a_start.isEqual(b_end)) && ((a_end.isAfter(b_start) || a_end.isEqual(b_start))));
    }

    /**
     * Function to determine whether the user has an appointment coming up within 15 minutes and display the
     * appropriate message.
     * @param name User to check for an upcoming appointment.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public void display15minAlert(String name) throws SQLException {
        ObservableList<Appointment> userAppointments = DBQuery.getDBAppointmentsByUsername(name);
        LocalDateTime loginTime = LocalDateTime.now();
        LocalDateTime loginPlusFifteen = loginTime.plusMinutes(15);
        boolean shown = false;
        for (Appointment app: userAppointments) {
            if (timesOverlapping(loginTime, loginPlusFifteen, app.getStart(), app.getEnd()) && !shown) {
                alertDialog("Upcoming", "Upcoming Appointment", String.format("Alert! You have an appointment upcoming within 15 minutes!\nAppointment: %d (%s) - %s", app.getId(), app.getTitle(), app.getStart()), Alert.AlertType.WARNING);
                shown = true;
            }
        }
        if (!shown) {
            alertDialog("Upcoming", "Upcoming Appointment", "Relax! You don't have any appointments within 15 minutes.", Alert.AlertType.INFORMATION);
        }
    }

    /**
     * Called upon clicking delete appointment. Calls DBQuery to perform the database action, then displays an
     * appropriate message.
     * @param event Callback method called after clicking delete appointment.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public void doDeleteAppointment(ActionEvent event) throws SQLException {
        Appointment highlighted = table_Appointment.getSelectionModel().getSelectedItem();
        if (highlighted == null) {
            alertDialog("Error", "No appointment selected", "Please select an appointment to delete.", Alert.AlertType.WARNING);
            return;
        }
        int deletedID = highlighted.getId();
        String deletedType = highlighted.getType();
        DBQuery.deleteDBAppointment(highlighted);
        table_Appointment.getItems().remove(highlighted);
        alertDialog("Appointment Cancelled", "Appointment has been cancelled.", "Appointment " + deletedID + " (" + deletedType + ") has been cancelled.", Alert.AlertType.INFORMATION);
    }
}
