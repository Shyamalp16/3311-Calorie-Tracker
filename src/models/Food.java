package models;

public class Food {
    private int foodID;
    private String foodDescription;
    private double calories;
    private double protein;
    private double carbs;
    private double fats;
    private double fiber;

    public Food(int foodID, String foodDescription, double calories, double protein, double carbs, double fats, double fiber) {
        this.foodID = foodID;
        this.foodDescription = foodDescription;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
        this.fiber = fiber;
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

    // Setters (if needed, though often not for immutable data models)
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
}