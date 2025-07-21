package Database;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;

public class DatabaseConnector {
    // These are the connection details - you'll change these for your setup
    private static final String URL = "jdbc:mysql://localhost:3306/nutrisci_db";
    private static final String USERNAME = "root"; // Change to your MySQL username
    private static final String PASSWORD = "Password"; // Change to your MySQL password
    
    // This holds our connection to the database
    private static Connection connection = null;
    
    /**
     * Gets a connection to the database
     * @return Connection object to interact with database
     */
    public static Connection getConnection() {
        try {
            // If we don't have a connection or it's closed, create a new one
            if (connection == null || connection.isClosed()) {
                // Load the MySQL driver (tells Java how to talk to MySQL)
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Actually connect to the database
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Database connected successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL driver not found: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }
    
    /**
     * Closes the database connection when we're done
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database: " + e.getMessage());
        }
    }
    
    /**
     * Test if database connection is working
     * @return true if connected, false otherwise
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}