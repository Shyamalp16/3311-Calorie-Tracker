package Database;

import models.SwapHistory;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SwapHistoryDAO {

    public int saveSwapHistory(SwapHistory swapHistory) {
        String sql = """
            INSERT INTO swap_history (user_id, meal_id, original_food_id, swapped_food_id, 
                                    quantity, unit, swap_reason, applied_at, is_active) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        int historyId = -1;
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, swapHistory.getUserId());
            pstmt.setInt(2, swapHistory.getMealId());
            pstmt.setInt(3, swapHistory.getOriginalFoodId());
            pstmt.setInt(4, swapHistory.getSwappedFoodId());
            pstmt.setDouble(5, swapHistory.getQuantity());
            pstmt.setString(6, swapHistory.getUnit());
            pstmt.setString(7, swapHistory.getSwapReason());
            pstmt.setTimestamp(8, swapHistory.getAppliedAt());
            pstmt.setBoolean(9, swapHistory.isActive());
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                historyId = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error saving swap history: " + e.getMessage());
            e.printStackTrace();
        }
        return historyId;
    }

    public List<SwapHistory> getSwapHistoryForUser(int userId) {
        List<SwapHistory> swapHistories = new ArrayList<>();
        String sql = """
            SELECT history_id, user_id, meal_id, original_food_id, swapped_food_id, 
                   quantity, unit, swap_reason, applied_at, is_active 
            FROM swap_history 
            WHERE user_id = ? 
            ORDER BY applied_at DESC
            """;
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                swapHistories.add(new SwapHistory(
                    rs.getInt("history_id"),
                    rs.getInt("user_id"),
                    rs.getInt("meal_id"),
                    rs.getInt("original_food_id"),
                    rs.getInt("swapped_food_id"),
                    rs.getDouble("quantity"),
                    rs.getString("unit"),
                    rs.getString("swap_reason"),
                    rs.getTimestamp("applied_at"),
                    rs.getBoolean("is_active")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching swap history: " + e.getMessage());
        }
        return swapHistories;
    }

    public List<SwapHistory> getActiveSwapsForMeal(int mealId) {
        List<SwapHistory> swapHistories = new ArrayList<>();
        String sql = """
            SELECT history_id, user_id, meal_id, original_food_id, swapped_food_id, 
                   quantity, unit, swap_reason, applied_at, is_active 
            FROM swap_history 
            WHERE meal_id = ? AND is_active = true
            """;
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, mealId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                swapHistories.add(new SwapHistory(
                    rs.getInt("history_id"),
                    rs.getInt("user_id"),
                    rs.getInt("meal_id"),
                    rs.getInt("original_food_id"),
                    rs.getInt("swapped_food_id"),
                    rs.getDouble("quantity"),
                    rs.getString("unit"),
                    rs.getString("swap_reason"),
                    rs.getTimestamp("applied_at"),
                    rs.getBoolean("is_active")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching active swaps for meal: " + e.getMessage());
        }
        return swapHistories;
    }

    public void deactivateSwap(int historyId) {
        String sql = "UPDATE swap_history SET is_active = false WHERE history_id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, historyId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deactivating swap: " + e.getMessage());
        }
    }

    public List<SwapHistory> getSwapHistoryForPeriod(int userId, Date startDate, Date endDate) {
        List<SwapHistory> swapHistories = new ArrayList<>();
        String sql = """
            SELECT sh.history_id, sh.user_id, sh.meal_id, sh.original_food_id, sh.swapped_food_id, 
                   sh.quantity, sh.unit, sh.swap_reason, sh.applied_at, sh.is_active 
            FROM swap_history sh
            INNER JOIN meals m ON sh.meal_id = m.meal_id
            WHERE sh.user_id = ? AND m.meal_date BETWEEN ? AND ?
            ORDER BY sh.applied_at DESC
            """;
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setDate(2, new java.sql.Date(startDate.getTime()));
            pstmt.setDate(3, new java.sql.Date(endDate.getTime()));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                swapHistories.add(new SwapHistory(
                    rs.getInt("history_id"),
                    rs.getInt("user_id"),
                    rs.getInt("meal_id"),
                    rs.getInt("original_food_id"),
                    rs.getInt("swapped_food_id"),
                    rs.getDouble("quantity"),
                    rs.getString("unit"),
                    rs.getString("swap_reason"),
                    rs.getTimestamp("applied_at"),
                    rs.getBoolean("is_active")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching swap history for period: " + e.getMessage());
        }
        return swapHistories;
    }

    public void createSwapHistoryTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS swap_history (
                history_id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL,
                meal_id INT NOT NULL,
                original_food_id INT NOT NULL,
                swapped_food_id INT NOT NULL,
                quantity DOUBLE NOT NULL,
                unit VARCHAR(10) NOT NULL,
                swap_reason TEXT,
                applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                is_active BOOLEAN DEFAULT TRUE,
                FOREIGN KEY (meal_id) REFERENCES meals(meal_id),
                INDEX idx_user_date (user_id, applied_at),
                INDEX idx_meal_active (meal_id, is_active)
            )
            """;
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating swap_history table: " + e.getMessage());
        }
    }
} 