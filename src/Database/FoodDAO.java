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
        String sql = "SELECT FoodID, FoodDescription, calories, protein, carbs, fats, fiber FROM food_name fn JOIN food_nutrients fnu ON fn.FoodID = fnu.food_id WHERE FoodDescription LIKE ?";
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
        }
        return foods;
    }
}