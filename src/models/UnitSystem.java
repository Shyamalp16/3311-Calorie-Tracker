package models;

/**
 * Enum for handling different unit systems (Metric vs Imperial)
 * Provides conversion utilities between different measurement units
 */
public enum UnitSystem {
    METRIC("Metric", "cm", "kg"),
    IMPERIAL("Imperial", "ft/in", "lbs");
    
    private final String displayName;
    private final String heightUnit;
    private final String weightUnit;
    
    UnitSystem(String displayName, String heightUnit, String weightUnit) {
        this.displayName = displayName;
        this.heightUnit = heightUnit;
        this.weightUnit = weightUnit;
    }
    
    public String getDisplayName() { return displayName; }
    public String getHeightUnit() { return heightUnit; }
    public String getWeightUnit() { return weightUnit; }
    
    /**
     * Convert height from one unit system to another
     */
    public static double convertHeight(double value, UnitSystem from, UnitSystem to) {
        if (from == to) return value;
        
        if (from == METRIC && to == IMPERIAL) {
            return cmToFeetInches(value);
        } else if (from == IMPERIAL && to == METRIC) {
            return feetInchesToCm(value);
        }
        return value;
    }
    
    /**
     * Convert weight from one unit system to another
     */
    public static double convertWeight(double value, UnitSystem from, UnitSystem to) {
        if (from == to) return value;
        
        if (from == METRIC && to == IMPERIAL) {
            return kgToPounds(value);
        } else if (from == IMPERIAL && to == METRIC) {
            return poundsToKg(value);
        }
        return value;
    }
    
    /**
     * Convert centimeters to feet and inches (decimal format)
     * Example: 180cm = 5.905 feet
     */
    private static double cmToFeetInches(double cm) {
        double inches = cm / 2.54;
        return inches / 12.0; 
    }
    
    /**
     * Convert feet (decimal) to centimeters
     */
    private static double feetInchesToCm(double feet) {
        double inches = feet * 12.0;
        return inches * 2.54;
    }
    
    /**
     * Convert kilograms to pounds
     */
    private static double kgToPounds(double kg) {
        return kg * 2.20462;
    }
    
    /**
     * Convert pounds to kilograms
     */
    private static double poundsToKg(double pounds) {
        return pounds / 2.20462;
    }
    
    /**
     * Format height for display based on unit system
     */
    public String formatHeight(double value) {
        if (this == METRIC) {
            return String.format("%.1f cm", value);
        } else {
            int feet = (int) value;
            double remainingInches = (value - feet) * 12;
            return String.format("%d'%.1f\"", feet, remainingInches);
        }
    }
    
    /**
     * Format weight for display based on unit system
     */
    public String formatWeight(double value) {
        if (this == METRIC) {
            return String.format("%.1f kg", value);
        } else {
            return String.format("%.1f lbs", value);
        }
    }
    
    /**
     * Parse height input based on unit system
     * Metric: "180" or "180.5"
     * Imperial: "5.9" or "5'11" or "5.11"
     */
    public static double parseHeight(String input, UnitSystem system) throws NumberFormatException {
        if (system == METRIC) {
            return Double.parseDouble(input.replaceAll("[^0-9.]", ""));
        } else {
            if (input.contains("'")) {
                String[] parts = input.split("'");
                double feet = Double.parseDouble(parts[0]);
                double inches = 0;
                if (parts.length > 1) {
                    inches = Double.parseDouble(parts[1].replaceAll("[^0-9.]", ""));
                }
                return feet + (inches / 12.0);
            } else {
                return Double.parseDouble(input);
            }
        }
    }
    
    /**
     * Parse weight input (simple for both systems)
     */
    public static double parseWeight(String input) throws NumberFormatException {
        return Double.parseDouble(input.replaceAll("[^0-9.]", ""));
    }
    
    /**
     * Convert food portion weights (grams to ounces or vice versa)
     */
    public static double convertFoodWeight(double value, UnitSystem from, UnitSystem to) {
        if (from == to) return value;
        
        if (from == METRIC && to == IMPERIAL) {
            return gramsToOunces(value);
        } else if (from == IMPERIAL && to == METRIC) {
            return ouncesToGrams(value);
        }
        return value;
    }
    
    /**
     * Convert liquid volumes (ml to fl oz or vice versa)
     */
    public static double convertLiquidVolume(double value, UnitSystem from, UnitSystem to) {
        if (from == to) return value;
        
        if (from == METRIC && to == IMPERIAL) {
            return mlToFluidOunces(value);
        } else if (from == IMPERIAL && to == METRIC) {
            return fluidOuncesToMl(value);
        }
        return value;
    }
    
    /**
     * Convert grams to ounces
     */
    private static double gramsToOunces(double grams) {
        return grams / 28.3495;
    }
    
    /**
     * Convert ounces to grams
     */
    private static double ouncesToGrams(double ounces) {
        return ounces * 28.3495;
    }
    
    /**
     * Convert milliliters to fluid ounces
     */
    private static double mlToFluidOunces(double ml) {
        return ml / 29.5735;
    }
    
    /**
     * Convert fluid ounces to milliliters
     */
    private static double fluidOuncesToMl(double flOz) {
        return flOz * 29.5735;
    }
    
    /**
     * Format food weight for display based on unit system
     */
    public String formatFoodWeight(double valueInGrams) {
        if (this == METRIC) {
            return String.format("%.1f g", valueInGrams);
        } else {
            double ounces = gramsToOunces(valueInGrams);
            return String.format("%.1f oz", ounces);
        }
    }
    
    /**
     * Format liquid volume for display based on unit system
     */
    public String formatLiquidVolume(double valueInMl) {
        if (this == METRIC) {
            return String.format("%.0f ml", valueInMl);
        } else {
            double flOz = mlToFluidOunces(valueInMl);
            return String.format("%.1f fl oz", flOz);
        }
    }
} 