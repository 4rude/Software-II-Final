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
 *
 */
public class FirstLevelDivisionDAO {

    /**
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
