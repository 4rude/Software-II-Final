package view_controller;

import DAO.AppointmentDAO;
import DAO.ContactDAO;
import DAO.CustomerDAO;
import DAO.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Appointment;
import model.Contact;
import model.Customer;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class CreateAppointment implements Initializable {
    // Initialize the resource bundle which holds the Locale information
    ResourceBundle rb = ResourceBundle.getBundle("main/Nat", Locale.getDefault());

    // Get the Time Zone ID of the users local time zone
    ZoneId localTimeZoneID = ZoneId.systemDefault();

    // Create allAppointments Observable List for filling the combo boxes
    private ObservableList<Appointment> allAppointments;
    // Create allCustomers Observable List for filling the customer name combo box
    private ObservableList<Customer> allCustomers;
    // Create allContacts Observable List for filling the contact name combo box
    private ObservableList<Contact> allContacts;
    // Create allUsers Observable List for filling the user name combo box
    private ObservableList<User> allUsers;
    // Create user object
    private User user;

    @FXML
    private TextField appointmentIDField;

    @FXML
    private TextField titleField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField locationField;

    @FXML
    private TextField typeField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private ComboBox<LocalTime> startTimeComboBox;

    @FXML
    private ComboBox<LocalTime> endTimeComboBox;

    @FXML
    private ComboBox<Integer> customerIDDropDown;

    @FXML
    private ComboBox<Integer> userIDDropDown;

    @FXML
    private ComboBox<String> contactNameDropDown;

    @FXML
    private Label timeBoundsErrorLabel;

    @FXML
    private Label apptOverlapErrorLabel;

    @FXML
    private Label timeOutOfOrderLabel;

    @FXML
    private Label emptyFieldLabel;

    @FXML
    void cancelCreateAppointment(MouseEvent event) {
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
    }

    @FXML
    void createAppointment(MouseEvent event) {
        // Use LocalDateTime of() operation to combine LocalDate and LocalTime from the time & date comboBox + DatePicker
        // The LocalDate and LocalTime is combined into a LocalDateTime object, which can be added to the DB
        // Add user input to temporary variables
        String title = titleField.getText().trim();
        String desc = descriptionField.getText().trim();
        String loc = locationField.getText().trim();
        String type = typeField.getText().trim();

        // Get local date and time and combine into LocalDateTime
        LocalTime startT = startTimeComboBox.getValue();
        LocalTime endT = endTimeComboBox.getValue();
        LocalDate startD = startDatePicker.getValue();
        // Convert Default Time Zone to UTC
        LocalDateTime startDT = LocalDateTime.of(startD, startT).atZone(localTimeZoneID).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
        // Convert Default Time Zone to UTC
        LocalDateTime endDT = LocalDateTime.of(startD, endT).atZone(localTimeZoneID).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();

        String lastUpdatedBy = user.getUserName();
        int customerID = customerIDDropDown.getValue();
        int userID = userIDDropDown.getValue();
        int contactID = 0;
        for (Contact contact: allContacts) { // Determine which contactID is selected from drop down of contact names
            if (contact.getContactName().equalsIgnoreCase(contactNameDropDown.getValue())) {
                contactID = contact.getContactID();
            }
        }

        // Use as a flag to determine if the appointment date/times pass the error handling
        boolean approvedAppointment;
        // if appointment start time is after 8am and before 10pm EST... && if appointment end time is after 8am and before 10pm EST...
        LocalTime businessOpenTime = LocalTime.of(13, 0); // 8AM EST == 1PM UTC
        LocalTime businessCloseTime = LocalTime.of(3, 0); // 10PM EST == 3AM UTC
        if (startDT.toLocalTime().isBefore(businessOpenTime) && startDT.toLocalTime().isAfter(businessCloseTime)) {
            // Unavailable time, create bad start time alert label
            timeBoundsErrorLabel.setText("Error: Start & End times must be within 8am - 10pm EST");
            approvedAppointment = false;
        } else if (endDT.toLocalTime().isBefore(businessOpenTime) && endDT.toLocalTime().isAfter(businessCloseTime)) {
            // Unavailable time, create bad end time alert label
            timeBoundsErrorLabel.setText("Error: Start & End times must be within 8am - 10pm EST");
            approvedAppointment = false;
        } else if (endT.isBefore(startT)) {
            timeOutOfOrderLabel.setText("Error: The start time must come before the end time");
            approvedAppointment = false;
        } else {
            approvedAppointment = true;
        }
        // For each appointment, if the customerID is the same as the updatedAppt & the appointment starts and if
        // the current appointment in the iteration does not equal the appointmentID of the selected appointment...
        for (Appointment appt: allAppointments) {
            if (appt.getCustomerID() == customerID) {
                if (startDT.isAfter(appt.getStart()) && startDT.isBefore(appt.getEnd())) {
                    approvedAppointment = false;
                    // Display error label saying appt starts when another appt is going on
                    apptOverlapErrorLabel.setText("Error: Appointment overlaps with another");
                } else if (endDT.isAfter(appt.getStart()) && endDT.isBefore(appt.getEnd())) {
                    approvedAppointment = false;
                    // Display error label saying appt ends during the time another appt is going on
                    apptOverlapErrorLabel.setText("Error: Appointment overlaps with another");
                }
            }
        }
        if (approvedAppointment) { // No errors
            try{
                // Convert from UTC to computers default locale (CANNOT BE HARD CODED, CHANGE THIS)
                LocalDateTime convertedStartDT = startDT.atZone(ZoneOffset.UTC).withZoneSameInstant(localTimeZoneID).toLocalDateTime();
                LocalDateTime convertedEndDT = endDT.atZone(ZoneOffset.UTC).withZoneSameInstant(localTimeZoneID).toLocalDateTime();
                LocalDateTime currentTimeInUTC = LocalDateTime.now(); // Used to fill the Appointment instance constructor

                // Create a new Appointment object which holds user input data
                Appointment newAppointment = new Appointment(0, title, desc, loc, type, convertedStartDT, convertedEndDT,
                        currentTimeInUTC, lastUpdatedBy, currentTimeInUTC, lastUpdatedBy, customerID, userID, contactID);

                // Execute the prepared statement using the selectedAppointment Appointment object
                AppointmentDAO.createAppointment(newAppointment);

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
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rb = resources;
        initializeDataFields();
        initializeErrorLabels();

    }

    public CreateAppointment(User user) throws SQLException {
        this.user = user;
    }

    private void initializeDataFields() {
        // Handle SQL exceptions when using DAO functions
        try {
            allAppointments = FXCollections.observableArrayList(AppointmentDAO.getAllAppointments());
            allCustomers = FXCollections.observableArrayList(CustomerDAO.getAllCustomers());
            allContacts = FXCollections.observableArrayList(ContactDAO.getAllContacts());
            allUsers = FXCollections.observableArrayList(UserDAO.getAllUsers());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // Create Observable Lists that can hold DB data
        ObservableList<Integer> customerIDs = FXCollections.observableArrayList();
        ObservableList<Integer> userIDs = FXCollections.observableArrayList();
        ObservableList<String> contactNames = FXCollections.observableArrayList();
        // Load ObservableLists with data from Appointments and Contacts so it can be used for ComboBoxes
        for (Customer customer: allCustomers) {
            customerIDs.add(customer.getCustomerID());
        }
        for (User user: allUsers) {
            userIDs.add(user.getUserID());
        }
        for (Contact contact: allContacts) {
            contactNames.add(contact.getContactName());
        }

        // Fill the combo boxes for user selection
        customerIDDropDown.getItems().addAll(customerIDs);
        contactNameDropDown.getItems().addAll(contactNames);
        userIDDropDown.getItems().addAll(userIDs);
        // Set the default selection for the combo boxes as the first index place
        customerIDDropDown.getSelectionModel().selectFirst();
        contactNameDropDown.getSelectionModel().selectFirst();
        userIDDropDown.getSelectionModel().selectFirst();

        // Start and end time LocalTime objects to use for bounds
        LocalTime startTime = LocalTime.of(0,0);
        LocalTime endTime = LocalTime.of(23, 51);

        // Fill start/end time combo boxes with UTC time for 10 minute intervals
        while(startTime.isBefore(endTime.minusSeconds(1))) {
            startTimeComboBox.getItems().add(startTime);
            endTimeComboBox.getItems().add(startTime);
            if(startTime.isAfter(LocalTime.of(23,40))){
                break; // End the loop
            }
            startTime = startTime.plusMinutes(10);
        }

        // Set default time to first time in time combo box list
        startTimeComboBox.getSelectionModel().selectFirst();
        endTimeComboBox.getSelectionModel().selectFirst();

        // Set default start/end dates to today's date
        startDatePicker.setValue(LocalDate.now());

    }

    private void initializeErrorLabels() {
        timeBoundsErrorLabel.setText("");
        apptOverlapErrorLabel.setText("");
        timeOutOfOrderLabel.setText("");
    }
}
