package controller;

import models.*;
import Database.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;

/**
 * Controller for meal logging operations
 * Handles business logic for food search, meal creation, and meal management
 */
public class MealController implements IMealController {
    
    private User currentUser;
    private UserSettings userSettings;
    
    private MealDAO mealDAO;
    private FoodDAO foodDAO;
    private UserSettingsDAO settingsDAO;
    
    public MealController(User user) {
        this.currentUser = user;
        this.mealDAO = new MealDAO();
        this.foodDAO = new FoodDAO();
        this.settingsDAO = new UserSettingsDAO();
        this.userSettings = settingsDAO.getOrCreateDefaultSettings(user.getUserId());
    }
    
    /**
     * Search for foods by description
     */
    public List<Food> searchFoods(String query) {
        return foodDAO.searchFoodByName(query);
    }
    
    /**
     * Get food details by ID
     */
    public Food getFoodById(int foodId) {
        return foodDAO.getFoodById(foodId).orElse(null);
    }
    
    /**
     * Get available measures for a food
     */
    public Map<String, Integer> getFoodMeasures(int foodId) {
        return foodDAO.getMeasuresForFood(foodId);
    }
    
    /**
     * Get food group by food ID
     */
    public String getFoodGroupById(int foodId) {
        return foodDAO.getFoodGroupById(foodId);
    }
    
    /**
     * Calculate nutrition values for a meal item
     */
    public MealItem calculateMealItem(Food food, double quantity, String unit) {
        double conversionFactor = getConversionFactor(food.getFoodID(), unit);
        
        // Calculate all nutrition values
        double calories = food.getCalories() * quantity * conversionFactor;
        double protein = food.getProtein() * quantity * conversionFactor;
        double carbs = food.getCarbs() * quantity * conversionFactor;
        double fats = food.getFats() * quantity * conversionFactor;
        double fiber = food.getFiber() * quantity * conversionFactor;
        double sodium = food.getSodium() * quantity * conversionFactor;
        double sugars = food.getSugars() * quantity * conversionFactor;
        double saturatedFats = food.getSaturatedFats() * quantity * conversionFactor;
        double iron = food.getIron() * quantity * conversionFactor;
        double calcium = food.getCalcium() * quantity * conversionFactor;
        double vitaminA = food.getVitaminA() * quantity * conversionFactor;
        double vitaminB = food.getVitaminB() * quantity * conversionFactor;
        double vitaminC = food.getVitaminC() * quantity * conversionFactor;
        double vitaminD = food.getVitaminD() * quantity * conversionFactor;
        
        return new MealItem.Builder()
            .foodId(food.getFoodID())
            .quantity(quantity)
            .unit(unit)
            .calories(calories)
            .protein(protein)
            .carbs(carbs)
            .fats(fats)
            .fiber(fiber)
            .sodium(sodium)
            .sugars(sugars)
            .saturatedFats(saturatedFats)
            .iron(iron)
            .calcium(calcium)
            .vitaminA(vitaminA)
            .vitaminB(vitaminB)
            .vitaminC(vitaminC)
            .vitaminD(vitaminD)
            .build();
    }
    
    /**
     * Get conversion factor for a food and unit
     */
    private double getConversionFactor(int foodId, String unit) {
        Map<String, Integer> measures = getFoodMeasures(foodId);
        
        if (measures.containsKey(unit)) {
            int measureId = measures.get(unit);
            return foodDAO.getConversionFactor(foodId, measureId);
        } else if (unit.equals("g")) {
            return 1.0 / 100.0;
        } else {
            return 1.0;
        }
    }
    
    /**
     * Save a meal with validation
     */
    public SaveMealResult saveMeal(String mealType, Date mealDate, List<MealItem> mealItems) {
        try {
            if (isDuplicateMeal(mealType, mealDate)) {
                return new SaveMealResult(false, "You have already logged a " + mealType + " for this date.");
            }
            
            if (mealItems.isEmpty()) {
                return new SaveMealResult(false, "Cannot save empty meal.");
            }
            
            MealTotals totals = calculateMealTotals(mealItems);
            
            Meal meal = new Meal.Builder()
                .userId(currentUser.getUserId())
                .mealType(mealType)
                .mealDate(new java.sql.Date(mealDate.getTime()))
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .totalCalories(totals.calories)
                .totalProtein(totals.protein)
                .totalCarbs(totals.carbs)
                .totalFats(totals.fats)
                .totalFiber(totals.fiber)
                .totalSodium(totals.sodium)
                .totalSugars(totals.sugars)
                .totalSaturatedFats(totals.saturatedFats)
                .totalIron(totals.iron)
                .totalCalcium(totals.calcium)
                .totalVitaminA(totals.vitaminA)
                .totalVitaminB(totals.vitaminB)
                .totalVitaminC(totals.vitaminC)
                .totalVitaminD(totals.vitaminD)
                .build();
            
            int mealId = mealDAO.saveMeal(meal);
            if (mealId != -1) {
                for (MealItem item : mealItems) {
                    item.setMealId(mealId);
                }
                mealDAO.saveMealItems(mealId, mealItems);
                return new SaveMealResult(true, "Meal saved successfully!");
            } else {
                return new SaveMealResult(false, "Failed to save meal to database.");
            }
            
        } catch (Exception e) {
            return new SaveMealResult(false, "Error saving meal: " + e.getMessage());
        }
    }
    
    /**
     * Check if meal type already exists for date
     * Snacks are unlimited, main meals (Breakfast, Lunch, Dinner) are limited to 1 per day
     */
    private boolean isDuplicateMeal(String mealType, Date mealDate) {
        if ("Snack".equalsIgnoreCase(mealType)) {
            return false;
        }
        
        List<Meal> existingMeals = mealDAO.getMealsForUserAndDate(
            currentUser.getUserId(), new java.sql.Date(mealDate.getTime()));
        
        return existingMeals.stream()
                           .anyMatch(meal -> meal.getMealType().equals(mealType));
    }
    
    /**
     * Calculate totals for a list of meal items
     */
    private MealTotals calculateMealTotals(List<MealItem> mealItems) {
        return new MealTotals(mealItems);
    }
    
    /**
     * Get meals for a specific date
     */
    public List<Meal> getMealsForDate(Date date) {
        return mealDAO.getMealsForUserAndDate(currentUser.getUserId(), new java.sql.Date(date.getTime()));
    }

    public Meal getMealById(int mealId) {
        return mealDAO.getMealById(mealId);
    }

    public List<MealItem> getMealItemsByMealId(int mealId) {
        return mealDAO.getMealItemsByMealId(mealId);
    }

    public List<MealItem> getMealItemsForDate(Date date) {
        List<Meal> meals = getMealsForDate(date);
        List<MealItem> mealItems = new java.util.ArrayList<>();
        for (Meal meal : meals) {
            mealItems.addAll(getMealItemsByMealId(meal.getMealId()));
        }
        return mealItems;
    }
    
    public UserSettings getUserSettings() { return userSettings; }
    
    /**
     * Data class for meal totals
     */
    private static class MealTotals {
        final double calories, protein, carbs, fats, fiber, sodium, sugars, saturatedFats;
        final double iron, calcium, vitaminA, vitaminB, vitaminC, vitaminD;
        
        MealTotals(List<MealItem> mealItems) {
            this.calories = mealItems.stream().mapToDouble(MealItem::getCalories).sum();
            this.protein = mealItems.stream().mapToDouble(MealItem::getProtein).sum();
            this.carbs = mealItems.stream().mapToDouble(MealItem::getCarbs).sum();
            this.fats = mealItems.stream().mapToDouble(MealItem::getFats).sum();
            this.fiber = mealItems.stream().mapToDouble(MealItem::getFiber).sum();
            this.sodium = mealItems.stream().mapToDouble(MealItem::getSodium).sum();
            this.sugars = mealItems.stream().mapToDouble(MealItem::getSugars).sum();
            this.saturatedFats = mealItems.stream().mapToDouble(MealItem::getSaturatedFats).sum();
            this.iron = mealItems.stream().mapToDouble(MealItem::getIron).sum();
            this.calcium = mealItems.stream().mapToDouble(MealItem::getCalcium).sum();
            this.vitaminA = mealItems.stream().mapToDouble(MealItem::getVitaminA).sum();
            this.vitaminB = mealItems.stream().mapToDouble(MealItem::getVitaminB).sum();
            this.vitaminC = mealItems.stream().mapToDouble(MealItem::getVitaminC).sum();
            this.vitaminD = mealItems.stream().mapToDouble(MealItem::getVitaminD).sum();
        }
    }
    
    /**
     * Data class for save meal result
     */
    public static class SaveMealResult {
        public final boolean success;
        public final String message;
        
        public SaveMealResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}