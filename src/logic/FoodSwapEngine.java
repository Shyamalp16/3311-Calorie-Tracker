 package logic;

import models.Food;
import models.User;
import Database.FoodDAO;
import Database.MealDAO;

import java.util.*;

public class FoodSwapEngine {
    
    private FoodDAO foodDAO;
    private MealDAO mealDAO;
    
    public FoodSwapEngine() {
        this.foodDAO = new FoodDAO();
        this.mealDAO = new MealDAO();
    }
    
    /**
     * Finds suitable food swaps based on user's nutritional goals
     * @param user The current user
     * @param primaryGoal The main nutritional goal (e.g., "Increase Fiber")
     * @param secondaryGoal Optional secondary goal (can be "None")
     * @param intensity The intensity level ("Slightly more", "Moderately more", "Significantly more")
     * @return Map of meal types to list of food swaps (original food, replacement food pairs)
     */
    public Map<String, List<Food>> findFoodSwaps(User user, String primaryGoal, String secondaryGoal, String intensity) {
        Map<String, List<Food>> swapSuggestions = new HashMap<>();
        
        // Get user's recent meals to analyze
        List<Food> recentFoods = getUserRecentFoods(user);
        
        if (recentFoods.isEmpty()) {
            // If no recent meals, provide general recommendations
            return getGeneralRecommendations(primaryGoal, secondaryGoal, intensity);
        }
        
        // Analyze each food item for potential swaps
        for (Food food : recentFoods) {
            List<Food> alternatives = findAlternatives(food, primaryGoal, secondaryGoal, intensity);
            
            if (!alternatives.isEmpty()) {
                String mealType = determineMealType(food);
                swapSuggestions.computeIfAbsent(mealType, k -> new ArrayList<>());
                
                // Add original food and best alternative as a pair
                swapSuggestions.get(mealType).add(food);
                swapSuggestions.get(mealType).add(alternatives.get(0));
            }
        }
        
        return swapSuggestions;
    }
    
    /**
     * Finds alternative foods for a given food item based on nutritional goals
     */
    private List<Food> findAlternatives(Food originalFood, String primaryGoal, String secondaryGoal, String intensity) {
        List<Food> alternatives = new ArrayList<>();
        
        // Search for foods in similar categories
        List<Food> candidateFoods = searchSimilarFoods(originalFood);
        
        // Filter and rank candidates based on goals
        for (Food candidate : candidateFoods) {
            if (isGoodAlternative(originalFood, candidate, primaryGoal, secondaryGoal, intensity)) {
                alternatives.add(candidate);
            }
        }
        
        // Sort by how well they meet the goals
        alternatives.sort((a, b) -> compareAlternatives(originalFood, a, b, primaryGoal, secondaryGoal));
        
        return alternatives;
    }
    
    /**
     * Searches for foods similar to the original food
     */
    private List<Food> searchSimilarFoods(Food originalFood) {
        List<Food> similarFoods = new ArrayList<>();
        
        // Extract key words from food description for searching
        String[] keywords = extractKeywords(originalFood.getFoodDescription());
        
        for (String keyword : keywords) {
            List<Food> results = foodDAO.searchFoodByName(keyword);
            similarFoods.addAll(results);
        }
        
        // Remove duplicates and original food
        Set<Integer> seenIds = new HashSet<>();
        similarFoods.removeIf(food -> 
            !seenIds.add(food.getFoodID()) || food.getFoodID() == originalFood.getFoodID()
        );
        
        return similarFoods;
    }
    
    /**
     * Extracts keywords from food description for similarity search
     */
    private String[] extractKeywords(String foodDescription) {
        // Convert to lowercase and split by common separators
        String cleaned = foodDescription.toLowerCase()
            .replaceAll("[,;()\\[\\]]", " ")
            .replaceAll("\\s+", " ")
            .trim();
        
        String[] words = cleaned.split(" ");
        List<String> keywords = new ArrayList<>();
        
        // Filter out common words and keep meaningful terms
        Set<String> stopWords = Set.of("with", "and", "or", "the", "a", "an", "in", "on", "at", 
                                     "raw", "cooked", "fresh", "frozen", "canned", "dried");
        
        for (String word : words) {
            if (word.length() > 2 && !stopWords.contains(word)) {
                keywords.add(word);
            }
        }
        
        return keywords.toArray(new String[0]);
    }
    
    /**
     * Determines if a candidate food is a good alternative based on goals
     */
    private boolean isGoodAlternative(Food original, Food candidate, String primaryGoal, String secondaryGoal, String intensity) {
        double improvementThreshold = getImprovementThreshold(intensity);
        
        // Check primary goal
        if (!meetsGoal(original, candidate, primaryGoal, improvementThreshold)) {
            return false;
        }
        
        // Check secondary goal if specified
        if (!"None".equals(secondaryGoal)) {
            if (!meetsGoal(original, candidate, secondaryGoal, improvementThreshold * 0.5)) {
                return false;
            }
        }
        
        // Ensure the alternative isn't significantly worse in other nutrients
        return !isSignificantlyWorse(original, candidate);
    }
    
    /**
     * Checks if a candidate meets a specific nutritional goal
     */
    private boolean meetsGoal(Food original, Food candidate, String goal, double threshold) {
        switch (goal) {
            case "Increase Fiber":
                return candidate.getFiber() > original.getFiber() + threshold;
            case "Reduce Calories":
                return candidate.getCalories() < original.getCalories() - (threshold * 10);
            case "Increase Protein":
                return candidate.getProtein() > original.getProtein() + threshold;
            case "Reduce Sodium":
                // Note: Sodium data would need to be added to Food model
                return true; // Placeholder
            case "Increase Calcium":
                // Note: Calcium data would need to be added to Food model
                return true; // Placeholder
            case "Reduce Sugar":
                // Note: Sugar data would need to be added to Food model
                return true; // Placeholder
            case "Increase Iron":
                // Note: Iron data would need to be added to Food model
                return true; // Placeholder
            default:
                return false;
        }
    }
    
    /**
     * Gets improvement threshold based on intensity level
     */
    private double getImprovementThreshold(String intensity) {
        switch (intensity) {
            case "Slightly more":
                return 1.0; // 1g for nutrients, 10 cal for calories
            case "Moderately more":
                return 2.0; // 2g for nutrients, 20 cal for calories
            case "Significantly more":
                return 3.0; // 3g for nutrients, 30 cal for calories
            default:
                return 1.0;
        }
    }
    
    /**
     * Checks if the alternative is significantly worse in other important nutrients
     */
    private boolean isSignificantlyWorse(Food original, Food candidate) {
        // Don't allow alternatives that are much higher in calories (unless goal is to increase calories)
        if (candidate.getCalories() > original.getCalories() * 1.5) {
            return true;
        }
        
        // Don't allow alternatives that are much lower in protein (unless goal is to reduce protein)
        if (candidate.getProtein() < original.getProtein() * 0.7) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Compares two alternatives to determine which better meets the goals
     */
    private int compareAlternatives(Food original, Food alt1, Food alt2, String primaryGoal, String secondaryGoal) {
        double score1 = calculateGoalScore(original, alt1, primaryGoal, secondaryGoal);
        double score2 = calculateGoalScore(original, alt2, primaryGoal, secondaryGoal);
        
        return Double.compare(score2, score1); // Higher score is better
    }
    
    /**
     * Calculates a score for how well an alternative meets the goals
     */
    private double calculateGoalScore(Food original, Food alternative, String primaryGoal, String secondaryGoal) {
        double score = 0;
        
        // Primary goal (weighted more heavily)
        score += getGoalImprovement(original, alternative, primaryGoal) * 2.0;
        
        // Secondary goal if specified
        if (!"None".equals(secondaryGoal)) {
            score += getGoalImprovement(original, alternative, secondaryGoal);
        }
        
        return score;
    }
    
    /**
     * Gets the improvement value for a specific goal
     */
    private double getGoalImprovement(Food original, Food alternative, String goal) {
        switch (goal) {
            case "Increase Fiber":
                return alternative.getFiber() - original.getFiber();
            case "Reduce Calories":
                return original.getCalories() - alternative.getCalories();
            case "Increase Protein":
                return alternative.getProtein() - original.getProtein();
            default:
                return 0;
        }
    }
    
    /**
     * Gets user's recent foods from meal history
     */
    private List<Food> getUserRecentFoods(User user) {
        // This would typically query the user's recent meals from the database
        // For now, return some sample foods
        List<Food> recentFoods = new ArrayList<>();
        
        // Add some common foods that users might eat
        try {
            List<Food> whiteBreads = foodDAO.searchFoodByName("white bread");
            List<Food> regularMilk = foodDAO.searchFoodByName("milk");
            List<Food> regularPasta = foodDAO.searchFoodByName("pasta");
            
            if (!whiteBreads.isEmpty()) recentFoods.add(whiteBreads.get(0));
            if (!regularMilk.isEmpty()) recentFoods.add(regularMilk.get(0));
            if (!regularPasta.isEmpty()) recentFoods.add(regularPasta.get(0));
            
        } catch (Exception e) {
            System.err.println("Error fetching recent foods: " + e.getMessage());
        }
        
        return recentFoods;
    }
    
    /**
     * Determines meal type based on food characteristics or time
     */
    private String determineMealType(Food food) {
        String description = food.getFoodDescription().toLowerCase();
        
        // Simple heuristics to determine meal type
        if (description.contains("cereal") || description.contains("oatmeal") || 
            description.contains("toast") || description.contains("breakfast")) {
            return "Breakfast";
        } else if (description.contains("salad") || description.contains("soup") || 
                   description.contains("sandwich")) {
            return "Lunch";
        } else if (description.contains("dinner") || description.contains("meat") || 
                   description.contains("chicken") || description.contains("beef")) {
            return "Dinner";
        } else {
            return "Snack";
        }
    }
    
    /**
     * Provides general recommendations when no user meal history is available
     */
    private Map<String, List<Food>> getGeneralRecommendations(String primaryGoal, String secondaryGoal, String intensity) {
        Map<String, List<Food>> recommendations = new HashMap<>();
        
        try {
            switch (primaryGoal) {
                case "Increase Fiber":
                    addFiberRecommendations(recommendations);
                    break;
                case "Reduce Calories":
                    addCalorieReductionRecommendations(recommendations);
                    break;
                case "Increase Protein":
                    addProteinRecommendations(recommendations);
                    break;
                default:
                    addGeneralHealthyRecommendations(recommendations);
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error generating recommendations: " + e.getMessage());
        }
        
        return recommendations;
    }
    
    private void addFiberRecommendations(Map<String, List<Food>> recommendations) {
        List<Food> whiteBreads = foodDAO.searchFoodByName("white bread");
        List<Food> wholegrainBreads = foodDAO.searchFoodByName("whole grain bread");
        
        if (!whiteBreads.isEmpty() && !wholegrainBreads.isEmpty()) {
            recommendations.computeIfAbsent("Breakfast", k -> new ArrayList<>());
            recommendations.get("Breakfast").add(whiteBreads.get(0));
            recommendations.get("Breakfast").add(wholegrainBreads.get(0));
        }
    }
    
    private void addCalorieReductionRecommendations(Map<String, List<Food>> recommendations) {
        List<Food> regularMilk = foodDAO.searchFoodByName("milk, whole");
        List<Food> lowFatMilk = foodDAO.searchFoodByName("milk, low fat");
        
        if (!regularMilk.isEmpty() && !lowFatMilk.isEmpty()) {
            recommendations.computeIfAbsent("Snack", k -> new ArrayList<>());
            recommendations.get("Snack").add(regularMilk.get(0));
            recommendations.get("Snack").add(lowFatMilk.get(0));
        }
    }
    
    private void addProteinRecommendations(Map<String, List<Food>> recommendations) {
        List<Food> regularYogurt = foodDAO.searchFoodByName("yogurt");
        List<Food> greekYogurt = foodDAO.searchFoodByName("greek yogurt");
        
        if (!regularYogurt.isEmpty() && !greekYogurt.isEmpty()) {
            recommendations.computeIfAbsent("Breakfast", k -> new ArrayList<>());
            recommendations.get("Breakfast").add(regularYogurt.get(0));
            recommendations.get("Breakfast").add(greekYogurt.get(0));
        }
    }
    
    private void addGeneralHealthyRecommendations(Map<String, List<Food>> recommendations) {
        // Add some general healthy swaps
        addFiberRecommendations(recommendations);
        addCalorieReductionRecommendations(recommendations);
    }
}
