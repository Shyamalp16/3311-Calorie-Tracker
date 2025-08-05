package utils;

import models.UserSettings;

public class UnitPrecisionHelper {

    /**
     * Formats weight with precision based on user settings.
     * This logic is intentionally separated from the UnitSystem enum.
     */
    public static String formatWeightWithPrecision(UserSettings settings, double weight) {
        if (settings.isPrecisionMode()) {
            return String.format("%.3f %s", weight, settings.getUnitSystem().getWeightUnit());
        }
        return String.format("%.1f %s", weight, settings.getUnitSystem().getWeightUnit());
    }

    /**
     * Formats height with precision based on user settings.
     */
    public static String formatHeightWithPrecision(UserSettings settings, double height) {
        if (settings.isPrecisionMode()) {
            return String.format("%.2f %s", height, settings.getUnitSystem().getHeightUnit());
        }
        return String.format("%.0f %s", height, settings.getUnitSystem().getHeightUnit());
    }
}
