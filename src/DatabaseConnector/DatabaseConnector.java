package DatabaseConnector;

import java.sql.*;

public class DatabaseConnector {
	private static DatabaseConnector instance;
	private Connection connection;

	private DatabaseConnector() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/nutrisci_db";
			String username = "root";
			String password = "Password";
			connection = DriverManager.getConnection(url, username, password);
			initializeDatabase(); // Calls the method below
		} catch (Exception e) {
			System.err.println("Database connection failed:");
			e.printStackTrace();
		}
	}

	private void initializeDatabase() throws SQLException {
		try (Statement stmt = connection.createStatement()) {
			// Create Users table
			stmt.execute("CREATE TABLE IF NOT EXISTS users (" + "user_id INT AUTO_INCREMENT PRIMARY KEY, "
					+ "name VARCHAR(100) NOT NULL, " + "gender VARCHAR(10), " + "birth_date DATE, " + "height DOUBLE, "
					+ "weight DOUBLE, " + "activity_level VARCHAR(20), "
					+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

			// Create Meals table
			stmt.execute("CREATE TABLE IF NOT EXISTS meals (" + "meal_id INT AUTO_INCREMENT PRIMARY KEY, "
					+ "user_id INT, " + "meal_type VARCHAR(20), " + "meal_date DATE, "
					+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
					+ "FOREIGN KEY (user_id) REFERENCES users(user_id))");

			// Create MealItems table
			stmt.execute("CREATE TABLE IF NOT EXISTS meal_items (" + "item_id INT AUTO_INCREMENT PRIMARY KEY, "
					+ "meal_id INT, " + "food_id INT, " + "quantity DOUBLE, " + "unit VARCHAR(20), "
					+ "FOREIGN KEY (meal_id) REFERENCES meals(meal_id))");

			// Create FoodNutrients table (from CNF data)
			stmt.execute("CREATE TABLE IF NOT EXISTS food_nutrients (" + "food_id INT PRIMARY KEY, "
					+ "description VARCHAR(255), " + "food_group VARCHAR(100), " + "calories DOUBLE, "
					+ "protein DOUBLE, " + "carbs DOUBLE, " + "fats DOUBLE, " + "fiber DOUBLE)");
		}
	}

	public static synchronized DatabaseConnector getInstance() {
		if (instance == null) {
			instance = new DatabaseConnector();
		}
		return instance;
	}

	public Connection getConnection() {
		    try {
		        if (connection == null || connection.isClosed()) {
		            String url = "jdbc:mysql://localhost:3306/nutrisci_db";
		            String username = "root";
		            String password = "Password";
		            connection = DriverManager.getConnection(url, username, password);
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return connection;
		}

	public void closeConnection() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}