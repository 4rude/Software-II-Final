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
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The UpdateCustomer is a ViewController that holds the jfx objects that create the UI for updating a customer,
 * specifically the labels, text fields, buttons, and a TableView. This ViewController holds the functionality for
 * loading a TableView with data, adding data from a selected customer to the TextFields, updating a customer in the
 * database, and deleting a customer from the database.
 */
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
    private Label emptyFieldLabel;

    /**
     * This method provides a definition for an Action Event, which determines what happens when a country is selected
     * from the drop down. When a country is selected, the first level division drop down is updated with its
     * related states/providences/etc.
     *
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

        // For fld in allFLDs, if the first level division equals the selected country ID then add that division name
        // to the filteredFLDs list
        for (FirstLevelDivision fld: allFLDs) {
            if(fld.getCountryID() == selectedCountryID) {
                filteredFLDs.add(fld.getDivision());
            }
        }
        // Add the filteredFLDs list data to the first level division drop down & set the default selection for the
        // drop down
        firstLevelDivisionDropDown.getItems().addAll(filteredFLDs);
        firstLevelDivisionDropDown.getSelectionModel().selectFirst();
    }

    /**
     * The cancelUpdateCustomer method moves the user from the UpdateCustomer scene to the main calendar scene.
     *
     * @param event
     */
    @FXML
    void cancelUpdateCustomer(MouseEvent event) {
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
     * The deleteSelectedCustomer method gets the currently selected customer from the TableView and clears the text
     * fields. If a customer is indeed selected, the customer is removed from the database and the label on the scene
     * is updated with its ID. The TableView is then updated with the current data.
     * @param event
     */
    @FXML
    void deleteSelectedCustomer(MouseEvent event) {
        Customer selectedCustomer = customerListTable.getSelectionModel().getSelectedItem();
        clearTextFields();

        // If no customer is selected from the table...
        if (selectedCustomer == null) {
            deleteAlertLabel.setText("You must selected a customer to be deleted from the table.");
        } else { // A customer is selected...
            // Delete all appointments related to the selected customers and then delete the selected customer
            try {
                AppointmentDAO.deleteAllAppointments(selectedCustomer.getCustomerID());
                CustomerDAO.deleteCustomer(selectedCustomer.getCustomerID());
                allCustomers = FXCollections.observableArrayList(CustomerDAO.getAllCustomers());
            } catch (SQLException e) {
                System.out.println(e);
            }

            // Show delete alert labels
            deleteAlertLabel.setText("The following customer was deleted");
            deletedCustomerIDLabel.setText("Customer ID: " + selectedCustomer.getCustomerID());

            // Repopulate the customer table with up to date information
            customerListTable.setItems(allCustomers);
            customerListTable.refresh();
        }
    }

    /**
     * The updateCustomer method is used to update a selected customer with new data and put that in the database. If a
     * customer is selected, the text fields with presumably new data is loaded into a customer object. That object data
     * is then set to the database to update it. Finally the TableView is refreshed.
     *
     * @param event
     */
    @FXML
    void updateCustomer(MouseEvent event) {
        // Get the selected customer to be updated
        Customer selectedCustomer = customerListTable.getSelectionModel().getSelectedItem();

        // If no customer is selected...
        if (selectedCustomer == null) {
            emptyFieldLabel.setText("You must selected a customer from the customer table.");
        } else { // If a customer is selected...
            // Create a boolean to hold a flag for customer approval
            boolean approvedCustomer = true;
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


            if (approvedCustomer) { // No errors detected in data
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
        }
    }

    /**
     * This is the contructor for the UpdateCustomer class. On creation it takes in a user class so user data can be
     * passed to different ViewControllers within this application.
     *
     * @param user
     */
    public UpdateCustomer(User user) {
        this.user = user;
    }

    /**
     * The initialize function is overridden so the local ObservableLists are loaded with data, the labels are
     * initialized, the TableView is loaded with Customer data, and finally the Row Event is initialized so when
     * a row is clicked the data show up in the text fields.
     *
     * @param location
     * @param resources
     */
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

    /**
     * This function initializes the local Observable lists with data from their respective database tables.
     *
     * @throws SQLException
     */
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

    /**
     * This method sets all the error labels on the UpdateCustomer view to clear.
     */
    private void initializeErrorLabels() {
        // Handle if a Customer is not selected from the TableView
        // Handle all fields must be complete "please complete all fields"
        deleteAlertLabel.setText("");
        deletedCustomerIDLabel.setText("");
        emptyFieldLabel.setText("");

    }

    /**
     * The initializeCustomerTable method sets the cellValueFactory on the pre-generated columns with properties
     * from an customer object (within the allCustomers observable list), then creates uses the
     * formatCountryColumn and formatFirstLevelDivisionColumn functions to create their respective TableColumns, then
     * it adds them to the TableView. Finally it sets all items on the TableView using an observable list and updates
     * the data shown on the TableView.
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

        customerListTable.setItems(allCustomers);
        customerListTable.refresh();

    }

    /**
     * This method returns a TableColumn that holds first level division data connected to a customer. The TableColumn
     * is created and its name is assigned and a Property Value Factory is then set on the column to assign it data.
     * The setCellFactory is applied to the TableColumn to format and within that the updateItem function is overridden
     * to customize the cell data. The data in the cell is determined by what first level division the customer
     * resides in.
     *
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
        /**
         * Below a lambda is used to easily format a TableCell within a TableColumn and pass that formatted TableColumn
         * to the setCellFactory on the startDateTimeCol. A lambda is useful here because removes the need to create an
         * anonymous class or a whole new TableColumn object outside of the setCellFactory argument. The lambda is also
         * arguably more readable because it removes code that doesn't help explain what this application does.
         */
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
     * This method returns a TableColumn that holds country data connected to a customer. The TableColumn
     * is created and its name is assigned and a Property Value Factory is then set on the column to assign it data.
     * The setCellFactory is applied to the TableColumn to format and within that the updateItem function is overridden
     * to customize the cell data. The data in the cell is determined by what country the customer resides in.
     *
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
        /**
         * Below a lambda is used to easily format a TableCell within a TableColumn and pass that formatted TableColumn
         * to the setCellFactory on the startDateTimeCol. A lambda is useful here because removes the need to create an
         * anonymous class or a whole new TableColumn object outside of the setCellFactory argument. The lambda is also
         * arguably more readable because it removes code that doesn't help explain what this application does.
         */
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
     * This method sets an event on the row in the Customer TableView. If a row is selected, it clears the customer
     * text fields, fills them with data from the selected row, and clears the error labels.
     */
    private void initializeRowEvent() {
        /**
         * The setRowFactory is used to create a TableRow with specific properties. The customerTableView has a lambda
         * that its allows it to be assigned (via Callback) to a block of code. This lambda makes the code more readable
         * by stating that the customerTableView is going to have rows that have a custom mouseClicked event assigned
         * to it. Within this code block, a TableRow is created and a mouseEvent is set as an argument for its
         * setOnMouseClicked method. This is done by using a lambda to clearly encapsulate the code which determines
         * whether the row has been pressed, if a row is empty or not, and which mouse button pressed the row. These all
         * allow the data from the selected row to be added to the text fields. The lambda is useful because it removes
         * the need to define an anonymous function to create the mouse event and also keeps the code readable.
         */
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

    /**
     * This method is fills the text fields, which holds customer data that can be edited, with a customer selected from
     * the TableView.
     *
     * @param selectedCustomer
     */
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

    /**
     * This method clears all data current existing in the fields and drop downs on the jfx scene.
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
