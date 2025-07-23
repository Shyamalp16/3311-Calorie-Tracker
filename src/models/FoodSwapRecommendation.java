package models;

public class FoodSwapRecommendation {
    private Food originalFood;
    private Food recommendedFood;
    private double quantity;
    private String unit;
    private String swapReason;
    private double calorieChange;
    private double proteinChange;
    private double carbsChange;
    private double fatsChange;
    private double fiberChange;

    public FoodSwapRecommendation(Food originalFood, Food recommendedFood, double quantity, String unit, String swapReason) {
        this.originalFood = originalFood;
        this.recommendedFood = recommendedFood;
        this.quantity = quantity;
        this.unit = unit;
        this.swapReason = swapReason;
        
        // Calculate changes
        this.calorieChange = recommendedFood.getCalories() - originalFood.getCalories();
        this.proteinChange = recommendedFood.getProtein() - originalFood.getProtein();
        this.carbsChange = recommendedFood.getCarbs() - originalFood.getCarbs();
        this.fatsChange = recommendedFood.getFats() - originalFood.getFats();
        this.fiberChange = recommendedFood.getFiber() - originalFood.getFiber();
    }

    // Getters
    public Food getOriginalFood() { return originalFood; }
    public Food getRecommendedFood() { return recommendedFood; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public String getSwapReason() { return swapReason; }
    public double getCalorieChange() { return calorieChange; }
    public double getProteinChange() { return proteinChange; }
    public double getCarbsChange() { return carbsChange; }
    public double getFatsChange() { return fatsChange; }
    public double getFiberChange() { return fiberChange; }

    // Utility methods for displaying changes
    public String getCalorieChangeDisplay() {
        return String.format("%+.0f", calorieChange);
    }

    public String getProteinChangeDisplay() {
        return String.format("%+.1fg", proteinChange);
    }

    public String getCarbsChangeDisplay() {
        return String.format("%+.1fg", carbsChange);
    }

    public String getFatsChangeDisplay() {
        return String.format("%+.1fg", fatsChange);
    }

    public String getFiberChangeDisplay() {
        return String.format("%+.1fg", fiberChange);
    }

    public String getPercentageChange(String nutrientType) {
        double originalValue = switch (nutrientType.toLowerCase()) {
            case "calories" -> originalFood.getCalories();
            case "protein" -> originalFood.getProtein();
            case "carbs" -> originalFood.getCarbs();
            case "fats" -> originalFood.getFats();
            case "fiber" -> originalFood.getFiber();
            default -> 0;
        };
        
        double change = switch (nutrientType.toLowerCase()) {
            case "calories" -> calorieChange;
            case "protein" -> proteinChange;
            case "carbs" -> carbsChange;
            case "fats" -> fatsChange;
            case "fiber" -> fiberChange;
            default -> 0;
        };
        
        if (originalValue == 0) return "N/A";
        double percentage = (change / originalValue) * 100;
        return String.format("%+.0f%%", percentage);
    }
} 