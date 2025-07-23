package models;

public class MealItem {
    private int itemId;
    private int mealId;
    private int foodId;
    private double quantity;
    private String unit;
    private double calories;
    private double protein;
    private double carbs;
    private double fats;
    private double fiber;

    public MealItem(int itemId, int mealId, int foodId, double quantity, String unit, double calories, double protein, double carbs, double fats, double fiber) {
        this.itemId = itemId;
        this.mealId = mealId;
        this.foodId = foodId;
        this.quantity = quantity;
        this.unit = unit;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
        this.fiber = fiber;
    }

    // Getters
    public int getItemId() {
        return itemId;
    }

    public int getMealId() {
        return mealId;
    }

    public int getFoodId() {
        return foodId;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
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

    // Setters
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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