package logic;

import Database.FoodDAO;
import models.*;
import java.util.*;
import java.util.stream.Collectors;

public class FoodSwapEngine {
    
    private FoodDAO foodDAO;
    private final double DEFAULT_TOLERANCE = 0.1; 
    
    public FoodSwapEngine() {
        this.foodDAO = new FoodDAO();
    }
    
    public List<FoodSwapRecommendation> findFoodSwaps(List<MealItem> mealItems, 
                                                     List<FoodSwapGoal> goals) {
        return findFoodSwaps(mealItems, goals, Collections.emptyList());
    }

    public List<FoodSwapRecommendation> findFoodSwapsWithMealType(List<MealItem> mealItems, 
                                                                 List<FoodSwapGoal> goals,
                                                                 List<FoodSwapRecommendation> exclusions,
                                                                 String mealType) {
        List<FoodSwapRecommendation> recommendations = new ArrayList<>();
        
        Set<Integer> excludedRecIds = exclusions.stream()
            .map(rec -> rec.getRecommendedFood().getFoodID())
            .collect(Collectors.toSet());

        // Track original food IDs to prevent duplicate swaps for the same food item
        Set<Integer> processedOriginalFoodIds = new HashSet<>();

        final int MAX_SWAPS_PER_MEAL = 2;
        int maxSwaps = Math.min(MAX_SWAPS_PER_MEAL, mealItems.size());
        int swapsFound = 0;
        
        List<MealItem> sortedItems = mealItems.stream()
            .sorted((a, b) -> Double.compare(b.getCalories(), a.getCalories()))
            .collect(Collectors.toList());
        
        for (MealItem mealItem : sortedItems) {
            if (swapsFound >= maxSwaps) break;
            
            Food originalFood = foodDAO.getFoodById(mealItem.getFoodId()).orElse(null);
            if (originalFood == null) continue;
            
            // Skip if we've already processed this food item
            if (processedOriginalFoodIds.contains(originalFood.getFoodID())) {
                continue;
            }
            
            FoodSwapRecommendation bestSwap = findBestSwapForFoodWithMealType(originalFood, goals, 
                                                                             mealItem.getQuantity(), 
                                                                             mealItem.getUnit(),
                                                                             excludedRecIds, mealType);
            
            if (bestSwap != null) {
                recommendations.add(bestSwap);
                excludedRecIds.add(bestSwap.getRecommendedFood().getFoodID());
                processedOriginalFoodIds.add(originalFood.getFoodID()); // Track this original food
                swapsFound++;
                
                // Double-check: break if we've reached the maximum
                if (swapsFound >= MAX_SWAPS_PER_MEAL) {
                    break;
                }
            }
        }
        
        // Safety check: ensure we never return more than the maximum allowed swaps
        if (recommendations.size() > MAX_SWAPS_PER_MEAL) {
            return recommendations.subList(0, MAX_SWAPS_PER_MEAL);
        }
        
        return recommendations;
    }

    public List<FoodSwapRecommendation> findFoodSwaps(List<MealItem> mealItems, 
                                                     List<FoodSwapGoal> goals,
                                                     List<FoodSwapRecommendation> exclusions) {
        List<FoodSwapRecommendation> recommendations = new ArrayList<>();
        
        Set<Integer> excludedRecIds = exclusions.stream()
            .map(rec -> rec.getRecommendedFood().getFoodID())
            .collect(Collectors.toSet());

        // Track original food IDs to prevent duplicate swaps for the same food item
        Set<Integer> processedOriginalFoodIds = new HashSet<>();

        final int MAX_SWAPS_PER_MEAL = 2;
        int maxSwaps = Math.min(MAX_SWAPS_PER_MEAL, mealItems.size());
        int swapsFound = 0;
        
        List<MealItem> sortedItems = mealItems.stream()
            .sorted((a, b) -> Double.compare(b.getCalories(), a.getCalories()))
            .collect(Collectors.toList());
        
        for (MealItem mealItem : sortedItems) {
            if (swapsFound >= maxSwaps) break;
            
            Food originalFood = foodDAO.getFoodById(mealItem.getFoodId()).orElse(null);
            if (originalFood == null) continue;
            
            // Skip if we've already processed this food item
            if (processedOriginalFoodIds.contains(originalFood.getFoodID())) {
                continue;
            }
            
            FoodSwapRecommendation bestSwap = findBestSwapForFood(originalFood, goals, 
                                                                 mealItem.getQuantity(), 
                                                                 mealItem.getUnit(),
                                                                 excludedRecIds);
            
            if (bestSwap != null) {
                recommendations.add(bestSwap);
                excludedRecIds.add(bestSwap.getRecommendedFood().getFoodID());
                processedOriginalFoodIds.add(originalFood.getFoodID()); // Track this original food
                swapsFound++;
                
                // Double-check: break if we've reached the maximum
                if (swapsFound >= MAX_SWAPS_PER_MEAL) {
                    break;
                }
            }
        }
        
        // Safety check: ensure we never return more than the maximum allowed swaps
        if (recommendations.size() > MAX_SWAPS_PER_MEAL) {
            return recommendations.subList(0, MAX_SWAPS_PER_MEAL);
        }
        
        return recommendations;
    }
    
    private FoodSwapRecommendation findBestSwapForFoodWithMealType(Food originalFood, 
                                                                  List<FoodSwapGoal> goals,
                                                                  double quantity, 
                                                                  String unit,
                                                                  Set<Integer> excludedRecIds,
                                                                  String mealType) {
        
        List<Food> candidates = foodDAO.findSimilarFoodsByGroup(originalFood.getFoodID(), 50);
        
        for (FoodSwapGoal goal : goals) {
            List<Food> nutrientCandidates = findCandidatesByNutrientGoal(originalFood, goal);
            for (Food candidate : nutrientCandidates) {
                if (!candidates.contains(candidate)) {
                    candidates.add(candidate);
                }
            }
        }
        
        
        Food bestCandidate = null;
        double bestScore = -1;
        String bestReason = "";
        
        for (Food candidate : candidates) {
            if (excludedRecIds.contains(candidate.getFoodID())) {
                continue;
            }

            SwapScore score = evaluateSwap(originalFood, candidate, goals);
            
            if (score.totalScore > bestScore && score.meetsGoals) {
                bestCandidate = candidate;
                bestScore = score.totalScore;
                bestReason = score.reason;
            }
        }
        
        
        if (bestCandidate != null) {
            return new FoodSwapRecommendation(originalFood, bestCandidate, 
                                            quantity, unit, bestReason, mealType);
        }
        
        return null;
    }
    
    private FoodSwapRecommendation findBestSwapForFood(Food originalFood, 
                                                      List<FoodSwapGoal> goals,
                                                      double quantity, 
                                                      String unit,
                                                      Set<Integer> excludedRecIds) {
        
        List<Food> candidates = foodDAO.findSimilarFoodsByGroup(originalFood.getFoodID(), 50);
        
        for (FoodSwapGoal goal : goals) {
            List<Food> nutrientCandidates = findCandidatesByNutrientGoal(originalFood, goal);
            for (Food candidate : nutrientCandidates) {
                if (!candidates.contains(candidate)) { // Avoid duplicates
                    candidates.add(candidate);
                }
            }
        }
        
        
        Food bestCandidate = null;
        double bestScore = -1;
        String bestReason = "";
        
        for (Food candidate : candidates) {
            if (excludedRecIds.contains(candidate.getFoodID())) {
                continue;
            }

            SwapScore score = evaluateSwap(originalFood, candidate, goals);
            
            if (score.totalScore > bestScore && score.meetsGoals) {
                bestCandidate = candidate;
                bestScore = score.totalScore;
                bestReason = score.reason;
            }
        }
        
        
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
        
        boolean allGoalsMet = true;
        StringBuilder reasonBuilder = new StringBuilder();
        
        for (FoodSwapGoal goal : goals) {
            double originalValue = getNutrientValue(original, goal.getNutrientType());
            double candidateValue = getNutrientValue(candidate, goal.getNutrientType());
            double targetValue = calculateTargetValue(originalValue, goal);
            
            boolean goalMet = false;
            if (goal.isIncrease()) {
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
        
        double preservationScore = 0;
        double[] originalNutrients = {original.getCalories(), original.getProtein(), 
                                     original.getCarbs(), original.getFats(), original.getFiber()};
        double[] candidateNutrients = {candidate.getCalories(), candidate.getProtein(), 
                                      candidate.getCarbs(), candidate.getFats(), candidate.getFiber()};
        
        for (int i = 0; i < originalNutrients.length; i++) {
            if (originalNutrients[i] > 0) {
                double diff = Math.abs(candidateNutrients[i] - originalNutrients[i]) / originalNutrients[i];
                if (diff <= DEFAULT_TOLERANCE) {
                    preservationScore += 20; 
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
        return swaps;
    }
}