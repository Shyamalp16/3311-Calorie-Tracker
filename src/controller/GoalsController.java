package controller;

import models.*;
import Database.*;
import utils.UnitHelper;

import java.util.Optional;

/**
 * Controller for nutritional goals business logic
 * Handles goal CRUD operations and validation
 */
public class GoalsController {
    
    private User currentUser;
    private UserSettings userSettings;
    
    private GoalDAO goalDAO;
    private UserSettingsDAO settingsDAO;
    
    private Runnable onGoalsChanged;
    
    public GoalsController(User user) {
        this.currentUser = user;
        this.goalDAO = new GoalDAO();
        this.settingsDAO = new UserSettingsDAO();
        this.userSettings = settingsDAO.getOrCreateDefaultSettings(user.getUserId());
    }
    
    /**
     * Set callback for when goals are updated
     */
    public void setOnGoalsChangedCallback(Runnable callback) {
        this.onGoalsChanged = callback;
    }
    
    /**
     * Get user's current goals
     */
    public Goal getUserGoals() {
        return goalDAO.getGoalByUserId(currentUser.getUserId())
                     .orElse(getDefaultGoals());
    }
    
    /**
     * Get user's goals for display (converted to user's unit system)
     */
    public GoalsForDisplay getUserGoalsForDisplay() {
        Goal goals = getUserGoals();
        
        return new GoalsForDisplay(
            goals.getCalories(),
            UnitHelper.convertFoodWeightForDisplay(goals.getProtein(), userSettings),
            UnitHelper.convertFoodWeightForDisplay(goals.getCarbs(), userSettings),
            UnitHelper.convertFoodWeightForDisplay(goals.getFats(), userSettings),
            UnitHelper.convertFoodWeightForDisplay(goals.getFiber(), userSettings)
        );
    }
    
    /**
     * Save new goals (input values are in user's display units)
     */
    public SaveGoalsResult saveGoals(double calories, double proteinDisplay, double carbsDisplay, 
                                   double fatsDisplay, double fiberDisplay) {
        try {
            if (calories <= 0 || proteinDisplay < 0 || carbsDisplay < 0 || fatsDisplay < 0 || fiberDisplay < 0) {
                return new SaveGoalsResult(false, "All values must be positive numbers");
            }
            
            double protein = UnitHelper.convertFoodWeightForStorage(proteinDisplay, userSettings);
            double carbs = UnitHelper.convertFoodWeightForStorage(carbsDisplay, userSettings);
            double fats = UnitHelper.convertFoodWeightForStorage(fatsDisplay, userSettings);
            double fiber = UnitHelper.convertFoodWeightForStorage(fiberDisplay, userSettings);
            
            Goal goal = new Goal(currentUser.getUserId(), calories, protein, carbs, fats, fiber);
            goalDAO.saveGoal(goal);
            
            if (onGoalsChanged != null) {
                onGoalsChanged.run();
            }
            
            return new SaveGoalsResult(true, "Goals saved successfully!");
            
        } catch (NumberFormatException e) {
            return new SaveGoalsResult(false, "Please enter valid numbers for all fields");
        } catch (Exception e) {
            return new SaveGoalsResult(false, "Error saving goals: " + e.getMessage());
        }
    }
    
    /**
     * Get default goals
     */
    private Goal getDefaultGoals() {
        return new Goal(currentUser.getUserId(), 2000, 75, 225, 70, 25);
    }
    
    /**
     * Get unit labels for the UI
     */
    public String getFoodWeightUnit() {
        return UnitHelper.getFoodWeightUnit(userSettings);
    }
    
    /**
     * Handle when user settings change
     */
    public void onSettingsChanged(UserSettings updatedSettings) {
        this.userSettings = updatedSettings;
    }
    
    public UserSettings getUserSettings() { return userSettings; }
    
    /**
     * Data class for goals in display units
     */
    public static class GoalsForDisplay {
        public final double calories;
        public final double protein;
        public final double carbs;
        public final double fats;
        public final double fiber;
        
        public GoalsForDisplay(double calories, double protein, double carbs, double fats, double fiber) {
            this.calories = calories;
            this.protein = protein;
            this.carbs = carbs;
            this.fats = fats;
            this.fiber = fiber;
        }
    }
    
    /**
     * Data class for save goals result
     */
    public static class SaveGoalsResult {
        public final boolean success;
        public final String message;
        
        public SaveGoalsResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}