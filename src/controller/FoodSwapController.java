package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import logic.FoodSwapEngine;
import logic.SwapApplicationService;
import models.FoodSwapGoal;
import models.FoodSwapRecommendation;
import models.MealItem;
import models.User;

public class FoodSwapController {

    private FoodSwapEngine swapEngine;
    private SwapApplicationService swapApplicationService;
    private User currentUser;
    private IMealController mealController;

    public FoodSwapController(User user, IMealController mealController) {
        this.swapEngine = new FoodSwapEngine();
        this.swapApplicationService = new SwapApplicationService();
        this.currentUser = user;
        this.mealController = mealController;
    }

    public List<FoodSwapRecommendation> findFoodSwaps(Date date, List<FoodSwapGoal> goals, List<FoodSwapRecommendation> exclusions) {
        List<models.Meal> meals = mealController.getMealsForDate(date);
        if (meals.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<FoodSwapRecommendation> allRecommendations = new ArrayList<>();
        
        for (models.Meal meal : meals) {
            List<MealItem> mealItems = mealController.getMealItemsByMealId(meal.getMealId());
            if (!mealItems.isEmpty()) {
                // FoodSwapEngine already limits to max 2 swaps per meal
                List<FoodSwapRecommendation> mealRecommendations = swapEngine.findFoodSwapsWithMealType(
                    mealItems, goals, exclusions, meal.getMealType());
                allRecommendations.addAll(mealRecommendations);
            }
        }
        
        return allRecommendations;
    }

    public boolean applySwapsToCurrentMeal(List<FoodSwapRecommendation> swaps, Date date) {
        return swapApplicationService.applySwapsToCurrentMeal(swaps, currentUser.getUserId(), date);
    }

    public boolean applySwapsToDateRange(List<FoodSwapRecommendation> swaps, Date startDate, Date endDate) {
        return swapApplicationService.applySwapsToDateRange(swaps, currentUser.getUserId(), startDate, endDate);
    }

    public SwapApplicationService.SwapEffectSummary calculateSwapEffects(Date startDate, Date endDate) {
        return swapApplicationService.calculateSwapEffects(currentUser.getUserId(), startDate, endDate);
    }

    public logic.facade.FoodSwapResult performSwap(int mealId, int foodToSwapOutId, FoodSwapGoal goal) {
        // 1. Retrieve the meal and its items
        models.Meal meal = mealController.getMealById(mealId);
        if (meal == null) {
            return new logic.facade.FoodSwapResult(false, "Meal not found.", Collections.emptyList());
        }
        List<models.MealItem> mealItems = mealController.getMealItemsByMealId(mealId);

        // 2. Find potential swaps for the specified food item within the meal
        List<models.FoodSwapRecommendation> recommendations = swapEngine.findFoodSwaps(
            mealItems, Collections.singletonList(goal), Collections.emptyList());

        // Filter recommendations to only include those for the foodToSwapOutId
        recommendations.removeIf(rec -> rec.getOriginalFood().getFoodID() != foodToSwapOutId);

        if (recommendations.isEmpty()) {
            return new logic.facade.FoodSwapResult(false, "No suitable swaps found for the selected food and goal.", Collections.emptyList());
        }

        // For simplicity, let's assume we take the first recommendation if multiple exist
        // In a real scenario, you might present options to the user
        models.FoodSwapRecommendation chosenRecommendation = recommendations.get(0);

        // 3. Apply the swap to the meal
        boolean success = swapApplicationService.applySwapToMealItem(
            mealId, foodToSwapOutId, chosenRecommendation.getRecommendedFood().getFoodID(),
            chosenRecommendation.getQuantity(), chosenRecommendation.getUnit());

        if (success) {
            return new logic.facade.FoodSwapResult(true, "Food swapped successfully!", Collections.singletonList(chosenRecommendation));
        } else {
            return new logic.facade.FoodSwapResult(false, "Failed to apply swap.", Collections.emptyList());
        }
    }
}
