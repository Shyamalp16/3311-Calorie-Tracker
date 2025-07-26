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
    private double sodium;
    private double sugars;
    private double saturatedFats;
    private double iron;
    private double calcium;
    private double vitaminA;
    private double vitaminB;
    private double vitaminC;
    private double vitaminD;
    private String foodGroup;
    private String foodSource;
    private Map<String, Double> nutrients;

    public Food(int foodID, String foodDescription, double calories, double protein, double carbs, double fats, double fiber, double sodium, double sugars, double saturatedFats, double iron, double calcium, double vitaminA, double vitaminB, double vitaminC, double vitaminD) {
        this(foodID, foodDescription, calories, protein, carbs, fats, fiber, sodium, sugars, saturatedFats, iron, calcium, vitaminA, vitaminB, vitaminC, vitaminD, "Unknown", "Unknown", new HashMap<>());
    }

    public Food(int foodID, String foodDescription, double calories, double protein, double carbs, double fats, double fiber, double sodium, double sugars, double saturatedFats, double iron, double calcium, double vitaminA, double vitaminB, double vitaminC, double vitaminD, String foodGroup, String foodSource, Map<String, Double> nutrients) {
        this.foodID = foodID;
        this.foodDescription = foodDescription;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
        this.fiber = fiber;
        this.sodium = sodium;
        this.sugars = sugars;
        this.saturatedFats = saturatedFats;
        this.iron = iron;
        this.calcium = calcium;
        this.vitaminA = vitaminA;
        this.vitaminB = vitaminB;
        this.vitaminC = vitaminC;
        this.vitaminD = vitaminD;
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

    public double getSodium() {
        return sodium;
    }

    public double getSugars() {
        return sugars;
    }

    public double getSaturatedFats() {
        return saturatedFats;
    }

    public double getIron() {
        return iron;
    }

    public double getCalcium() {
        return calcium;
    }

    public double getVitaminA() {
        return vitaminA;
    }

    public double getVitaminB() {
        return vitaminB;
    }

    public double getVitaminC() {
        return vitaminC;
    }

    public double getVitaminD() {
        return vitaminD;
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

    public void setSodium(double sodium) {
        this.sodium = sodium;
    }

    public void setSugars(double sugars) {
        this.sugars = sugars;
    }

    public void setSaturatedFats(double saturatedFats) {
        this.saturatedFats = saturatedFats;
    }

    public void setIron(double iron) {
        this.iron = iron;
    }

    public void setCalcium(double calcium) {
        this.calcium = calcium;
    }

    public void setVitaminA(double vitaminA) {
        this.vitaminA = vitaminA;
    }

    public void setVitaminB(double vitaminB) {
        this.vitaminB = vitaminB;
    }

    public void setVitaminC(double vitaminC) {
        this.vitaminC = vitaminC;
    }

    public void setVitaminD(double vitaminD) {
        this.vitaminD = vitaminD;
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
