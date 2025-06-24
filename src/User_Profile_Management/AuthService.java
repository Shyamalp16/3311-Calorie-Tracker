package User_Profile_Management;

import DatabaseConnector.DatabaseConnector;
import java.sql.*;

public class AuthService {
    
    /**
     * Authenticates user login
     * @param username The username
     * @param password The plaintext password
     * @return user_id on success, -1 on failure
     */
    public static int login(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty() || password.isEmpty()) {
            return -1;
        }
        
        String sql = "SELECT user_id, password FROM users WHERE name = ?";
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username.trim());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (storedPassword.equals(password)) {
                    return rs.getInt("user_id");
                }
            }
            return -1;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Registers a new user with username, password, and email
     * @param username The username
     * @param password The plaintext password
     * @param email The user's email
     * @return new user_id on success, -1 on failure
     */
    public static int register(String username, String password, String email) {
        if (username == null || password == null || email == null || 
            username.trim().isEmpty() || password.isEmpty() || email.trim().isEmpty()) {
            return -1;
        }
        
        if (usernameExists(username.trim())) {
            return -1;
        }
        
        String sql = "INSERT INTO users (name, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, username.trim());
            pstmt.setString(2, password);
            pstmt.setString(3, email.trim());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return -1;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Registers a new user with full profile information
     * @param profile The user profile
     * @param password The plaintext password
     * @return new user_id on success, -1 on failure
     */
    public static int register(UserProfile profile, String password) {
        if (profile == null || password == null || password.isEmpty()) {
            return -1;
        }
        
        if (usernameExists(profile.getName())) {
            return -1;
        }
        
        String sql = "INSERT INTO users (name, password, gender, birth_date, height, weight, activity_level, email) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, profile.getName());
            pstmt.setString(2, password);
            pstmt.setString(3, profile.getGender());
            pstmt.setDate(4, Date.valueOf(profile.getBirthDate()));
            pstmt.setDouble(5, profile.getHeight());
            pstmt.setDouble(6, profile.getWeight());
            pstmt.setString(7, profile.getActivityLevel());
            
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int userId = rs.getInt(1);
                    profile.setUserId(userId);
                    return userId;
                }
            }
            return -1;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Checks if a username already exists in the database
     * @param username The username to check
     * @return true if username exists, false otherwise
     */
    private static boolean usernameExists(String username) {
        if (username == null || username.trim().isEmpty()) {
            return true; 
        }
        
        String sql = "SELECT user_id FROM users WHERE name = ?";
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username.trim());
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }
    
    /**
     * Validates user credentials without returning user ID
     * @param username The username
     * @param password The password
     * @return true if credentials are valid, false otherwise
     */
    public static boolean validateCredentials(String username, String password) {
        return login(username, password) != -1;
    }
    
    /**
     * Checks if a user exists by username
     * @param username The username to check
     * @return true if user exists, false otherwise
     */
    public static boolean userExists(String username) {
        return usernameExists(username);
    }
}