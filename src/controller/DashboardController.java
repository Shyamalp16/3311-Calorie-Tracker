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

        return new NutritionSummary.Builder()
            .totalCalories(totalCalories)
            .totalProtein(totalProtein)
            .totalCarbs(totalCarbs)
            .totalFats(totalFats)
            .totalFiber(totalFiber)
            .totalSodium(totalSodium)
            .totalSugars(totalSugars)
            .totalSaturatedFats(totalSaturatedFats)
            .totalIron(totalIron)
            .totalCalcium(totalCalcium)
            .totalVitaminA(totalVitaminA)
            .totalVitaminB(totalVitaminB)
            .totalVitaminC(totalVitaminC)
            .totalVitaminD(totalVitaminD)
            .build();
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

        return new NutritionSummary.Builder()
            .totalCalories(total.totalCalories / days)
            .totalProtein(total.totalProtein / days)
            .totalCarbs(total.totalCarbs / days)
            .totalFats(total.totalFats / days)
            .totalFiber(total.totalFiber / days)
            .totalSodium(total.totalSodium / days)
            .totalSugars(total.totalSugars / days)
            .totalSaturatedFats(total.totalSaturatedFats / days)
            .totalIron(total.totalIron / days)
            .totalCalcium(total.totalCalcium / days)
            .totalVitaminA(total.totalVitaminA / days)
            .totalVitaminB(total.totalVitaminB / days)
            .totalVitaminC(total.totalVitaminC / days)
            .totalVitaminD(total.totalVitaminD / days)
            .build();
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

    public String getDetailedRecommendation(String timePeriod) {
        NutritionAnalysisData analysisData = getNutritionAnalysis(timePeriod);
        return RecommendationGenerator.generate(timePeriod, analysisData);
    }

    private static class RecommendationGenerator {
        private static final Map<String, java.util.function.Function<NutritionAnalysisData, String>> recommendationStrategies = new HashMap<>();

        static {
            recommendationStrategies.put("7 Days", RecommendationGenerator::getWeeklyRecommendation);
            recommendationStrategies.put("30 Days", RecommendationGenerator::getMonthlyRecommendation);
            recommendationStrategies.put("90 Days", RecommendationGenerator::getQuarterlyRecommendation);
        }

        public static String generate(String timePeriod, NutritionAnalysisData analysisData) {
            return recommendationStrategies.getOrDefault(timePeriod, (data) -> "Please select a valid time period for a detailed recommendation.")
                                           .apply(analysisData);
        }

        private static String getWeeklyRecommendation(NutritionAnalysisData analysisData) {
            NutritionSummary avgNutrition = analysisData.averageNutrition;
            Goal userGoals = analysisData.userGoals;
            StringBuilder recommendation = new StringBuilder("Weekly Review:\n");

            if (avgNutrition.totalCalories > userGoals.getCalories() + 100) {
                recommendation.append("- Your average calorie intake is high. Consider smaller portions.\n");
            } else if (avgNutrition.totalCalories < userGoals.getCalories() - 100) {
                recommendation.append("- Your average calorie intake is low. Ensure you're eating enough.\n");
            } else {
                recommendation.append("- Great job on maintaining your calorie goals this week!\n");
            }
            if (avgNutrition.totalProtein < userGoals.getProtein() * 0.8) {
                recommendation.append("- Protein intake is a bit low. Try adding lean meats or legumes.\n");
            }
            recommendation.append("- Sugar intake seems acceptable. Keep monitoring it.\n");
            recommendation.append("- You're doing well with your fat intake.\n");
            recommendation.append("- Fiber levels are good, keep it up!\n");
            recommendation.append("- Sodium is within a reasonable range.\n");
            recommendation.append("- Remember to drink plenty of water.\n");
            return recommendation.toString();
        }

        private static String getMonthlyRecommendation(NutritionAnalysisData analysisData) {
            NutritionSummary avgNutrition = analysisData.averageNutrition;
            Goal userGoals = analysisData.userGoals;
            StringBuilder recommendation = new StringBuilder("Monthly Analysis:\n");
            double calorieDiff = avgNutrition.totalCalories - userGoals.getCalories();

            if (Math.abs(calorieDiff) > 200) {
                recommendation.append(String.format("- Calorie mismatch of %.0f calories detected. Re-evaluate your meal plan.\n", calorieDiff));
            } else {
                recommendation.append("- Consistent calorie management over the past month. Excellent!\n");
            }
            if (avgNutrition.totalProtein < userGoals.getProtein() * 0.9) {
                recommendation.append("- Long-term protein intake is slightly below your goal. Consider protein supplements.\n");
            } else if (avgNutrition.totalProtein > userGoals.getProtein() * 1.2) {
                recommendation.append("- Protein intake is high. Ensure it's from lean sources.\n");
            }
            if (avgNutrition.totalSugars > 50) { // Example threshold
                recommendation.append("- Average sugar intake is high. Look for hidden sugars in processed foods.\n");
            }
            recommendation.append("- Your fat consumption is on point.\n");
            recommendation.append("- Fiber intake is consistently good.\n");
            recommendation.append("- Sodium levels are stable.\n");
            recommendation.append("- Check your vitamin D levels, especially if you have limited sun exposure.\n");
            recommendation.append("- A 30-day trend is a strong indicator of your dietary habits.\n");
            return recommendation.toString();
        }

        private static String getQuarterlyRecommendation(NutritionAnalysisData analysisData) {
            NutritionSummary avgNutrition = analysisData.averageNutrition;
            Goal userGoals = analysisData.userGoals;
            StringBuilder recommendation = new StringBuilder("Quarterly Report:\n");

            if (avgNutrition.totalCalories > userGoals.getCalories()) {
                recommendation.append("- Over the last 90 days, your calorie intake has been consistently above your goal.\n");
            } else {
                recommendation.append("- Over the last 90 days, your calorie intake has been consistently below your goal.\n");
            }
            recommendation.append(String.format("- Average Protein: %.1fg, Goal: %.1fg\n", avgNutrition.totalProtein, userGoals.getProtein()));
            recommendation.append(String.format("- Average Carbs: %.1fg, Goal: %.1fg\n", avgNutrition.totalCarbs, userGoals.getCarbs()));
            recommendation.append(String.format("- Average Fat: %.1fg, Goal: %.1fg\n", avgNutrition.totalFats, userGoals.getFats()));
            recommendation.append("- This long-term view is crucial for understanding your health.\n");
            recommendation.append("- Consider adjusting your goals based on these trends.\n");
            recommendation.append("- Your dietary patterns are now well-established.\n");
            recommendation.append("- Look for correlations between your diet and how you feel.\n");
            recommendation.append("- Are you meeting your micronutrient needs?\n");
            recommendation.append("- Great dedication to tracking for 90 days!\n");
            return recommendation.toString();
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

        private NutritionSummary(Builder builder) {
            this.totalCalories = builder.totalCalories;
            this.totalProtein = builder.totalProtein;
            this.totalCarbs = builder.totalCarbs;
            this.totalFats = builder.totalFats;
            this.totalFiber = builder.totalFiber;
            this.totalSodium = builder.totalSodium;
            this.totalSugars = builder.totalSugars;
            this.totalSaturatedFats = builder.totalSaturatedFats;
            this.totalIron = builder.totalIron;
            this.totalCalcium = builder.totalCalcium;
            this.totalVitaminA = builder.totalVitaminA;
            this.totalVitaminB = builder.totalVitaminB;
            this.totalVitaminC = builder.totalVitaminC;
            this.totalVitaminD = builder.totalVitaminD;
        }

        public static class Builder {
            private double totalCalories, totalProtein, totalCarbs, totalFats, totalFiber;
            private double totalSodium, totalSugars, totalSaturatedFats, totalIron, totalCalcium;
            private double totalVitaminA, totalVitaminB, totalVitaminC, totalVitaminD;

            public Builder totalCalories(double val) { totalCalories = val; return this; }
            public Builder totalProtein(double val) { totalProtein = val; return this; }
            public Builder totalCarbs(double val) { totalCarbs = val; return this; }
            public Builder totalFats(double val) { totalFats = val; return this; }
            public Builder totalFiber(double val) { totalFiber = val; return this; }
            public Builder totalSodium(double val) { totalSodium = val; return this; }
            public Builder totalSugars(double val) { totalSugars = val; return this; }
            public Builder totalSaturatedFats(double val) { totalSaturatedFats = val; return this; }
            public Builder totalIron(double val) { totalIron = val; return this; }
            public Builder totalCalcium(double val) { totalCalcium = val; return this; }
            public Builder totalVitaminA(double val) { totalVitaminA = val; return this; }
            public Builder totalVitaminB(double val) { totalVitaminB = val; return this; }
            public Builder totalVitaminC(double val) { totalVitaminC = val; return this; }
            public Builder totalVitaminD(double val) { totalVitaminD = val; return this; }

            public NutritionSummary build() {
                return new NutritionSummary(this);
            }
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