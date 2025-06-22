package Nutrition_Analysis_Visualization;
import java.time.LocalDate;
import java.util.List;

import Meal_Logging_Calculation.Meal;
import java.time.LocalDate;
import java.util.List;

public class NutritionAnalysis {
 public static double calculateTotalNutrient(List<Meal> meals, NutrientType type) {
     return meals.stream()
         .mapToDouble(meal -> {
             switch (type) {
                 case CALORIES: return meal.getTotalCalories();
                 case PROTEIN: return meal.getTotalProtein();
                 case CARBS: return meal.getTotalCarbs();
                 case FATS: return meal.getTotalFats();
                 case FIBER: return meal.getTotalFiber();
                 default: return 0;
             }
         })
         .sum();
 }
 
 public static double calculateDailyAverage(List<Meal> meals, NutrientType type, int days) {
     if (days <= 0) return 0;
     return calculateTotalNutrient(meals, type) / days;
 }
 
 public static double calculateCFGCompliance(List<Meal> meals) {
     double totalItems = meals.stream()
         .mapToInt(meal -> meal.getFoodItems().size())
         .sum();
     
     if (totalItems == 0) return 0;
     
     long healthyItems = meals.stream()
         .flatMap(meal -> meal.getFoodItems().stream())
         .filter(item -> isHealthyFoodGroup(item.getFoodGroup()))
         .count();
     
     return (healthyItems / totalItems) * 100;
 }
 
 private static boolean isHealthyFoodGroup(String foodGroup) {
     return !foodGroup.equalsIgnoreCase("processed foods") &&
            !foodGroup.equalsIgnoreCase("sweets");
 }
 
 public enum NutrientType {
     CALORIES, PROTEIN, CARBS, FATS, FIBER
 }
}

