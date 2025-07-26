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
    private double sodium;
    private double sugars;
    private double saturatedFats;
    private double iron;
    private double calcium;
    private double vitaminA;
    private double vitaminB;
    private double vitaminC;
    private double vitaminD;

    public MealItem(int itemId, int mealId, int foodId, double quantity, String unit, double calories, double protein, double carbs, double fats, double fiber, double sodium, double sugars, double saturatedFats, double iron, double calcium, double vitaminA, double vitaminB, double vitaminC, double vitaminD) {
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
        this.sodium = sodium;
        this.sugars = sugars;
        this.saturatedFats = saturatedFats;
        this.iron = iron;
        this.calcium = calcium;
        this.vitaminA = vitaminA;
        this.vitaminB = vitaminB;
        this.vitaminC = vitaminC;
        this.vitaminD = vitaminD;
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
}