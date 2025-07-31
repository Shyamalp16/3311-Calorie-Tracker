package controller;

import models.Food;
import models.MealItem;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class LoggingMealControllerDecorator extends MealControllerDecorator {

    public LoggingMealControllerDecorator(IMealController decoratedController) {
        super(decoratedController);
    }

    @Override
    public MealController.SaveMealResult saveMeal(String mealType, Date mealDate, List<MealItem> items) {
        MealController.SaveMealResult result = super.saveMeal(mealType, mealDate, items);
        return result;
    }

    @Override
    public List<Food> searchFoods(String query) {
        List<Food> foods = super.searchFoods(query);
        return foods;
    }

    @Override
    public String getFoodGroupById(int foodId) {
        return decoratedController.getFoodGroupById(foodId);
    }
}