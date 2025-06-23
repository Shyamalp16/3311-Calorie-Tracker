package Meal_Logging_Calculation;


import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Meal {
private int mealId;
private int userId;
private String mealType; // breakfast, lunch, dinner, snack
private LocalDate mealDate;
private List<FoodItem> foodItems;

public Meal() {
   this.foodItems = new ArrayList<>();
}

// Getters and setters
public int getMealId() { return mealId; }
public void setMealId(int mealId) { this.mealId = mealId; }
public int getUserId() { return userId; }
public void setUserId(int userId) { this.userId = userId; }
public String getMealType() { return mealType; }
public void setMealType(String mealType) { this.mealType = mealType; }
public LocalDate getMealDate() { return mealDate; }
public void setMealDate(LocalDate mealDate) { this.mealDate = mealDate; }
public List<FoodItem> getFoodItems() { return foodItems; }
public void setFoodItems(List<FoodItem> foodItems) { this.foodItems = foodItems; }

public void addFoodItem(FoodItem item) {
   foodItems.add(item);
}

// Calculate total nutritional values for the meal
public double getTotalCalories() {
   return foodItems.stream().mapToDouble(FoodItem::getCalculatedCalories).sum();
}

public double getTotalProtein() {
   return foodItems.stream().mapToDouble(FoodItem::getCalculatedProtein).sum();
}

public double getTotalCarbs() {
   return foodItems.stream().mapToDouble(FoodItem::getCalculatedCarbs).sum();
}

public double getTotalFats() {
   return foodItems.stream().mapToDouble(FoodItem::getCalculatedFats).sum();
}

public double getTotalFiber() {
   return foodItems.stream().mapToDouble(FoodItem::getCalculatedFiber).sum();
}

// Get food group distribution
public String getFoodGroupDistribution() {
   // Implementation would analyze food items and return distribution
   return ""; // Placeholder
}

}

