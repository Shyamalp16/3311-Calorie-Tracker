package Meal_Logging_Calculation;


public class FoodItem {
 private int foodId;
 private String description;
 private String foodGroup;
 private double quantity;
 private String unit;
 
 // Nutritional values per 100g
 private double calories;
 private double protein;
 private double carbs;
 private double fats;
 private double fiber;
 
 public int getFoodId() { return foodId; }
 public void setFoodId(int foodId) { this.foodId = foodId; }
 public String getDescription() { return description; }
 public void setDescription(String description) { this.description = description; }
 public String getFoodGroup() { return foodGroup; }
 public void setFoodGroup(String foodGroup) { this.foodGroup = foodGroup; }
 public double getQuantity() { return quantity; }
 public void setQuantity(double quantity) { this.quantity = quantity; }
 public String getUnit() { return unit; }
 public void setUnit(String unit) { this.unit = unit; }
 public double getCalories() { return calories; }
 public void setCalories(double calories) { this.calories = calories; }
 public double getProtein() { return protein; }
 public void setProtein(double protein) { this.protein = protein; }
 public double getCarbs() { return carbs; }
 public void setCarbs(double carbs) { this.carbs = carbs; }
 public double getFats() { return fats; }
 public void setFats(double fats) { this.fats = fats; }
 public double getFiber() { return fiber; }
 public void setFiber(double fiber) { this.fiber = fiber; }
 
 public double getCalculatedCalories() {
     return (calories / 100) * quantity;
 }
 
 public double getCalculatedProtein() {
     return (protein / 100) * quantity;
 }
 
 public double getCalculatedCarbs() {
     return (carbs / 100) * quantity;
 }
 
 public double getCalculatedFats() {
     return (fats / 100) * quantity;
 }
 
 public double getCalculatedFiber() {
     return (fiber / 100) * quantity;
 }
}

