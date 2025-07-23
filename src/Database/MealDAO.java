package Database;

import models.Meal;
import models.MealItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

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

    public List<Meal> getMealsForUserAndDate(int userId, Date date) {
        List<Meal> meals = new ArrayList<>();
        // Set time to start of day
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startDate = calendar.getTime();

        // Set time to end of day
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date endDate = calendar.getTime();

        String sql = "SELECT meal_id, user_id, meal_type, meal_date, created_at, total_calories, total_protein, total_carbs, total_fat, total_fiber FROM meals WHERE user_id = ? AND meal_date BETWEEN ? AND ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setTimestamp(2, new Timestamp(startDate.getTime()));
            pstmt.setTimestamp(3, new Timestamp(endDate.getTime()));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                meals.add(new Meal(
                    rs.getInt("meal_id"),
                    rs.getInt("user_id"),
                    rs.getString("meal_type"),
                    rs.getDate("meal_date"),
                    rs.getTimestamp("created_at"),
                    rs.getDouble("total_calories"),
                    rs.getDouble("total_protein"),
                    rs.getDouble("total_carbs"),
                    rs.getDouble("total_fat"),
                    rs.getDouble("total_fiber")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching meals: " + e.getMessage());
        }
        return meals;
    }

    public List<MealItem> getMealItemsByMealId(int mealId) {
        List<MealItem> mealItems = new ArrayList<>();
        String sql = "SELECT itemId, meal_id, food_id, quantity, unit, calories, protein, carbs, fats, fiber FROM meal_items WHERE meal_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, mealId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                mealItems.add(new MealItem(
                    rs.getInt("item_id"),
                    rs.getInt("meal_id"),
                    rs.getInt("food_id"),
                    rs.getDouble("quantity"),
                    rs.getString("unit"),
                    rs.getDouble("calories"),
                    rs.getDouble("protein"),
                    rs.getDouble("carbs"),
                    rs.getDouble("fats"),
                    rs.getDouble("fiber")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching meal items: " + e.getMessage());
        }
        return mealItems;
    }
}