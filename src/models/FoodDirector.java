package models;

import java.util.Map;

public class FoodDirector {
    private FoodBuilder builder;

    public FoodDirector(FoodBuilder builder) {
        this.builder = builder;
    }

    public void changeBuilder(FoodBuilder builder) {
        this.builder = builder;
    }

    public Food constructBasicFood(int id, String description, double calories) {
        builder.reset();
        return builder
            .setFoodID(id)
            .setFoodDescription(description)
            .setCalories(calories)
            .getResult();
    }

    public Food constructMacronutrientFood(int id, String description, double protein, double carbs, double fats) {
        builder.reset();
        return builder
            .setFoodID(id)
            .setFoodDescription(description)
            .setProtein(protein)
            .setCarbs(carbs)
            .setFats(fats)
            .getResult();
    }

    public Food constructCompleteFoodFromDatabase(int id, String description, double calories, 
                                                 double protein, double carbs, double fats, 
                                                 double fiber, double sodium, double sugars, 
                                                 double saturatedFats, double iron, double calcium, 
                                                 double vitaminA, double vitaminB, double vitaminC, 
                                                 double vitaminD, String foodGroup, String foodSource) {
        builder.reset();
        return builder
            .setFoodID(id)
            .setFoodDescription(description)
            .setCalories(calories)
            .setProtein(protein)
            .setCarbs(carbs)
            .setFats(fats)
            .setFiber(fiber)
            .setSodium(sodium)
            .setSugars(sugars)
            .setSaturatedFats(saturatedFats)
            .setIron(iron)
            .setCalcium(calcium)
            .setVitaminA(vitaminA)
            .setVitaminB(vitaminB)
            .setVitaminC(vitaminC)
            .setVitaminD(vitaminD)
            .setFoodGroup(foodGroup)
            .setFoodSource(foodSource)
            .getResult();
    }

    public Food constructCustomFood(int id, String description, Map<String, Double> nutrients) {
        builder.reset();
        builder
            .setFoodID(id)
            .setFoodDescription(description);
        
        for (Map.Entry<String, Double> nutrient : nutrients.entrySet()) {
            String key = nutrient.getKey().toLowerCase();
            double value = nutrient.getValue();
            
            switch (key) {
                case "calories" -> builder.setCalories(value);
                case "protein" -> builder.setProtein(value);
                case "carbs", "carbohydrates" -> builder.setCarbs(value);
                case "fats", "fat" -> builder.setFats(value);
                case "fiber" -> builder.setFiber(value);
                case "sodium" -> builder.setSodium(value);
                case "sugars" -> builder.setSugars(value);
                case "saturatedfats", "saturated_fats" -> builder.setSaturatedFats(value);
                case "iron" -> builder.setIron(value);
                case "calcium" -> builder.setCalcium(value);
                case "vitamina", "vitamin_a" -> builder.setVitaminA(value);
                case "vitaminb", "vitamin_b" -> builder.setVitaminB(value);
                case "vitaminc", "vitamin_c" -> builder.setVitaminC(value);
                case "vitamind", "vitamin_d" -> builder.setVitaminD(value);
                default -> builder.addNutrient(nutrient.getKey(), value);
            }
        }
        
        return builder.getResult();
    }

    public Food constructHighProteinFood(int id, String description, double protein, double calories) {
        builder.reset();
        double carbs = Math.max(0, (calories - (protein * 4)) / 4 * 0.3);
        double fats = Math.max(0, (calories - (protein * 4) - (carbs * 4)) / 9);
        
        return builder
            .setFoodID(id)
            .setFoodDescription(description)
            .setCalories(calories)
            .setProtein(protein)
            .setCarbs(carbs)
            .setFats(fats)
            .setFoodGroup("Protein Foods")
            .getResult();
    }

    public Food constructLowCalorieFood(int id, String description, double maxCalories) {
        builder.reset();
        double fiber = Math.min(maxCalories * 0.1, 5.0);
        double protein = Math.min(maxCalories * 0.2, maxCalories / 4);
        double carbs = Math.max(0, (maxCalories - (protein * 4)) / 4);
        
        return builder
            .setFoodID(id)
            .setFoodDescription(description)
            .setCalories(maxCalories)
            .setProtein(protein)
            .setCarbs(carbs)
            .setFats(0.5)
            .setFiber(fiber)
            .setFoodGroup("Vegetables & Fruits")
            .getResult();
    }
}