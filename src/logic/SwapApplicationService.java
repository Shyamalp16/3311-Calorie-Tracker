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
        
        swapHistoryDAO.createSwapHistoryTable();
    }
    
    public boolean applySwapsToCurrentMeal(List<FoodSwapRecommendation> swaps, int userId, Date date) {
        boolean allSuccessful = true;
        
        for (FoodSwapRecommendation swap : swaps) {
            // Try to find meal using both food ID and meal type for better accuracy
            int mealId = findMealIdForFoodAndType(userId, swap.getOriginalFood().getFoodID(), date, swap.getMealType());
            
            if (mealId == -1) {
                // Fallback to original method if meal type approach fails
                mealId = findMealIdForFood(userId, swap.getOriginalFood().getFoodID(), date);
            }
            
            if (mealId != -1) {
                boolean success = applySingleSwap(swap, mealId, userId);
                if (!success) {
                    allSuccessful = false;
                }
            } else {
                allSuccessful = false;
            }
        }
        
        return allSuccessful;
    }
    
    public boolean applySwapsToDateRange(List<FoodSwapRecommendation> swaps, int userId, 
                                        Date startDate, Date endDate) {
        boolean allSuccessful = true;
        
        try {
            List<Meal> mealsInRange = mealDAO.getMealsInDateRange(userId, startDate, endDate);
            
            if (mealsInRange.isEmpty()) {
                return true; // Not an error if no meals exist
            }
            
            for (Meal meal : mealsInRange) {
                List<MealItem> mealItems = mealDAO.getMealItemsByMealId(meal.getMealId());
                
                // Track which foods have been swapped in this specific meal
                Set<Integer> swappedFoodsInThisMeal = new HashSet<>();
                
                for (FoodSwapRecommendation swap : swaps) {
                    int originalFoodId = swap.getOriginalFood().getFoodID();
                    
                    // Skip if we've already swapped this food in this meal
                    if (swappedFoodsInThisMeal.contains(originalFoodId)) {
                        continue;
                    }
                    
                    boolean hasOriginalFood = mealItems.stream()
                        .anyMatch(item -> item.getFoodId() == originalFoodId);
                    
                    if (hasOriginalFood) {
                        boolean success = applySingleSwap(swap, meal.getMealId(), userId);
                        if (!success) {
                            allSuccessful = false;
                        } else {
                            // Mark this food as swapped in this meal
                            swappedFoodsInThisMeal.add(originalFoodId);
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
        return allSuccessful;
    }

    public boolean applySwapToMealItem(int mealId, int originalFoodId, int newFoodId, double newQuantity, String newUnit) {
        try {
            boolean updateSuccess = mealDAO.updateMealItem(
                mealId,
                originalFoodId,
                newFoodId,
                newQuantity,
                newUnit
            );
            return updateSuccess;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean applySingleSwap(FoodSwapRecommendation swap, int mealId, int userId) {
        try {
            boolean updateSuccess = mealDAO.updateMealItem(
                mealId,
                swap.getOriginalFood().getFoodID(),
                swap.getRecommendedFood().getFoodID(),
                swap.getQuantity(),
                swap.getUnit()
            );
            
            if (updateSuccess) {
                SwapHistory swapHistory = new SwapHistory(
                    userId,
                    mealId,
                    swap.getOriginalFood().getFoodID(),
                    swap.getRecommendedFood().getFoodID(),
                    swap.getQuantity(),
                    swap.getUnit(),
                    swap.getSwapReason(),
                    true 
                );
                
                int historyId = swapHistoryDAO.saveSwapHistory(swapHistory);
                return historyId != -1;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    private int findMealIdForFood(int userId, int foodId, Date date) {
        // Convert java.util.Date to java.sql.Date for database query
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        List<Meal> mealsOnDate = mealDAO.getMealsForUserAndDate(userId, sqlDate);
        
        for (Meal meal : mealsOnDate) {
            if (mealDAO.hasMealItemWithFood(meal.getMealId(), foodId)) {
                return meal.getMealId();
            }
        }
        return -1; 
    }
    
    private int findMealIdForFoodAndType(int userId, int foodId, Date date, String mealType) {
        // Convert java.util.Date to java.sql.Date for database query
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        List<Meal> mealsOnDate = mealDAO.getMealsForUserAndDate(userId, sqlDate);
        
        for (Meal meal : mealsOnDate) {
            // First check if meal type matches
            if (meal.getMealType().equalsIgnoreCase(mealType)) {
                if (mealDAO.hasMealItemWithFood(meal.getMealId(), foodId)) {
                    return meal.getMealId();
                }
            }
        }
        return -1; 
    }
    
    public boolean revertSwap(int historyId, int userId) {
        try {
            List<SwapHistory> userHistory = swapHistoryDAO.getSwapHistoryForUser(userId);
            SwapHistory targetSwap = userHistory.stream()
                .filter(sh -> sh.getHistoryId() == historyId)
                .findFirst()
                .orElse(null);
            
            if (targetSwap != null && targetSwap.isActive()) {
                boolean revertSuccess = mealDAO.updateMealItem(
                    targetSwap.getMealId(),
                    targetSwap.getSwappedFoodId(),  
                    targetSwap.getOriginalFoodId(), 
                    targetSwap.getQuantity(),
                    targetSwap.getUnit()
                );
                
                if (revertSuccess) {
                    swapHistoryDAO.deactivateSwap(historyId);
                    return true;
                }
            }
            
        } catch (Exception e) {
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
    
    private int getCurrentUserId() {
        return 1; 
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