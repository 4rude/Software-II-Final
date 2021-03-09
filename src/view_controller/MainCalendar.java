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

public class MainCalendar implements Initializable {

    // The Observable List that holds all the appointments
    private final ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    // Observable List to hold the filtered appointments to be shown in the TableView
    private final ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();
    // Get the Time Zone ID of the users local time zone
    ZoneId localTimeZoneID = ZoneId.systemDefault();

    // Create user object to pass through ViewControllers
    private User appUser;

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

    public MainCalendar(User user) throws SQLException {
        this.appUser = user;
    }

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

        } catch (IOException | SQLException e) {
            System.out.println(e);
        }
    }

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

    @FXML
    void clickMonthlyView(MouseEvent event) throws SQLException {
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

    @FXML
    void clickWeeklyView(MouseEvent event) throws SQLException {
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

        // Add an Event Handler on the appointmentCalendar so it updates the TableView when a day node is clicked
        appointmentCalendar.setOnAction(event-> {
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


    }

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

    /*
    * @param <T>
     * @return TableColumn
     */
    private <T> TableColumn<T, LocalDateTime> formatStartDateTime(){
        // Create a TableColumn that accepts a generic type of object
        TableColumn<T, LocalDateTime> startDateTimeCol = new TableColumn("Start Date & Time");
        // Set up a CellValueFactory that takes the "start" member from an Appointment object
        startDateTimeCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        // Reformat column cells as new date/time pattern & local time zone
        startDateTimeCol.setCellFactory((TableColumn<T, LocalDateTime> column) -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                if (!empty) {
                    // Alter the UTC time zone from DB to Local Time Zone
                    LocalDateTime newItem = item.atZone(ZoneOffset.UTC).withZoneSameInstant(localTimeZoneID).toLocalDateTime();
                    // Format the Date & Time to be displayed in the TableView
                    setText(String.format(newItem.format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"))));
                } else if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                }
            }
        });
        return startDateTimeCol;
    }

    /*
     * @param <T>
     * @return TableColumn
     */
    private <T> TableColumn<T, LocalDateTime> formatEndDateTime(){
        TableColumn<T, LocalDateTime> endDateTimeCol = new TableColumn("End Date & Time");
        endDateTimeCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        // Reformat column cells as new date/time pattern & local time zone
        endDateTimeCol.setCellFactory((TableColumn<T, LocalDateTime> column) -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                if (!empty) {
                    // Alter the UTC time zone from DB to Local Time Zone
                    LocalDateTime newItem = item.atZone(ZoneOffset.UTC).withZoneSameInstant(localTimeZoneID).toLocalDateTime();
                    // Format the Date & Time to be displayed in the TableView
                    setText(String.format(newItem.format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"))));
                } else if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                }
            }
        });
        return endDateTimeCol;
    }

    public void initializeDefaultCalendarSettings() { // Issue is found here??
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
            //System.out.println("All appointment start date and time: " + appointment.getStart());
            // Add appointment to filteredAppointments
            if (appointment.getStart().toLocalDate().getMonthValue() == currentMonth) {
                //System.out.println(appointment.getStart().atZone(ZoneOffset.UTC).withZoneSameInstant(localTimeZoneID).toLocalDateTime());
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

    public void setLanguageForLabelsAndAlerts() {
        apptNotSelectedLabel.setText("");
    }

}
