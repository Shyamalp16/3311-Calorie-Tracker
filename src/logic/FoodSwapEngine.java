 package logic;

import Database.FoodDAO;
import models.*;
import java.util.*;
import java.util.stream.Collectors;

public class FoodSwapEngine {
    
    private FoodDAO foodDAO;
    private final double DEFAULT_TOLERANCE = 0.1; // 10% tolerance for non-target nutrients
    
    public FoodSwapEngine() {
        this.foodDAO = new FoodDAO();
    }
    
    public List<FoodSwapRecommendation> findFoodSwaps(List<MealItem> mealItems, 
                                                     List<FoodSwapGoal> goals) {
        List<FoodSwapRecommendation> recommendations = new ArrayList<>();
        
        // Limit to max 2 swaps per meal as per requirements
        int maxSwaps = Math.min(2, mealItems.size());
        int swapsFound = 0;
        
        // Sort meal items by priority for swapping (higher calorie items first for better impact)
        List<MealItem> sortedItems = mealItems.stream()
            .sorted((a, b) -> Double.compare(b.getCalories(), a.getCalories()))
            .collect(Collectors.toList());
        
        for (MealItem mealItem : sortedItems) {
            if (swapsFound >= maxSwaps) break;
            
            Food originalFood = foodDAO.getFoodById(mealItem.getFoodId());
            if (originalFood == null) continue;
            
            FoodSwapRecommendation bestSwap = findBestSwapForFood(originalFood, goals, 
                                                                 mealItem.getQuantity(), 
                                                                 mealItem.getUnit());
            
            if (bestSwap != null) {
                recommendations.add(bestSwap);
                swapsFound++;
            }
        }
        
        return recommendations;
    }
    
    private FoodSwapRecommendation findBestSwapForFood(Food originalFood, 
                                                      List<FoodSwapGoal> goals,
                                                      double quantity, 
                                                      String unit) {
        
        // First try to find alternatives from the same food group
        List<Food> candidates = foodDAO.findSimilarFoodsByGroup(originalFood.getFoodID(), 50);
        
        // Always expand search with nutrient-based candidates for better results
        for (FoodSwapGoal goal : goals) {
            List<Food> nutrientCandidates = findCandidatesByNutrientGoal(originalFood, goal);
            for (Food candidate : nutrientCandidates) {
                if (!candidates.contains(candidate)) { // Avoid duplicates
                    candidates.add(candidate);
                }
            }
        }
        
        System.out.println("Found " + candidates.size() + " candidates for food: " + originalFood.getFoodDescription());
        
        Food bestCandidate = null;
        double bestScore = -1;
        String bestReason = "";
        
        for (Food candidate : candidates) {
            SwapScore score = evaluateSwap(originalFood, candidate, goals);
            System.out.println("Evaluating candidate: " + candidate.getFoodDescription() + 
                             " Score: " + score.totalScore + " Meets goals: " + score.meetsGoals);
            
            if (score.totalScore > bestScore && score.meetsGoals) {
                bestCandidate = candidate;
                bestScore = score.totalScore;
                bestReason = score.reason;
            }
        }
        
        System.out.println("Best candidate: " + (bestCandidate != null ? bestCandidate.getFoodDescription() : "None") + 
                          " with score: " + bestScore);
        
        if (bestCandidate != null) {
            return new FoodSwapRecommendation(originalFood, bestCandidate, 
                                            quantity, unit, bestReason);
        }
        
        return null;
    }
    
    private List<Food> findCandidatesByNutrientGoal(Food originalFood, FoodSwapGoal goal) {
        List<Food> candidates = new ArrayList<>();
        
        double originalValue = getNutrientValue(originalFood, goal.getNutrientType());
        double targetValue = calculateTargetValue(originalValue, goal);
        
        // Search for foods with the target nutrient in a reasonable range
        double searchMin = Math.min(originalValue * 0.5, targetValue * 0.8);
        double searchMax = Math.max(originalValue * 2.0, targetValue * 1.5);
        
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
    
    private double calculateTargetValue(double originalValue, FoodSwapGoal goal) {
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
    
    private SwapScore evaluateSwap(Food original, Food candidate, List<FoodSwapGoal> goals) {
        SwapScore score = new SwapScore();
        
        // Check if goals are met
        boolean allGoalsMet = true;
        StringBuilder reasonBuilder = new StringBuilder();
        
        for (FoodSwapGoal goal : goals) {
            double originalValue = getNutrientValue(original, goal.getNutrientType());
            double candidateValue = getNutrientValue(candidate, goal.getNutrientType());
            double targetValue = calculateTargetValue(originalValue, goal);
            
            boolean goalMet = false;
            if (goal.isIncrease()) {
                // More lenient criteria - any improvement counts
                goalMet = candidateValue > originalValue;
                if (goalMet) {
                    double improvement = candidateValue - originalValue;
                    if (originalValue > 0) {
                        score.goalScore += improvement / originalValue * 100; // Percentage improvement
                    } else {
                        score.goalScore += improvement * 10; // Fixed bonus for zero baseline
                    }
                    reasonBuilder.append(String.format("Increases %s by %.1fg; ", 
                        goal.getNutrientType().getDisplayName().toLowerCase(), improvement));
                }
            } else if (goal.isDecrease()) {
                // Any reduction counts
                goalMet = candidateValue < originalValue;
                if (goalMet) {
                    double reduction = originalValue - candidateValue;
                    if (originalValue > 0) {
                        score.goalScore += reduction / originalValue * 100; // Percentage reduction
                    } else {
                        score.goalScore += reduction * 10; // Fixed bonus
                    }
                    reasonBuilder.append(String.format("Reduces %s by %.1fg; ", 
                        goal.getNutrientType().getDisplayName().toLowerCase(), reduction));
                }
            }
            
            if (!goalMet) {
                allGoalsMet = false;
            }
        }
        
        score.meetsGoals = allGoalsMet;
        
        // Evaluate how well other nutrients are preserved (within 10% tolerance)
        double preservationScore = 0;
        double[] originalNutrients = {original.getCalories(), original.getProtein(), 
                                     original.getCarbs(), original.getFats(), original.getFiber()};
        double[] candidateNutrients = {candidate.getCalories(), candidate.getProtein(), 
                                      candidate.getCarbs(), candidate.getFats(), candidate.getFiber()};
        
        for (int i = 0; i < originalNutrients.length; i++) {
            if (originalNutrients[i] > 0) {
                double diff = Math.abs(candidateNutrients[i] - originalNutrients[i]) / originalNutrients[i];
                if (diff <= DEFAULT_TOLERANCE) {
                    preservationScore += 20; // Max 100 points for preserving all nutrients
                } else {
                    preservationScore += Math.max(0, 20 - (diff * 100)); // Penalty for deviation
                }
            }
        }
        
        score.preservationScore = preservationScore;
        score.totalScore = score.goalScore + score.preservationScore;
        score.reason = reasonBuilder.toString();
        
        return score;
    }
    
    private double getNutrientValue(Food food, FoodSwapGoal.NutrientType nutrientType) {
        return switch (nutrientType) {
            case INCREASE_FIBER -> food.getFiber();
            case REDUCE_CALORIES -> food.getCalories();
            case INCREASE_PROTEIN -> food.getProtein();
            case REDUCE_FAT -> food.getFats();
            case REDUCE_CARBS, INCREASE_CARBS -> food.getCarbs();
        };
    }
    
    private static class SwapScore {
        double goalScore = 0;
        double preservationScore = 0;
        double totalScore = 0;
        boolean meetsGoals = false;
        String reason = "";
    }
    
    public List<FoodSwapRecommendation> generateMealComparison(List<MealItem> originalMeal, 
                                                              List<FoodSwapRecommendation> swaps) {
        // This method could be used to generate the before/after comparison
        // shown in the mockup UI
        return swaps;
    }
}
