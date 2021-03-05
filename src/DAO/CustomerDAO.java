package DAO;

import database.DBConnection;
import database.DBQuery;
import model.Appointment;
import model.Customer;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CustomerDAO {


    public static ArrayList<Customer> getAllCustomers() throws SQLException {
        // This CANNOT be created outside of this function. It will create duplicates when called more than once
        // within a controller.
        final ArrayList<Customer> allCustomers = new ArrayList();

        Connection conn = DBConnection.getConnection();

        // SQL Query statement
        String selectStatement = "SELECT * FROM customers";

        DBQuery.setPreparedStatement(conn, selectStatement);

        // Create the prepared statement
        PreparedStatement ps = DBQuery.getPreparedStatement();

        // Execute the prepared statement
        ps.execute();

        // Create a result set
        ResultSet rs = ps.getResultSet();

        // Scroll through the result set
        while (rs.next()) {
            // Fill up a user object using a constructor
            Customer customer = new Customer(
                    rs.getInt("Customer_ID"),
                    rs.getString("Customer_Name"),
                    rs.getString("Address"),
                    rs.getString("Postal_Code"),
                    rs.getString("Phone"),
                    rs.getObject("Create_Date", LocalDateTime.class),
                    rs.getString("Created_By"),
                    rs.getObject("Last_Update", LocalDateTime.class),
                    rs.getString("Last_Updated_By"),
                    rs.getInt("Division_ID"));

            // Add user object to the allUsers ArrayList
            allCustomers.add(customer);
        }

        return allCustomers;
    }

    public static void updateCustomer(Customer customer) throws SQLException {
        Connection conn = DBConnection.getConnection();

        // SQL Query statement
        String updateStatement = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, " +
                "Last_Update = now(), Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?";

        DBQuery.setPreparedStatement(conn, updateStatement);

        // Create the prepared statement
        PreparedStatement ps = DBQuery.getPreparedStatement();

        // Set the Prepared Statement key-value pairs
        ps.setString(1, customer.getCustomerName());
        ps.setString(2, customer.getAddress());
        ps.setString(3, customer.getPostalCode());
        ps.setString(4, customer.getPhoneNumber());
        ps.setString(5, customer.getLastUpdatedBy());
        ps.setInt(6, customer.getDivisionID());
        ps.setInt(7, customer.getCustomerID());

        // Execute the prepared statement
        ps.execute();
    }

    public static void createCustomer(Customer customer) throws SQLException {
        Connection conn = DBConnection.getConnection();

        // SQL Query statement
        String insertStatement = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Create_Date, " +
                "Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES (?, ?, ?, ?, now(), ?, now(), ?, ?)";

        DBQuery.setPreparedStatement(conn, insertStatement);

        // Create the prepared statement
        PreparedStatement ps = DBQuery.getPreparedStatement();

        // Set the Prepared Statement key-value pairs
        ps.setString(1, customer.getCustomerName());
        ps.setString(2, customer.getAddress());
        ps.setString(3, customer.getPostalCode());
        ps.setString(4, customer.getPhoneNumber());
        ps.setString(5, customer.getCreatedBy());
        ps.setString(6, customer.getLastUpdatedBy());
        ps.setInt(7, customer.getDivisionID());

        // Execute the prepared statement
        ps.execute();
    }

    public static void deleteCustomer(int customerID) throws SQLException {
        Connection conn = DBConnection.getConnection();

        // SQL Query statement
        String deleteAppointment = "DELETE FROM customers WHERE Customer_ID = ?";

        DBQuery.setPreparedStatement(conn, deleteAppointment);

        // Create the prepared statement
        PreparedStatement ps = DBQuery.getPreparedStatement();

        // Set the Prepared Statement key-value pairs
        ps.setInt(1, customerID);

        // Execute the prepared statement
        ps.execute();
    }
}
