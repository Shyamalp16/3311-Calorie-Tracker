package Database;

import models.Goal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GoalDAO {

    public void saveGoal(Goal goal) {
        String checkIfExistsSql = "SELECT COUNT(*) FROM goals WHERE user_id = ?";
        String insertSql = "INSERT INTO goals (user_id, calories, protein, carbs, fats, fiber) VALUES (?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE goals SET calories = ?, protein = ?, carbs = ?, fats = ?, fiber = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnector.getConnection()) {
            // Check if a goal for the user already exists
            try (PreparedStatement checkStmt = conn.prepareStatement(checkIfExistsSql)) {
                checkStmt.setInt(1, goal.getUserId());
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    // Update existing goal
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setDouble(1, goal.getCalories());
                        updateStmt.setDouble(2, goal.getProtein());
                        updateStmt.setDouble(3, goal.getCarbs());
                        updateStmt.setDouble(4, goal.getFats());
                        updateStmt.setDouble(5, goal.getFiber());
                        updateStmt.setInt(6, goal.getUserId());
                        updateStmt.executeUpdate();
                    }
                } else {
                    // Insert new goal
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, goal.getUserId());
                        insertStmt.setDouble(2, goal.getCalories());
                        insertStmt.setDouble(3, goal.getProtein());
                        insertStmt.setDouble(4, goal.getCarbs());
                        insertStmt.setDouble(5, goal.getFats());
                        insertStmt.setDouble(6, goal.getFiber());
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Goal getGoalByUserId(int userId) {
        String sql = "SELECT * FROM goals WHERE user_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Goal(
                        rs.getInt("user_id"),
                        rs.getDouble("calories"),
                        rs.getDouble("protein"),
                        rs.getDouble("carbs"),
                        rs.getDouble("fats"),
                        rs.getDouble("fiber")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no goal is found
    }
}