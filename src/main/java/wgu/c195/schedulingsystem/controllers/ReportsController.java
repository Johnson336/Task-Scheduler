package wgu.c195.schedulingsystem.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import wgu.c195.schedulingsystem.components.Appointment;
import wgu.c195.schedulingsystem.components.Contact;
import wgu.c195.schedulingsystem.components.ReportItem;
import wgu.c195.schedulingsystem.components.WeeklyReportItem;
import wgu.c195.schedulingsystem.database.DBQuery;

import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Controller for the reports form.
 */
public class ReportsController extends GenericController implements Initializable {
    @FXML
    private TableView<ReportItem> table_byTypeAndMonth;
    @FXML
    private TableColumn<ReportItem, String> table_month;
    @FXML
    private TableColumn<ReportItem, String> table_type;
    @FXML
    private TableColumn<ReportItem, Integer> table_quantity;
    @FXML
    private ListView<Contact> list_contacts;
    @FXML
    private TableView<Appointment> table_contactSchedule;
    @FXML
    private TableColumn<Appointment, Integer> table_AppID;
    @FXML
    private TableColumn<Appointment, String> table_AppTitle;
    @FXML
    private TableColumn<Appointment, String> table_AppType;
    @FXML
    private TableColumn<Appointment, String> table_AppDescription;
    @FXML
    private TableColumn<Appointment, Date> table_AppStart;
    @FXML
    private TableColumn<Appointment, Date> table_AppEnd;
    @FXML
    private TableColumn<Appointment, Integer> table_AppCustomerID;
    @FXML
    private TableView<WeeklyReportItem> table_weekly;
    @FXML
    private TableColumn<WeeklyReportItem, Integer> table_weeklyYear;
    @FXML
    private TableColumn<WeeklyReportItem, Integer> table_weeklyWeek;
    @FXML
    private TableColumn<WeeklyReportItem, Integer> table_weeklyQuantity;
    @FXML
    private Button button_back;
    private Contact selectedContact;
    @Override
    public void initialize(URL url, ResourceBundle resources) {
        try {
            initMonthlyReports();
            initContacts();
            initContactSchedule();
            initBarChart();
        // implement Contact selection listener functionality
        list_contacts.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                try {
                    selectedContact = newSelection;
                    populateContactSchedule(selectedContact);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initialize monthly reports by calling the DBQuery database action and displaying that information on the form.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public void initMonthlyReports() throws SQLException {
        table_byTypeAndMonth.setItems(DBQuery.getDBReportItems());
        table_month.setCellValueFactory(new PropertyValueFactory<>("month"));
        table_type.setCellValueFactory(new PropertyValueFactory<>("type"));
        table_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
    }

    /**
     * Initialize
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public void initContactSchedule() throws SQLException {
        table_contactSchedule.setItems(DBQuery.getDBAppointments("all"));
        table_AppID.setCellValueFactory(new PropertyValueFactory<>("id"));
        table_AppTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        table_AppDescription.setCellValueFactory(new PropertyValueFactory<>("desc"));
        table_AppType.setCellValueFactory(new PropertyValueFactory<>("type"));
        table_AppStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        table_AppEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        table_AppCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerId"));
    }
    public void initContacts() throws SQLException {
        list_contacts.setItems(DBQuery.getDBContacts());
    }
    public void populateContactSchedule(Contact c) throws SQLException {
        table_contactSchedule.setItems(DBQuery.getDBAppointmentsByContactID(c.getId()));
        table_AppID.setCellValueFactory(new PropertyValueFactory<>("id"));
        table_AppTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        table_AppDescription.setCellValueFactory(new PropertyValueFactory<>("desc"));
        table_AppType.setCellValueFactory(new PropertyValueFactory<>("type"));
        table_AppStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        table_AppEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        table_AppCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerId"));
    }
    public void initBarChart() throws SQLException {
        table_weekly.setItems(DBQuery.getDBAppointmentsByWeek());
        table_weeklyYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        table_weeklyWeek.setCellValueFactory(new PropertyValueFactory<>("week"));
        table_weeklyQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
    }
}
