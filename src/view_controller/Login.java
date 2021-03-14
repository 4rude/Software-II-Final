package view_controller;

import DAO.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.User;
import utilities.LoginIO;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The Login class is used to implement the Login ViewController scene with its jfx buttons, text fields, and labels. It
 * specifically holds the functionality for attempting to log in a user to the application, check if the users data
 * is accepted, and if not it displays an error label to the user. It also allows the user to clear/reset the text
 * fields and error labels.
 */
public class Login implements Initializable {

    // Create user object to be used by the class
    ArrayList<User> allUsers = new ArrayList<>();

    // Create Resource Bundle object to hold Locale data
    private ResourceBundle rb;

    // Login error alert message
    private final Alert loginAlertMessage = new Alert(Alert.AlertType.ERROR);

    @FXML
    private Label companyLoginHeaderLabel;

    @FXML
    private Label languageLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label passwordLabel;

    @FXML
    private TextField usernameTextBox;

    @FXML
    private TextField passwordTextBox;

    @FXML
    private Button loginButton;

    @FXML
    private Button refreshButton;

    @FXML
    private Label detectedLanguageLabel;

    @FXML
    private Label loginErrorLabel;

    /**
     * The clickLogin method clears the error label, checks the user info in the database against the user name/password
     * that the user entered. If the user name and password is accepted, the user is sent to the MainCalendar page, if
     * it is not accepted, the user is kept on the page and an error is thrown.
     *
     * @param event
     */
    @FXML
    void clickLogin(MouseEvent event) {
        int counter = 0;
        // Reset error label
        loginErrorLabel.setText("");
        // Compare the user data w/ the data from the list of user data
        // For user in allUsers
        for (User user: allUsers){
            // If the user/password is correct
            if (user.getUserName().equals(usernameTextBox.getText()) && user.getPassword().equals(passwordTextBox.getText())) {
                try{
                    // Add the attempt to the login_activity.txt file
                    LoginIO.addLoginAttempt(usernameTextBox.getText(), passwordTextBox.getText(), true);

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("MainCalendar.fxml"), rb);
                    MainCalendar controller = new MainCalendar(user, null);
                    loader.setController(controller);
                    Parent root = loader.load();
                    Scene scene = new Scene(root);
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(scene);
                    stage.setResizable(false);
                    stage.show();
                    System.out.println("Accepted");
                } catch (IOException | SQLException e) {
                    System.out.println(e);
                }
            } else {
                counter += 1;
            }
        }
        if (counter == allUsers.size()) {
            System.out.println("Not Accepted");
            // Add the attempt to the login_activity.txt file
            LoginIO.addLoginAttempt(usernameTextBox.getText(), passwordTextBox.getText(), false);
            // If it is the incorrect user/password
            loginErrorLabel.setText(rb.getString("login_error_label"));
        }
    }

    /**
     * The clickRefresh event happens when someone clicks the refresh button. When that happens, it clears the text
     * fields and error labels.
     *
     * @param event
     */
    @FXML
    void clickRefresh(MouseEvent event) {
        // Refresh text boxes
        usernameTextBox.clear();
        passwordTextBox.clear();
        loginErrorLabel.setText("");

    }

    /**
     * The initialize method is ran after the constructor is ran. Within this function the labels are initialized and
     * user data is gathered from the database.
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rb = resources;
        setLabelAndAlertLanguage();
        getUserData();
    }

    /**
     * The getUserData class is used to execute a query to the database and fill the allUser ArrayList with the user
     * table data.
     *
     */
    private void getUserData() {
        // Add all users to the allUsers list
        try {
            allUsers = UserDAO.getAllUsers();
        } catch (SQLException e) {
            System.out.println(e);
        }

    }

    /**
     * The setLabelAndAlertLanguage method sets all labels to default language and initializes the error label.
     */
    private void setLabelAndAlertLanguage(){
        loginButton.setText(rb.getString(loginButton.getText()));
        refreshButton.setText(rb.getString(refreshButton.getText()));
        passwordLabel.setText(rb.getString(passwordLabel.getText()));
        usernameLabel.setText(rb.getString(usernameLabel.getText()));
        companyLoginHeaderLabel.setText(rb.getString(companyLoginHeaderLabel.getText()));
        detectedLanguageLabel.setText(Locale.getDefault().getDisplayLanguage());
        languageLabel.setText(rb.getString(languageLabel.getText()));
        loginErrorLabel.setText("");
    }
}
