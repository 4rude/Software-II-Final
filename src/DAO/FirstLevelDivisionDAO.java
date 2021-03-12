package DAO;

import database.DBConnection;
import database.DBQuery;
import model.FirstLevelDivision;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This Class is a Data Access Object class, which uses the FirstLevelDivision model in tandem with the JDBC class and
 * SQL statements to provide high level functions to access the data within the database. This class allows the
 * programmer to read all first level division rows in the database, and then access that data from objects held
 * within an ArrayList.
 */
public class FirstLevelDivisionDAO {

    /**
     * This class method is used to return an ArrayList of all first level divisions found within the business database.
     * It first creates an ArrayList, then creates a connection with the database. Next it creates a SELECT statement to
     * return all the rows of first level division data, and adds that data to a ResultSet object. The object is stepped
     * through, and each row is added to a FirstLevelDivision object. The object is then added to an ArrayList, which
     * holds all the FirstLevelDivision objects created. The ArrayList is then returned from this function.
     *
     * @return ArrayList<FirstLevelDivision>
     * @throws SQLException
     */
    public static ArrayList<FirstLevelDivision> getAllFirstLevelDivisions() throws SQLException {
        // This CANNOT be created outside of this function. It will create duplicates when called more than once
        // within a controller.
        final ArrayList<FirstLevelDivision> allFirstLevelDivisions = new ArrayList();

        Connection conn = DBConnection.getConnection();

        // SQL Query statement
        String selectStatement = "SELECT * FROM first_level_divisions";

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
            FirstLevelDivision firstLevelDivision = new FirstLevelDivision(
                    rs.getInt("Division_ID"),
                    rs.getString("Division"),
                    rs.getInt("Country_ID"));

            // Add user object to the allUsers ArrayList
            allFirstLevelDivisions.add(firstLevelDivision);
        }

        return allFirstLevelDivisions;
    }
}
