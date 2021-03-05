package view_controller;

import DAO.AppointmentDAO;
import DAO.CountryDAO;
import DAO.CustomerDAO;
import DAO.FirstLevelDivisionDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class UpdateCustomer implements Initializable {
    // Initialize the resource bundle which holds the Locale information
    ResourceBundle rb = ResourceBundle.getBundle("main/Nat", Locale.getDefault());

    // Create a user object to hold current app user data
    private User user;
    // Create allCustomers Observable List for filling the TableView
    private ObservableList<Customer> allCustomers;
    // Create allFLDs Observable list
    private ObservableList<FirstLevelDivision> allFLDs;
    // Create allCountries Observable list
    private ObservableList<Country> allCountries;
    // Create filtered country names list
    private ObservableList<String> countryNames = FXCollections.observableArrayList();
    // Created filtered First Level Division names list
    private ObservableList<String> filteredFLDs = FXCollections.observableArrayList();


    @FXML
    private TableView<Customer> customerListTable;

    @FXML
    private TableColumn<Customer, Integer> customerIDCol;

    @FXML
    private TableColumn<Customer, String> nameCol;

    @FXML
    private TableColumn<Customer, String> addressCol;

    @FXML
    private TableColumn<Customer, String> postalCodeCol;

    @FXML
    private TableColumn<Customer, String> phoneNumberCol;

    @FXML
    private TextField customerIDField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField postalCodeField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private ComboBox<String> firstLevelDivisionDropDown;

    @FXML
    private ComboBox<String> countryDropDown;

    @FXML
    private Label deleteAlertLabel;

    @FXML
    private Label deletedCustomerIDLabel;

    @FXML
    void selectCountryName(ActionEvent event) {
        // Clear the list of filtered first level divisions
        filteredFLDs.clear();
        firstLevelDivisionDropDown.getItems().clear();
        int selectedCountryID = 0;

        // Find the country ID of the selected country name
        for (Country country: allCountries) {
            if(country.getCountry().equalsIgnoreCase(countryDropDown.getValue())) {
                selectedCountryID = country.getCountryID();
            }
        }

        // For fld in allFLDs, if the first level division equals the selected country ID then add that division name
        // to the filteredFLDs list
        for (FirstLevelDivision fld: allFLDs) {
            if(fld.getCountryID() == selectedCountryID) {
                filteredFLDs.add(fld.getDivision());
            }
        }
        // Add the filteredFLDs list datat to the first level division drop down & set the default selection for the
        // drop down
        firstLevelDivisionDropDown.getItems().addAll(filteredFLDs);
        firstLevelDivisionDropDown.getSelectionModel().selectFirst();
    }

    @FXML
    void cancelUpdateCustomer(MouseEvent event) {
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
    void deleteSelectedCustomer(MouseEvent event) {
        Customer selectedCustomer = customerListTable.getSelectionModel().getSelectedItem();
        clearTextFields();

        // Delete all appointments related to the selected customers and then delete the selected customer
        try {
            AppointmentDAO.deleteAllAppointments(selectedCustomer.getCustomerID());
            CustomerDAO.deleteCustomer(selectedCustomer.getCustomerID());
        } catch (SQLException e) {
            System.out.println(e);
        }

        // Show delete alert labels
        deleteAlertLabel.setText(rb.getString("deleted_customer_alert"));
        deletedCustomerIDLabel.setText("Customer ID: " + selectedCustomer.getCustomerID());

    }

    @FXML
    void updateCustomer(MouseEvent event) {
        // Create customer data using data from customer fields
        int customerID = Integer.parseInt(customerIDField.getText());
        String customerName = nameField.getText();
        String customerAddress = addressField.getText();
        String customerCountry = countryDropDown.getValue();
        String customerPostalCode = postalCodeField.getText();
        String customerFirstLevelDivision = firstLevelDivisionDropDown.getValue();
        int customerFLD = 0;
        for (FirstLevelDivision fld: allFLDs) {
            if (customerFirstLevelDivision.equalsIgnoreCase(fld.getDivision())) {
                customerFLD = fld.getDivisionID();
            }
        }
        String customerPhoneNumber = phoneNumberField.getText();
        String lastUpdatedBy = user.getUserName();
        LocalDateTime emptyLDT = LocalDateTime.now(); // Empty date time to fill up customer object

        // Create customer object and call DAO function to update the selected customer
        try {
            Customer customer = new Customer(customerID, customerName, customerAddress, customerPostalCode, customerPhoneNumber, emptyLDT, "", emptyLDT, lastUpdatedBy, customerFLD);
            CustomerDAO.updateCustomer(customer);
        } catch (SQLException e) {
            System.out.println(e);
        }

        // Update the data holding arrays, clear the fields, and re-initialize the customer table
        try {
            initializeDataFields();
            clearTextFields();
            customerListTable.setItems(allCustomers);
            customerListTable.refresh();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public UpdateCustomer(User user) {
        this.user = user;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rb = resources;
        // Call the initialization functions for the data fields, the error labels, the data table, and the row event
        try {
            initializeDataFields();
        } catch (SQLException e) {
            System.out.println(e);
        }
        initializeErrorLabels();
        initializeCustomerTable();
        initializeRowEvent();

    }

    private void initializeDataFields() throws SQLException {
        // Use a try/catch to query data for the customer, country, and first level division tables. Then add the data
        // to its respective Observable List
        try {
            allCustomers = FXCollections.observableArrayList(CustomerDAO.getAllCustomers());
            allCountries = FXCollections.observableArrayList(CountryDAO.getAllCountries());
            allFLDs = FXCollections.observableArrayList(FirstLevelDivisionDAO.getAllFirstLevelDivisions());
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void initializeErrorLabels() {
        // Handle if a Customer is not selected from the TableView
        // Handle all fields must be complete "please complete all fields"
        deleteAlertLabel.setText("");
        deletedCustomerIDLabel.setText("");

    }

    private void initializeCustomerTable() {
        // Set the values for the Appointment Calendar TableView columns
        customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        // Setup first level division and county column
        TableColumn<Customer, Integer> countryCol = formatCountryColumn();
        TableColumn<Customer, Integer> firstLevelDivisionColumn = formatFirstLevelDivisionColumn();
        customerListTable.getColumns().addAll(firstLevelDivisionColumn, countryCol);

        postalCodeCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        phoneNumberCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        customerListTable.setItems(allCustomers);
        customerListTable.refresh();

    }

    /*
     * @param <T>
     * @return TableColumn
     */
    private <T> TableColumn<T, Integer> formatFirstLevelDivisionColumn(){
        // Create a TableColumn that accepts a generic type of object and a value of Integer
        TableColumn<T, Integer> firstLevelDivisionColumn = new TableColumn("First Level Division");
        // Set up a CellValueFactory that takes the "divisionID" property value from an FirstLevelDivision object
        firstLevelDivisionColumn.setCellValueFactory(new PropertyValueFactory<>("divisionID"));
        // Create a lambda expression to use as a parameter for the setCellFactory, using parameter column of type
        // TableColumn<> with a with an expression that creates a new TableCell instance with the updateItem() function
        // overridden
        firstLevelDivisionColumn.setCellFactory((TableColumn<T, Integer> column) -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty); // Java Documentation recommended this to prevent graphical issues
                if (!empty) {
                    // Determine which first level division id equals the item id, then set the text pattern to show
                    // both the id and the division name
                    for (FirstLevelDivision fld : allFLDs) {
                        if (item == fld.getDivisionID()) {
                            setText(item + " - " + fld.getDivision());
                        }
                    }
                }
            }
        });
        return firstLevelDivisionColumn;
    }

    /*
     * @param <T>
     * @return TableColumn
     */
    private <T> TableColumn<T, Integer> formatCountryColumn(){
        // Create a TableColumn that accepts a generic type of object
        TableColumn<T, Integer> countryCol = new TableColumn("Country");
        // Set up a CellValueFactory that takes the "divisionID" member from an FirstLevelDivision object
        countryCol.setCellValueFactory(new PropertyValueFactory<>("divisionID"));
        // Create a lambda expression to use as a parameter for the setCellFactory, using parameter column of type
        // TableColumn<> with a with an expression that creates a new TableCell instance with the updateItem() function
        // overridden
        countryCol.setCellFactory((TableColumn<T, Integer> column) -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty); // Java Documentation recommended this to prevent graphical issues
                if (!empty) {
                    // Determine which first level division id equals the item id, then set the text pattern to show
                    // both the id and the division name
                    for (FirstLevelDivision fld : allFLDs) {
                        if (item == fld.getDivisionID()) {
                            // For each country in all countries, take the chosen first level division and determine
                            // which country name its respective countryID equals. Then set the cell text to the
                            // country name.
                            for (Country country: allCountries) {
                                if (fld.getCountryID() == country.getCountryID()) {
                                    setText(country.getCountry());
                                }
                            }
                        }
                    }
                }
            }
        });
        return countryCol;
    }

    private void initializeRowEvent() {
        customerListTable.setRowFactory(customerTableView -> {
            TableRow<Customer> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                if(!row.isEmpty() && mouseEvent.getClickCount() == 1 && mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    Customer selectedRow = row.getItem();
                    clearTextFields();
                    fillTextFields(selectedRow);
                    initializeErrorLabels();
                }
            });
            return row;
        });
    }

    private void fillTextFields(Customer selectedCustomer) {
        // Update all Observable Lists with DB data
        try {
            initializeDataFields();
        } catch (SQLException e) {
            System.out.println(e);
        }

        // Set text fields and combo boxes
        customerIDField.setText(Integer.toString(selectedCustomer.getCustomerID()));
        nameField.setText(selectedCustomer.getCustomerName());
        for (Country country: allCountries) {
            countryNames.add(country.getCountry());
        }

        addressField.setText(selectedCustomer.getAddress());
        countryDropDown.getItems().addAll(countryNames);
        int selectedCountry = 0;
        for (FirstLevelDivision fld : allFLDs) {
            if (selectedCustomer.getDivisionID() == fld.getDivisionID()) {
                for(Country country: allCountries) {
                    if (fld.getCountryID() == country.getCountryID()) {
                        countryDropDown.setValue(country.getCountry());
                        selectedCountry = country.getCountryID();
                    }
                }
            }
        }
        phoneNumberField.setText(selectedCustomer.getPhoneNumber());

        for (FirstLevelDivision fld: allFLDs) {
            if(fld.getCountryID() == selectedCountry) {
                filteredFLDs.add(fld.getDivision());
            }
        }
        firstLevelDivisionDropDown.getItems().addAll(filteredFLDs);
        for (FirstLevelDivision fld: allFLDs) {
            if (fld.getDivisionID() == selectedCustomer.getDivisionID()) {
                firstLevelDivisionDropDown.setValue(fld.getDivision());
            }
        }
        postalCodeField.setText(selectedCustomer.getPostalCode());
    }

    private void clearTextFields() {
        // Clear all the editable/selectable fields
        nameField.clear();
        customerIDField.clear();
        postalCodeField.clear();
        addressField.clear();
        phoneNumberField.clear();
        firstLevelDivisionDropDown.getItems().clear();
        countryDropDown.getItems().clear();
        filteredFLDs.clear();
        countryNames.clear();


    }
}
