package logic.facade;

import controller.DashboardController.NutritionAnalysisData;
import controller.DashboardController.NutritionSummary;
import controller.FoodSwapController;
import controller.GoalsController.GoalsForDisplay;
import controller.MealController.SaveMealResult;
import models.Food;
import models.MealItem;
import models.FoodSwapGoal;
import models.Meal;
import models.UserSettings;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Facade Interface to provide a simplified, unified interface to the business logic subsystems.
 * This is the single entry point for the UI layer.
 */
public interface INutritionFacade {

    // Methods from MealController
    SaveMealResult saveMeal(String mealType, Date mealDate, List<MealItem> items);
    List<Food> searchFoods(String query);
    Food getFoodById(int foodId);
    Meal getMealById(int mealId);
    Map<String, Integer> getFoodMeasures(int foodId);
    MealItem calculateMealItem(Food food, double quantity, String unit);
    List<MealItem> getMealItemsByMealId(int mealId);
    List<Meal> getMealsForDate(Date date);
    List<models.Meal> getMealsInDateRange(Date startDate, Date endDate);

    // Methods from DashboardController
    NutritionSummary getNutritionSummaryForDate(Date date);
    
    Map<String, Double> calculateUserPlateData(String timePeriod);
    String generateCFGRecommendations(String timePeriod);
    NutritionAnalysisData getNutritionAnalysis(String timePeriod);

    // Methods from GoalsController
    GoalsForDisplay getUserGoals();
    void saveGoals(GoalsForDisplay goals);
    UserSettings getUserSettings();

    // Methods from FoodSwapController
    FoodSwapResult performSwap(int mealId, int foodToSwapOutId, FoodSwapGoal goal);
    java.util.List<models.FoodSwapRecommendation> findFoodSwaps(java.util.Date date, java.util.List<models.FoodSwapGoal> goals, java.util.List<models.FoodSwapRecommendation> exclusions);
    boolean applyFoodSwaps(java.util.Date date, java.util.List<models.FoodSwapRecommendation> swaps);
    boolean applyFoodSwapsToDateRange(java.util.Date startDate, java.util.Date endDate, java.util.List<models.FoodSwapRecommendation> swaps);
}
