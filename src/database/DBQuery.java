package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * The DBQuery class is a kind of model for a database query. It has a local PreparedStatement instance that is used
 * in the its methods. This class holds behavior to set a prepared statement to the a connection object that is
 * connected to a database. It also returns a PreparedStatement object.
 */
public class DBQuery {

    // Statement reference
    private static PreparedStatement statement;

    /**
     * This method returns a statement object that holds a connection to a database and a SQL query that is passed in
     * as an argument to this method.
     *
     * @param connection
     * @param sqlStatement
     * @throws SQLException
     */
    // Create statement object
    public static void setPreparedStatement(Connection connection, String sqlStatement) throws SQLException {
        statement = connection.prepareStatement(sqlStatement);
    }

    /**
     * This method returns the prepared statement object that has been filled with the connection & query data that
     * is ready to be executed.
     *
     * @return PreparedStatement
     */
    public static PreparedStatement getPreparedStatement() {
        return statement;
    }
}
