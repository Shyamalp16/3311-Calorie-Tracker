package View;

import java.util.Map;

/**
 * ConcreteCreator - LineChartFactory
 * Implements the factoryMethod() to create LineChart instances
 * This follows the Factory Method pattern structure
 */
public class LineChartFactory extends ChartFactory {
    
    /**
     * Factory Method implementation for creating LineChart instances
     * This is the concrete implementation of the abstract factory method
     * @param title Chart title
     * @param data Chart data
     * @return LineChart instance
     */
    @Override
    public Chart factoryMethod(String title, Map<String, Double> data) {
        return new LineChart(title, data);
    }
    
    /**
     * Additional factory method with custom axis labels
     * Provides more flexibility for line chart creation
     * @param title Chart title
     * @param xAxisLabel X-axis label
     * @param yAxisLabel Y-axis label
     * @param data Chart data
     * @return LineChart instance with custom labels
     */
    public Chart factoryMethod(String title, String xAxisLabel, String yAxisLabel, Map<String, Double> data) {
        return new LineChart(title, xAxisLabel, yAxisLabel, data);
    }
} 