package User_Profile_Management;

import java.sql.*;
import java.time.LocalDate;

import DatabaseConnector.DatabaseConnector;

public class ProfileManager {
	public UserProfile createProfile(UserProfile profile, String password) {
	    String sql = "INSERT INTO users (name, password, gender, birth_date, height, weight, activity_level) " +
	                 "VALUES (?, ?, ?, ?, ?, ?, ?)";
	    
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
	            try (ResultSet rs = pstmt.getGeneratedKeys()) {
	                if (rs.next()) {
	                    profile.setUserId(rs.getInt(1));
	                    return profile;
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}

public UserProfile getProfile(int userId) {
   String sql = "SELECT * FROM users WHERE user_id = ?";
   UserProfile profile = new UserProfile();
   
   try (Connection conn = DatabaseConnector.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
       
       pstmt.setInt(1, userId);
       ResultSet rs = pstmt.executeQuery();
       
       if (rs.next()) {
           profile.setUserId(rs.getInt("user_id"));
           profile.setName(rs.getString("name"));
           profile.setGender(rs.getString("gender"));
           profile.setBirthDate(rs.getDate("birth_date").toLocalDate());
           profile.setHeight(rs.getDouble("height"));
           profile.setWeight(rs.getDouble("weight"));
           profile.setActivityLevel(rs.getString("activity_level"));
           return profile;
       }
   } catch (SQLException e) {
       e.printStackTrace();
   }
   return null;
}

public boolean updateProfile(UserProfile profile, String password) {
   String sql = "UPDATE users SET name = ?, gender = ?, birth_date = ?, " +
                "height = ?, weight = ?, activity_level = ? WHERE user_id = ?";
   
   try (Connection conn = DatabaseConnector.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
       
       pstmt.setString(1, profile.getName());
       pstmt.setString(2, profile.getGender());
       pstmt.setDate(3, Date.valueOf(profile.getBirthDate()));
       pstmt.setDouble(4, profile.getHeight());
       pstmt.setDouble(5, profile.getWeight());
       pstmt.setString(6, profile.getActivityLevel());
       pstmt.setInt(7, profile.getUserId());
       
       return pstmt.executeUpdate() > 0;
   } catch (SQLException e) {
       e.printStackTrace();
   }
   return false;
}
}