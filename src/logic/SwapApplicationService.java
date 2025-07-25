package logic;

import Database.*;
import models.*;
import java.util.*;

public class SwapApplicationService {
    
    private MealDAO mealDAO;
    private SwapHistoryDAO swapHistoryDAO;
    private FoodDAO foodDAO;
    
    public SwapApplicationService() {
        this.mealDAO = new MealDAO();
        this.swapHistoryDAO = new SwapHistoryDAO();
        this.foodDAO = new FoodDAO();
        
        // Ensure swap history table exists
        swapHistoryDAO.createSwapHistoryTable();
    }
    
    public boolean applySwapsToCurrentMeal(List<FoodSwapRecommendation> swaps, int userId, Date date) {
        boolean allSuccessful = true;
        
        for (FoodSwapRecommendation swap : swaps) {
            // Find which meal contains this food item on the given date
            int mealId = findMealIdForFood(userId, swap.getOriginalFood().getFoodID(), date);
            
            if (mealId != -1) {
                boolean success = applySingleSwap(swap, mealId, userId);
                if (!success) {
                    allSuccessful = false;
                }
            } else {
                System.err.println("Could not find meal for food: " + swap.getOriginalFood().getFoodDescription() + " on date " + date);
                allSuccessful = false;
            }
        }
        
        return allSuccessful;
    }
    
    public boolean applySwapsToDateRange(List<FoodSwapRecommendation> swaps, int userId, 
                                        Date startDate, Date endDate) {
        boolean allSuccessful = true;
        
        // Get all meals in the date range
        List<Meal> mealsInRange = mealDAO.getMealsInDateRange(userId, startDate, endDate);
        
        for (Meal meal : mealsInRange) {
            List<MealItem> mealItems = mealDAO.getMealItemsByMealId(meal.getMealId());
            
            // Apply relevant swaps to this meal
            for (FoodSwapRecommendation swap : swaps) {
                // Check if this meal contains the original food
                boolean hasOriginalFood = mealItems.stream()
                    .anyMatch(item -> item.getFoodId() == swap.getOriginalFood().getFoodID());
                
                if (hasOriginalFood) {
                    boolean success = applySingleSwap(swap, meal.getMealId(), userId);
                    if (!success) {
                        allSuccessful = false;
                    }
                }
            }
        }
        
        return allSuccessful;
    }
    
    private boolean applySingleSwap(FoodSwapRecommendation swap, int mealId, int userId) {
        try {
            // Update the meal item in the database
            boolean updateSuccess = mealDAO.updateMealItem(
                mealId,
                swap.getOriginalFood().getFoodID(),
                swap.getRecommendedFood().getFoodID(),
                swap.getQuantity(),
                swap.getUnit()
            );
            
            if (updateSuccess) {
                // Record the swap in history
                SwapHistory swapHistory = new SwapHistory(
                    userId,
                    mealId,
                    swap.getOriginalFood().getFoodID(),
                    swap.getRecommendedFood().getFoodID(),
                    swap.getQuantity(),
                    swap.getUnit(),
                    swap.getSwapReason(),
                    true // isActive
                );
                
                int historyId = swapHistoryDAO.saveSwapHistory(swapHistory);
                return historyId != -1;
            }
            
        } catch (Exception e) {
            System.err.println("Error applying swap: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    private int findMealIdForFood(int userId, int foodId, Date date) {
        // Get meals for the specified date and check which one contains this food
        List<Meal> mealsOnDate = mealDAO.getMealsForUserAndDate(userId, date);
        
        for (Meal meal : mealsOnDate) {
            if (mealDAO.hasMealItemWithFood(meal.getMealId(), foodId)) {
                return meal.getMealId();
            }
        }
        
        return -1; // Not found
    }
    
    public boolean revertSwap(int historyId, int userId) {
        try {
            // Get the swap history record
            List<SwapHistory> userHistory = swapHistoryDAO.getSwapHistoryForUser(userId);
            SwapHistory targetSwap = userHistory.stream()
                .filter(sh -> sh.getHistoryId() == historyId)
                .findFirst()
                .orElse(null);
            
            if (targetSwap != null && targetSwap.isActive()) {
                // Revert the meal item back to original food
                boolean revertSuccess = mealDAO.updateMealItem(
                    targetSwap.getMealId(),
                    targetSwap.getSwappedFoodId(),  // Current food (swapped)
                    targetSwap.getOriginalFoodId(), // Back to original
                    targetSwap.getQuantity(),
                    targetSwap.getUnit()
                );
                
                if (revertSuccess) {
                    // Mark the swap as inactive
                    swapHistoryDAO.deactivateSwap(historyId);
                    return true;
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error reverting swap: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public List<SwapHistory> getSwapHistory(int userId) {
        return swapHistoryDAO.getSwapHistoryForUser(userId);
    }
    
    public List<SwapHistory> getSwapHistoryForPeriod(int userId, Date startDate, Date endDate) {
        return swapHistoryDAO.getSwapHistoryForPeriod(userId, startDate, endDate);
    }
    
    public SwapEffectSummary calculateSwapEffects(int userId, Date startDate, Date endDate) {
        List<SwapHistory> swaps = swapHistoryDAO.getSwapHistoryForPeriod(userId, startDate, endDate);
        
        double totalCalorieChange = 0;
        double totalProteinChange = 0;
        double totalFiberChange = 0;
        double totalFatChange = 0;
        double totalCarbChange = 0;
        
        for (SwapHistory swap : swaps) {
            if (swap.isActive()) {
                Food originalFood = foodDAO.getFoodById(swap.getOriginalFoodId()).orElse(null);
                Food swappedFood = foodDAO.getFoodById(swap.getSwappedFoodId()).orElse(null);
                
                if (originalFood != null && swappedFood != null) {
                    double quantity = swap.getQuantity();
                    totalCalorieChange += (swappedFood.getCalories() - originalFood.getCalories()) * quantity;
                    totalProteinChange += (swappedFood.getProtein() - originalFood.getProtein()) * quantity;
                    totalFiberChange += (swappedFood.getFiber() - originalFood.getFiber()) * quantity;
                    totalFatChange += (swappedFood.getFats() - originalFood.getFats()) * quantity;
                    totalCarbChange += (swappedFood.getCarbs() - originalFood.getCarbs()) * quantity;
                }
            }
        }
        
        return new SwapEffectSummary(totalCalorieChange, totalProteinChange, totalFiberChange, 
                                   totalFatChange, totalCarbChange, swaps.size());
    }
    
    // Helper method - in a real app this would come from session/context
    private int getCurrentUserId() {
        return 1; // Placeholder - should be injected or from session
    }
    
    public static class SwapEffectSummary {
        public final double calorieChange;
        public final double proteinChange;
        public final double fiberChange;
        public final double fatChange;
        public final double carbChange;
        public final int totalSwaps;
        
        public SwapEffectSummary(double calorieChange, double proteinChange, double fiberChange,
                               double fatChange, double carbChange, int totalSwaps) {
            this.calorieChange = calorieChange;
            this.proteinChange = proteinChange;
            this.fiberChange = fiberChange;
            this.fatChange = fatChange;
            this.carbChange = carbChange;
            this.totalSwaps = totalSwaps;
        }
    }
}