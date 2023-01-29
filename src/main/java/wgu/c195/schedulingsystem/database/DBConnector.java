package wgu.c195.schedulingsystem.database;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Class to facilitate opening and closing a database connection for use throughout the program lifecycle.
 */
public class DBConnector {
    public static Connection connection_state;
    private static final String jdbcURL = "jdbc:mysql://localhost/client_schedule?connectionTimeZone = SERVER";
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String user = "sqlUser";
    private static final String pass = "Passw0rd!";

    public DBConnector() {

    }

    /**
     * Open the Database connection by calling the DriverManager JDBC driver.
     */
    public static void connectDB() {
        try {
            Class.forName(driver);
            connection_state = DriverManager.getConnection(jdbcURL, user, pass);
            System.out.println("Database connected.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Close the database connection upon program exit.
     */
    public static void closeDB() {
        try {
            connection_state.close();
            System.out.println("Database disconnected.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Getter method to return the current Connection state to numerous functions.
     * @return Current database connection state for performing database actions.
     */
    public static Connection getState() {
        return connection_state;
    }



}
