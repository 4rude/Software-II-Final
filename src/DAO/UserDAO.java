package DAO;

import database.DBConnection;
import database.DBQuery;
import model.User;

import java.sql.*;
import java.util.ArrayList;

/**
 * This Class is a Data Access Object class, which uses the User model in tandem with the JDBC class and
 * SQL statements to provide high level functions to access the data within the database. This class allows the
 * programmer to read all user rows in the database, and then access that data from objects held within an ArrayList.
 */
public class UserDAO {

    /**
     * This class method is used to return an ArrayList of all users found within the business database. It first
     * creates an ArrayList, then creates a connection with the database. Next it creates a SELECT statement to return
     * all the rows of user data, and adds that data to a ResultSet object. The object is stepped through, and each
     * row is added to a User object. The object is then added to an ArrayList, which holds all the User objects
     * created. The ArrayList is then returned from this function.
     *
     * @return ArrayList<User>
     * @throws SQLException
     */
    public static ArrayList<User> getAllUsers() throws SQLException {
        // This CANNOT be created outside of this function. It will create duplicates when called more than once
        // within a controller.
        final ArrayList<User> allUsers = new ArrayList();

        Connection conn = DBConnection.getConnection();

        // SQL Query statement
        String selectStatement = "SELECT  User_ID, User_Name, Password FROM users";

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
            User user = new User(
                    rs.getInt("User_ID"),
                    rs.getString("User_Name"),
                    rs.getString("Password"));

            // Add user object to the allUsers ArrayList
            allUsers.add(user);
        }

        return allUsers;
    }
}
