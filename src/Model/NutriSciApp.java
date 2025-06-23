package Model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;

import DatabaseConnector.DatabaseConnector;
import Food_Swap_Engine.SwapCriteria;
import Food_Swap_Engine.SwapRecommendation;
import Meal_Logging_Calculation.FoodItem;
import Meal_Logging_Calculation.Meal;
import Meal_Logging_Calculation.MealService;
import Nutrition_Analysis_Visualization.ChartData;
import Nutrition_Analysis_Visualization.ChartGenerator;
import User_Profile_Management.ProfileManager;
import User_Profile_Management.UserProfile;


import java.util.HashMap;

public class NutriSciApp {
 private ProfileManager profileManager;
 private MealService mealService;
 private SwapRecommendation swapEngine;
 private ChartGenerator chartGenerator;
 
 public NutriSciApp() {
     // Initialize database connection
     DatabaseConnector dbConnector = DatabaseConnector.getInstance();
     
     // Initialize services
     profileManager = new ProfileManager();
     mealService = new MealService();
     swapEngine = new SwapRecommendation();
     chartGenerator = new ChartGenerator();
 }
 
 public void run() {
     
	 UserProfile profile = new UserProfile();
	    profile.setName("John Doe");
	    profile.setGender("male");
	    profile.setBirthDate(LocalDate.of(1985, 5, 15));
	    profile.setHeight(175);
	    profile.setWeight(80);
	    profile.setActivityLevel("moderately active");
	    
     
	    profile = profileManager.createProfile(profile, "tempPassword123");
	    if (profile == null) {
	        System.err.println("Failed to create profile");
	        return;
	    }
	    
	  System.out.println("Created profile for: " + profile.getName());
     System.out.println("Created profile for: " + profile.getName());
     System.out.println("Daily calorie needs: " + profile.calculateDailyCalories());
     
     // 2. Log a meal
     Meal breakfast = new Meal();
     breakfast.setUserId(profile.getUserId());
     breakfast.setMealType("breakfast");
     breakfast.setMealDate(LocalDate.now());
     
     FoodItem eggs = mealService.findFoodItem("egg");
     if (eggs != null) {
         eggs.setQuantity(100); // grams
         breakfast.addFoodItem(eggs);
     }
     
     FoodItem bread = mealService.findFoodItem("whole wheat bread");
     if (bread != null) {
         bread.setQuantity(50); // grams
         breakfast.addFoodItem(bread);
     }
     
     mealService.logMeal(breakfast);
     System.out.println("Logged breakfast with " + breakfast.getTotalCalories() + " calories");
     
     // 3. Find food swaps
     SwapCriteria criteria = new SwapCriteria();
     criteria.setPrimaryGoal(SwapCriteria.GoalType.REDUCE_CALORIES);
     criteria.setIntensity(10); // Reduce by at least 10%
     
     List<FoodItem> swaps = swapEngine.findPotentialSwaps(bread, criteria);
     if (!swaps.isEmpty()) {
         System.out.println("Potential swap for " + bread.getDescription() + ": " + 
             swaps.get(0).getDescription() + " (" + swaps.get(0).getCalories() + " kcal)");
         
         // 4. Apply swap and create new meal
         Meal swappedMeal = swapEngine.applySwapToMeal(breakfast, bread, swaps.get(0));
         System.out.println("After swap: " + swappedMeal.getTotalCalories() + " calories");
         
         // 5. Visualize nutrition data
         Map<String, Double> nutritionData = new HashMap<>();
         nutritionData.put("Calories", breakfast.getTotalCalories());
         nutritionData.put("Protein", breakfast.getTotalProtein());
         nutritionData.put("Carbs", breakfast.getTotalCarbs());
         nutritionData.put("Fats", breakfast.getTotalFats());
         
         ChartData chartData = new ChartData(
             "Breakfast Nutrition",
             nutritionData,
             ChartData.ChartType.PIE
         );
         
         showChart(chartGenerator.generateChart(chartData));
     }
     
 }
 
 
 private void showChart(JFreeChart chart) {
     ChartFrame frame = new ChartFrame("Nutrition Chart", chart);
     frame.pack();
     frame.setVisible(true);
 }
 

}