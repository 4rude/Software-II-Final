package DAO;

import database.DBConnection;
import database.DBQuery;
import model.User;

import java.sql.*;
import java.util.ArrayList;

/**
 *
 */
public class UserDAO {

    /**
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
