package View;

import org.jfree.chart.ChartPanel;
import java.util.Map;

/**
 * Abstract Creator Class for Factory Method Pattern
 * Declares the factory method that returns a Chart object
 * Subclasses implement this method to create specific chart types
 */
public abstract class ChartFactory {

    /**
     * Factory Method - Abstract method to be implemented by concrete creators
     * This is the core of the Factory Method pattern
     * @param title Chart title
     * @param data Chart data
     * @return Chart product instance
     */
    public abstract Chart factoryMethod(String title, Map<String, Double> data);
    
    /**
     * Template Method - Uses the factory method to create and return ChartPanel
     * This method demonstrates how the factory method is used in a template
     * @param title Chart title
     * @param data Chart data
     * @return ChartPanel for display
     */
    public ChartPanel createChartPanel(String title, Map<String, Double> data) {
        Chart chart = factoryMethod(title, data);
        return chart.getChartPanel();
    }
    
    /**
     * An operation that uses the factory method
     * This shows the typical pattern usage where the creator uses its factory method
     */
    public void displayChart(String title, Map<String, Double> data) {
        Chart chart = factoryMethod(title, data);
    }

    
    /**
     * Static factory method for backward compatibility and convenience
     * This delegates to the appropriate concrete factory
     */
    public static ChartPanel createChart(Chart.ChartType type, String title, Map<String, Double> data) {
        ChartFactory factory = getFactory(type);
        return factory.createChartPanel(title, data);
    }
    
    /**
     * Factory selection method - returns appropriate concrete factory
     */
    public static ChartFactory getFactory(Chart.ChartType type) {
        switch (type) {
            case PIE:
                return new PieChartFactory();
            case LINE:
                return new LineChartFactory();
            case BAR:
                return new BarChartFactory();
            default:
                throw new IllegalArgumentException("Unsupported chart type: " + type);
        }
    }
}