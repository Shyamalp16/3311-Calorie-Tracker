package utils;

import models.Food;

public class NutrientHelper {

    /**
     * Gets the protein value from a food item.
     * @param food The food item.
     * @return The protein value.
     */
    public static double getProteinValue(Food food) {
        return food.getProtein();
    }

    /**
     * Gets the fat value from a food item.
     * @param food The food item.
     * @return The fat value.
     */
    public static double getFatValue(Food food) {
        return food.getFats();
    }

    /**
     * Gets the carbohydrate value from a food item.
     * @param food The food item.
     * @return The carbohydrate value.
     */
    public static double getCarbsValue(Food food) {
        return food.getCarbs();
    }
}
