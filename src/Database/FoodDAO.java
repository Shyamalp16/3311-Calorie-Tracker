package Database;

import models.Food;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FoodDAO {

    private final Random random = new Random();

    public List<Food> searchFoodByName(String searchTerm) {
        List<Food> foods = new ArrayList<>();
        String sql = """
            SELECT fn.FoodID, fn.FoodDescription,
                   COALESCE(n_cal.NutrientValue, 0) as calories,
                   COALESCE(n_prot.NutrientValue, 0) as protein,
                   COALESCE(n_carb.NutrientValue, 0) as carbs,
                   COALESCE(n_fat.NutrientValue, 0) as fats,
                   COALESCE(n_fiber.NutrientValue, 0) as fiber
            FROM food_name fn
            LEFT JOIN nutrient_amount n_cal ON fn.FoodID = n_cal.FoodID AND n_cal.NutrientNameID = 208
            LEFT JOIN nutrient_amount n_prot ON fn.FoodID = n_prot.FoodID AND n_prot.NutrientNameID = 203
            LEFT JOIN nutrient_amount n_carb ON fn.FoodID = n_carb.FoodID AND n_carb.NutrientNameID = 205
            LEFT JOIN nutrient_amount n_fat ON fn.FoodID = n_fat.FoodID AND n_fat.NutrientNameID = 204
            LEFT JOIN nutrient_amount n_fiber ON fn.FoodID = n_fiber.FoodID AND n_fiber.NutrientNameID = 291
            WHERE fn.FoodDescription LIKE ?
            LIMIT 20
            """;
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, searchTerm + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Food food = new Food(
                    rs.getInt("FoodID"),
                    rs.getString("FoodDescription"),
                    rs.getDouble("calories"),
                    rs.getDouble("protein"),
                    rs.getDouble("carbs"),
                    rs.getDouble("fats"),
                    rs.getDouble("fiber")
                );
                foods.add(food);
            }
        } catch (SQLException e) {
            System.err.println("Error searching food: " + e.getMessage());
        }
        return foods;
    }

    public Food getFoodById(int foodId) {
        String sql = """
            SELECT fn.FoodID, fn.FoodDescription,
                   COALESCE(n_cal.NutrientValue, 0) as calories,
                   COALESCE(n_prot.NutrientValue, 0) as protein,
                   COALESCE(n_carb.NutrientValue, 0) as carbs,
                   COALESCE(n_fat.NutrientValue, 0) as fats,
                   COALESCE(n_fiber.NutrientValue, 0) as fiber
            FROM food_name fn
            LEFT JOIN nutrient_amount n_cal ON fn.FoodID = n_cal.FoodID AND n_cal.NutrientNameID = 208
            LEFT JOIN nutrient_amount n_prot ON fn.FoodID = n_prot.FoodID AND n_prot.NutrientNameID = 203
            LEFT JOIN nutrient_amount n_carb ON fn.FoodID = n_carb.FoodID AND n_carb.NutrientNameID = 205
            LEFT JOIN nutrient_amount n_fat ON fn.FoodID = n_fat.FoodID AND n_fat.NutrientNameID = 204
            LEFT JOIN nutrient_amount n_fiber ON fn.FoodID = n_fiber.FoodID AND n_fiber.NutrientNameID = 291
            WHERE fn.FoodID = ?
            """;
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, foodId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Food(
                    rs.getInt("FoodID"),
                    rs.getString("FoodDescription"),
                    rs.getDouble("calories"),
                    rs.getDouble("protein"),
                    rs.getDouble("carbs"),
                    rs.getDouble("fats"),
                    rs.getDouble("fiber")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting food by ID: " + e.getMessage());
        }
        return null;
    }

    public List<Food> findSimilarFoodsByGroup(int originalFoodId, int limitCount) {
        List<Food> foods = new ArrayList<>();
        String sql = """
            SELECT fn2.FoodID, fn2.FoodDescription,
                   COALESCE(n_cal.NutrientValue, 0) as calories,
                   COALESCE(n_prot.NutrientValue, 0) as protein,
                   COALESCE(n_carb.NutrientValue, 0) as carbs,
                   COALESCE(n_fat.NutrientValue, 0) as fats,
                   COALESCE(n_fiber.NutrientValue, 0) as fiber
            FROM food_name fn1
            INNER JOIN food_name fn2 ON fn1.FoodGroupID = fn2.FoodGroupID
            LEFT JOIN nutrient_amount n_cal ON fn2.FoodID = n_cal.FoodID AND n_cal.NutrientNameID = 208
            LEFT JOIN nutrient_amount n_prot ON fn2.FoodID = n_prot.FoodID AND n_prot.NutrientNameID = 203
            LEFT JOIN nutrient_amount n_carb ON fn2.FoodID = n_carb.FoodID AND n_carb.NutrientNameID = 205
            LEFT JOIN nutrient_amount n_fat ON fn2.FoodID = n_fat.FoodID AND n_fat.NutrientNameID = 204
            LEFT JOIN nutrient_amount n_fiber ON fn2.FoodID = n_fiber.FoodID AND n_fiber.NutrientNameID = 291
            WHERE fn1.FoodID = ? AND fn2.FoodID != ?
            LIMIT ?
            """;
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, originalFoodId);
            pstmt.setInt(2, originalFoodId);
            pstmt.setInt(3, limitCount);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Food food = new Food(
                    rs.getInt("FoodID"),
                    rs.getString("FoodDescription"),
                    rs.getDouble("calories"),
                    rs.getDouble("protein"),
                    rs.getDouble("carbs"),
                    rs.getDouble("fats"),
                    rs.getDouble("fiber")
                );
                foods.add(food);
            }
        } catch (SQLException e) {
            System.err.println("Error finding similar foods: " + e.getMessage());
        }
        return foods;
    }

    public List<Food> findFoodsByNutrientRange(String nutrientType, double minValue, double maxValue, int limitCount) {
        List<Food> foods = new ArrayList<>();
        
        // Map nutrient types to database IDs
        int nutrientId = switch (nutrientType.toLowerCase()) {
            case "calories" -> 208;
            case "protein" -> 203;
            case "carbs", "carbohydrates" -> 205;
            case "fat", "fats" -> 204;
            case "fiber" -> 291;
            default -> 208; // Default to calories
        };
        
        String sql = """
            SELECT fn.FoodID, fn.FoodDescription,
                   COALESCE(n_cal.NutrientValue, 0) as calories,
                   COALESCE(n_prot.NutrientValue, 0) as protein,
                   COALESCE(n_carb.NutrientValue, 0) as carbs,
                   COALESCE(n_fat.NutrientValue, 0) as fats,
                   COALESCE(n_fiber.NutrientValue, 0) as fiber
            FROM food_name fn
            INNER JOIN nutrient_amount na ON fn.FoodID = na.FoodID
            LEFT JOIN nutrient_amount n_cal ON fn.FoodID = n_cal.FoodID AND n_cal.NutrientNameID = 208
            LEFT JOIN nutrient_amount n_prot ON fn.FoodID = n_prot.FoodID AND n_prot.NutrientNameID = 203
            LEFT JOIN nutrient_amount n_carb ON fn.FoodID = n_carb.FoodID AND n_carb.NutrientNameID = 205
            LEFT JOIN nutrient_amount n_fat ON fn.FoodID = n_fat.FoodID AND n_fat.NutrientNameID = 204
            LEFT JOIN nutrient_amount n_fiber ON fn.FoodID = n_fiber.FoodID AND n_fiber.NutrientNameID = 291
            WHERE na.NutrientNameID = ? AND na.NutrientValue BETWEEN ? AND ?
            LIMIT ?
            """;
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nutrientId);
            pstmt.setDouble(2, minValue);
            pstmt.setDouble(3, maxValue);
            pstmt.setInt(4, limitCount);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Food food = new Food(
                    rs.getInt("FoodID"),
                    rs.getString("FoodDescription"),
                    rs.getDouble("calories"),
                    rs.getDouble("protein"),
                    rs.getDouble("carbs"),
                    rs.getDouble("fats"),
                    rs.getDouble("fiber")
                );
                foods.add(food);
            }
        } catch (SQLException e) {
            System.err.println("Error finding foods by nutrient range: " + e.getMessage());
        }
        return foods;
    }
}