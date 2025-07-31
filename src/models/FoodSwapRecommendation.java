package models;

public class FoodSwapRecommendation {
    private Food originalFood;
    private Food recommendedFood;
    private double quantity;
    private String unit;
    private String swapReason;
    private String mealType;
    private double calorieChange;
    private double proteinChange;
    private double carbsChange;
    private double fatsChange;
    private double fiberChange;
    private double vitaminAChange;
    private double vitaminBChange;
    private double vitaminCChange;
    private double vitaminDChange;

    public FoodSwapRecommendation(Food originalFood, Food recommendedFood, double quantity, String unit, String swapReason) {
        this.originalFood = originalFood;
        this.recommendedFood = recommendedFood;
        this.quantity = quantity;
        this.unit = unit;
        this.swapReason = swapReason;
        this.mealType = "meal"; // Default fallback
        
        calculateNutritionalChanges();
    }

    public FoodSwapRecommendation(Food originalFood, Food recommendedFood, double quantity, String unit, String swapReason, String mealType) {
        this.originalFood = originalFood;
        this.recommendedFood = recommendedFood;
        this.quantity = quantity;
        this.unit = unit;
        this.swapReason = swapReason;
        this.mealType = mealType != null ? mealType : "meal";
        
        calculateNutritionalChanges();
    }

    private void calculateNutritionalChanges() {
        
        this.calorieChange = recommendedFood.getCalories() - originalFood.getCalories();
        this.proteinChange = recommendedFood.getProtein() - originalFood.getProtein();
        this.carbsChange = recommendedFood.getCarbs() - originalFood.getCarbs();
        this.fatsChange = recommendedFood.getFats() - originalFood.getFats();
        this.fiberChange = recommendedFood.getFiber() - originalFood.getFiber();
        this.vitaminAChange = recommendedFood.getVitaminA() - originalFood.getVitaminA();
        this.vitaminBChange = recommendedFood.getVitaminB() - originalFood.getVitaminB();
        this.vitaminCChange = recommendedFood.getVitaminC() - originalFood.getVitaminC();
        this.vitaminDChange = recommendedFood.getVitaminD() - originalFood.getVitaminD();
    }

    public Food getOriginalFood() { return originalFood; }
    public Food getRecommendedFood() { return recommendedFood; }
    public Food getSwappedFood() { return recommendedFood; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public String getSwapReason() { return swapReason; }
    public String getMealType() { return mealType; }
    public double getCalorieChange() { return calorieChange; }
    public double getProteinChange() { return proteinChange; }
    public double getCarbsChange() { return carbsChange; }
    public double getFatsChange() { return fatsChange; }
    public double getFiberChange() { return fiberChange; }
    public double getVitaminAChange() { return vitaminAChange; }
    public double getVitaminBChange() { return vitaminBChange; }
    public double getVitaminCChange() { return vitaminCChange; }
    public double getVitaminDChange() { return vitaminDChange; }

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

    public String getVitaminAChangeDisplay() {
        return String.format("%+.0fµg", vitaminAChange);
    }

    public String getVitaminBChangeDisplay() {
        return String.format("%+.0fmg", vitaminBChange);
    }

    public String getVitaminCChangeDisplay() {
        return String.format("%+.0fmg", vitaminCChange);
    }

    public String getVitaminDChangeDisplay() {
        return String.format("%+.0fµg", vitaminDChange);
    }

    public String getPercentageChange(String nutrientType) {
        double originalValue = switch (nutrientType.toLowerCase()) {
            case "calories" -> originalFood.getCalories();
            case "protein" -> originalFood.getProtein();
            case "carbs" -> originalFood.getCarbs();
            case "fats" -> originalFood.getFats();
            case "fiber" -> originalFood.getFiber();
            case "vitamina" -> originalFood.getVitaminA();
            case "vitaminb" -> originalFood.getVitaminB();
            case "vitaminc" -> originalFood.getVitaminC();
            case "vitamind" -> originalFood.getVitaminD();
            default -> 0;
        };
        
        double change = switch (nutrientType.toLowerCase()) {
            case "calories" -> calorieChange;
            case "protein" -> proteinChange;
            case "carbs" -> carbsChange;
            case "fats" -> fatsChange;
            case "fiber" -> fiberChange;
            case "vitamina" -> vitaminAChange;
            case "vitaminb" -> vitaminBChange;
            case "vitaminc" -> vitaminCChange;
            case "vitamind" -> vitaminDChange;
            default -> 0;
        };
        
        if (originalValue == 0) return "N/A";
        double percentage = (change / originalValue) * 100;
        return String.format("%+.0f%%", percentage);
    }
} 