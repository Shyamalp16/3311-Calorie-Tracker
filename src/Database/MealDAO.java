package Database;

import models.Meal;
import models.MealItem;
import models.SwapHistory;

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
        String sql = "SELECT meal_id, user_id, meal_type, meal_date, created_at, total_calories, total_protein, total_carbs, total_fat, total_fiber FROM meals WHERE user_id = ? AND meal_date = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setDate(2, new java.sql.Date(date.getTime()));
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
            System.err.println("Error fetching meals for user and date: " + e.getMessage());
        }
        return meals;
    }

    public List<MealItem> getMealItemsByMealId(int mealId) {
        List<MealItem> mealItems = new ArrayList<>();
        String sql = "SELECT item_id, meal_id, food_id, quantity, unit, calories, protein, carbs, fats, fiber FROM meal_items WHERE meal_id = ?";
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

    public boolean updateMealItem(int mealId, int originalFoodId, int newFoodId, double newQuantity, String newUnit) {
        String sql = """
            UPDATE meal_items 
            SET food_id = ?, quantity = ?, unit = ?, 
                calories = (SELECT COALESCE(n_cal.NutrientValue, 0) * ? FROM nutrient_amount n_cal WHERE n_cal.FoodID = ? AND n_cal.NutrientNameID = 208 LIMIT 1),
                protein = (SELECT COALESCE(n_prot.NutrientValue, 0) * ? FROM nutrient_amount n_prot WHERE n_prot.FoodID = ? AND n_prot.NutrientNameID = 203 LIMIT 1),
                carbs = (SELECT COALESCE(n_carb.NutrientValue, 0) * ? FROM nutrient_amount n_carb WHERE n_carb.FoodID = ? AND n_carb.NutrientNameID = 205 LIMIT 1),
                fats = (SELECT COALESCE(n_fat.NutrientValue, 0) * ? FROM nutrient_amount n_fat WHERE n_fat.FoodID = ? AND n_fat.NutrientNameID = 204 LIMIT 1),
                fiber = (SELECT COALESCE(n_fiber.NutrientValue, 0) * ? FROM nutrient_amount n_fiber WHERE n_fiber.FoodID = ? AND n_fiber.NutrientNameID = 291 LIMIT 1)
            WHERE meal_id = ? AND food_id = ?
            """;
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, newFoodId);
            pstmt.setDouble(2, newQuantity);
            pstmt.setString(3, newUnit);
            
            // Set quantity multipliers for nutrient calculations
            pstmt.setDouble(4, newQuantity);  // calories
            pstmt.setInt(5, newFoodId);
            pstmt.setDouble(6, newQuantity);  // protein
            pstmt.setInt(7, newFoodId);
            pstmt.setDouble(8, newQuantity);  // carbs
            pstmt.setInt(9, newFoodId);
            pstmt.setDouble(10, newQuantity); // fats
            pstmt.setInt(11, newFoodId);
            pstmt.setDouble(12, newQuantity); // fiber
            pstmt.setInt(13, newFoodId);
            
            pstmt.setInt(14, mealId);
            pstmt.setInt(15, originalFoodId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Update meal totals
                updateMealTotals(mealId);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating meal item: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public void updateMealTotals(int mealId) {
        String sql = """
            UPDATE meals 
            SET total_calories = (SELECT COALESCE(SUM(calories), 0) FROM meal_items WHERE meal_id = ?),
                total_protein = (SELECT COALESCE(SUM(protein), 0) FROM meal_items WHERE meal_id = ?),
                total_carbs = (SELECT COALESCE(SUM(carbs), 0) FROM meal_items WHERE meal_id = ?),
                total_fat = (SELECT COALESCE(SUM(fats), 0) FROM meal_items WHERE meal_id = ?),
                total_fiber = (SELECT COALESCE(SUM(fiber), 0) FROM meal_items WHERE meal_id = ?)
            WHERE meal_id = ?
            """;
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, mealId);
            pstmt.setInt(2, mealId);
            pstmt.setInt(3, mealId);
            pstmt.setInt(4, mealId);
            pstmt.setInt(5, mealId);
            pstmt.setInt(6, mealId);
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error updating meal totals: " + e.getMessage());
        }
    }

    public List<Meal> getMealsInDateRange(int userId, Date startDate, Date endDate) {
        List<Meal> meals = new ArrayList<>();
        String sql = """
            SELECT meal_id, user_id, meal_type, meal_date, created_at, 
                   total_calories, total_protein, total_carbs, total_fat, total_fiber 
            FROM meals 
            WHERE user_id = ? AND meal_date BETWEEN ? AND ?
            ORDER BY meal_date DESC, created_at DESC
            """;
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setDate(2, new java.sql.Date(startDate.getTime()));
            pstmt.setDate(3, new java.sql.Date(endDate.getTime()));
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
            System.err.println("Error fetching meals in date range: " + e.getMessage());
        }
        return meals;
    }

    public boolean hasMealItemWithFood(int mealId, int foodId) {
        String sql = "SELECT COUNT(*) FROM meal_items WHERE meal_id = ? AND food_id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, mealId);
            pstmt.setInt(2, foodId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking meal item existence: " + e.getMessage());
        }
        return false;
    }
}