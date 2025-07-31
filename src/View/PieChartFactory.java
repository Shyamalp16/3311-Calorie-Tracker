package View;

import java.util.Map;

/**
 * ConcreteCreator - PieChartFactory
 * Implements the factoryMethod() to create PieChart instances
 * This follows the Factory Method pattern structure
 */
public class PieChartFactory extends ChartFactory {
    
    /**
     * Factory Method implementation for creating PieChart instances
     * This is the concrete implementation of the abstract factory method
     * @param title Chart title
     * @param data Chart data
     * @return PieChart instance
     */
    @Override
    public Chart factoryMethod(String title, Map<String, Double> data) {
        return new PieChart(title, data);
    }
} 