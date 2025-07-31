package models;

import java.sql.Timestamp;

/**
 * Model class for storing user preferences and settings
 * Includes unit system, theme preferences, and other user-specific configurations
 */
public class UserSettings {
    private int settingsId;
    private int userId;
    private UnitSystem unitSystem;
    private String theme;
    private boolean enableNotifications;
    private String dateFormat;
    private int dailyGoalCalories;
    private Timestamp lastUpdated;
    
    public UserSettings() {
        this.unitSystem = UnitSystem.METRIC;
        this.theme = "Default";
        this.enableNotifications = true;
        this.dateFormat = "yyyy-MM-dd";
        this.dailyGoalCalories = 2000;
    }
    
    public UserSettings(int userId, UnitSystem unitSystem) {
        this();
        this.userId = userId;
        this.unitSystem = unitSystem;
    }
    
    public UserSettings(int settingsId, int userId, UnitSystem unitSystem, String theme, 
                       boolean enableNotifications, String dateFormat, int dailyGoalCalories, 
                       Timestamp lastUpdated) {
        this.settingsId = settingsId;
        this.userId = userId;
        this.unitSystem = unitSystem;
        this.theme = theme;
        this.enableNotifications = enableNotifications;
        this.dateFormat = dateFormat;
        this.dailyGoalCalories = dailyGoalCalories;
        this.lastUpdated = lastUpdated;
    }
    
    public int getSettingsId() { return settingsId; }
    public void setSettingsId(int settingsId) { this.settingsId = settingsId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public UnitSystem getUnitSystem() { return unitSystem; }
    public void setUnitSystem(UnitSystem unitSystem) { this.unitSystem = unitSystem; }
    
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
    
    public boolean isEnableNotifications() { return enableNotifications; }
    public void setEnableNotifications(boolean enableNotifications) { this.enableNotifications = enableNotifications; }
    
    public String getDateFormat() { return dateFormat; }
    public void setDateFormat(String dateFormat) { this.dateFormat = dateFormat; }
    
    public int getDailyGoalCalories() { return dailyGoalCalories; }
    public void setDailyGoalCalories(int dailyGoalCalories) { this.dailyGoalCalories = dailyGoalCalories; }
    
    public Timestamp getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Timestamp lastUpdated) { this.lastUpdated = lastUpdated; }
    
    /**
     * Convert user's height from stored metric value to display value based on unit system
     */
    public double convertHeightForDisplay(double heightInCm) {
        return UnitSystem.convertHeight(heightInCm, UnitSystem.METRIC, this.unitSystem);
    }
    
    /**
     * Convert user's weight from stored metric value to display value based on unit system
     */
    public double convertWeightForDisplay(double weightInKg) {
        return UnitSystem.convertWeight(weightInKg, UnitSystem.METRIC, this.unitSystem);
    }
    
    /**
     * Convert display height value to metric for storage
     */
    public double convertHeightForStorage(double displayValue) {
        return UnitSystem.convertHeight(displayValue, this.unitSystem, UnitSystem.METRIC);
    }
    
    /**
     * Convert display weight value to metric for storage
     */
    public double convertWeightForStorage(double displayValue) {
        return UnitSystem.convertWeight(displayValue, this.unitSystem, UnitSystem.METRIC);
    }
    
    /**
     * Format height for display using current unit system
     */
    public String formatHeight(double heightInCm) {
        double displayValue = convertHeightForDisplay(heightInCm);
        return unitSystem.formatHeight(displayValue);
    }
    
    /**
     * Format weight for display using current unit system
     */
    public String formatWeight(double weightInKg) {
        double displayValue = convertWeightForDisplay(weightInKg);
        return unitSystem.formatWeight(displayValue);
    }
    
    /**
     * Convert food portion weight from stored metric value to display value based on unit system
     */
    public double convertFoodWeightForDisplay(double weightInGrams) {
        return UnitSystem.convertFoodWeight(weightInGrams, UnitSystem.METRIC, this.unitSystem);
    }
    
    /**
     * Convert liquid volume from stored metric value to display value based on unit system
     */
    public double convertLiquidVolumeForDisplay(double volumeInMl) {
        return UnitSystem.convertLiquidVolume(volumeInMl, UnitSystem.METRIC, this.unitSystem);
    }
    
    /**
     * Convert display food weight value to metric for storage
     */
    public double convertFoodWeightForStorage(double displayValue) {
        return UnitSystem.convertFoodWeight(displayValue, this.unitSystem, UnitSystem.METRIC);
    }
    
    /**
     * Convert display liquid volume value to metric for storage
     */
    public double convertLiquidVolumeForStorage(double displayValue) {
        return UnitSystem.convertLiquidVolume(displayValue, this.unitSystem, UnitSystem.METRIC);
    }
    
    /**
     * Format food portion weight for display using current unit system
     */
    public String formatFoodWeight(double weightInGrams) {
        return unitSystem.formatFoodWeight(weightInGrams);
    }
    
    /**
     * Format liquid volume for display using current unit system
     */
    public String formatLiquidVolume(double volumeInMl) {
        return unitSystem.formatLiquidVolume(volumeInMl);
    }
    
    @Override
    public String toString() {
        return "UserSettings{" +
                "settingsId=" + settingsId +
                ", userId=" + userId +
                ", unitSystem=" + unitSystem +
                ", theme='" + theme + '\'' +
                ", enableNotifications=" + enableNotifications +
                ", dateFormat='" + dateFormat + '\'' +
                ", dailyGoalCalories=" + dailyGoalCalories +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
} 