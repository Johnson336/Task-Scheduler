package wgu.c195.schedulingsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import wgu.c195.schedulingsystem.database.DBConnector;

import java.io.IOException;
import java.sql.SQLException;

/**
 * WGU C195 Software II - Task 1
 * @author Cole Niermeyer
 * Student ID: 010690919
 */
public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login_form.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) throws SQLException {
        // Connect to database
        DBConnector.connectDB();
        launch();
        // Disconnect database
        DBConnector.closeDB();
    }
}