package DAO;

import database.DBConnection;
import database.DBQuery;
import model.Contact;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This Class is a Data Access Object class, which uses the Contact model in tandem with the JDBC class and
 * SQL statements to provide high level functions to access the data within the database. This class allows the
 * programmer to read all contact rows in the database, and then access that data from objects held within an ArrayList.
 */
public class ContactDAO {


    /**
     * This class method is used to return an ArrayList of all contacts found within the business database. It first
     * creates an ArrayList, then creates a connection with the database. Next it creates a SELECT statement to return
     * all the rows of Contact data, and adds that data to a ResultSet object. The object is stepped through, and each
     * row is added to a Contact object. The object is then added to an ArrayList, which holds all the Contact objects
     * created. The ArrayList is then returned from this function.
     *
     * @return ArrayList<Contact>
     * @throws SQLException
     */
    public static ArrayList<Contact> getAllContacts() throws SQLException {

        // This CANNOT be created outside of this function. It will create duplicates when called more than once
        // within a controller.
        final ArrayList<Contact> allContacts = new ArrayList();

        Connection conn = DBConnection.getConnection();

        // SQL Query statement
        String selectStatement = "SELECT * FROM contacts";

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
            Contact contact = new Contact(
                    rs.getInt("Contact_ID"),
                    rs.getString("Contact_Name"),
                    rs.getString("Email"));

            // Add user object to the allUsers ArrayList
            allContacts.add(contact);
        }

        return allContacts;
    }
}
