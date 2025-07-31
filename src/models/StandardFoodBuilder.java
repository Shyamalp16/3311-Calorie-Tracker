package models;

import java.util.HashMap;
import java.util.Map;

public class StandardFoodBuilder implements FoodBuilder {
    private Food food;

    public StandardFoodBuilder() {
        reset();
    }

    @Override
    public void reset() {
        this.food = new Food(0, "", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    @Override
    public FoodBuilder setFoodID(int foodID) {
        food.setFoodID(foodID);
        return this;
    }

    @Override
    public FoodBuilder setFoodDescription(String foodDescription) {
        food.setFoodDescription(foodDescription);
        return this;
    }

    @Override
    public FoodBuilder setCalories(double calories) {
        food.setCalories(calories);
        return this;
    }

    @Override
    public FoodBuilder setProtein(double protein) {
        food.setProtein(protein);
        return this;
    }

    @Override
    public FoodBuilder setCarbs(double carbs) {
        food.setCarbs(carbs);
        return this;
    }

    @Override
    public FoodBuilder setFats(double fats) {
        food.setFats(fats);
        return this;
    }

    @Override
    public FoodBuilder setFiber(double fiber) {
        food.setFiber(fiber);
        return this;
    }

    @Override
    public FoodBuilder setSodium(double sodium) {
        food.setSodium(sodium);
        return this;
    }

    @Override
    public FoodBuilder setSugars(double sugars) {
        food.setSugars(sugars);
        return this;
    }

    @Override
    public FoodBuilder setSaturatedFats(double saturatedFats) {
        food.setSaturatedFats(saturatedFats);
        return this;
    }

    @Override
    public FoodBuilder setIron(double iron) {
        food.setIron(iron);
        return this;
    }

    @Override
    public FoodBuilder setCalcium(double calcium) {
        food.setCalcium(calcium);
        return this;
    }

    @Override
    public FoodBuilder setVitaminA(double vitaminA) {
        food.setVitaminA(vitaminA);
        return this;
    }

    @Override
    public FoodBuilder setVitaminB(double vitaminB) {
        food.setVitaminB(vitaminB);
        return this;
    }

    @Override
    public FoodBuilder setVitaminC(double vitaminC) {
        food.setVitaminC(vitaminC);
        return this;
    }

    @Override
    public FoodBuilder setVitaminD(double vitaminD) {
        food.setVitaminD(vitaminD);
        return this;
    }

    @Override
    public FoodBuilder setFoodGroup(String foodGroup) {
        food.setFoodGroup(foodGroup);
        return this;
    }

    @Override
    public FoodBuilder setFoodSource(String foodSource) {
        food.setFoodSource(foodSource);
        return this;
    }

    @Override
    public FoodBuilder setNutrients(Map<String, Double> nutrients) {
        food.setNutrients(new HashMap<>(nutrients));
        return this;
    }

    @Override
    public FoodBuilder addNutrient(String name, double value) {
        if (food.getNutrients() == null) {
            food.setNutrients(new HashMap<>());
        }
        food.getNutrients().put(name, value);
        return this;
    }

    @Override
    public Food getResult() {
        Food result = this.food;
        reset();
        return result;
    }
}