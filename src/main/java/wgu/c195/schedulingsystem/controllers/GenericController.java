package wgu.c195.schedulingsystem.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import wgu.c195.schedulingsystem.Main;
import wgu.c195.schedulingsystem.database.DBQuery;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

/**
 * Generic Controller class containing some helper functions that the other Controllers will use.
 */
public class GenericController {

    /**
     * Method used to load window FXML files and move between screens while maintaining a single Stage element
     * to avoid wasted memory usage.
     * @param event Callback function upon clicking button that navigates to new form.
     * @param windowPath Path of the intended FXML form file.
     * @param windowTitle Title for the new form screen.
     * @throws IOException Thrown when a FXML file cannot be located.
     */
    public void moveToWindow(ActionEvent event, String windowPath, String windowTitle) throws IOException {
        try {
            // Path to FXML File
            String FXMLPath = windowPath;
            // Create the FXMLLoader
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(FXMLPath));

            // create the scene
            Scene scene = new Scene(fxmlLoader.load());
            // Get existing stage relative to Button pressed
            Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
            // Set the title to the Stage
            stage.setTitle(windowTitle);
            // Set the Scene to the Stage
            stage.setScene(scene);
            // Set resizable false
            stage.setResizable(false);
            // Display the Stage
            stage.show();
        } catch (Exception exception) {
            System.err.println(String.format("Error: %s", exception.getMessage()));
            exception.printStackTrace();
        }
    }
    /**
     * Called on any window that clicks 'Cancel' to return to the main form.
     * @param event Callback upon clicking 'Cancel' button.
     * @throws IOException Thrown if we're unable to find the main form FXML file.
     */
    // Cancel a window, move back to main
    public void onClickBack(ActionEvent event) throws IOException {
        moveToWindow(event, "crud_form.fxml", "Scheduling System");
    }

    /**
     * Called upon clicking 'Exit' button on the main form.
     * @param event Callback upon clicking 'Exit' button.
     */
    // Exit the program
    public void handleExit(ActionEvent event) {
        System.exit(0);
    }

    /**
     * Function to log a login attempt to an external file.
     * @param user User that tried to login.
     * @param success Did the attempt succeed?
     */
    public void loginAttempt(String user, boolean success) {
        try {
            PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter("login_activity.txt", true)));
            output.format("%s | User: %s, Login Attempted (%s)\n", DBQuery.convertTime(LocalDateTime.now(), "local", "UTC"), user, ((success ? "succeeded" : "failed")));
            output.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Function to display an alert dialog for numerous purposes.
     * @param title Title of alert window.
     * @param head Heading of alert window.
     * @param body Main body text of alert window.
     * @param type Type of alert to display.
     */
    public void alertDialog(String title, String head, String body, Alert.AlertType type) {
        Alert error = new Alert(type);
        error.setTitle(title);
        error.setHeaderText(head);
        error.setContentText(body);
        error.showAndWait();
    }
}
