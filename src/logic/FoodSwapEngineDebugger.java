package logic;

import Database.FoodDAO;
import models.*;
import java.util.*;

public class FoodSwapEngineDebugger {
    
    public static void debugSwapEngine(List<MealItem> mealItems, List<FoodSwapGoal> goals) {
        System.out.println("=== FOOD SWAP ENGINE DEBUG ===");
        System.out.println("Input meal items: " + mealItems.size());
        System.out.println("Input goals: " + goals.size());
        
        FoodDAO foodDAO = new FoodDAO();
        
        for (MealItem item : mealItems) {
            System.out.println("\nMeal Item: Food ID " + item.getFoodId() + ", Quantity: " + item.getQuantity());
            
            Food food = foodDAO.getFoodById(item.getFoodId()).orElse(null);
            if (food != null) {
                System.out.println("Food: " + food.getFoodDescription());
                System.out.println("Nutrients - Cal: " + food.getCalories() + ", Protein: " + food.getProtein() + 
                                 ", Fiber: " + food.getFiber() + ", Fat: " + food.getFats() + ", Carbs: " + food.getCarbs());
                
                // Test finding similar foods
                List<Food> similar = foodDAO.findSimilarFoodsByGroup(food.getFoodID(), 10);
                System.out.println("Similar foods found: " + similar.size());
                for (Food sim : similar) {
                    System.out.println("  - " + sim.getFoodDescription());
                }
                
                // Test nutrient-based search for each goal
                for (FoodSwapGoal goal : goals) {
                    System.out.println("\nTesting goal: " + goal.getNutrientType());
                    List<Food> nutrientFoods = findCandidatesByNutrientGoal(food, goal, foodDAO);
                    System.out.println("Nutrient-based candidates: " + nutrientFoods.size());
                    for (int i = 0; i < Math.min(5, nutrientFoods.size()); i++) {
                        Food candidate = nutrientFoods.get(i);
                        System.out.println("  - " + candidate.getFoodDescription() + 
                                         " (Fiber: " + candidate.getFiber() + ", Cal: " + candidate.getCalories() + ")");
                    }
                }
            } else {
                System.out.println("ERROR: Could not find food with ID " + item.getFoodId());
            }
        }
        
        for (FoodSwapGoal goal : goals) {
            System.out.println("\nGoal: " + goal.getNutrientType() + " - " + goal.getIntensityLevel());
        }
        
        System.out.println("=== END DEBUG ===\n");
    }
    
    private static List<Food> findCandidatesByNutrientGoal(Food originalFood, FoodSwapGoal goal, FoodDAO foodDAO) {
        List<Food> candidates = new ArrayList<>();
        
        double originalValue = getNutrientValue(originalFood, goal.getNutrientType());
        double targetValue = calculateTargetValue(originalValue, goal);
        
        // Search for foods with the target nutrient in a reasonable range
        double searchMin = Math.min(originalValue * 0.5, targetValue * 0.8);
        double searchMax = Math.max(originalValue * 2.0, targetValue * 1.5);
        
        System.out.println("Searching for " + goal.getNutrientType() + " in range " + searchMin + " to " + searchMax);
        
        String nutrientType = goal.getNutrientType().name().toLowerCase();
        if (nutrientType.contains("fiber")) {
            candidates.addAll(foodDAO.findFoodsByNutrientRange("fiber", searchMin, searchMax, 30));
        } else if (nutrientType.contains("calories")) {
            candidates.addAll(foodDAO.findFoodsByNutrientRange("calories", searchMin, searchMax, 30));
        } else if (nutrientType.contains("protein")) {
                        candidates.addAll(foodDAO.findFoodsByNutrientRange("protein", searchMin, searchMax, 30));
        } else if (nutrientType.contains("fat")) {
            candidates.addAll(foodDAO.findFoodsByNutrientRange("fats", searchMin, searchMax, 30));
        } else if (nutrientType.contains("carb")) {
            candidates.addAll(foodDAO.findFoodsByNutrientRange("carbs", searchMin, searchMax, 30));
        }
        
        return candidates;
    }
    
    private static double getNutrientValue(Food food, FoodSwapGoal.NutrientType nutrientType) {
        return switch (nutrientType) {
            case INCREASE_FIBER -> food.getFiber();
            case REDUCE_CALORIES -> food.getCalories();
            case INCREASE_PROTEIN -> food.getProtein();
            case REDUCE_FAT -> food.getFats();
            case REDUCE_CARBS, INCREASE_CARBS -> food.getCarbs();
        };
    }
    
    private static double calculateTargetValue(double originalValue, FoodSwapGoal goal) {
        if (goal.getSpecificValue() != null) {
            return goal.isIncrease() ? originalValue + goal.getSpecificValue() 
                                     : originalValue - goal.getSpecificValue();
        }
        
        if (goal.getSpecificPercentage() != null) {
            double change = originalValue * (goal.getSpecificPercentage() / 100.0);
            return goal.isIncrease() ? originalValue + change 
                                     : originalValue - change;
        }
        
        if (goal.getIntensityLevel() != null) {
            double multiplier = goal.getIntensityLevel().getMultiplier();
            return goal.isIncrease() ? originalValue * multiplier 
                                     : originalValue / multiplier;
        }
        
        return originalValue;
    }
}