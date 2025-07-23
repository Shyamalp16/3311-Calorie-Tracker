package Database;

import models.Meal;
import models.MealItem;

import java.sql.*;
import java.util.List;

public class MealDAO {

    public int saveMeal(Meal meal) {
        String sql = "INSERT INTO meals (user_id, meal_type, meal_date, created_at, total_calories, total_protein, total_carbs, total_fat, total_fiber) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int mealId = -1;
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, meal.getUserId());
            pstmt.setString(2, meal.getMealType());
            pstmt.setDate(3, new java.sql.Date(meal.getMealDate().getTime()));
            pstmt.setTimestamp(4, meal.getCreatedAt());
            pstmt.setDouble(5, meal.getTotalCalories());
            pstmt.setDouble(6, meal.getTotalProtein());
            pstmt.setDouble(7, meal.getTotalCarbs());
            pstmt.setDouble(8, meal.getTotalFats());
            pstmt.setDouble(9, meal.getTotalFiber());
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
        String sql = "INSERT INTO meal_items (meal_id, food_id, quantity, unit, calories, protein, carbs, fats, fiber) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (MealItem item : mealItems) {
                pstmt.setInt(1, mealId);
                pstmt.setInt(2, item.getFoodId());
                pstmt.setDouble(3, item.getQuantity());
                pstmt.setString(4, item.getUnit());
                pstmt.setDouble(5, item.getCalories());
                pstmt.setDouble(6, item.getProtein());
                pstmt.setDouble(7, item.getCarbs());
                pstmt.setDouble(8, item.getFats());
                pstmt.setDouble(9, item.getFiber());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("Error saving meal items: " + e.getMessage());
        }
    }
}