package wgu.c195.schedulingsystem.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import wgu.c195.schedulingsystem.database.DBQuery;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Login Form to perform language translations and user authentication.
 */
public class LoginController extends GenericController implements Initializable {
    @FXML
    private Label label_language;
    @FXML
    private Label label_disp_language;
    @FXML
    private Label label_user;
    @FXML
    private TextField textfield_user;
    @FXML
    private Label label_pass;
    @FXML
    private TextField textfield_pass;
    @FXML
    private Button button_login;
    @FXML
    private Button button_clear;
    private URL _url;
    private ResourceBundle _resources;
    private static String user;

    /**
     * Main initialize function to run upon program start. Loads the user's resource bundle based on the
     * default Locale setting in order to display the correct language to the user.
     *
     * @param url
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param bundle
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle bundle) {
         // Locale.setDefault(new Locale("fr"));
        _url = url;
        try {
            _resources = ResourceBundle.getBundle("wgu.c195.schedulingsystem.bundles/LoginForm", Locale.getDefault());
            updateLanguage(_resources);
        } catch (Exception e) {
            System.err.println(String.format("Unable to load locale %s", Locale.getDefault().toString()));
        }

    }

    /**
     * After discovering the correct ResourceBundle to use, populate all the fields on this form with the correct
     * translated text.
     * @param bundle ResourceBundle to be used for translations.
     */
    private void updateLanguage(ResourceBundle bundle) {
        label_language.setText(bundle.getString("labelLanguage"));
        label_disp_language.setText(ZoneId.systemDefault().getDisplayName(TextStyle.FULL, Locale.getDefault()));
        //label_disp_language.setText(ZoneId.systemDefault().toString());
        label_user.setText(bundle.getString("labelUser"));
        textfield_user.clear();
        textfield_user.setPromptText(bundle.getString("labelUser"));
        label_pass.setText(bundle.getString("labelPass"));
        textfield_pass.clear();
        textfield_pass.setPromptText(bundle.getString("labelPass"));
        button_login.setText(bundle.getString("buttonLogin"));
        button_clear.setText(bundle.getString("buttonClear"));
    }

    /**
     * Attempt to login a user by calling DBQuery to handle the database actions.
     * @param event Callback method upon clicking the login button.
     * @return True or false based on login success condition.
     * @throws SQLException Thrown if the database couldn't be accessed.
     * @throws IOException Thrown if the main CRUD form couldn't be loaded.
     */
    @FXML
    protected boolean doLogin(ActionEvent event) throws SQLException, IOException {
        // perform input validation
        if (textfield_user.getText().isBlank() || textfield_pass.getText().isBlank()) {
            alertDialog(_resources.getString("ErrTitle"), _resources.getString("ErrHeader"), _resources.getString("ErrEmptyText"), Alert.AlertType.ERROR);
            return false;
        }
        // fields are valid, move on to login authentication
        boolean success = DBQuery.validateLogin(textfield_user.getText(), textfield_pass.getText());
        // Send this attempt to be logged
        loginAttempt(textfield_user.getText(), success);
        // Set our global user to pass on to forms once we logged in
        user = textfield_user.getText();
        if (!success) {
            alertDialog(_resources.getString("ErrTitle"), _resources.getString("ErrHeader"), _resources.getString("ErrFailedAuthText"), Alert.AlertType.ERROR);
            return false;
        } else {
            moveToWindow(event, "crud_form.fxml", "Scheduling System");
        }

        return true;
    }

    /**
     * Clear fields and go back to a clean slate.
     */
    @FXML
    protected void doClear() {
        initialize(_url, _resources);

    }

    /**
     * Helper function to get the currently logged in user.
     * @return Username that is logged in.
     */
    protected static String getUser() {
        return user;
    }
}