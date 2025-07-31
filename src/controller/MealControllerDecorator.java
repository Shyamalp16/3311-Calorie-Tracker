package controller;

import models.Food;
import models.Meal;
import models.MealItem;
import models.UserSettings;

import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract class MealControllerDecorator implements IMealController {
    protected IMealController decoratedController;

    public MealControllerDecorator(IMealController decoratedController) {
        this.decoratedController = decoratedController;
    }

    @Override
    public MealController.SaveMealResult saveMeal(String mealType, Date mealDate, List<MealItem> items) {
        return decoratedController.saveMeal(mealType, mealDate, items);
    }

    @Override
    public List<Food> searchFoods(String query) {
        return decoratedController.searchFoods(query);
    }

    @Override
    public Food getFoodById(int foodId) {
        return decoratedController.getFoodById(foodId);
    }

    @Override
    public Map<String, Integer> getFoodMeasures(int foodId) {
        return decoratedController.getFoodMeasures(foodId);
    }

    @Override
    public MealItem calculateMealItem(Food food, double quantity, String unit) {
        return decoratedController.calculateMealItem(food, quantity, unit);
    }

    @Override
    public List<Meal> getMealsForDate(Date date) {
        return decoratedController.getMealsForDate(date);
    }

    @Override
    public Meal getMealById(int mealId) {
        return decoratedController.getMealById(mealId);
    }

    @Override
    public List<MealItem> getMealItemsByMealId(int mealId) {
        return decoratedController.getMealItemsByMealId(mealId);
    }

    @Override
    public List<MealItem> getMealItemsForDate(Date date) {
        return decoratedController.getMealItemsForDate(date);
    }

    @Override
    public UserSettings getUserSettings() {
        return decoratedController.getUserSettings();
    }
}