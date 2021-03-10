package DAO;

import database.DBConnection;
import database.DBQuery;
import model.Appointment;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;


/**
 * @author matt
 *
 * This Class is a Data Access Object class, which uses the Appointment model in tandem with the JDBC class and
 * SQL statements to provide high level functions to access the data within the database. This class allows the
 * programmer to create, read, update, and delete appointments, as well as delete all appointments tied to a
 * customer ID.
 */
public class AppointmentDAO {


    /**
     * @return ArrayList<Appointment>
     * @throws SQLException
     */
    public static ArrayList<Appointment> getAllAppointments() throws SQLException {

        // This CANNOT be created outside of this function. It will create duplicates when called more than once
        // within a controller.
        final ArrayList<Appointment> allAppointments = new ArrayList();

        // Create a connection object for use within the DBQuery class
        Connection conn = DBConnection.getConnection();

        // SQL Query statement
        String selectStatement = "SELECT * FROM appointments";

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
            Appointment appointment = new Appointment(
                    rs.getInt("Appointment_ID"),
                    rs.getString("Title"),
                    rs.getString("Description"),
                    rs.getString("Location"),
                    rs.getString("Type"),
                    rs.getObject("Start", LocalDateTime.class),
                    rs.getObject("End", LocalDateTime.class),
                    rs.getObject("Create_Date", LocalDateTime.class),
                    rs.getString("Created_By"),
                    rs.getObject("Last_Update", LocalDateTime.class),
                    rs.getString("Last_Updated_By"),
                    rs.getInt("Customer_ID"),
                    rs.getInt("User_ID"),
                    rs.getInt("Contact_ID"));

            // Add user object to the allUsers ArrayList
            allAppointments.add(appointment);
        }

        return allAppointments;
    }

    /**
     * @param appt
     * @throws SQLException
     */
    public static void updateAppointment(Appointment appt) throws SQLException {
        Connection conn = DBConnection.getConnection();

        // SQL Query statement
        String updateStatement = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, " +
                "Start = ?, End = ?, Last_Update = now(), Last_Updated_By = ?,  " +
                "Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";

        DBQuery.setPreparedStatement(conn, updateStatement);

        // Create the prepared statement
        PreparedStatement ps = DBQuery.getPreparedStatement();

        // Set the Prepared Statement key-value pairs
        ps.setString(1, appt.getTitle());
        ps.setString(2, appt.getDescription());
        ps.setString(3, appt.getLocation());
        ps.setString(4, appt.getType());
        ps.setObject(5, Timestamp.valueOf(appt.getStart()));
        ps.setObject(6, Timestamp.valueOf(appt.getEnd()));
        ps.setString(7, appt.getLastUpdatedBy());
        ps.setInt(8, appt.getCustomerID());
        ps.setInt(9, appt.getUserID());
        ps.setInt(10, appt.getContactID());
        ps.setInt(11, appt.getAppointmentID());

        // Execute the prepared statement
        ps.execute();
    }

    /**
     * @param appt
     * @throws SQLException
     */
    public static void createAppointment(Appointment appt) throws SQLException {
        Connection conn = DBConnection.getConnection();

        // SQL Query statement
        String insertStatement = "INSERT INTO appointments (Title, Description, Location, Type, Start, End, " +
                "Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) " +
                "VALUES (?, ?, ?, ?, ?, ?, now(), ?, now(), ?, ?, ?, ?)";

        DBQuery.setPreparedStatement(conn, insertStatement);

        // Create the prepared statement
        PreparedStatement ps = DBQuery.getPreparedStatement();

        // Set the Prepared Statement key-value pairs
        ps.setString(1, appt.getTitle());
        ps.setString(2, appt.getDescription());
        ps.setString(3, appt.getLocation());
        ps.setString(4, appt.getType());
        ps.setObject(5, Timestamp.valueOf(appt.getStart()));
        ps.setObject(6, Timestamp.valueOf(appt.getEnd()));
        ps.setString(7, appt.getCreatedBy());
        ps.setString(8, appt.getLastUpdatedBy());
        ps.setInt(9, appt.getCustomerID());
        ps.setInt(10, appt.getUserID());
        ps.setInt(11, appt.getContactID());

        // Execute the prepared statement
        ps.execute();
    }

    /**
     * @param appointmentID
     * @throws SQLException
     */
    public static void deleteAppointment(int appointmentID) throws SQLException {
        Connection conn = DBConnection.getConnection();

        // SQL Query statement
        String deleteAppointment = "DELETE FROM appointments WHERE Appointment_ID = ?";

        DBQuery.setPreparedStatement(conn, deleteAppointment);

        // Create the prepared statement
        PreparedStatement ps = DBQuery.getPreparedStatement();

        // Set the Prepared Statement key-value pairs
        ps.setInt(1, appointmentID);

        // Execute the prepared statement
        ps.execute();
    }

    /**
     * @param CustomerID
     * @throws SQLException
     */
    public static void deleteAllAppointments(int CustomerID) throws SQLException {
        // Deletes all appointments that a customer has. Must be done before a customer is deleted.
        Connection conn = DBConnection.getConnection();

        // SQL Query statement
        String deleteAppointment = "DELETE FROM appointments WHERE Customer_ID = ?";

        DBQuery.setPreparedStatement(conn, deleteAppointment);

        // Create the prepared statement
        PreparedStatement ps = DBQuery.getPreparedStatement();

        // Set the Prepared Statement key-value pairs
        ps.setInt(1, CustomerID);

        // Execute the prepared statement
        ps.execute();
    }
}
