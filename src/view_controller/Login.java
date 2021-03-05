package view_controller;

import DAO.AppointmentDAO;
import DAO.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import java.io.IOException;
import java.net.URL;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

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
    void clickLogin(MouseEvent event) {
        int counter = 0;
        // Compare the user data w/ the data from the list of user data
        // For user in allUsers
        for (User user: allUsers){
            // If the user/password is correct
            if (user.getUserName().equals(usernameTextBox.getText()) && user.getPassword().equals(passwordTextBox.getText())) {
                try{
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("MainCalendar.fxml"), rb);
                    MainCalendar controller = new MainCalendar(user);
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
            // If it is the incorrect user/password
            // *** THIS MUST BE CHANGED PRIOR TO SUBMISSION *** NO HARD CODED TRANSLATIONS FOR ERRORS
            if (Locale.getDefault().getDisplayLanguage().equals("français")){
                loginAlertMessage.setContentText("Identifiant ou mot de passe incorrect.");
            } else {
                loginAlertMessage.setContentText("Incorrect Username or Password.");
            }
            loginAlertMessage.show();
        }
    }

    @FXML
    void clickRefresh(MouseEvent event) {
        // Refresh text boxes
        usernameTextBox.clear();
        passwordTextBox.clear();
    }

    // Initialize the class with the correct label and alert language
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rb = resources;
        setLabelAndAlertLanguage();

        try {
            getUserData();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void getUserData() throws SQLException {
        // Add all users to the allUsers list
        allUsers = UserDAO.getAllUsers();
    }

    // Set all labels to default language
    private void setLabelAndAlertLanguage(){
        loginButton.setText(rb.getString(loginButton.getText()));
        refreshButton.setText(rb.getString(refreshButton.getText()));
        passwordLabel.setText(rb.getString(passwordLabel.getText()));
        usernameLabel.setText(rb.getString(usernameLabel.getText()));
        companyLoginHeaderLabel.setText(rb.getString(companyLoginHeaderLabel.getText()));
        detectedLanguageLabel.setText(Locale.getDefault().getDisplayLanguage());
        languageLabel.setText(rb.getString(languageLabel.getText()));
    }
}
