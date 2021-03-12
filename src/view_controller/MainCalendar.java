package view_controller;

import DAO.AppointmentDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Appointment;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;

/**
 * MainCalendar is a ViewController that holds jfx object & functions that display a calendar with appointment details,
 * filter options for the calendar, error labels, as well as buttons to route the user to creating & modifying
 * appointments and customers plus routing to the reports scene.
 */
public class MainCalendar implements Initializable {

    // The Observable List that holds all the appointments
    private final ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    // Observable List to hold the filtered appointments to be shown in the TableView
    private final ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();
    // Get the Time Zone ID of the users local time zone
    ZoneId localTimeZoneID = ZoneId.systemDefault();

    // Create user object to pass through ViewControllers
    private User appUser;

    // Create appointment object to hold deleted appointment data
    private Appointment deletedAppointment = null;

    // Initialize the resource bundle which holds the Locale information
    private ResourceBundle rb;

    @FXML
    private Label appointmentAlertTextField;

    @FXML
    private Label apptNotSelectedLabel;

    @FXML
    private TableView<Appointment> appointmentCalendarTable;

    @FXML
    private TableColumn<Appointment, Integer> appointmentIDCol;

    @FXML
    private TableColumn<Appointment, String> titleCol;

    @FXML
    private TableColumn<Appointment, String> descriptionCol;

    @FXML
    private TableColumn<Appointment, String> locationCol;

    @FXML
    private TableColumn<Appointment, String> contactCol;

    @FXML
    private TableColumn<Appointment, String> typeCol;

    @FXML
    private TableColumn<Appointment, Integer> customerIDCol;

    @FXML
    private RadioButton weeklyViewButton;

    @FXML
    private RadioButton monthlyViewButton;

    @FXML
    private DatePicker appointmentCalendar;

    @FXML
    private Label appointmentDeletedLabel;

    /**
     * This method is a constructor for the MainCalendar ViewController. It takes in a user object and a appointment
     * object which is assigned to the appUser and deletedAppointment local variables.
     *
     * @param user
     * @param appt
     * @throws SQLException
     */
    public MainCalendar(User user, Appointment appt) throws SQLException {
        this.appUser = user;
        this.deletedAppointment = appt;
    }

    /**
     * The clickCreateAppointment method holds functionality which sends the application user to the Create Appointment
     * scene. The controller object is given the appUser object as an argument to keep the current user data up-to-date
     * around the application.
     *
     * @param event
     */
    @FXML
    void clickCreateAppointment(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateAppointment.fxml"), rb);
            CreateAppointment controller = new CreateAppointment(appUser);
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * The clickCreateCustomer method holds functionality which sends the application user to the Create Customer
     * scene. The controller object is given the appUser object as an argument to keep the current user data up-to-date
     * around the application.
     *
     * @param event
     */
    @FXML
    void clickCreateCustomer(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateCustomer.fxml"), rb);
            CreateCustomer controller = new CreateCustomer(appUser);
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * The clickExitSystem method essentially logs the user out and sends them to the login page. All appointments are
     * cleared from the allAppointments Observable List.
     *
     * @param event
     * @throws SQLException
     */
    @FXML
    void clickExitSystem(MouseEvent event) throws SQLException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view_controller/Login.fxml"), rb);
            view_controller.Login controller = new view_controller.Login();
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            // Clear all appointments gathered from the SQL statement
            allAppointments.clear();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * The clickMonthlyView radio button is used to filter the TableView so it displays all appointments for the
     * currently selected month in the DatePicker. It un-selects the weekly radio button, adds the filtered appointments
     * to the TableView, and refreshed the TableView.
     *
     * @param event
     */
    @FXML
    void clickMonthlyView(MouseEvent event) {
        // Unset the weekly view radio button and keep monthly view set
        weeklyViewButton.setSelected(false);
        monthlyViewButton.setSelected(true);
        // Empty the appointments List so new filtered appointments can be added
        filteredAppointments.clear();
        // Clear the rows of the Appointment Calendar TableView
        appointmentCalendarTable.getItems().clear();
        LocalDate selectedDate = appointmentCalendar.getValue();
        // For appointments in the selected month, held within all appointments
            // Add appointment to filteredAppointments
        for(Appointment appointment: allAppointments) {
            // Add appointment to filteredAppointments
            if (appointment.getStart().toLocalDate().getMonthValue() == selectedDate.getMonthValue()) {
                System.out.println(appointment.getStart().atZone(ZoneOffset.UTC).withZoneSameInstant(localTimeZoneID).toLocalDateTime());
                filteredAppointments.add(appointment);

            }
        }
        // Repopulate the Appointment calendar table cells with up-to-date data
        appointmentCalendarTable.refresh();
    }

    /**
     * The clickWeeklyView radio button is used to filter the TableView so it displays all appointments for the
     * currently selected week in the DatePicker. It un-selects the monthly radio button, adds the filtered appointments
     * to the TableView, and refreshed the TableView.
     *
     * @param event
     */
    @FXML
    void clickWeeklyView(MouseEvent event) {
        // Unset the monthly view radio button and keep weekly view set
        monthlyViewButton.setSelected(false);
        weeklyViewButton.setSelected(true);
        // Empty the appointments List so new filtered appointments can be added
        filteredAppointments.clear();
        // Clear the rows of the Appointment Calendar TableView
        appointmentCalendarTable.getItems().clear();
        LocalDate selectedDate = appointmentCalendar.getValue();
        // For appointments in the selected month, held within all appointments
        // Add appointment to filteredAppointments
        for(Appointment appointment: allAppointments) {
            // Add appointment to filteredAppointments
            if (appointment.getStart().toLocalDate().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()) == selectedDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())) {
                System.out.println(appointment.getStart().atZone(ZoneOffset.UTC).withZoneSameInstant(localTimeZoneID).toLocalDateTime());
                filteredAppointments.add(appointment);
            }
        }
        // Repopulate the Appointment calendar table cells with up-to-date data
        appointmentCalendarTable.refresh();

    }

    /**
     * The clickUpdateAppointment method send the user to the UpdateAppointment scene. It first grabs the
     * selected appointment from the TableView, determines if an appointment has/has not been selected, and if one has--
     * it loads up the UpdateAppointment scene with the selected appointment and user object passed in as arguments.
     *
     * @param event
     */
    @FXML
    void clickUpdateAppointment(MouseEvent event) {
        try {
            // Get selected appointment from TableView
            Appointment selectedAppointment = appointmentCalendarTable.getSelectionModel().getSelectedItem();

            // If an appointment is not selected...
            if (selectedAppointment == null) {
                apptNotSelectedLabel.setText("You must selected an appointment from the table below.");

            } else { // An appointment is selected
                // Load the UpdateAppointment ViewController
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view_controller/UpdateAppointment.fxml"), rb);
                UpdateAppointment controller = new UpdateAppointment(selectedAppointment, appUser);
                loader.setController(controller);
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();

                // Clear all appointments gathered from the SQL statement
                allAppointments.clear();
            }
        } catch (IOException | SQLException e ) {
            System.out.println(e);
        }
    }

    /**
     * The clickUpdateCustomer method is used to move the user from the MainCalendar scene to the UpdateCustomer
     * scene. The UpdateCustomer controller takes a user object to move around the application for user data, and
     * then displays the scene.
     *
     * @param event
     */
    @FXML
    void clickUpdateCustomer(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateCustomer.fxml"), rb);
            UpdateCustomer controller = new UpdateCustomer(appUser);
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * The clickReports method is used to move the user from the MainCalendar scene to the Reports scene. It passes in a
     * user object to the Reports controller and then loads and displays the Reports scene.
     *
     * @param event
     */
    @FXML
    void clickReports(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Reports.fxml"), rb);
            Reports controller = new Reports(appUser);
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            System.out.println(e);
        }
    }


    /**
     * The initialize method resets the lists used to hold appointments, fills the appointment list with all
     * appointments in the database, sets an action event on the DatePicker to determine how to filter the TableView,
     * clears the jfx labels, initializes the default calender settings, then generates the initial calendar with
     * information held in the tableview (with this month selected). Finally if the appointment is not null in the
     * constructor, that means an appointment was cancelled and it displays that on the MainCalendar scene.
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rb = resources;

        // Clear the Observable Lists
        allAppointments.clear();
        filteredAppointments.clear();

        // Query the DB for appointment data and store it in a ObservableList
        try {
            allAppointments.addAll(AppointmentDAO.getAllAppointments());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        /**
         * Below, a lambda is used to set a block of code on an event object. First an setOnAction function is
         * applied to a DatePicker object. An event is taken in as a argument, and how the event is handled is done
         * by using a lambda on the event object to return the code block, which updates the TableView with either
         * data from a month or week. The lambda is effective because it eliminates the need to create an anonymous
         * class an override a function.
         */
        // Add an Event Handler on the appointmentCalendar so it updates the TableView when a day node is clicked
        appointmentCalendar.setOnAction(event -> {
            // If the weekly view radio button is selected...
            if(weeklyViewButton.isSelected()) {
                // Empty the appointments List so new filtered appointments can be added
                filteredAppointments.clear();
                // Clear the rows of the Appointment Calendar TableView
                appointmentCalendarTable.getItems().clear();
                LocalDate selectedDate = appointmentCalendar.getValue();
                // For appointments in the selected month, held within all appointments
                // Add appointment to filteredAppointments
                for(Appointment appointment: allAppointments) {
                    // Add appointment to filteredAppointments
                    if (appointment.getStart().toLocalDate().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()) == selectedDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())) {
                        System.out.println(appointment.getStart().atZone(ZoneOffset.UTC).withZoneSameInstant(localTimeZoneID).toLocalDateTime());
                        filteredAppointments.add(appointment);
                    }
                }
                // appointmentCalendarTable.setItems(filteredAppointments);****************
                appointmentCalendarTable.refresh();
            } else {
                // Empty the appointments List so new filtered appointments can be added
                filteredAppointments.clear();
                // Clear the rows of the Appointment Calendar TableView
                appointmentCalendarTable.getItems().clear();
                LocalDate selectedDate = appointmentCalendar.getValue();
                // For appointments in the selected month, held within all appointments
                // Add appointment to filteredAppointments
                for(Appointment appointment: allAppointments) {
                    // Add appointment to filteredAppointments
                    if (appointment.getStart().toLocalDate().getMonthValue() == selectedDate.getMonthValue()) {
                        System.out.println(appointment.getStart().atZone(ZoneOffset.UTC).withZoneSameInstant(localTimeZoneID).toLocalDateTime());
                        filteredAppointments.add(appointment); // For testing purposes ??

                    }
                }
                // appointmentCalendarTable.setItems(filteredAppointments); ****************
                appointmentCalendarTable.refresh();
            }
        });


        // Initialize language labels and alerts
        setLanguageForLabelsAndAlerts();

        // Initialize settings and ObservableList for the default Calendar
        initializeDefaultCalendarSettings();
        // Try to generate the data for the Appointment Calendar
        try {
            generateAppointmentCalendar();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // If appointment in constructor is not null... set label text to Appt ID and Appt Type Cancelled
        if (deletedAppointment != null) {
            appointmentDeletedLabel.setText("*Appointment Cancelled* || Appointment ID: " +
                    deletedAppointment.getAppointmentID() + " || Appointment Type: " + deletedAppointment.getType() +
                    " ||");
        }

    }

    /**
     * The generateAppointmentCalendar method sets the cellValueFactory on the pre-generated columns with properties
     * from an appointment object (within the filteredAppointments observable list), then creates uses the
     * formatStartDateTime and formatEndDateTime functions to create their respective TableColumns, then it adds them
     * to the TableView. Finally it sets all items on the TableView using an observable list and updates the data shown
     * on the TableView.
     *
     * @throws SQLException
     */
    private void generateAppointmentCalendar() throws SQLException {
        // Set the values for the Appointment Calendar TableView columns
        appointmentIDCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        // Setup start and end date/time columns
        TableColumn<Appointment, LocalDateTime> startDateTimeCol = formatStartDateTime();
        TableColumn<Appointment, LocalDateTime> endDateTimeCol = formatEndDateTime();
        appointmentCalendarTable.getColumns().addAll(startDateTimeCol, endDateTimeCol);

        customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));

        appointmentCalendarTable.setItems(filteredAppointments);
        appointmentCalendarTable.refresh();
    }

    /**
     * The formatStartDateTime function creates a column specifically for the Calendar TableView. It formats the row in
     * the Start Date/Time TableColumn to display the time/date to the local time zone, as well as formats its
     * appearance in a readable format.
     * @param <T>
     * @return TableColumn<T, LocalDateTime>
     */
    private <T> TableColumn<T, LocalDateTime> formatStartDateTime(){
        // Create a TableColumn that accepts a generic type of object
        TableColumn<T, LocalDateTime> startDateTimeCol = new TableColumn("Start Date & Time");
        // Set up a CellValueFactory that takes the "start" member from an Appointment object
        startDateTimeCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        /**
         * Below a lambda is used to easily format a TableCell within a TableColumn and pass that formatted TableColumn
         * to the setCellFactory on the startDateTimeCol. A lambda is useful here because removes the need to create an
         * anonymous class or a whole new TableColumn object outside of the setCellFactory argument. It is also
         * arguably more readable because it removes code that doesn't help explain what this application does.
         */
        // Reformat column cells as new date/time pattern & local time zone
        startDateTimeCol.setCellFactory((TableColumn<T, LocalDateTime> column) -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty); // Java Documentation recommended this to prevent graphical issues
                if (!empty) {
                    // Alter the UTC time zone from DB to Local Time Zone
                    LocalDateTime newItem = item.atZone(ZoneOffset.UTC).withZoneSameInstant(localTimeZoneID).toLocalDateTime();
                    // Format the Date & Time to be displayed in the TableView
                    setText(String.format(newItem.format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"))));
                }
            }
        });
        return startDateTimeCol;
    }

    /**
     * The formatEndDateTime function creates a column specifically for the Calendar TableView. It formats the row in
     * the End Date/Time TableColumn to display the time/date to the local time zone, as well as formats its
     * appearance in a readable format.
     *
     * @param <T>
     * @return TableColumn<T, LocalDateTime>
     */
    private <T> TableColumn<T, LocalDateTime> formatEndDateTime(){
        TableColumn<T, LocalDateTime> endDateTimeCol = new TableColumn("End Date & Time");
        endDateTimeCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        /**
         * Below a lambda is used to easily format a TableCell within a TableColumn and pass that formatted TableColumn
         * to the setCellFactory on the startDateTimeCol. A lambda is useful here because removes the need to create an
         * anonymous class or a whole new TableColumn object outside of the setCellFactory argument. It is also
         * arguably more readable because it removes code that doesn't help explain what this application does.
         */
        // Reformat column cells as new date/time pattern & local time zone
        endDateTimeCol.setCellFactory((TableColumn<T, LocalDateTime> column) -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty); // Java Documentation recommended this to prevent graphical issues
                if (!empty) {
                    // Alter the UTC time zone from DB to Local Time Zone
                    LocalDateTime newItem = item.atZone(ZoneOffset.UTC).withZoneSameInstant(localTimeZoneID).toLocalDateTime();
                    // Format the Date & Time to be displayed in the TableView
                    setText(String.format(newItem.format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"))));
                }
            }
        });
        return endDateTimeCol;
    }

    /**
     * This method initializes the default calendar settings. It sets the appointment alert text to its default text,
     * sets the DatePicker to the current month, sets the monthly radio button to selected, and finally checks the
     * list of appointments to see if any are within 15 minutes of the current time. If one is found, it displays it on
     * the MainCalendar scene.
     */
    public void initializeDefaultCalendarSettings() {
        // Default near appointment alert settings
        appointmentAlertTextField.setText("None detected");
        // Get the current time and put it into a LocalDateTime object
        LocalDateTime currentDateTime = LocalDateTime.now(ZoneOffset.UTC);
        // Get the current month in integer format
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        // Set DatePicker to current month
        appointmentCalendar.setValue(LocalDate.now());
        // Set radio label to "monthly view"
        monthlyViewButton.setSelected(true);
        // For appointments in the current month, held within all appointments
        for(Appointment appointment: allAppointments) {
            // Add appointment to filteredAppointments
            if (appointment.getStart().toLocalDate().getMonthValue() == currentMonth) {
                filteredAppointments.add(appointment);
                // If appointment is within 15 minutes from now...
                if (currentDateTime.isEqual(appointment.getStart()) || (appointment.getStart().isAfter(currentDateTime) && appointment.getStart().isBefore(currentDateTime.plusMinutes(15))) ||
                        currentDateTime.isEqual(appointment.getStart().plusMinutes(15))) {
                    // Add data to alertLabel for user to see
                    // Alter code above to make sure it doesn't go off for appointments before the current time
                    LocalDateTime upcomingAppointment = appointment.getStart().atZone(ZoneOffset.UTC).withZoneSameInstant(localTimeZoneID).toLocalDateTime();
                    appointmentAlertTextField.setText("Appointment starts soon:" + System.lineSeparator() +
                            "Appointment ID: " + appointment.getAppointmentID() + System.lineSeparator() +
                            "Appointment Date & Time: " + upcomingAppointment.format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")));
                }

            }
        }
    }

    /**
     * This method clears all data current existing in the error labels on the MainCalendar jfx scene.
     */
    public void setLanguageForLabelsAndAlerts() {
        apptNotSelectedLabel.setText("");
        appointmentDeletedLabel.setText("");

    }

}
