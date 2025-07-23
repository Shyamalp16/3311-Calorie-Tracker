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
        String sql = "SELECT FoodID, FoodDescription FROM food_name WHERE FoodDescription LIKE ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, searchTerm + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // Generate random values between 1 and 50 for nutrients
                double calories = random.nextInt(50) + 1;
                double protein = random.nextInt(50) + 1;
                double carbs = random.nextInt(50) + 1;
                double fats = random.nextInt(50) + 1;
                double fiber = random.nextInt(50) + 1;

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
}