package view_controller;

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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Country;
import model.Customer;
import model.FirstLevelDivision;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 */
public class CreateCustomer implements Initializable {
    // Initialize the resource bundle which holds the Locale information
    ResourceBundle rb = ResourceBundle.getBundle("main/Nat", Locale.getDefault());

    // Create a user object
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
    private Label emptyFieldLabel;

    /**
     * @param event
     */
    @FXML
    void cancelCreateCustomer(MouseEvent event) {
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
    void createCustomer(MouseEvent event) {
        // Create a boolean to hold a flag for customer approval
        boolean approvedCustomer = true;
        // Create customer data using data from customer fields
        int customerID = 0;
        String customerName = nameField.getText();
        String customerAddress = addressField.getText();
        String customerCountry = countryDropDown.getValue();
        String customerPostalCode = postalCodeField.getText();
        String customerFirstLevelDivision = firstLevelDivisionDropDown.getValue();
        int customerFLD = 0;
        // Get the ID of the selected first level division from the allFLDs list
        for (FirstLevelDivision fld: allFLDs) {
            if (customerFirstLevelDivision.equalsIgnoreCase(fld.getDivision())) {
                customerFLD = fld.getDivisionID();
            }
        }
        String customerPhoneNumber = phoneNumberField.getText();
        String lastUpdatedBy = user.getUserName();
        LocalDateTime emptyLDT = LocalDateTime.now(); // Empty date time to fill up customer object

        // Determine if a text field is empty, and if it is, set the error label to the appropriate text and
        // declare that the customer is not approved
        if (nameField.getText().trim().isEmpty()) {
            emptyFieldLabel.setText("The customer name text field must not be empty.");
            approvedCustomer = false;
        } else if (addressField.getText().trim().isEmpty()) {
            emptyFieldLabel.setText("The address text field must not be empty.");
            approvedCustomer = false;
        } else if (postalCodeField.getText().trim().isEmpty()) {
            emptyFieldLabel.setText("The postal code text field must not be empty.");
            approvedCustomer = false;
        } else if (phoneNumberField.getText().trim().isEmpty()) {
            emptyFieldLabel.setText("The phone number text field must not be empty.");
            approvedCustomer = false;
        }

        if (approvedCustomer) { // No errors detected in customer data
            // Create customer object and call DAO function to update the selected customer
            try {
                Customer customer = new Customer(customerID, customerName, customerAddress, customerPostalCode, customerPhoneNumber, emptyLDT, "", emptyLDT, lastUpdatedBy, customerFLD);
                CustomerDAO.createCustomer(customer);
            } catch (SQLException e) {
                System.out.println(e);
            }

            // Update the data holding arrays, clear the fields, and re-initialize the customer table
            initializeDataFields();
            fillTextFields();
            customerListTable.setItems(allCustomers);
            customerListTable.refresh();

        }

    }

    /**
     * @param event
     */
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

        // For each first level divison, if the fld's countryID matches the selected country then add the FLD to the
        // list of filtered FLD's
        for (FirstLevelDivision fld: allFLDs) {
            if(fld.getCountryID() == selectedCountryID) {
                filteredFLDs.add(fld.getDivision());
            }
        }
        // Add the list of filtered to its respective drop down
        firstLevelDivisionDropDown.getItems().addAll(filteredFLDs);
        // Select the first available index for the first level division drop down
        firstLevelDivisionDropDown.getSelectionModel().selectFirst();


    }

    /**
     * @param user
     */
    public CreateCustomer(User user) {
        this.user = user;
    }

    /**
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rb = resources;
        // Fire the initializing functions on the ViewController initiation
        initializeDataFields();
        initializeErrorLabels();
        initializeCustomerTable();
        fillTextFields();

    }

    /**
     *
     */
    private void initializeDataFields() {
        try {
            allCustomers = FXCollections.observableArrayList(CustomerDAO.getAllCustomers());
            allCountries = FXCollections.observableArrayList(CountryDAO.getAllCountries());
            allFLDs = FXCollections.observableArrayList(FirstLevelDivisionDAO.getAllFirstLevelDivisions());
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     *
     */
    private void initializeErrorLabels() {

    }

    /**
     *
     */
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

        // Set the list of allCustomers to each row in the customer list table
        customerListTable.setItems(allCustomers);
        customerListTable.refresh();
    }

    /**
     *
     */
    private void fillTextFields() {
        // Clear all the fields so they can be re-filled
        clearTextFields();

        // Update all Observable Lists with DB data
        initializeDataFields();

        // Create an all country names list
        for (Country country: allCountries) {
            countryNames.add(country.getCountry());
        }

        // Fill countryDropDown with country data
        countryDropDown.getItems().addAll(countryNames);
        // Set default country to USA
        countryDropDown.getSelectionModel().select(0);
        // ID connected to country of USA
        int countryUsaID = 1;

        // Fill list of first level divisions that match the default USA country ID
        for (FirstLevelDivision fld: allFLDs) {
            if(fld.getCountryID() == countryUsaID) {
                filteredFLDs.add(fld.getDivision());
            }
        }
        // Add list of filtered first level divisions to the firstLevelDivision drop down
        firstLevelDivisionDropDown.getItems().addAll(filteredFLDs);
        // Set default selected drop down item as the first index
        firstLevelDivisionDropDown.getSelectionModel().select(0);
    }

    /**
     * @param <T>
     * @return TableColumn<T, Integer>
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

    /**
     * @param <T>
     * @return TableColumn<T, Integer>
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

    /**
     *
     */
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
