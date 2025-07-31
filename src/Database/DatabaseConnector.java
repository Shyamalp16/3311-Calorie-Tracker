package Database;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class DatabaseConnector {
    private static DatabaseConnector instance;
    private static final Properties properties = new Properties();

    static {
        try {
            InputStream input = null;
            
            input = DatabaseConnector.class.getClassLoader().getResourceAsStream("config.properties");
            
            if (input == null) {
                try {
                    input = new FileInputStream("src/config.properties");
                } catch (IOException e1) {
                    try {
                        input = new FileInputStream("config.properties");
                    } catch (IOException e2) {
                        input = new FileInputStream("bin/config.properties");
                    }
                }
            }
            
            properties.load(input);
            try {
                Class.forName(properties.getProperty("db.driver"));
            } catch (ClassNotFoundException e) {
                System.err.println("WARNING: MySQL JDBC driver not found. Please add mysql-connector-java.jar to lib/ directory");
                System.err.println("Download from: https://dev.mysql.com/downloads/connector/j/");
                throw new RuntimeException("MySQL JDBC driver not found. Please add mysql-connector-java.jar to classpath", e);
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load database configuration: " + e.getMessage(), e);
        }
    }

    private DatabaseConnector() {
        
    }

    public static DatabaseConnector getInstance() {
        if (instance == null) {
            instance = new DatabaseConnector();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            properties.getProperty("db.url"),
            properties.getProperty("db.username"),
            properties.getProperty("db.password")
        );
    }

    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
