package database;

// MySQL Connection Packages
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The DBConnection class is used to hold database connection data, the JDBC info used in this application, and holds
 * behavior to start a connection to the database, return a connection to a programmer requesting it, and safely closing
 * a connection with the database.
 */
public class DBConnection {

    // WGU Database server data
    private static final String protocol = "jdbc";
    private static final String vendorName = ":mysql:";
    private static final String ipAddress = "";
    private static final String dbName = "";
    private static final String password = "";

    // Will build full jdbc connection string
    private static final String jdbcURL = protocol + vendorName + ipAddress + dbName + "?connectionTimeZone=SERVER";

    // String that holds the name of the JDBC class
    private static final String MYSQLJDBCDriver = "com.mysql.cj.jdbc.Driver";

    // Username to log into the WGU database
    private static final String username = "";
    private static Connection conn = null;

    /**
     * Starts the database connection
     *
     * @return Connection
     * **/
    public static Connection startConnection() {
        try{
            Class.forName(MYSQLJDBCDriver);
            conn = DriverManager.getConnection(jdbcURL, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * Serves up the database connection when needed
     *
     * @return Connection
     * **/
    public static Connection getConnection() {
        return conn;
    }

    /**
     * Closes the database connection
     *
     **/
    public static void closeConnection() {
        try{
            conn.close();
        } catch (Exception e) {
            // Do nothing, not needed because the application is ending
        }
    }
}
