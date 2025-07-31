package controller;

import models.Food;
import models.Meal;
import models.MealItem;
import models.UserSettings;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IMealController {
    MealController.SaveMealResult saveMeal(String mealType, Date mealDate, List<MealItem> items);
    List<Food> searchFoods(String query);
    Food getFoodById(int foodId);
    Map<String, Integer> getFoodMeasures(int foodId);
    MealItem calculateMealItem(Food food, double quantity, String unit);
    List<Meal> getMealsForDate(Date date);
    Meal getMealById(int mealId);
    List<MealItem> getMealItemsByMealId(int mealId);
    List<MealItem> getMealItemsForDate(Date date);
    UserSettings getUserSettings();
    String getFoodGroupById(int foodId);
}