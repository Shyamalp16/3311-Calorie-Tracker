package models;

public class FoodSwapGoal {
    public enum NutrientType {
        INCREASE_FIBER("Increase Fiber"),
        REDUCE_CALORIES("Reduce Calories"),
        INCREASE_PROTEIN("Increase Protein"),
        REDUCE_FAT("Reduce Fat"),
        REDUCE_CARBS("Reduce Carbs"),
        INCREASE_CARBS("Increase Carbs");

        private final String displayName;

        NutrientType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum IntensityLevel {
        SLIGHTLY_MORE("Slightly more", 1.1, 0.05),
        MODERATELY_MORE("Moderately more", 1.25, 0.1),
        SIGNIFICANTLY_MORE("Significantly more", 1.5, 0.15);

        private final String displayName;
        private final double multiplier;
        private final double tolerance;

        IntensityLevel(String displayName, double multiplier, double tolerance) {
            this.displayName = displayName;
            this.multiplier = multiplier;
            this.tolerance = tolerance;
        }

        public String getDisplayName() { return displayName; }
        public double getMultiplier() { return multiplier; }
        public double getTolerance() { return tolerance; }
    }

    private NutrientType nutrientType;
    private IntensityLevel intensityLevel;
    private Double specificValue;
    private Double specificPercentage;

    public FoodSwapGoal(NutrientType nutrientType, IntensityLevel intensityLevel) {
        this.nutrientType = nutrientType;
        this.intensityLevel = intensityLevel;
    }

    public FoodSwapGoal(NutrientType nutrientType, double specificValue) {
        this.nutrientType = nutrientType;
        this.specificValue = specificValue;
    }

    public FoodSwapGoal(NutrientType nutrientType, double specificPercentage, boolean isPercentage) {
        this.nutrientType = nutrientType;
        if (isPercentage) {
            this.specificPercentage = specificPercentage;
        } else {
            this.specificValue = specificPercentage;
        }
    }

    public NutrientType getNutrientType() { return nutrientType; }
    public IntensityLevel getIntensityLevel() { return intensityLevel; }
    public Double getSpecificValue() { return specificValue; }
    public Double getSpecificPercentage() { return specificPercentage; }

    public void setNutrientType(NutrientType nutrientType) { this.nutrientType = nutrientType; }
    public void setIntensityLevel(IntensityLevel intensityLevel) { this.intensityLevel = intensityLevel; }
    public void setSpecificValue(Double specificValue) { this.specificValue = specificValue; }
    public void setSpecificPercentage(Double specificPercentage) { this.specificPercentage = specificPercentage; }

    public boolean isIncrease() {
        return nutrientType == NutrientType.INCREASE_FIBER || 
               nutrientType == NutrientType.INCREASE_PROTEIN ||
               nutrientType == NutrientType.INCREASE_CARBS;
    }

    public boolean isDecrease() {
        return nutrientType == NutrientType.REDUCE_CALORIES || 
               nutrientType == NutrientType.REDUCE_FAT ||
               nutrientType == NutrientType.REDUCE_CARBS;
    }
} 