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
import javafx.fxml.LoadException;
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

/**
 *
 */
public class UpdateAppointment implements Initializable {

    // Initialize the resource bundle which holds the Locale information
    ResourceBundle rb = ResourceBundle.getBundle("main/Nat", Locale.getDefault());

    // Get the Time Zone ID of the users local time zone
    ZoneId localTimeZoneID = ZoneId.systemDefault();

    // Create local appointment object
    Appointment selectedAppointment;
    // Create allAppointments Observable List for filling the combo boxes
    private final ObservableList<Appointment> allAppointments = FXCollections.observableArrayList(AppointmentDAO.getAllAppointments());
    // Create allContacts Observable List for filling the contact name combo box
    private final ObservableList<Customer> allCustomers = FXCollections.observableArrayList(CustomerDAO.getAllCustomers());
    // Create allContacts Observable List for filling the contact name combo box
    private final ObservableList<Contact> allContacts = FXCollections.observableArrayList(ContactDAO.getAllContacts());
    // Create allContacts Observable List for filling the contact name combo box
    private final ObservableList<User> allUsers = FXCollections.observableArrayList(UserDAO.getAllUsers());
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

    /**
     * @param event
     */
    @FXML
    void clickDeleteAppointment(MouseEvent event) {
        try{
            // Send selectedAppointment ID to the delete query in AppointmentDAO
            AppointmentDAO.deleteAppointment(selectedAppointment.getAppointmentID());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainCalendar.fxml"), rb);
            MainCalendar controller = new MainCalendar(user, selectedAppointment);
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (IOException | SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * @param event
     */
    @FXML
    void cancelUpdateAppointment(MouseEvent event) {
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
     * @param event
     */
    @FXML
    void updateAppointment(MouseEvent event) {
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
            if (appt.getCustomerID() == customerID && appt.getAppointmentID() != selectedAppointment.getAppointmentID()) {
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

        // Determine if a text field is empty, and if it is, set the error label to the appropriate text and
        // declare that the appointment is not approved
        if (titleField.getText().trim().isEmpty()) {
            emptyFieldLabel.setText("The title text field must not be empty.");
            approvedAppointment = false;
        } else if (descriptionField.getText().trim().isEmpty()) {
            emptyFieldLabel.setText("The description text field must not be empty.");
            approvedAppointment = false;
        } else if (locationField.getText().trim().isEmpty()) {
            emptyFieldLabel.setText("The location text field must not be empty.");
            approvedAppointment = false;
        } else if (typeField.getText().trim().isEmpty()) {
            emptyFieldLabel.setText("The type text field must not be empty.");
            approvedAppointment = false;
        }


        if (approvedAppointment) { // No errors
            try{
                // Convert from UTC to computers default locale (CANNOT BE HARD CODED, CHANGE THIS)
                LocalDateTime convertedstartDT = startDT.atZone(ZoneOffset.UTC).withZoneSameInstant(localTimeZoneID).toLocalDateTime();
                LocalDateTime convertedEndDT = endDT.atZone(ZoneOffset.UTC).withZoneSameInstant(localTimeZoneID).toLocalDateTime();


                // Use setters to update the data in the selectedAppointment
                selectedAppointment.setTitle(title);
                selectedAppointment.setDescription(desc);
                selectedAppointment.setLocation(loc);
                selectedAppointment.setType(type);
                selectedAppointment.setStart(convertedstartDT);
                selectedAppointment.setEnd(convertedEndDT);
                selectedAppointment.setLastUpdatedBy(lastUpdatedBy);
                selectedAppointment.setCustomerID(customerID);
                selectedAppointment.setUserID(userID);
                selectedAppointment.setContactID(contactID);

                // Execute the prepared statement using the selectedAppointment Appointment object
                AppointmentDAO.updateAppointment(selectedAppointment);

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

    }

    /**
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rb = resources;
        initializeDataFields();
        initializeErrorLabels();

    }

    /**
     * UpdateAppointment is the constructor method for the UpdateAppointment class. It takes in a user and appointment
     * object, which hold data about the user currently logged in to this application, and the appointment selected
     * from the MainCalendar scene.
     *
     * @param appointment
     * @param user
     * @throws SQLException
     */
    public UpdateAppointment (Appointment appointment, User user) throws SQLException {
        this.user = user;
        this.selectedAppointment = appointment;
    }

    /**
     *
     */
    private void initializeDataFields() {
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
            if (selectedAppointment.getContactID() == contact.getContactID()) {
                contactNameDropDown.getSelectionModel().select(contact.getContactName());
            }
        }

        // Fill the combo boxes for user selection
        customerIDDropDown.getItems().addAll(customerIDs);
        contactNameDropDown.getItems().addAll(contactNames);
        userIDDropDown.getItems().addAll(userIDs);

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

        // Convert LocalDateTime objects time to local time zone
        LocalDateTime apptStartDateTime = selectedAppointment.getStart().atZone(ZoneOffset.UTC).withZoneSameInstant(localTimeZoneID).toLocalDateTime();
        LocalDateTime apptEndDateTime = selectedAppointment.getEnd().atZone(ZoneOffset.UTC).withZoneSameInstant(localTimeZoneID).toLocalDateTime();

        // Convert LocalDateTime appointment objects to LocalTime
        LocalTime apptStartTime = apptStartDateTime.toLocalTime();
        LocalTime apptEndTime = apptEndDateTime.toLocalTime();
        // Convert LocalDateTime appointment objects to LocalDate
        LocalDate apptStartDate = apptStartDateTime.toLocalDate();

        // Fill the text boxes with selectedAppointment information
        appointmentIDField.setText(Integer.toString(selectedAppointment.getAppointmentID()));
        titleField.setText(selectedAppointment.getTitle());
        descriptionField.setText(selectedAppointment.getDescription());
        locationField.setText(selectedAppointment.getLocation());
        typeField.setText(selectedAppointment.getType());
        startDatePicker.setValue(apptStartDate);
        startTimeComboBox.getSelectionModel().select(apptStartTime);
        endTimeComboBox.getSelectionModel().select(apptEndTime);

        customerIDDropDown.getSelectionModel().select(selectedAppointment.getCustomerID());
        customerIDDropDown.setValue(selectedAppointment.getCustomerID()); // To make sure the value is set
        userIDDropDown.getSelectionModel().select(selectedAppointment.getUserID());
        userIDDropDown.setValue(selectedAppointment.getUserID());

    }

    /**
     * This method clears all data current existing in the error labels on the UpdateAppointment jfx scene.
     */
    private void initializeErrorLabels() {
        timeBoundsErrorLabel.setText("");
        apptOverlapErrorLabel.setText("");
        timeOutOfOrderLabel.setText("");
    }
}
