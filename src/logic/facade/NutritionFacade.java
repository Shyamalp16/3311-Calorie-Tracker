package logic.facade;

import controller.CanadaFoodGuideController;
import controller.DashboardController;
import controller.FoodSwapController;
import controller.GoalsController;
import controller.IMealController;
import controller.MealController;
import controller.LoggingMealControllerDecorator;
import controller.DashboardController.NutritionAnalysisData;
import controller.DashboardController.NutritionSummary;
import logic.facade.FoodSwapResult;
import controller.GoalsController.GoalsForDisplay;
import controller.MealController.SaveMealResult;
import models.Food;
import models.FoodSwapGoal;
import models.Goal;
import models.MealItem;
import models.User;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Concrete Facade that provides a single point of contact to the business logic for the UI.
 * It delegates requests to the appropriate controllers.
 */
public class NutritionFacade implements INutritionFacade {

    private final IMealController mealController;
    private final DashboardController dashboardController;
    private final GoalsController goalsController;
    private final FoodSwapController foodSwapController;
    private final CanadaFoodGuideController canadaFoodGuideController;

    public NutritionFacade(User user) {
        this.mealController = new LoggingMealControllerDecorator(new MealController(user));
        this.dashboardController = new DashboardController(user);
        this.goalsController = new GoalsController(user);
        this.foodSwapController = new FoodSwapController(user, this.mealController);
        this.canadaFoodGuideController = new CanadaFoodGuideController(this.mealController, this.dashboardController);
    }

    @Override
    public SaveMealResult saveMeal(String mealType, Date mealDate, List<MealItem> items) {
        return mealController.saveMeal(mealType, mealDate, items);
    }

    @Override
    public List<Food> searchFoods(String query) {
        return mealController.searchFoods(query);
    }

    @Override
    public Food getFoodById(int foodId) {
        return mealController.getFoodById(foodId);
    }

    @Override
    public models.Meal getMealById(int mealId) {
        return mealController.getMealById(mealId);
    }

    @Override
    public Map<String, Integer> getFoodMeasures(int foodId) {
        return mealController.getFoodMeasures(foodId);
    }

    @Override
    public MealItem calculateMealItem(Food food, double quantity, String unit) {
        return mealController.calculateMealItem(food, quantity, unit);
    }

    @Override
    public List<models.MealItem> getMealItemsByMealId(int mealId) {
        return mealController.getMealItemsByMealId(mealId);
    }

    @Override
    public List<models.Meal> getMealsForDate(Date date) {
        return mealController.getMealsForDate(date);
    }

    @Override
    public NutritionSummary getNutritionSummaryForDate(Date date) {
        return dashboardController.getNutritionForDate(date);
    }

    @Override
    public List<models.Meal> getMealsInDateRange(Date startDate, Date endDate) {
        return dashboardController.getMealsInDateRange(startDate, endDate);
    }

    @Override
    public Map<String, Double> calculateUserPlateData(String timePeriod) {
        return canadaFoodGuideController.calculateUserPlateData(timePeriod);
    }

    @Override
    public String generateCFGRecommendations(String timePeriod) {
        return canadaFoodGuideController.generateCFGRecommendations(timePeriod);
    }

    @Override
    public NutritionAnalysisData getNutritionAnalysis(String timePeriod) {
        // Get analysis data from dashboard controller
        NutritionAnalysisData analysisData = dashboardController.getNutritionAnalysis(timePeriod);
        
        // Override the goals with fresh data from goals controller to ensure consistency
        Goal freshGoals = goalsController.getUserGoals();
        
        // Create new analysis data with fresh goals
        return new NutritionAnalysisData(
            analysisData.totalNutrition,
            analysisData.averageNutrition,
            freshGoals,
            analysisData.days
        );
    }

    @Override
    public GoalsForDisplay getUserGoals() {
        return goalsController.getUserGoalsForDisplay();
    }

    @Override
    public void saveGoals(GoalsForDisplay goals) {
        goalsController.saveGoals(goals.calories, goals.protein, goals.carbs, goals.fats, goals.fiber);
    }

    @Override
    public FoodSwapResult performSwap(int mealId, int foodToSwapOutId, FoodSwapGoal goal) {
        return foodSwapController.performSwap(mealId, foodToSwapOutId, goal);
    }

    @Override
    public List<models.FoodSwapRecommendation> findFoodSwaps(Date date, List<FoodSwapGoal> goals, List<models.FoodSwapRecommendation> exclusions) {
        return foodSwapController.findFoodSwaps(date, goals, exclusions);
    }

    @Override
    public boolean applyFoodSwaps(Date date, List<models.FoodSwapRecommendation> swaps) {
        return foodSwapController.applySwapsToCurrentMeal(swaps, date);
    }

    @Override
    public boolean applyFoodSwapsToDateRange(Date startDate, Date endDate, List<models.FoodSwapRecommendation> swaps) {
        return foodSwapController.applySwapsToDateRange(swaps, startDate, endDate);
    }

    @Override
    public models.UserSettings getUserSettings() {
        return dashboardController.getUserSettings();
    }
}

