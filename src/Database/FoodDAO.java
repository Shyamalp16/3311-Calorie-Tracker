package Database;

import models.Food;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FoodDAO extends AbstractDAO<Food> {

    public List<Food> searchFoodByName(String searchTerm) {
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
        return findMany(sql, searchTerm + "%");
    }

    public Optional<Food> getFoodById(int foodId) {
        String sql = """
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
        return findOne(sql, foodId);
    }

    public List<Food> findSimilarFoodsByGroup(int originalFoodId, int limitCount) {
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
        return findMany(sql, originalFoodId, originalFoodId, limitCount);
    }

    public List<Food> findFoodsByNutrientRange(String nutrientType, double minValue, double maxValue, int limitCount) {
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
        return findMany(sql, nutrientId, minValue, maxValue, limitCount);
    }

    public String getFoodGroupById(int foodId) {
        String sql = "SELECT fg.FoodGroupName FROM food_name fn JOIN food_group fg ON fn.FoodGroupID = fg.FoodGroupID WHERE fn.FoodID = ?";
        try (var conn = DatabaseConnector.getConnection(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, foodId);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("FoodGroupName");
            }
        } catch (SQLException e) {
            handleSQLException(e, sql);
        }
        return "Unknown";
    }

    public Map<String, Integer> getMeasuresForFood(int foodId) {
        Map<String, Integer> measures = new HashMap<>();
        String sql = "SELECT mn.MeasureName, mn.MeasureID FROM measure_name mn JOIN conversion_factor cf ON mn.MeasureID = cf.MeasureID WHERE cf.FoodID = ?";
        try (var conn = DatabaseConnector.getConnection(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, foodId);
            var rs = pstmt.executeQuery();
            while (rs.next()) {
                measures.put(rs.getString("MeasureName"), rs.getInt("MeasureID"));
            }
        } catch (SQLException e) {
            handleSQLException(e, sql);
        }
        return measures;
    }

    public double getConversionFactor(int foodId, int measureId) {
        String sql = "SELECT ConversionFactorValue FROM conversion_factor WHERE FoodID = ? AND MeasureID = ?";
        try (var conn = DatabaseConnector.getConnection(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, foodId);
            pstmt.setInt(2, measureId);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("ConversionFactorValue");
            }
        } catch (SQLException e) {
            handleSQLException(e, sql);
        }
        return 1.0;
    }

    @Override
    protected void setParameters(PreparedStatement pstmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
    }

    @Override
    protected Food parseResultSet(ResultSet rs) throws SQLException {
        double calories = rs.getDouble("calories");
        double protein = rs.getDouble("protein");
        double carbs = rs.getDouble("carbs");
        double fats = rs.getDouble("fats");

        if (calories == 0 && (protein > 0 || carbs > 0 || fats > 0)) {
            calories = (protein * 4) + (carbs * 4) + (fats * 9);
        }

        return new Food(
            rs.getInt("FoodID"),
            rs.getString("FoodDescription"),
            calories,
            protein,
            carbs,
            fats,
            rs.getDouble("fiber")
        );
    }
}