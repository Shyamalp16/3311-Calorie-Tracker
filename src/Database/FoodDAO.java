package Database;

import models.Food;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FoodDAO {

    public List<Food> searchFoodByName(String searchTerm) {
        List<Food> foods = new ArrayList<>();
        String sql = """
            SELECT DISTINCT fn.FoodID, fn.FoodDescription,
                   COALESCE(calories.NutrientValue, 0) as calories,
                   COALESCE(protein.NutrientValue, 0) as protein,
                   COALESCE(carbs.NutrientValue, 0) as carbs,
                   COALESCE(fats.NutrientValue, 0) as fats,
                   COALESCE(fiber.NutrientValue, 0) as fiber
            FROM food_name fn
            LEFT JOIN nutrient_amount calories ON fn.FoodID = calories.FoodID 
                AND calories.NutrientNameID = (SELECT NutrientNameID FROM nutrient_name WHERE NutrientSymbol = 'KCAL')
            LEFT JOIN nutrient_amount protein ON fn.FoodID = protein.FoodID 
                AND protein.NutrientNameID = (SELECT NutrientNameID FROM nutrient_name WHERE NutrientSymbol = 'PROT')
            LEFT JOIN nutrient_amount carbs ON fn.FoodID = carbs.FoodID 
                AND carbs.NutrientNameID = (SELECT NutrientNameID FROM nutrient_name WHERE NutrientSymbol = 'CARB')
            LEFT JOIN nutrient_amount fats ON fn.FoodID = fats.FoodID 
                AND fats.NutrientNameID = (SELECT NutrientNameID FROM nutrient_name WHERE NutrientSymbol = 'FAT')
            LEFT JOIN nutrient_amount fiber ON fn.FoodID = fiber.FoodID 
                AND fiber.NutrientNameID = (SELECT NutrientNameID FROM nutrient_name WHERE NutrientName LIKE '%FIBER%')
            WHERE fn.FoodDescription LIKE ?
            LIMIT 50
            """;
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + searchTerm + "%");
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
            e.printStackTrace();
        }
        return foods;
    }
}