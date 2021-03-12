package DAO;

import database.DBConnection;
import database.DBQuery;
import model.Country;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This Class is a Data Access Object class, which uses the Country model in tandem with the JDBC class and
 * SQL statements to provide high level functions to access the data within the database. This class allows the
 * programmer to read all country rows in the database, and then access that data from objects held within an ArrayList.
 */
public class CountryDAO {

    /**
     * This class method is used to return an ArrayList of all countries found within the business database. It first
     * creates an ArrayList, then creates a connection with the database. Next it creates a SELECT statement to return
     * all the rows of country data, and adds that data to a ResultSet object. The object is stepped through, and each
     * row is added to a Country object. The object is then added to an ArrayList, which holds all the Country objects
     * created. The ArrayList is then returned from this function.
     *
     * @return ArrayList<Country>
     * @throws SQLException
     */
    public static ArrayList<Country> getAllCountries() throws SQLException {

        // This CANNOT be created outside of this function. It will create duplicates when called more than once
        // within a controller.
        final ArrayList<Country> allCountries = new ArrayList();

        Connection conn = DBConnection.getConnection();

        // SQL Query statement
        String selectStatement = "SELECT * FROM countries";

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
            Country country = new Country(
                    rs.getInt("Country_ID"),
                    rs.getString("Country"));

            // Add user object to the allUsers ArrayList
            allCountries.add(country);
        }

        return allCountries;
    }
}
