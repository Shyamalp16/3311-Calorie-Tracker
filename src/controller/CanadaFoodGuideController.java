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
        Date startDate = getStartDateForTimePeriod(timePeriod);
        Date endDate = new Date();
        
        List<Meal> allMeals = dashboardController.getMealsInDateRange(startDate, endDate);
        
        PlateData plateData = processMeals(allMeals);
        
        return calculateFinalPercentages(plateData);
    }

    private Date getStartDateForTimePeriod(String timePeriod) {
        Calendar cal = Calendar.getInstance();
        switch (timePeriod) {
            case "Today":
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                return cal.getTime();
            case "Last 7 Days":
                cal.add(Calendar.DAY_OF_MONTH, -7);
                return cal.getTime();
            case "Last 30 Days":
                cal.add(Calendar.DAY_OF_MONTH, -30);
                return cal.getTime();
            case "All Time":
            default:
                return new Date(0); // The epoch
        }
    }

    private PlateData processMeals(List<Meal> allMeals) {
        PlateData plateData = new PlateData();

        for (Meal meal : allMeals) {
            List<MealItem> items = mealController.getMealItemsByMealId(meal.getMealId());
            for (MealItem item : items) {
                processMealItem(item, plateData);
            }
        }
        return plateData;
    }

    private void processMealItem(MealItem item, PlateData plateData) {
        Food food = mealController.getFoodById(item.getFoodId());
        if (food == null) {
            return;
        }

        String databaseFoodGroup = mealController.getFoodGroupById(food.getFoodID());
        String cfgCategory = mapToCFGCategory(databaseFoodGroup);
        
        double portionWeight = calculateCFGPortionWeight(item.getQuantity(), item.getUnit(), cfgCategory);
        
        plateData.addPortion(cfgCategory, portionWeight);
    }

    private Map<String, Double> calculateFinalPercentages(PlateData plateData) {
        Map<String, Double> userPlateData = new HashMap<>();
        double totalPortions = plateData.getTotalPortions();

        if (totalPortions > 0) {
            for (Map.Entry<String, Double> entry : plateData.getFoodGroupPortions().entrySet()) {
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
                    recommendations.append(getDetailedRecommendation(category, true, gap));
                } else {
                    recommendations.append("• Consider reducing ").append(category.toLowerCase())
                        .append(" by ").append(String.format("%.0f", Math.abs(gap))).append("%");
                    recommendations.append(getDetailedRecommendation(category, false, gap));
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

    private String getDetailedRecommendation(String category, boolean isIncrease, double gap) {
        return RecommendationLogic.getRecommendation(category, isIncrease, gap);
    }

    private static class RecommendationLogic {
        private static final Map<String, RecommendationFunction> increaseRecommendations = new HashMap<>();
        private static final Map<String, RecommendationFunction> decreaseRecommendations = new HashMap<>();

        static {
            increaseRecommendations.put("Vegetables & Fruits", (absGap) -> {
                if (absGap > 20) return "(Aim to make half your plate vegetables and fruits at every meal. Explore new recipes with diverse produce.)";
                if (absGap > 10) return "(Incorporate more fruits as snacks and add extra vegetables to your main dishes.)";
                return "(Try adding one more serving of vegetables or fruit to your daily intake.)";
            });
            increaseRecommendations.put("Whole Grains", (absGap) -> {
                if (absGap > 15) return "(Switch to whole grain options for all breads, pastas, and cereals. Explore quinoa, oats, and brown rice.)";
                if (absGap > 7) return "(Choose whole grain bread and pasta more often. Opt for whole grain snacks.)";
                return "(Consider swapping one refined grain serving for a whole grain alternative daily.)";
            });
            increaseRecommendations.put("Protein Foods", (absGap) -> {
                if (absGap > 10) return "(Prioritize plant-based proteins like beans, lentils, and tofu. Include lean meats, fish, and eggs regularly.)";
                if (absGap > 5) return "(Ensure a good source of protein at each meal. Try adding nuts or seeds to snacks.)";
                return "(Look for opportunities to add a little more protein, like an extra egg or a handful of almonds.)";
            });
            increaseRecommendations.put("Dairy & Alternatives", (absGap) -> {
                if (absGap > 5) return "(Include milk, yogurt, or fortified plant-based alternatives daily. Consider cheese in moderation.)";
                return "(Ensure you're meeting your calcium needs through dairy or fortified alternatives.)";
            });

            decreaseRecommendations.put("Vegetables & Fruits", (absGap) -> {
                if (absGap > 20) return "(While good, ensure variety and balance with other food groups. You might be over-relying on this group.)";
                if (absGap > 10) return "(Review your portion sizes for vegetables and fruits to ensure balance across all food groups.)";
                return "(Slightly adjust portions to make room for other essential nutrients.)";
            });
            decreaseRecommendations.put("Whole Grains", (absGap) -> {
                if (absGap > 15) return "(Evaluate your intake of large portions of grains. Balance with more vegetables and protein.)";
                if (absGap > 7) return "(Consider reducing portion sizes of grain products slightly to diversify your plate.)";
                return "(A minor adjustment in grain portions could help balance your overall intake.)";
            });
            decreaseRecommendations.put("Protein Foods", (absGap) -> {
                if (absGap > 10) return "(Ensure you're not consuming excessive amounts of protein, especially from processed sources. Focus on variety.)";
                if (absGap > 5) return "(Balance your protein intake with ample vegetables and whole grains.)";
                return "(A small reduction in protein portion size might be beneficial for overall balance.)";
            });
            decreaseRecommendations.put("Dairy & Alternatives", (absGap) -> {
                if (absGap > 5) return "(Review your dairy intake; excessive amounts might displace other important food groups.)";
                return "(Consider if your dairy portions are balanced with the rest of your meal.)";
            });
        }

        public static String getRecommendation(String category, boolean isIncrease, double gap) {
            double absGap = Math.abs(gap);
            Map<String, RecommendationFunction> selectedMap = isIncrease ? increaseRecommendations : decreaseRecommendations;
            RecommendationFunction func = selectedMap.get(category);
            return func != null ? func.getRecommendation(absGap) : "";
        }

        @FunctionalInterface
        interface RecommendationFunction {
            String getRecommendation(double absGap);
        }
    }
    
    private static class PlateData {
        private final Map<String, Double> foodGroupPortions = new HashMap<>();
        private double totalPortions = 0.0;

        public void addPortion(String category, double weight) {
            foodGroupPortions.put(category, foodGroupPortions.getOrDefault(category, 0.0) + weight);
            totalPortions += weight;
        }

        public Map<String, Double> getFoodGroupPortions() {
            return foodGroupPortions;
        }

        public double getTotalPortions() {
            return totalPortions;
        }
    }
}
