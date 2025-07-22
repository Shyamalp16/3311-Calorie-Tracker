package models;

public class MealItem {
    private int itemId;
    private int mealId;
    private int foodId;
    private double quantity;
    private String unit;

    public MealItem(int itemId, int mealId, int foodId, double quantity, String unit) {
        this.itemId = itemId;
        this.mealId = mealId;
        this.foodId = foodId;
        this.quantity = quantity;
        this.unit = unit;
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
}