package models;

import java.util.Map;

public interface FoodBuilder {
    FoodBuilder setFoodID(int foodID);
    FoodBuilder setFoodDescription(String foodDescription);
    FoodBuilder setCalories(double calories);
    FoodBuilder setProtein(double protein);
    FoodBuilder setCarbs(double carbs);
    FoodBuilder setFats(double fats);
    FoodBuilder setFiber(double fiber);
    FoodBuilder setSodium(double sodium);
    FoodBuilder setSugars(double sugars);
    FoodBuilder setSaturatedFats(double saturatedFats);
    FoodBuilder setIron(double iron);
    FoodBuilder setCalcium(double calcium);
    FoodBuilder setVitaminA(double vitaminA);
    FoodBuilder setVitaminB(double vitaminB);
    FoodBuilder setVitaminC(double vitaminC);
    FoodBuilder setVitaminD(double vitaminD);
    FoodBuilder setFoodGroup(String foodGroup);
    FoodBuilder setFoodSource(String foodSource);
    FoodBuilder setNutrients(Map<String, Double> nutrients);
    FoodBuilder addNutrient(String name, double value);
    void reset();
    Food getResult();
}