package View;

import java.util.Map;

/**
 * ConcreteCreator - BarChartFactory
 * Implements the factoryMethod() to create BarChart instances
 * This follows the Factory Method pattern structure
 */
public class BarChartFactory extends ChartFactory {
    
    /**
     * Factory Method implementation for creating BarChart instances
     * This is the concrete implementation of the abstract factory method
     * @param title Chart title
     * @param data Chart data
     * @return BarChart instance
     */
    @Override
    public Chart factoryMethod(String title, Map<String, Double> data) {
        return new BarChart(title, data);
    }
    
    /**
     * Additional factory method with custom axis labels
     * Provides more flexibility for bar chart creation
     * @param title Chart title
     * @param xAxisLabel X-axis label
     * @param yAxisLabel Y-axis label
     * @param data Chart data
     * @return BarChart instance with custom labels
     */
    public Chart factoryMethod(String title, String xAxisLabel, String yAxisLabel, Map<String, Double> data) {
        return new BarChart(title, xAxisLabel, yAxisLabel, data);
    }
} 