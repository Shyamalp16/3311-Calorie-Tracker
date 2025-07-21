package Database;

import Database.DatabaseConnector;
import models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO = Data Access Object
 * This class handles all database operations for users
 * It's like a translator between Java objects and SQL database
 */
public class UserDAO {
    /**
     * Saves a new user to the database
     * @param user The user object to save
     * @return the user with updated userId from database, or null if failed
     */
    public User createUser(User user) {
        // SQL statement to insert new user - ? are placeholders for values
        String sql = "INSERT INTO users (name, gender, birth_date, height, weight, activity_level, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, NOW())";
        
        try {
            // Get database connection
            Connection conn = DatabaseConnector.getConnection();
            
            // PreparedStatement prevents SQL injection attacks and handles data types
            // RETURN_GENERATED_KEYS means we want to get the auto-generated user_id back
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            // Fill in the ? placeholders with actual values from user object
            stmt.setString(1, user.getName());           // First ? becomes name
            stmt.setString(2, user.getGender());         // Second ? becomes gender
            stmt.setDate(3, user.getBirthDate());        // Third ? becomes birth_date
            stmt.setDouble(4, user.getHeight());         // Fourth ? becomes height
            stmt.setDouble(5, user.getWeight());         // Fifth ? becomes weight
            stmt.setString(6, user.getActivityLevel());  // Sixth ? becomes activity_level
            // NOW() is a MySQL function that sets created_at to current timestamp
            
            // Execute the SQL statement
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the auto-generated user_id from database
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    // Set the userId in our user object
                    user.setUserId(generatedKeys.getInt(1));
                }
                System.out.println("User created successfully with ID: " + user.getUserId());
                return user; // Return the user with their new ID
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null; // Return null if something went wrong
    }
    
    /**
     * Gets all users from database for dropdown menu
     * @return List of all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY name"; // Order by name for nice dropdown
        
        try {
            Connection conn = DatabaseConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            // Execute query and get results
            ResultSet rs = stmt.executeQuery();
            
            // Loop through each row in the results
            while (rs.next()) {
                // Create a new User object for each row
                User user = new User();
                
                // Fill user object with data from database row
                user.setUserId(rs.getInt("user_id"));
                user.setName(rs.getString("name"));
                user.setGender(rs.getString("gender"));
                user.setBirthDate(rs.getDate("birth_date"));
                user.setHeight(rs.getDouble("height"));
                user.setWeight(rs.getDouble("weight"));
                user.setActivityLevel(rs.getString("activity_level"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                
                // Add this user to our list
                users.add(user);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting users: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users; // Return list of all users
    }
    
    /**
     * Gets a specific user by their ID
     * @param userId the ID to search for
     * @return User object or null if not found
     */
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try {
            Connection conn = DatabaseConnector.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId); // Replace ? with the userId we're looking for
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) { // If we found a user
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setName(rs.getString("name"));
                user.setGender(rs.getString("gender"));
                user.setBirthDate(rs.getDate("birth_date"));
                user.setHeight(rs.getDouble("height"));
                user.setWeight(rs.getDouble("weight"));
                user.setActivityLevel(rs.getString("activity_level"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                
                return user;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null; // User not found
    }
}