package controller;

import models.Food;
import models.Meal;
import models.MealItem;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CanadaFoodGuideController {

    private IMealController mealController;
    private DashboardController dashboardController;

    public CanadaFoodGuideController(IMealController mealController, DashboardController dashboardController) {
        this.mealController = mealController;
        this.dashboardController = dashboardController;
    }

    public Map<String, Double> calculateUserPlateData(String timePeriod) {
        Map<String, Double> userPlateData = new HashMap<>();
        
        Calendar cal = Calendar.getInstance();
        Date endDate = new Date();
        Date startDate;
        
        switch (timePeriod) {
            case "Today":
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                startDate = cal.getTime();
                break;
            case "Last 7 Days":
                cal.add(Calendar.DAY_OF_MONTH, -7);
                startDate = cal.getTime();
                break;
            case "Last 30 Days":
                cal.add(Calendar.DAY_OF_MONTH, -30);
                startDate = cal.getTime();
                break;
            case "All Time":
            default:
                startDate = new Date(0);
                break;
        }
        
        List<Meal> allMeals = dashboardController.getMealsInDateRange(startDate, endDate);
        Map<String, Double> foodGroupPortions = new HashMap<>();
        double totalPortions = 0.0;

        for (Meal meal : allMeals) {
            List<MealItem> items = mealController.getMealItemsByMealId(meal.getMealId());
            
            for (MealItem item : items) {
                Food food = mealController.getFoodById(item.getFoodId());
                if (food != null) {
                    String databaseFoodGroup = mealController.getFoodGroupById(food.getFoodID());
                    String cfgCategory = mapToCFGCategory(databaseFoodGroup);
                    
                    double portionWeight = calculateCFGPortionWeight(item.getQuantity(), item.getUnit(), cfgCategory);
                    
                    foodGroupPortions.put(cfgCategory, foodGroupPortions.getOrDefault(cfgCategory, 0.0) + portionWeight);
                    totalPortions += portionWeight;
                }
            }
        }

        if (totalPortions > 0) {
            for (Map.Entry<String, Double> entry : foodGroupPortions.entrySet()) {
                double percentage = entry.getValue() / totalPortions * 100;
                userPlateData.put(entry.getKey(), percentage);
            }
        } else {
            userPlateData.put("Vegetables & Fruits", 0.0);
            userPlateData.put("Whole Grains", 0.0);
            userPlateData.put("Protein Foods", 0.0);
            userPlateData.put("Dairy & Alternatives", 0.0);
        }
        
        return userPlateData;
    }

    private String mapToCFGCategory(String databaseFoodGroup) {
        if (databaseFoodGroup == null || databaseFoodGroup.equals("Unknown")) {
            return "Others";
        }
        
        String lowerGroup = databaseFoodGroup.toLowerCase().trim();
        
        if (lowerGroup.contains("vegetable") || lowerGroup.contains("fruit") || 
            lowerGroup.contains("berries") || lowerGroup.contains("citrus") ||
            lowerGroup.equals("vegetables and vegetable products") ||
            lowerGroup.equals("fruits and fruit juices")) {
            return "Vegetables & Fruits";
        }
        
        if (lowerGroup.contains("grain") || lowerGroup.contains("cereal") ||
            lowerGroup.contains("bread") || lowerGroup.contains("pasta") ||
            lowerGroup.contains("rice") || lowerGroup.contains("wheat") ||
            lowerGroup.equals("cereal grains and pasta") ||
            lowerGroup.equals("baked products")) {
            return "Whole Grains";
        }
        
        if (lowerGroup.contains("dairy") || lowerGroup.contains("milk") ||
            lowerGroup.contains("cheese") || lowerGroup.contains("yogurt") ||
            lowerGroup.equals("dairy and egg products")) {
            return "Dairy & Alternatives";
        }
        
        if (lowerGroup.contains("meat") || lowerGroup.contains("poultry") ||
            lowerGroup.contains("fish") || lowerGroup.contains("seafood") ||
            lowerGroup.contains("egg") || lowerGroup.contains("bean") ||
            lowerGroup.contains("nut") || lowerGroup.contains("seed") ||
            lowerGroup.contains("legume") || lowerGroup.contains("protein") ||
            lowerGroup.equals("poultry products") ||
            lowerGroup.equals("finfish and shellfish products") ||
            lowerGroup.equals("legumes and legume products") ||
            lowerGroup.equals("nut and seed products") ||
            lowerGroup.equals("beef products") ||
            lowerGroup.equals("pork products") ||
            lowerGroup.equals("lamb, veal, and game products") ||
            lowerGroup.equals("sausages and luncheon meats")) {
            return "Protein Foods";
        }
        
        return "Others";
    }

    private double calculateCFGPortionWeight(double quantity, String unit, String cfgCategory) {
        double baseWeight = quantity;
        
        switch (unit.toLowerCase()) {
            case "cup":
            case "cups":
                baseWeight = quantity * 1.0; 
                break;
            case "tbsp":
            case "tablespoon":
                baseWeight = quantity * 0.0625; 
                break;
            case "tsp":
            case "teaspoon":
                baseWeight = quantity * 0.0208;
                break;
            case "g":
            case "gram":
            case "grams":
                baseWeight = quantity * 0.001; 
                break;
            case "ml":
            case "milliliter":
                baseWeight = quantity * 0.00423; 
                break;
            case "piece":
            case "pieces":
            case "item":
            case "items":
            default:
                baseWeight = quantity * 1.0; 
        }
        
        double finalWeight;
        switch (cfgCategory) {
            case "Vegetables & Fruits":
                finalWeight = baseWeight * 1.0; 
                break;
            case "Whole Grains":
                finalWeight = baseWeight * 1.0; 
                break;
            case "Protein Foods":
                finalWeight = baseWeight * 1.2; 
                break;
            case "Dairy & Alternatives":
                finalWeight = baseWeight * 1.0; 
                break;
            default:
                finalWeight = baseWeight * 0.5; 
                break;
        }
        
        return finalWeight;
    }

    public String generateCFGRecommendations(String timePeriod) {
        Map<String, Double> userData = calculateUserPlateData(timePeriod);
        StringBuilder recommendations = new StringBuilder();
        
        Map<String, Double> cfgTargets = new HashMap<>();
        cfgTargets.put("Vegetables & Fruits", 50.0);
        cfgTargets.put("Whole Grains", 25.0);
        cfgTargets.put("Protein Foods", 17.5);
        cfgTargets.put("Dairy & Alternatives", 7.5);
        
        boolean hasRecommendations = false;
        
        for (Map.Entry<String, Double> target : cfgTargets.entrySet()) {
            String category = target.getKey();
            double targetPercent = target.getValue();
            double userPercent = userData.getOrDefault(category, 0.0);
            double gap = targetPercent - userPercent;
            
            if (Math.abs(gap) > 5.0) { 
                if (hasRecommendations) recommendations.append(" ");
                
                if (gap > 0) {
                    recommendations.append("• Increase ").append(category.toLowerCase())
                        .append(" by ").append(String.format("%.0f", gap)).append("% ");
                    
                    switch (category) {
                        case "Vegetables & Fruits":
                            recommendations.append("(add more salads, fruits as snacks, vegetable-based meals)");
                            break;
                        case "Whole Grains":
                            recommendations.append("(choose brown rice, whole wheat bread, oatmeal)");
                            break;
                        case "Protein Foods":
                            recommendations.append("(include lean meats, fish, beans, nuts)");
                            break;
                        case "Dairy & Alternatives":
                            recommendations.append("(add milk, yogurt, cheese, or fortified plant alternatives)");
                            break;
                    }
                } else {
                    recommendations.append("• Consider reducing ").append(category.toLowerCase())
                        .append(" by ").append(String.format("%.0f", Math.abs(gap))).append("%");
                }
                hasRecommendations = true;
            }
        }
        
        if (!hasRecommendations) {
            recommendations.append("🎉 Great job! Your diet aligns well with Canada Food Guide recommendations. ")
                .append("Keep maintaining this balanced approach to nutrition.");
        } else {
            recommendations.insert(0, "To better align with CFG recommendations: ");
        }
        
        return recommendations.toString();
    }
}
