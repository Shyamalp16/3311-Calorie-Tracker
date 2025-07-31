package models;

import java.util.Map;
import java.util.HashMap;

public class Food {
    private int foodID;
    private String foodDescription;
    private double calories;
    private double protein;
    private double carbs;
    private double fats;
    private double fiber;
    private double sodium;
    private double sugars;
    private double saturatedFats;
    private double iron;
    private double calcium;
    private double vitaminA;
    private double vitaminB;
    private double vitaminC;
    private double vitaminD;
    private String foodGroup;
    private String foodSource;
    private Map<String, Double> nutrients;

    public Food(int foodID, String foodDescription, double calories, double protein, double carbs, double fats, double fiber, double sodium, double sugars, double saturatedFats, double iron, double calcium, double vitaminA, double vitaminB, double vitaminC, double vitaminD) {
        this(foodID, foodDescription, calories, protein, carbs, fats, fiber, sodium, sugars, saturatedFats, iron, calcium, vitaminA, vitaminB, vitaminC, vitaminD, "Unknown", "Unknown", new HashMap<>());
    }

    public Food(int foodID, String foodDescription, double calories, double protein, double carbs, double fats, double fiber, double sodium, double sugars, double saturatedFats, double iron, double calcium, double vitaminA, double vitaminB, double vitaminC, double vitaminD, String foodGroup, String foodSource, Map<String, Double> nutrients) {
        this.foodID = foodID;
        this.foodDescription = foodDescription;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
        this.fiber = fiber;
        this.sodium = sodium;
        this.sugars = sugars;
        this.saturatedFats = saturatedFats;
        this.iron = iron;
        this.calcium = calcium;
        this.vitaminA = vitaminA;
        this.vitaminB = vitaminB;
        this.vitaminC = vitaminC;
        this.vitaminD = vitaminD;
        this.foodGroup = foodGroup;
        this.foodSource = foodSource;
        this.nutrients = nutrients;
    }

    private Food(Builder builder) {
        this.foodID = builder.foodID;
        this.foodDescription = builder.foodDescription;
        this.calories = builder.calories;
        this.protein = builder.protein;
        this.carbs = builder.carbs;
        this.fats = builder.fats;
        this.fiber = builder.fiber;
        this.sodium = builder.sodium;
        this.sugars = builder.sugars;
        this.saturatedFats = builder.saturatedFats;
        this.iron = builder.iron;
        this.calcium = builder.calcium;
        this.vitaminA = builder.vitaminA;
        this.vitaminB = builder.vitaminB;
        this.vitaminC = builder.vitaminC;
        this.vitaminD = builder.vitaminD;
        this.foodGroup = builder.foodGroup;
        this.foodSource = builder.foodSource;
        this.nutrients = new HashMap<>(builder.nutrients);
    }

    public int getFoodID() {
        return foodID;
    }

    public String getFoodDescription() {
        return foodDescription;
    }

    public double getCalories() {
        return calories;
    }

    public double getProtein() {
        return protein;
    }

    public double getCarbs() {
        return carbs;
    }

    public double getFats() {
        return fats;
    }

    public double getFiber() {
        return fiber;
    }

    public double getSodium() {
        return sodium;
    }

    public double getSugars() {
        return sugars;
    }

    public double getSaturatedFats() {
        return saturatedFats;
    }

    public double getIron() {
        return iron;
    }

    public double getCalcium() {
        return calcium;
    }

    public double getVitaminA() {
        return vitaminA;
    }

    public double getVitaminB() {
        return vitaminB;
    }

    public double getVitaminC() {
        return vitaminC;
    }

    public double getVitaminD() {
        return vitaminD;
    }

    public String getFoodGroup() {
        return foodGroup;
    }

    public String getFoodSource() {
        return foodSource;
    }

    public Map<String, Double> getNutrients() {
        return nutrients;
    }

    public double getNutrientValue(String nutrientName) {
        return nutrients.getOrDefault(nutrientName, 0.0);
    }

    public void setFoodID(int foodID) {
        this.foodID = foodID;
    }

    public void setFoodDescription(String foodDescription) {
        this.foodDescription = foodDescription;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public void setFats(double fats) {
        this.fats = fats;
    }

    public void setFiber(double fiber) {
        this.fiber = fiber;
    }

    public void setSodium(double sodium) {
        this.sodium = sodium;
    }

    public void setSugars(double sugars) {
        this.sugars = sugars;
    }

    public void setSaturatedFats(double saturatedFats) {
        this.saturatedFats = saturatedFats;
    }

    public void setIron(double iron) {
        this.iron = iron;
    }

    public void setCalcium(double calcium) {
        this.calcium = calcium;
    }

    public void setVitaminA(double vitaminA) {
        this.vitaminA = vitaminA;
    }

    public void setVitaminB(double vitaminB) {
        this.vitaminB = vitaminB;
    }

    public void setVitaminC(double vitaminC) {
        this.vitaminC = vitaminC;
    }

    public void setVitaminD(double vitaminD) {
        this.vitaminD = vitaminD;
    }

    public void setFoodGroup(String foodGroup) {
        this.foodGroup = foodGroup;
    }

    public void setFoodSource(String foodSource) {
        this.foodSource = foodSource;
    }

    public void setNutrients(Map<String, Double> nutrients) {
        this.nutrients = nutrients;
    }

    public static class Builder {
        private int foodID;
        private String foodDescription = "";
        private double calories = 0.0;
        private double protein = 0.0;
        private double carbs = 0.0;
        private double fats = 0.0;
        private double fiber = 0.0;
        private double sodium = 0.0;
        private double sugars = 0.0;
        private double saturatedFats = 0.0;
        private double iron = 0.0;
        private double calcium = 0.0;
        private double vitaminA = 0.0;
        private double vitaminB = 0.0;
        private double vitaminC = 0.0;
        private double vitaminD = 0.0;
        private String foodGroup = "Unknown";
        private String foodSource = "Unknown";
        private Map<String, Double> nutrients = new HashMap<>();

        public Builder id(int foodID) {
            this.foodID = foodID;
            return this;
        }

        public Builder description(String foodDescription) {
            this.foodDescription = foodDescription;
            return this;
        }

        public Builder calories(double calories) {
            this.calories = calories;
            return this;
        }

        public Builder protein(double protein) {
            this.protein = protein;
            return this;
        }

        public Builder carbs(double carbs) {
            this.carbs = carbs;
            return this;
        }

        public Builder fats(double fats) {
            this.fats = fats;
            return this;
        }

        public Builder fiber(double fiber) {
            this.fiber = fiber;
            return this;
        }

        public Builder sodium(double sodium) {
            this.sodium = sodium;
            return this;
        }

        public Builder sugars(double sugars) {
            this.sugars = sugars;
            return this;
        }

        public Builder saturatedFats(double saturatedFats) {
            this.saturatedFats = saturatedFats;
            return this;
        }

        public Builder iron(double iron) {
            this.iron = iron;
            return this;
        }

        public Builder calcium(double calcium) {
            this.calcium = calcium;
            return this;
        }

        public Builder vitaminA(double vitaminA) {
            this.vitaminA = vitaminA;
            return this;
        }

        public Builder vitaminB(double vitaminB) {
            this.vitaminB = vitaminB;
            return this;
        }

        public Builder vitaminC(double vitaminC) {
            this.vitaminC = vitaminC;
            return this;
        }

        public Builder vitaminD(double vitaminD) {
            this.vitaminD = vitaminD;
            return this;
        }

        public Builder foodGroup(String foodGroup) {
            this.foodGroup = foodGroup;
            return this;
        }

        public Builder foodSource(String foodSource) {
            this.foodSource = foodSource;
            return this;
        }

        public Builder nutrients(Map<String, Double> nutrients) {
            this.nutrients = new HashMap<>(nutrients);
            return this;
        }

        public Builder addNutrient(String name, double value) {
            this.nutrients.put(name, value);
            return this;
        }

//        public Food build() {
//            if (calories == 0.0 && (protein > 0 || carbs > 0 || fats > 0)) {
//                calories = (protein * 4) + (carbs * 4) + (fats * 9);
//            }
//            return new Food(this);
//        }
    }
}
