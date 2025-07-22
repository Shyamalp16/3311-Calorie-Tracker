package Database;

import models.Meal;
import models.MealItem;

import java.sql.*;
import java.util.List;

public class MealDAO {

    public int saveMeal(Meal meal) {
        String sql = "INSERT INTO meals (user_id, meal_type, meal_date, created_at) VALUES (?, ?, ?, ?)";
        int mealId = -1;
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, meal.getUserId());
            pstmt.setString(2, meal.getMealType());
            pstmt.setDate(3, new java.sql.Date(meal.getMealDate().getTime()));
            pstmt.setTimestamp(4, meal.getCreatedAt());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                mealId = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error saving meal: " + e.getMessage());
        }
        return mealId;
    }

    public void saveMealItems(int mealId, List<MealItem> mealItems) {
        String sql = "INSERT INTO meal_items (meal_id, food_id, quantity, unit) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (MealItem item : mealItems) {
                pstmt.setInt(1, mealId);
                pstmt.setInt(2, item.getFoodId());
                pstmt.setDouble(3, item.getQuantity());
                pstmt.setString(4, item.getUnit());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("Error saving meal items: " + e.getMessage());
        }
    }
}