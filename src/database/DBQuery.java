package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 */
public class DBQuery {

    // Statement reference
    private static PreparedStatement statement;

    /**
     * @param connection
     * @param sqlStatement
     * @throws SQLException
     */
    // Create statement object
    public static void setPreparedStatement(Connection connection, String sqlStatement) throws SQLException {
        statement = connection.prepareStatement(sqlStatement);
    }

    /**
     * @return PreparedStatement
     */
    public static PreparedStatement getPreparedStatement() {
        return statement;
    }
}
