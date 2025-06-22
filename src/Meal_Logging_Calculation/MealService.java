package Meal_Logging_Calculation;


import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import DatabaseConnector.DatabaseConnector;


public class MealService {
public Meal logMeal(Meal meal) {
 String mealSql = "INSERT INTO meals (user_id, meal_type, meal_date) VALUES (?, ?, ?)";
 String itemSql = "INSERT INTO meal_items (meal_id, food_id, quantity, unit) VALUES (?, ?, ?, ?)";
 
 try (Connection conn = DatabaseConnector.getInstance().getConnection();
      PreparedStatement mealStmt = conn.prepareStatement(mealSql, Statement.RETURN_GENERATED_KEYS);
      PreparedStatement itemStmt = conn.prepareStatement(itemSql)) {
     
     mealStmt.setInt(1, meal.getUserId());
     mealStmt.setString(2, meal.getMealType());
     mealStmt.setDate(3, Date.valueOf(meal.getMealDate()));
     
     int affectedRows = mealStmt.executeUpdate();
     
     if (affectedRows > 0) {
         try (ResultSet rs = mealStmt.getGeneratedKeys()) {
             if (rs.next()) {
                 int mealId = rs.getInt(1);
                 meal.setMealId(mealId);
                 
                 // Insert meal items
                 for (FoodItem item : meal.getFoodItems()) {
                     itemStmt.setInt(1, mealId);
                     itemStmt.setInt(2, item.getFoodId());
                     itemStmt.setDouble(3, item.getQuantity());
                     itemStmt.setString(4, item.getUnit());
                     itemStmt.addBatch();
                 }
                 
                 itemStmt.executeBatch();
                 return meal;
             }
         }
     }
 } catch (SQLException e) {
     e.printStackTrace();
 }
 return null;
}

public List<Meal> getMealsByUserAndDateRange(int userId, LocalDate startDate, LocalDate endDate) {
 List<Meal> meals = new ArrayList<>();
 String sql = "SELECT m.*, mi.*, fn.* FROM meals m " +
              "JOIN meal_items mi ON m.meal_id = mi.meal_id " +
              "JOIN food_nutrients fn ON mi.food_id = fn.food_id " +
              "WHERE m.user_id = ? AND m.meal_date BETWEEN ? AND ? " +
              "ORDER BY m.meal_date, m.meal_type";
 
 try (Connection conn = DatabaseConnector.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql)) {
     
     pstmt.setInt(1, userId);
     pstmt.setDate(2, Date.valueOf(startDate));
     pstmt.setDate(3, Date.valueOf(endDate));
     
     ResultSet rs = pstmt.executeQuery();
     Meal currentMeal = null;
     
     while (rs.next()) {
         int mealId = rs.getInt("m.meal_id");
         
         if (currentMeal == null || currentMeal.getMealId() != mealId) {
             if (currentMeal != null) {
                 meals.add(currentMeal);
             }
             
             currentMeal = new Meal();
             currentMeal.setMealId(mealId);
             currentMeal.setUserId(rs.getInt("m.user_id"));
             currentMeal.setMealType(rs.getString("m.meal_type"));
             currentMeal.setMealDate(rs.getDate("m.meal_date").toLocalDate());
         }
         
         FoodItem item = new FoodItem();
         item.setFoodId(rs.getInt("mi.food_id"));
         item.setDescription(rs.getString("fn.description"));
         item.setFoodGroup(rs.getString("fn.food_group"));
         item.setQuantity(rs.getDouble("mi.quantity"));
         item.setUnit(rs.getString("mi.unit"));
         item.setCalories(rs.getDouble("fn.calories"));
         item.setProtein(rs.getDouble("fn.protein"));
         item.setCarbs(rs.getDouble("fn.carbs"));
         item.setFats(rs.getDouble("fn.fats"));
         item.setFiber(rs.getDouble("fn.fiber"));
         
         currentMeal.addFoodItem(item);
     }
     
     if (currentMeal != null) {
         meals.add(currentMeal);
     }
 } catch (SQLException e) {
     e.printStackTrace();
 }
 
 return meals;
}

public FoodItem findFoodItem(String searchTerm) {
 String sql = "SELECT * FROM food_nutrients WHERE description LIKE ? LIMIT 1";
 
 try (Connection conn = DatabaseConnector.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement(sql)) {
     
     pstmt.setString(1, "%" + searchTerm + "%");
     ResultSet rs = pstmt.executeQuery();
     
     if (rs.next()) {
         FoodItem item = new FoodItem();
         item.setFoodId(rs.getInt("food_id"));
         item.setDescription(rs.getString("description"));
         item.setFoodGroup(rs.getString("food_group"));
         item.setCalories(rs.getDouble("calories"));
         item.setProtein(rs.getDouble("protein"));
         item.setCarbs(rs.getDouble("carbs"));
         item.setFats(rs.getDouble("fats"));
         item.setFiber(rs.getDouble("fiber"));
         return item;
     }
 } catch (SQLException e) {
     e.printStackTrace();
 }
 return null;
}
}

