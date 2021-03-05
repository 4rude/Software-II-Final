package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DBQuery {

    // Statement reference
    private static PreparedStatement statement;

    // Create statement object
    public static void setPreparedStatement(Connection connection, String sqlStatement) throws SQLException {
        statement = connection.prepareStatement(sqlStatement);
    }

    // Return statement object
    public static PreparedStatement getPreparedStatement() {
        return statement;
    }
}
