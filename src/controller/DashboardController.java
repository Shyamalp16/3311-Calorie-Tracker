package controller;

import models.*;
import Database.*;
import utils.UnitHelper;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Controller for Dashboard business logic
 * Handles data processing and coordinates between view and model
 */
public class DashboardController {
    
    private User currentUser;
    private UserSettings userSettings;
    
    private MealDAO mealDAO;
    private UserSettingsDAO settingsDAO;
    private GoalDAO goalDAO;
    
    public DashboardController(User user) {
        this.currentUser = user;
        this.mealDAO = new MealDAO();
        this.settingsDAO = new UserSettingsDAO();
        this.goalDAO = new GoalDAO();
        this.userSettings = settingsDAO.getOrCreateDefaultSettings(user.getUserId());
    }
    
    /**
     * Get today's nutrition summary
     */
    public NutritionSummary getTodaysNutrition() {
        Date today = new Date();
        java.sql.Date sqlDate = new java.sql.Date(today.getTime());
        List<Meal> todaysMeals = mealDAO.getMealsForUserAndDate(currentUser.getUserId(), sqlDate);
        
        return calculateNutritionSummary(todaysMeals);
    }
    
    /**
     * Get nutrition for a specific date
     */
    public NutritionSummary getNutritionForDate(Date date) {
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        List<Meal> meals = mealDAO.getMealsForUserAndDate(currentUser.getUserId(), sqlDate);
        
        return calculateNutritionSummary(meals);
    }
    
    /**
     * Get meals for a specific date
     */
    public List<Meal> getMealsForDate(Date date) {
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        return mealDAO.getMealsForUserAndDate(currentUser.getUserId(), sqlDate);
    }
    
    /**
     * Get meals for a date range
     */
    public List<Meal> getMealsInDateRange(Date startDate, Date endDate) {
        return mealDAO.getMealsInDateRange(currentUser.getUserId(), startDate, endDate);
    }
    
    /**
     * Calculate nutrition summary from meals
     */
    private NutritionSummary calculateNutritionSummary(List<Meal> meals) {
        double totalCalories = 0, totalProtein = 0, totalCarbs = 0, totalFats = 0, totalFiber = 0;
        double totalSodium = 0, totalSugars = 0, totalSaturatedFats = 0, totalIron = 0, totalCalcium = 0;
        double totalVitaminA = 0, totalVitaminB = 0, totalVitaminC = 0, totalVitaminD = 0;
        
        for (Meal meal : meals) {
            totalCalories += meal.getTotalCalories();
            totalProtein += meal.getTotalProtein();
            totalCarbs += meal.getTotalCarbs();
            totalFats += meal.getTotalFats();
            totalFiber += meal.getTotalFiber();
            totalSodium += meal.getTotalSodium();
            totalSugars += meal.getTotalSugars();
            totalSaturatedFats += meal.getTotalSaturatedFats();
            totalIron += meal.getTotalIron();
            totalCalcium += meal.getTotalCalcium();
            totalVitaminA += meal.getTotalVitaminA();
            totalVitaminB += meal.getTotalVitaminB();
            totalVitaminC += meal.getTotalVitaminC();
            totalVitaminD += meal.getTotalVitaminD();
        }
        
        return new NutritionSummary(totalCalories, totalProtein, totalCarbs, totalFats, totalFiber,
                                  totalSodium, totalSugars, totalSaturatedFats, totalIron, totalCalcium,
                                  totalVitaminA, totalVitaminB, totalVitaminC, totalVitaminD);
    }
    
    /**
     * Get nutrition analysis data for time periods
     */
    public NutritionAnalysisData getNutritionAnalysis(String timePeriod) {
        Date[] dateRange = calculateDateRange(timePeriod);
        Date startDate = dateRange[0];
        Date endDate = dateRange[1];
        int days = calculateDays(timePeriod);
        
        List<Meal> meals = mealDAO.getMealsInDateRange(currentUser.getUserId(), startDate, endDate);
        NutritionSummary totalNutrition = calculateNutritionSummary(meals);
        NutritionSummary avgNutrition = calculateAverageNutrition(totalNutrition, days);
        
        Goal userGoals = goalDAO.getGoalByUserId(currentUser.getUserId())
                               .orElse(getDefaultGoals());
        
        return new NutritionAnalysisData(totalNutrition, avgNutrition, userGoals, days);
    }
    
    /**
     * Calculate average nutrition by dividing totals by days
     */
    private NutritionSummary calculateAverageNutrition(NutritionSummary total, int days) {
        if (days <= 0) days = 1;
        
        return new NutritionSummary(
            total.totalCalories / days,
            total.totalProtein / days,
            total.totalCarbs / days,
            total.totalFats / days,
            total.totalFiber / days,
            total.totalSodium / days,
            total.totalSugars / days,
            total.totalSaturatedFats / days,
            total.totalIron / days,
            total.totalCalcium / days,
            total.totalVitaminA / days,
            total.totalVitaminB / days,
            total.totalVitaminC / days,
            total.totalVitaminD / days
        );
    }
    
    /**
     * Calculate date range for time periods
     */
    private Date[] calculateDateRange(String timePeriod) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        Date endDate = cal.getTime();
        
        switch (timePeriod) {
            case "7 Days":
                cal.add(java.util.Calendar.DAY_OF_MONTH, -7);
                break;
            case "30 Days":
                cal.add(java.util.Calendar.DAY_OF_MONTH, -30);
                break;
            case "90 Days":
                cal.add(java.util.Calendar.DAY_OF_MONTH, -90);
                break;
            default:
                cal.add(java.util.Calendar.DAY_OF_MONTH, -7);
        }
        
        Date startDate = cal.getTime();
        return new Date[]{startDate, endDate};
    }
    
    /**
     * Calculate number of days for time period
     */
    private int calculateDays(String timePeriod) {
        switch (timePeriod) {
            case "7 Days": return 7;
            case "30 Days": return 30;
            case "90 Days": return 90;
            default: return 7;
        }
    }
    
    /**
     * Get default goals
     */
    private Goal getDefaultGoals() {
        return new Goal(currentUser.getUserId(), 2000, 75, 225, 70, 25);
    }
    
    /**
     * Handle when user profile/settings change
     */
    public void onProfileChanged(User updatedUser, UserSettings updatedSettings) {
        this.currentUser = updatedUser;
        this.userSettings = updatedSettings;
    }
    
    // Getters
    public User getCurrentUser() { return currentUser; }
    public UserSettings getUserSettings() { return userSettings; }
    
    /**
     * Data class for nutrition summary
     */
    public static class NutritionSummary {
        public final double totalCalories, totalProtein, totalCarbs, totalFats, totalFiber;
        public final double totalSodium, totalSugars, totalSaturatedFats, totalIron, totalCalcium;
        public final double totalVitaminA, totalVitaminB, totalVitaminC, totalVitaminD;
        
        public NutritionSummary(double totalCalories, double totalProtein, double totalCarbs, 
                              double totalFats, double totalFiber, double totalSodium, 
                              double totalSugars, double totalSaturatedFats, double totalIron, 
                              double totalCalcium, double totalVitaminA, double totalVitaminB, 
                              double totalVitaminC, double totalVitaminD) {
            this.totalCalories = totalCalories;
            this.totalProtein = totalProtein;
            this.totalCarbs = totalCarbs;
            this.totalFats = totalFats;
            this.totalFiber = totalFiber;
            this.totalSodium = totalSodium;
            this.totalSugars = totalSugars;
            this.totalSaturatedFats = totalSaturatedFats;
            this.totalIron = totalIron;
            this.totalCalcium = totalCalcium;
            this.totalVitaminA = totalVitaminA;
            this.totalVitaminB = totalVitaminB;
            this.totalVitaminC = totalVitaminC;
            this.totalVitaminD = totalVitaminD;
        }
    }
    
    /**
     * Data class for nutrition analysis
     */
    public static class NutritionAnalysisData {
        public final NutritionSummary totalNutrition;
        public final NutritionSummary averageNutrition;
        public final Goal userGoals;
        public final int days;
        
        public NutritionAnalysisData(NutritionSummary totalNutrition, NutritionSummary averageNutrition,
                                   Goal userGoals, int days) {
            this.totalNutrition = totalNutrition;
            this.averageNutrition = averageNutrition;
            this.userGoals = userGoals;
            this.days = days;
        }
    }
}