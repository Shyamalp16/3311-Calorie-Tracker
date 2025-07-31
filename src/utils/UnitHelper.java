package utils;

import models.UnitSystem;
import models.UserSettings;

/**
 * Simple utility to help with unit conversions throughout the app
 * Centralizes the unit conversion logic that was scattered everywhere
 */
public class UnitHelper {
    
    /**
     * Format food weight with proper units based on user settings
     */
    public static String formatFoodWeight(double weightInGrams, UserSettings userSettings) {
        return userSettings.formatFoodWeight(weightInGrams);
    }
    
    /**
     * Get the unit symbol for food weights (g or oz)
     */
    public static String getFoodWeightUnit(UserSettings userSettings) {
        return userSettings.getUnitSystem() == UnitSystem.METRIC ? "g" : "oz";
    }
    
    /**
     * Get the unit name for food weights (grams or ounces)
     */
    public static String getFoodWeightUnitName(UserSettings userSettings) {
        return userSettings.getUnitSystem() == UnitSystem.METRIC ? "grams" : "ounces";
    }
    
    /**
     * Convert food weight for display
     */
    public static double convertFoodWeightForDisplay(double weightInGrams, UserSettings userSettings) {
        return userSettings.convertFoodWeightForDisplay(weightInGrams);
    }
    
    /**
     * Convert food weight back to metric for storage
     */
    public static double convertFoodWeightForStorage(double displayValue, UserSettings userSettings) {
        return userSettings.convertFoodWeightForStorage(displayValue);
    }
}