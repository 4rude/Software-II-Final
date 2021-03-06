package view_controller;

import DAO.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Appointment;
import model.Contact;
import model.User;
import utilities.LoginIO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *  The Reports class is a ViewController that is responsible for displaying the scene that displays the three different
 *  reports required for this application.
 */
public class Reports {
    // Initialize the resource bundle which holds the Locale information
    ResourceBundle rb = ResourceBundle.getBundle("main/Nat", Locale.getDefault());
    // Create a user object to hold current app user data
    private User user;

    @FXML
    private TextArea reportTextArea;

    /**
     * This method is responsible for moving the app user from the Reports scene to the MainCalendar scene. When a user
     * presses the Exit button, the applcation will take the resource bundle data and user data and pass it to the
     * Main Calendar scene for use.
     *
     * @param event
     */
    @FXML
    void exitReportScreen(MouseEvent event) {
        try{
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
    }

    /**
     * This method generates the first report. It creates a variable to store data that is to be published to the
     * textArea field on the scene. It then clears the textArea, fills an Observable list with data on all the
     * appointments, then takes all the type of appointments and months of appointments and adds that data to an
     * ArrayList. The method then groups appointments by month by using the following libraries: The java stream library
     * provides operations on streams of elements. The collect() function then accumulates the type & month elements
     * into a container. The Collector class then uses the groupBy function to determine how to group its arguments. The
     * Collector counting() class then counts the number of given elements (type or month), and add the respective type
     * or month to the key, and the counted amount to the value of a Map. This Map data structure uses a String type to
     * hold the key, and a long type to hold the value. A for loop then iterates through the Map to add to a string,
     * key by key, the amount of appointments for each month and type. The dataStore string, holding this data, is then
     * appended to the textArea.
     *
     * @param event
     */
    @FXML
    void generateReportOne(MouseEvent event) {
        // Store all data in String format to be added to the textArea
        String dataStore = "";
        // Clear textArea
        reportTextArea.clear();

        ObservableList<Appointment> allAppointments = null;
        try {
            allAppointments = FXCollections.observableArrayList(AppointmentDAO.getAllAppointments());
        } catch (SQLException e) {
            System.out.println(e);
        }
        ArrayList<String> months = new ArrayList();
        ArrayList<String> types = new ArrayList();

        // Create a list of names of months from each appointment
        for (Appointment appointment: allAppointments) {
            months.add(appointment.getStart().getMonth().toString());
        }
        // Create a list and add each appointment by type
        for (Appointment appointment: allAppointments) {
            types.add(appointment.getType());
        }

        // Maps each unique month value in the months ArrayList to the appointmentsByMonth key, and its respective
        // value is the frequency of those months
        // Streams pattern is from https://www.techiedelight.com/count-frequency-elements-list-java/
        Map<String, Long> appointmentsByMonth = months.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // Maps each unique appointment type value in the types ArrayList to the appointmentsByMonth key, and its
        // respective value is the frequency of those types
        Map<String, Long> appointmentsByType = types.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        dataStore += "Appointments by Month:" + System.lineSeparator() + "-------------------------------" + System.lineSeparator() + System.lineSeparator();

        // For each month in appointmentsByMonth, print Month: "Amount Appointments" + System.lineSeparator()
        for (Map.Entry<String, Long> month: appointmentsByMonth.entrySet()) {
            dataStore += month.getKey() + ":  " + month.getValue() + " appointments" + System.lineSeparator();
        }

        dataStore += System.lineSeparator() + "Appointments by Type:" + System.lineSeparator() + "------------------------------" + System.lineSeparator() + System.lineSeparator();

        // For each type in appointmentsByType, print Type: "Amount Appointments" + System.lineSeparator()
        for (Map.Entry<String, Long> type: appointmentsByType.entrySet()) {
            dataStore += type.getKey() + ":  " + type.getValue() + " appointments" + System.lineSeparator();
        }

        // Append string to report textArea
        reportTextArea.appendText(dataStore);
    }

    /**
     * This method generates report three, which displays the data log of user login attempts. It displays each line of
     * the user logs so the user can easily see who has (attempted to) log in to the application.
     *
     * @param event
     */
    @FXML
    void generateReportThree(MouseEvent event) {
        // Add a string as a header for the textArea
        String queryData = "User Logs:" + System.lineSeparator() + "-----------" + System.lineSeparator() + System.lineSeparator();

        // Get data from log activity file & add to String variable
        queryData += LoginIO.getLoginAttempts();

        // Clear textArea
        reportTextArea.clear();

        // Append string to report textArea
        reportTextArea.appendText(queryData);

    }

    /**
     * This method generates and displays (on the textArea) the second report required for this project. The report
     * displays an appointment schedule for each contact within this fictional business organization. For each contact
     * within the organization, it prints out each appointment tied to them, including the appointment ID, title, type,
     * description, appointment start and end date & time, and its respective customer ID.
     *
     * @param event
     */
    @FXML
    void generateReportTwo(MouseEvent event) {
        // Create string to hold queried data
        String queryData = "";

        // Create observable lists to hold appointment and contact data
        ObservableList<Appointment> allAppointments = null;
        ObservableList<Contact> allContacts = null;
        // Query data and fill up observable lists
        try {
            allContacts = FXCollections.observableArrayList(ContactDAO.getAllContacts());
            allAppointments = FXCollections.observableArrayList(AppointmentDAO.getAllAppointments());
        } catch (SQLException e) {
            System.out.println(e);
        }

        // For each contact, add the contact name to the textArea
        for (Contact contact: allContacts) {
            queryData += contact.getContactName() + "'s Appointment Schedule" + System.lineSeparator() +
            "-------------------------------------" + System.lineSeparator() + System.lineSeparator();
            for (Appointment appointment: allAppointments) {
                // For each appointment that matches the contact ID, display the appointment data for contact
                if(contact.getContactID() == appointment.getContactID()) {
                    queryData += "Appointment ID: " + appointment.getAppointmentID() + " || Title: " +
                            appointment.getTitle() + " || Type: " + appointment.getType() + " || Description: " +
                            appointment.getDescription() + " || Appointment Start Date & Time in UTC: " +
                            appointment.getStart().toString() + " || Appointment End Date & Time in UTC: " +
                            appointment.getEnd().toString() + " || Customer ID: " + appointment.getCustomerID() +
                            System.lineSeparator() + System.lineSeparator();
                }
            }


        }

        // Clear textArea
        reportTextArea.clear();
        // Append string to report textArea
        reportTextArea.appendText(queryData);
    }

    /**
     * This method is the constructor of the Reports class, it takes in a user object to be used by the Reports class.
     * The user variable is passed around this application.
     *
     * @param user
     */
    public Reports(User user) {
        this.user = user;
    }
}
