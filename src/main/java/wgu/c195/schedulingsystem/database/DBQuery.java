package wgu.c195.schedulingsystem.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import wgu.c195.schedulingsystem.components.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Main method for interacting with the database performing all appropriate statement preparation and execution
 * actions. Acts as an abstraction layer to provide a common location where all database actions take place in order
 * to make changes apply to every instance simultaneously.
 */
public abstract class DBQuery {

    /**
     * Validate a user's login attempt against the database.
     * @param username Username to be validated.
     * @param pass Password to be validated.
     * @return True or false based on user/pass combination found in the database.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public static boolean validateLogin(String username, String pass) throws SQLException {
        String sql = "SELECT * FROM users WHERE User_Name = ?";
        PreparedStatement stmt = DBConnector.connection_state.prepareStatement(sql);
        stmt.setString(1, username);
        ResultSet results = stmt.executeQuery();
        // Logging moved to GenericController
        // System.out.println(String.format("Login attempt: User: %s Pass: %s", username, pass));
        while (results.next()) {
            if (results.getString("Password").equals(pass)) {
                // user found and password verified
                return true;
            }
        }
        // user and password could not be found
        return false;
    }

    /**
     * Gets all Customers from the database.
     * @return ObservableList<Customer> containing all customers in the database.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public static ObservableList<Customer> getDBCustomers() throws SQLException {
        String sql = "select * from customers " +
                "INNER JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID " +
                "INNER JOIN countries ON first_level_divisions.Country_ID = countries.Country_ID";
        // String sql = "SELECT * FROM customers";
        PreparedStatement stmt = DBConnector.connection_state.prepareStatement(sql);
        ResultSet results = stmt.executeQuery();
        ObservableList<Customer> output = FXCollections.observableArrayList();
        while (results.next()) {
            Country c = findCountry(results);
            Division d = findDivision(results);
            Customer cu = findCustomer(results);
            d.setCountry(c);
            cu.setDivision(d);

            output.add(cu);
        }
        return output;
    }

    /**
     * Get all Appointments from the database.
     * @param filter Filtering used to modify the SQL statements to adjust the date of which appointments get returned
     *               and displayed in the form.
     * @return ObservableList<Appointment> containing all appointments in the database.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public static ObservableList<Appointment> getDBAppointments(String filter) throws SQLException {
        String sql = "select * from appointments inner join contacts on appointments.Contact_ID = contacts.Contact_ID";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startRange = LocalDateTime.now();
        LocalDateTime endRange = LocalDateTime.now();
        if (!filter.equals("all")) {
            if (filter.equals("week")) {
                endRange = startRange.plusWeeks(1);
            } else if (filter.equals("month")) {
                endRange = startRange.plusMonths(1);
            }
            sql += " where Start between ? and ?";
        }
        PreparedStatement stmt = DBConnector.connection_state.prepareStatement(sql);
        if (!filter.equals("all")) {
            stmt.setString(1, formatter.format(startRange));
            stmt.setString(2, formatter.format(endRange));
        }
        ResultSet results = stmt.executeQuery();
        ObservableList<Appointment> output = FXCollections.observableArrayList();
        while (results.next()) {
            Appointment a = findAppointment(results);
            Contact c = findContact(results);
            a.setContact(c);
            output.add(a);
        }
        return output;
    }

    /**
     * Get all appointments from the database with a matching Customer ID. Used for reports generation.
     * @param c Customer ID to be matched against.
     * @return List of all matching appointments.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public static ObservableList<Appointment> getDBAppointmentsByCustomerID(int c) throws SQLException {
        String sql = "select * from appointments where Customer_ID = ?";
        PreparedStatement stmt = DBConnector.connection_state.prepareStatement(sql);
        stmt.setInt(1, c);
        ResultSet results = stmt.executeQuery();
        ObservableList<Appointment> output = FXCollections.observableArrayList();
        while (results.next()) {
            Appointment a = findAppointment(results);
            output.add(a);
        }
        return output;
    }

    /**
     * Get all appointments from the database with a matching Username. Used for determing 15 minute warning status.
     * @param name User to find appointments for.
     * @return List of appointments matching the given Username.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public static ObservableList<Appointment> getDBAppointmentsByUsername(String name) throws SQLException {
        String sql = "select * from appointments inner join users on appointments.User_ID = users.User_ID where users.User_Name = ?";
        PreparedStatement stmt = DBConnector.connection_state.prepareStatement(sql);
        stmt.setString(1, name);
        ResultSet results = stmt.executeQuery();
        ObservableList<Appointment> output = FXCollections.observableArrayList();
        while (results.next()) {
            Appointment a = findAppointment(results);
            output.add(a);
        }
        return output;
    }

    /**
     * Get all appointments from the database with a matching Contact ID.
     * @param c Contact ID to matching against.
     * @return List of appointments matching the given Contact ID.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public static ObservableList<Appointment> getDBAppointmentsByContactID(int c) throws SQLException {
        String sql = "select * from appointments where Contact_ID = ?";
        PreparedStatement stmt = DBConnector.connection_state.prepareStatement(sql);
        stmt.setInt(1, c);
        ResultSet results = stmt.executeQuery();
        ObservableList<Appointment> output = FXCollections.observableArrayList();
        while (results.next()) {
            Appointment a = findAppointment(results);
            output.add(a);
        }
        return output;
    }

    /**
     * Get all appointments from the database grouped by week and year for reports generation.
     * @return List of appointments grouped by week and year.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public static ObservableList<WeeklyReportItem> getDBAppointmentsByWeek() throws SQLException {
        String sql = "select YEAR(Start) As Year, WEEK(Start) as Week, COUNT(*) as Quantity from Appointments GROUP BY WEEK(Start), YEAR(Start) ORDER BY YEAR(Start), WEEK(Start)";
        PreparedStatement stmt = DBConnector.connection_state.prepareStatement(sql);
        ResultSet results = stmt.executeQuery();
        ObservableList<WeeklyReportItem> dataPoints = FXCollections.observableArrayList();
        while (results.next()) {
            WeeklyReportItem temp = new WeeklyReportItem();
            temp.setYear(results.getInt("Year"));
            temp.setWeek(results.getInt("Week"));
            temp.setQuantity(results.getInt("Quantity"));
            dataPoints.add(temp);
        }
        return dataPoints;
    }

    /**
     * Get all appointments formatted in a report style for report generation.
     * @return List of appointments formatted for a report.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public static ObservableList<ReportItem> getDBReportItems() throws SQLException {
        String sql = "select MONTHNAME(Start) AS Month, Type, Count(*) AS Quantity FROM Appointments GROUP BY MONTHNAME(Start), Type";
        PreparedStatement stmt = DBConnector.connection_state.prepareStatement(sql);
        ResultSet results = stmt.executeQuery();
        ObservableList<ReportItem> output = FXCollections.observableArrayList();
        while (results.next()) {
            ReportItem temp = findReportItem(results);
            output.add(temp);
        }
        return output;
    }

    /**
     * Get all divisions from the database.
     * @return List of all divisions.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public static ObservableList<Division> getDBDivisions() throws SQLException {
        String sql = "select * from first_level_divisions";
        PreparedStatement stmt = DBConnector.connection_state.prepareStatement(sql);
        ResultSet results = stmt.executeQuery();
        ObservableList<Division> output = FXCollections.observableArrayList();
        while (results.next()) {
            Division d = findDivision(results);
            output.add(d);
        }
        return output;
    }

    /**
     * Get all countries from the database.
     * @return List of all countries.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public static ObservableList<Country> getDBCountries() throws SQLException {
        String sql = "select * from countries";
        PreparedStatement stmt = DBConnector.connection_state.prepareStatement(sql);
        ResultSet results = stmt.executeQuery();
        ObservableList<Country> output = FXCollections.observableArrayList();
        while (results.next()) {
            Country c = findCountry(results);
            output.add(c);
        }
        return output;
    }

    /**
     * Get all contacts from the database.
     * @return List of all contacts.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public static ObservableList<Contact> getDBContacts() throws SQLException {
        String sql = "select * from contacts";
        PreparedStatement stmt = DBConnector.connection_state.prepareStatement(sql);
        ResultSet results = stmt.executeQuery();
        ObservableList<Contact> output = FXCollections.observableArrayList();
        while (results.next()) {
            Contact c = findContact(results);
            output.add(c);
        }
        return output;
    }

    /**
     * Get all Divisions that belong to a given Country.
     * @param c The selected Country to find appropriate Divisions for.
     * @return List of Divions belonging to the given Country.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public static ObservableList<Division> getDBCountryDivisions(Country c) throws SQLException {
        if (c == null) {
            // Fail silently.
            return FXCollections.observableArrayList();
        }
        String sql = "select * from first_level_divisions where Country_ID = ?";
        PreparedStatement stmt = DBConnector.connection_state.prepareStatement(sql);
        stmt.setInt(1, c.getId());
        ResultSet results = stmt.executeQuery();
        ObservableList<Division> output = FXCollections.observableArrayList();
        while (results.next()) {
            Division d = findDivision(results);
            output.add(d);
        }
        return output;
    }

    /**
     * Perform database insertion to create a new customer.
     * @param c Customer to be added to the database.
     * @return True or false if the insert was successful.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public static boolean addDBCustomer(Customer c) throws SQLException {
        String sql = "INSERT INTO customers " +
                "(Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID)" +
                " VALUES(?,?,?,?,?,?,?,?,?)";
        PreparedStatement stmt = DBConnector.connection_state.prepareStatement(sql);
        stmt.setString(1, c.getName());
        stmt.setString(2, c.getAddress());
        stmt.setString(3, c.getPostalCode());
        stmt.setString(4, c.getPhone());
        stmt.setTimestamp(5, java.sql.Timestamp.valueOf(LocalToUTC(c.getCreateDate())));
        stmt.setString(6, c.getCreatedBy());
        stmt.setTimestamp(7, java.sql.Timestamp.valueOf(LocalToUTC(c.getUpdatedDate())));
        stmt.setString(8, c.getUpdatedBy());
        stmt.setInt(9, c.getDivisionId());

        int rowsChanged = stmt.executeUpdate();
        System.out.printf("Added %d rows to customer DB.\n", rowsChanged);
        return (rowsChanged > 0);
    }

    /**
     * Perform database insertion to create a new appointment.
     * @param a Appointment to be added to the database.
     * @return True or false if the insertion was successful.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public static boolean addDBAppointment(Appointment a) throws SQLException {
        String sql = "INSERT INTO appointments " +
                "(Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID)" +
                " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = DBConnector.connection_state.prepareStatement(sql);
        stmt.setString(1, a.getTitle());
        stmt.setString(2, a.getDesc());
        stmt.setString(3, a.getLocation());
        stmt.setString(4, a.getType());
        stmt.setTimestamp(5, java.sql.Timestamp.valueOf(LocalToUTC(a.getStart())));
        stmt.setTimestamp(6, java.sql.Timestamp.valueOf(LocalToUTC(a.getEnd())));
        stmt.setTimestamp(7, java.sql.Timestamp.valueOf(LocalToUTC(a.getCreateDate())));
        stmt.setString(8, a.getTimestamp().getCreatedBy());
        stmt.setTimestamp(9, java.sql.Timestamp.valueOf(LocalToUTC(a.getUpdatedDate())));
        stmt.setString(10, a.getTimestamp().getUpdatedBy());
        stmt.setInt(11, a.getCustomerId());
        stmt.setInt(12, a.getUserId());
        stmt.setInt(13, a.getContactId());

        int rowsChanged = stmt.executeUpdate();
        System.out.printf("Added %d rows to appointments DB.\n", rowsChanged);
        return (rowsChanged > 0);
    }

    /**
     * Update an existing customer record in the database by matching their Customer_ID.
     * @param c Customer record to be updated.
     * @return True or false whether the update was successful.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public static boolean updateDBCustomer(Customer c) throws SQLException {
        String sql = "UPDATE customers " +
                "SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, " +
                "Last_Update = ?, Last_Updated_By = ?, " +
                "Division_ID = ? " +
                "WHERE Customer_ID = ?";
        PreparedStatement stmt = DBConnector.connection_state.prepareStatement(sql);
        stmt.setString(1, c.getName());
        stmt.setString(2, c.getAddress());
        stmt.setString(3, c.getPostalCode());
        stmt.setString(4, c.getPhone());
        stmt.setTimestamp(5, java.sql.Timestamp.valueOf(LocalToUTC(c.getUpdatedDate())));
        stmt.setString(6, c.getUpdatedBy());
        stmt.setInt(7, c.getDivisionId());
        stmt.setInt(8, c.getId());

        int rowsChanged = stmt.executeUpdate();
        System.out.printf("Updated %d rows in customer DB.\n", rowsChanged);
        return (rowsChanged > 0);

    }

    /**
     * Update an existing appointment record in the database by matching the Appointment_ID.
     * @param a Appointment record to be updated.
     * @return True or false if the update was successful.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public static boolean updateDBAppointment(Appointment a) throws SQLException {
        String sql = "UPDATE appointments " +
                "SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, " +
                "Last_Update = ?, Last_Updated_By = ?, " +
                "Customer_ID = ?, User_ID = ?, Contact_ID = ? " +
                "WHERE Appointment_ID = ?";
        PreparedStatement stmt = DBConnector.connection_state.prepareStatement(sql);
        stmt.setString(1, a.getTitle());
        stmt.setString(2, a.getDesc());
        stmt.setString(3, a.getLocation());
        stmt.setString(4, a.getType());
        stmt.setTimestamp(5, java.sql.Timestamp.valueOf(LocalToUTC(a.getStart())));
        stmt.setTimestamp(6, java.sql.Timestamp.valueOf(LocalToUTC(a.getEnd())));
        stmt.setTimestamp(7, java.sql.Timestamp.valueOf(LocalToUTC(a.getUpdatedDate())));
        stmt.setString(8, a.getTimestamp().getUpdatedBy());
        stmt.setInt(9, a.getCustomerId());
        stmt.setInt(10, a.getUserId());
        stmt.setInt(11, a.getContactId());
        stmt.setInt(12, a.getId());

        int rowsChanged = stmt.executeUpdate();
        System.out.printf("Updated %d rows in appointments DB.\n", rowsChanged);
        return (rowsChanged > 0);
    }

    /**
     * Perform database deletion of a Customer. Checks if there are foreign key restrictions in place and
     * deletes any associated appointments prior to deleting a customer. Displays an informative message after
     * delete has taken place.
     * @param c Customer to be deleted from database.
     * @return True or false if the deletion was successful.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public static boolean deleteDBCustomer(Customer c) throws SQLException {
        // Handle appointment deletion first.
        String sql = "DELETE FROM appointments WHERE Customer_ID = ?";
        PreparedStatement stmt = DBConnector.connection_state.prepareStatement(sql);
        stmt.setInt(1, c.getId());
        int rowsChanged = stmt.executeUpdate();
        System.out.printf("Deleted %d rows from appointments DB.\n", rowsChanged);

        // Now handle deleting customer.
        sql = "DELETE FROM customers WHERE Customer_ID = ?";
        stmt = DBConnector.connection_state.prepareStatement(sql);
        stmt.setInt(1, c.getId());
        rowsChanged = stmt.executeUpdate();
        System.out.printf("Deleted %d rows from customer DB.\n", rowsChanged);
        return (rowsChanged > 0);
    }

    /**
     * Perform database deletion of an Appointment.
     * @param a Appointment record to be deleted.
     * @return True or false if the deletion was successful.
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    public static boolean deleteDBAppointment(Appointment a) throws SQLException {
        String sql = "DELETE FROM appointments WHERE Appointment_ID = ?";
        PreparedStatement stmt = DBConnector.connection_state.prepareStatement(sql);
        stmt.setInt(1, a.getId());

        int rowsChanged = stmt.executeUpdate();
        System.out.printf("Deleted %d rows from appointments DB.\n", rowsChanged);
        return (rowsChanged > 0);
    }

    /**
     * Parse a ResultSet for the required fields to build a Country data class.
     *
     * The next several classes all perform the same action but on different data classes.
     *
     * @param results ResultSet to be searched.
     * @return Country record found in the ResultSet
     * @throws SQLException Thrown if the database couldn't be accessed.
     */
    protected static Country findCountry(ResultSet results) throws SQLException {
        Country temp = new Country();
        temp.setId(results.getInt("Country_ID"));
        temp.setCountry(results.getString("Country"));
        temp.setTimestamp(findTimestamp(results));
        return temp;
    }
    protected static Division findDivision(ResultSet results) throws SQLException {
        Division temp = new Division();
        temp.setId(results.getInt("Division_ID"));
        temp.setDivision(results.getString("Division"));
        temp.setCountryId(results.getInt("Country_ID"));
        temp.setTimestamp(findTimestamp(results));
        return temp;
    }
    protected static Customer findCustomer(ResultSet results) throws SQLException {
        Customer temp = new Customer();
        temp.setId(results.getInt("Customer_ID"));
        temp.setName(results.getString("Customer_Name"));
        temp.setAddress(results.getString("Address"));
        temp.setPostalCode(results.getString("Postal_Code"));
        temp.setPhone(results.getString("Phone"));
        temp.setDivisionId(results.getInt("Division_ID"));
        temp.setTimestamp(findTimestamp(results));
        return temp;
    }
    protected static Appointment findAppointment(ResultSet results) throws SQLException {
        Appointment temp = new Appointment();
        temp.setId(results.getInt("Appointment_ID"));
        temp.setTitle(results.getString("Title"));
        temp.setDesc(results.getString("Description"));
        temp.setLocation(results.getString("Location"));
        temp.setType(results.getString("Type"));
        temp.setStart(UTCToLocal(results.getTimestamp("Start").toLocalDateTime()));
        temp.setEnd(UTCToLocal(results.getTimestamp("End").toLocalDateTime()));
        temp.setCustomerId(results.getInt("Customer_ID"));
        temp.setUserId(results.getInt("User_ID"));
        temp.setContactId(results.getInt("Contact_ID"));
        temp.setTimestamp(findTimestamp(results));
        return temp;

    }
    protected static Contact findContact(ResultSet results) throws SQLException {
        Contact temp = new Contact();
        temp.setId(results.getInt("Contact_ID"));
        temp.setName(results.getString("Contact_Name"));
        temp.setEmail(results.getString("Email"));
        return temp;
    }
    protected static ReportItem findReportItem(ResultSet results) throws SQLException {
        ReportItem temp = new ReportItem();
        temp.setMonth(results.getString("Month"));
        temp.setType(results.getString("Type"));
        temp.setQuantity(results.getInt("Quantity"));
        return temp;
    }
    protected static Timestamp findTimestamp(ResultSet results) throws SQLException {
        Timestamp time = new Timestamp();
        time.setCreateDate(UTCToLocal(results.getTimestamp("Create_Date").toLocalDateTime()));
        time.setCreatedBy(results.getString("Created_By"));
        time.setUpdatedDate(UTCToLocal(results.getTimestamp("Last_Update").toLocalDateTime()));
        time.setUpdatedBy(results.getString("Last_Updated_By"));

        return time;
    }


    /**
     * Helper function to convert from one timezone to another.
     * @param time Takes a LocalDateTime zone-less time.
     * @param from Takes a String matching a ZoneId
     * @param to Takes a String matching a ZoneId
     * @return Returns the LocalDateTime FROM the first ZoneId TO the second ZoneId
     */
    // Convert any timezone
    public static LocalDateTime convertTime(LocalDateTime time, String from, String to) {
        ZonedDateTime fromDateTime = time.atZone(from.equals("local") ? ZoneId.systemDefault() : ZoneId.of(from));
        ZonedDateTime toDateTime = fromDateTime.withZoneSameInstant(to.equals("local") ? ZoneId.systemDefault() : ZoneId.of(to));
        // System.out.printf("Time converted:\n%s time: %s\n%s time: %s\n", from, fromDateTime, to, toDateTime);
        return toDateTime.toLocalDateTime();
    }

    /**
     * Helper functions to convert explicitly Local to UTC and vice-versa.
     * @param time LocalDateTime to be converted.
     * @return Converted time either to or from UTC from local time.
     */
    // UTC <--> Local Time
    public static LocalDateTime LocalToUTC(LocalDateTime time) {
        ZonedDateTime localDateTime = time.atZone(ZoneId.systemDefault());
        ZonedDateTime zonedDateTime = localDateTime.withZoneSameInstant(ZoneId.of("UTC"));
        // System.out.printf("Local time: %s\nUTC Time: %s\n", localDateTime, zonedDateTime);
        return zonedDateTime.toLocalDateTime();
    }
    public static LocalDateTime UTCToLocal(LocalDateTime time) {
        ZonedDateTime zonedDateTime = time.atZone(ZoneId.of("UTC"));
        ZonedDateTime localDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault());
        // System.out.printf("UTC Time: %s\nLocal Time: %s\n", zonedDateTime, localDateTime);
        return localDateTime.toLocalDateTime();
    }

}
