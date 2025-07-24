package models;

import java.util.Map;
import java.util.HashMap;

public class Food {
    private int foodID;
    private String foodDescription;
    private double calories;
    private double protein;
    private double carbs;
    private double fats;
    private double fiber;
    private String foodGroup;
    private String foodSource;
    private Map<String, Double> nutrients;

    public Food(int foodID, String foodDescription, double calories, double protein, double carbs, double fats, double fiber) {
        this(foodID, foodDescription, calories, protein, carbs, fats, fiber, "Unknown", "Unknown", new HashMap<>());
    }

    public Food(int foodID, String foodDescription, double calories, double protein, double carbs, double fats, double fiber, String foodGroup, String foodSource, Map<String, Double> nutrients) {
        this.foodID = foodID;
        this.foodDescription = foodDescription;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
        this.fiber = fiber;
        this.foodGroup = foodGroup;
        this.foodSource = foodSource;
        this.nutrients = nutrients;
    }

    // Getters
    public int getFoodID() {
        return foodID;
    }

    public String getFoodDescription() {
        return foodDescription;
    }

    public double getCalories() {
        return calories;
    }

    public double getProtein() {
        return protein;
    }

    public double getCarbs() {
        return carbs;
    }

    public double getFats() {
        return fats;
    }

    public double getFiber() {
        return fiber;
    }

    public String getFoodGroup() {
        return foodGroup;
    }

    public String getFoodSource() {
        return foodSource;
    }

    public Map<String, Double> getNutrients() {
        return nutrients;
    }

    public double getNutrientValue(String nutrientName) {
        return nutrients.getOrDefault(nutrientName, 0.0);
    }

    // Setters
    public void setFoodID(int foodID) {
        this.foodID = foodID;
    }

    public void setFoodDescription(String foodDescription) {
        this.foodDescription = foodDescription;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public void setFats(double fats) {
        this.fats = fats;
    }

    public void setFiber(double fiber) {
        this.fiber = fiber;
    }

    public void setFoodGroup(String foodGroup) {
        this.foodGroup = foodGroup;
    }

    public void setFoodSource(String foodSource) {
        this.foodSource = foodSource;
    }

    public void setNutrients(Map<String, Double> nutrients) {
        this.nutrients = nutrients;
    }
}
