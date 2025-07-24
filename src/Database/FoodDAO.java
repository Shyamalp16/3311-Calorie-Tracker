package Database;

import models.Food;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                double calories = rs.getDouble("calories");
                double protein = rs.getDouble("protein");
                double carbs = rs.getDouble("carbs");
                double fats = rs.getDouble("fats");
                double fiber = rs.getDouble("fiber");

                if (calories == 0 && (protein > 0 || carbs > 0 || fats > 0)) {
                    calories = (protein * 4) + (carbs * 4) + (fats * 9);
                }

                Food food = new Food(
                    rs.getInt("FoodID"),
                    rs.getString("FoodDescription"),
                    calories,
                    protein,
                    carbs,
                    fats,
                    fiber
                );
                foods.add(food);
            }
        } catch (SQLException e) {
            System.err.println("Error searching food: " + e.getMessage());
        }
        return foods;
    }

    public Food getFoodById(int foodId) {
        return getFoodDetails(foodId);
    }

    public Food getFoodDetails(int foodId) {
        System.out.println("DAO: Fetching details for foodId: " + foodId);
        Food food = null;
        String mainDetailsSql = """
            SELECT
                fn.FoodID,
                fn.FoodDescription,
                fg.FoodGroupName,
                fs.FoodSourceDescription,
                COALESCE(n_cal.NutrientValue, 0) as calories,
                COALESCE(n_prot.NutrientValue, 0) as protein,
                COALESCE(n_carb.NutrientValue, 0) as carbs,
                COALESCE(n_fat.NutrientValue, 0) as fats,
                COALESCE(n_fiber.NutrientValue, 0) as fiber
            FROM
                food_name fn
            LEFT JOIN food_group fg ON fn.FoodGroupID = fg.FoodGroupID
            LEFT JOIN food_source fs ON fn.FoodSourceID = fs.FoodSourceID
            LEFT JOIN nutrient_amount n_cal ON fn.FoodID = n_cal.FoodID AND n_cal.NutrientNameID = 208
            LEFT JOIN nutrient_amount n_prot ON fn.FoodID = n_prot.FoodID AND n_prot.NutrientNameID = 203
            LEFT JOIN nutrient_amount n_carb ON fn.FoodID = n_carb.FoodID AND n_carb.NutrientNameID = 205
            LEFT JOIN nutrient_amount n_fat ON fn.FoodID = n_fat.FoodID AND n_fat.NutrientNameID = 204
            LEFT JOIN nutrient_amount n_fiber ON fn.FoodID = n_fiber.FoodID AND n_fiber.NutrientNameID = 291
            WHERE fn.FoodID = ?
            """;

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(mainDetailsSql)) {
            
            pstmt.setInt(1, foodId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double calories = rs.getDouble("calories");
                double protein = rs.getDouble("protein");
                double carbs = rs.getDouble("carbs");
                double fats = rs.getDouble("fats");
                double fiber = rs.getDouble("fiber");

                if (calories == 0 && (protein > 0 || carbs > 0 || fats > 0)) {
                    calories = (protein * 4) + (carbs * 4) + (fats * 9);
                }

                System.out.println("DAO: Fetched macros from DB: Cals=" + calories + ", Prot=" + protein + ", Carbs=" + carbs + ", Fats=" + fats + ", Fib=" + fiber);

                Map<String, Double> nutrients = new HashMap<>();
                nutrients.put("ENERGY (KILOCALORIES)", calories);
                nutrients.put("PROTEIN", protein);
                nutrients.put("CARBOHYDRATE, TOTAL (BY DIFFERENCE)", carbs);
                nutrients.put("FAT (TOTAL LIPIDS)", fats);
                nutrients.put("FIBRE, TOTAL DIETARY", fiber);

                food = new Food(
                    rs.getInt("FoodID"),
                    rs.getString("FoodDescription"),
                    calories,
                    protein,
                    carbs,
                    fats,
                    fiber,
                    rs.getString("FoodGroupName"),
                    rs.getString("FoodSourceDescription"),
                    new HashMap<>()
                );
                food.setNutrients(nutrients);
                System.out.println("DAO: Successfully created Food object for: " + food.getFoodDescription());
            } else {
                System.out.println("DAO: No food found for foodId: " + foodId);
            }
        } catch (SQLException e) {
            System.err.println("DAO: Error getting main food details by ID: " + e.getMessage());
            return null;
        }

        if (food != null) {
            String allNutrientsSql = """
                SELECT nn.NutrientName, na.NutrientValue
                FROM nutrient_amount na
                JOIN nutrient_name nn ON na.NutrientNameID = nn.NutrientNameID
                WHERE na.FoodID = ?
                """;
            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(allNutrientsSql)) {
                
                pstmt.setInt(1, foodId);
                ResultSet rs = pstmt.executeQuery();
                
                Map<String, Double> allNutrients = food.getNutrients();
                while (rs.next()) {
                    allNutrients.put(rs.getString("NutrientName"), rs.getDouble("NutrientValue"));
                }
                food.setNutrients(allNutrients);
                System.out.println("DAO: Populated full nutrient map with " + allNutrients.size() + " items.");

            } catch (SQLException e) {
                System.err.println("DAO: Error getting all nutrients for food ID " + foodId + ": " + e.getMessage());
            }
        }
        
        return food;
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
                double calories = rs.getDouble("calories");
                double protein = rs.getDouble("protein");
                double carbs = rs.getDouble("carbs");
                double fats = rs.getDouble("fats");
                double fiber = rs.getDouble("fiber");

                if (calories == 0 && (protein > 0 || carbs > 0 || fats > 0)) {
                    calories = (protein * 4) + (carbs * 4) + (fats * 9);
                }

                Food food = new Food(
                    rs.getInt("FoodID"),
                    rs.getString("FoodDescription"),
                    calories,
                    protein,
                    carbs,
                    fats,
                    fiber
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
                double calories = rs.getDouble("calories");
                double protein = rs.getDouble("protein");
                double carbs = rs.getDouble("carbs");
                double fats = rs.getDouble("fats");
                double fiber = rs.getDouble("fiber");

                if (calories == 0 && (protein > 0 || carbs > 0 || fats > 0)) {
                    calories = (protein * 4) + (carbs * 4) + (fats * 9);
                }

                Food food = new Food(
                    rs.getInt("FoodID"),
                    rs.getString("FoodDescription"),
                    calories,
                    protein,
                    carbs,
                    fats,
                    fiber
                );
                foods.add(food);
            }
        } catch (SQLException e) {
            System.err.println("Error finding foods by nutrient range: " + e.getMessage());
        }
        return foods;
    }

    public String getFoodGroupById(int foodId) {
        String sql = "SELECT fg.FoodGroupName " +
                     "FROM food_name fn " +
                     "JOIN food_group fg ON fn.FoodGroupID = fg.FoodGroupID " +
                     "WHERE fn.FoodID = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, foodId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("FoodGroupName");
            }
        } catch (SQLException e) {
            System.err.println("Error getting food group by ID: " + e.getMessage());
        }
        return "Unknown";
    }
	
	public Map<String, Integer> getMeasuresForFood(int foodId) {
        Map<String, Integer> measures = new HashMap<>();
        String sql = "SELECT mn.MeasureName, mn.MeasureID FROM measure_name mn " +
                     "JOIN conversion_factor cf ON mn.MeasureID = cf.MeasureID " +
                     "WHERE cf.FoodID = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, foodId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                measures.put(rs.getString("MeasureName"), rs.getInt("MeasureID"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting measures for food: " + e.getMessage());
        }
        return measures;
    }

    public double getConversionFactor(int foodId, int measureId) {
        String sql = "SELECT ConversionFactorValue FROM conversion_factor WHERE FoodID = ? AND MeasureID = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, foodId);
            pstmt.setInt(2, measureId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("ConversionFactorValue");
            }
        } catch (SQLException e) {
            System.err.println("Error getting conversion factor: " + e.getMessage());
        }
        return 1.0; // Default to 1.0 if not found
    }
}

