package Food_Swap_Engine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import DatabaseConnector.DatabaseConnector;
import Meal_Logging_Calculation.FoodItem;
import Meal_Logging_Calculation.Meal;


public class SwapRecommendation {
 public List<FoodItem> findPotentialSwaps(FoodItem originalItem, SwapCriteria criteria) {
     List<FoodItem> potentialSwaps = new ArrayList<>();
     String sql = "SELECT * FROM food_nutrients WHERE food_group = ? AND food_id != ?";
     
     try (Connection conn = DatabaseConnector.getInstance().getConnection();
          PreparedStatement pstmt = conn.prepareStatement(sql)) {
         
         pstmt.setString(1, originalItem.getFoodGroup());
         pstmt.setInt(2, originalItem.getFoodId());
         
         ResultSet rs = pstmt.executeQuery();
         
         while (rs.next()) {
             FoodItem item = new FoodItem();
             item.setFoodId(rs.getInt("food_id"));
             item.setDescription(rs.getString("description"));
             item.setFoodGroup(rs.getString("food_group"));
             item.setCalories(rs.getDouble("calories"));
             item.setProtein(rs.getDouble("protein"));
             item.setCarbs(rs.getDouble("carbs"));
             item.setFats(rs.getDouble("fats"));
             item.setFiber(rs.getDouble("fiber"));
             
             // Check if this item meets the swap criteria
             if (meetsSwapCriteria(originalItem, item, criteria)) {
                 potentialSwaps.add(item);
             }
         }
     } catch (SQLException e) {
         e.printStackTrace();
     }
     
     return potentialSwaps;
 }
 
 private boolean meetsSwapCriteria(FoodItem original, FoodItem potential, SwapCriteria criteria) {
     // Calculate differences per 100g
     double calDiff = potential.getCalories() - original.getCalories();
     double fiberDiff = potential.getFiber() - original.getFiber();
     double proteinDiff = potential.getProtein() - original.getProtein();
     double carbDiff = potential.getCarbs() - original.getCarbs();
     
     // Check primary goal
     boolean meetsPrimary = false;
     switch (criteria.getPrimaryGoal()) {
         case INCREASE_FIBER:
             meetsPrimary = fiberDiff >= criteria.getIntensity();
             break;
         case REDUCE_CALORIES:
             meetsPrimary = calDiff <= -criteria.getIntensity();
             break;
         case INCREASE_PROTEIN:
             meetsPrimary = proteinDiff >= criteria.getIntensity();
             break;
         case REDUCE_CARBS:
             meetsPrimary = carbDiff <= -criteria.getIntensity();
             break;
     }
     
     // Check secondary goal if exists
     if (criteria.getSecondaryGoal() == null) {
         return meetsPrimary;
     }
     
     boolean meetsSecondary = false;
     switch (criteria.getSecondaryGoal()) {
         case INCREASE_FIBER:
             meetsSecondary = fiberDiff >= 0;
             break;
         case REDUCE_CALORIES:
             meetsSecondary = calDiff <= 0;
             break;
         case INCREASE_PROTEIN:
             meetsSecondary = proteinDiff >= 0;
             break;
         case REDUCE_CARBS:
             meetsSecondary = carbDiff <= 0;
             break;
     }
     
     return meetsPrimary && meetsSecondary;
 }
 
 public Meal applySwapToMeal(Meal originalMeal, FoodItem originalItem, FoodItem newItem) {
     Meal newMeal = new Meal();
     newMeal.setUserId(originalMeal.getUserId());
     newMeal.setMealType(originalMeal.getMealType());
     newMeal.setMealDate(originalMeal.getMealDate());
     
     for (FoodItem item : originalMeal.getFoodItems()) {
         if (item.getFoodId() == originalItem.getFoodId()) {
             // Replace with new item
             FoodItem swappedItem = new FoodItem();
             swappedItem.setFoodId(newItem.getFoodId());
             swappedItem.setDescription(newItem.getDescription());
             swappedItem.setFoodGroup(newItem.getFoodGroup());
             swappedItem.setQuantity(item.getQuantity());
             swappedItem.setUnit(item.getUnit());
             swappedItem.setCalories(newItem.getCalories());
             swappedItem.setProtein(newItem.getProtein());
             swappedItem.setCarbs(newItem.getCarbs());
             swappedItem.setFats(newItem.getFats());
             swappedItem.setFiber(newItem.getFiber());
             
             newMeal.addFoodItem(swappedItem);
         } else {
             // Keep original item
             newMeal.addFoodItem(item);
         }
     }
     
     return newMeal;
 }
}