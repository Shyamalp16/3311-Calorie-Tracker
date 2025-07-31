package View;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import java.awt.Color;
import java.awt.Paint;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * ConcreteProduct - BarChart
 * Implements the Chart interface for creating bar charts
 */
public class BarChart implements Chart {
    
    private static final Set<String> MAIN_MACROS = new HashSet<>(Arrays.asList("Protein", "Fats", "Carbohydrates", "Fiber"));
    private static final Set<String> VITAMINS = new HashSet<>(Arrays.asList("Vitamin A", "Vitamin B", "Vitamin C", "Vitamin D"));
    private static final Set<String> CFG_CATEGORIES = new HashSet<>(Arrays.asList("Vegetables & Fruits", "Whole Grains", "Protein Foods", "Dairy & Alternatives"));
    
    private String title;
    private JFreeChart jfreeChart;
    private ChartPanel chartPanel;
    private Map<String, Double> data;
    private String xAxisLabel;
    private String yAxisLabel;

    public BarChart(String title, Map<String, Double> data) {
        this(title, "Category", "Value", data);
    }

    public BarChart(String title, String xAxisLabel, String yAxisLabel, Map<String, Double> data) {
        this.title = title;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.data = data;
        createBarChart();
    }

    private void createBarChart() {
        DefaultCategoryDataset dataset = createBarDataset(data);
        jfreeChart = org.jfree.chart.ChartFactory.createBarChart(title, xAxisLabel, yAxisLabel, dataset,
                PlotOrientation.VERTICAL, true, false, false);
        styleBarChart();
        chartPanel = new ChartPanel(jfreeChart);
    }

    private DefaultCategoryDataset createBarDataset(Map<String, Double> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (data.isEmpty()) {
            dataset.addValue(0, "No data", "");
        } else {
            boolean isCFGData = data.keySet().stream().anyMatch(CFG_CATEGORIES::contains);
            
            if (isCFGData) {
                // Handle Canada Food Guide data
                for (Map.Entry<String, Double> entry : data.entrySet()) {
                    if (CFG_CATEGORIES.contains(entry.getKey())) {
                        dataset.addValue(entry.getValue(), entry.getKey(), "");
                    }
                }
            } else {
                // Handle nutrition macro data
                double othersValue = 0;
                double vitaminsValue = 0;
                for (Map.Entry<String, Double> entry : data.entrySet()) {
                    String key = entry.getKey();
                    if (MAIN_MACROS.contains(key)) {
                        dataset.addValue(entry.getValue(), entry.getKey(), "");
                    } else if (VITAMINS.contains(key)) {
                        vitaminsValue += entry.getValue();
                    } else {
                        othersValue += entry.getValue();
                    }
                }
                if (vitaminsValue > 0) {
                    dataset.addValue(vitaminsValue, "Vitamins", "");
                }
                if (othersValue > 0) {
                    dataset.addValue(othersValue, "Others", "");
                }
            }
        }
        return dataset;
    }

    private void styleBarChart() {
        jfreeChart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = jfreeChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setMaximumBarWidth(0.1);
        
        // Set colors for different data types
        boolean isCFGData = data.keySet().stream().anyMatch(CFG_CATEGORIES::contains);
        if (isCFGData) {
            // Colors for Canada Food Guide categories
            Paint[] cfgColors = {
                new Color(76, 175, 80),   // Green for Vegetables & Fruits
                new Color(255, 193, 7),   // Amber for Whole Grains
                new Color(244, 67, 54),   // Red for Protein Foods
                new Color(33, 150, 243)   // Blue for Dairy & Alternatives
            };
            for (int i = 0; i < cfgColors.length && i < renderer.getRowCount(); i++) {
                renderer.setSeriesPaint(i, cfgColors[i]);
            }
        } else {
            // Colors for nutrition data
            Paint[] nutritionColors = {
                new Color(76, 175, 80),   // Green
                new Color(255, 152, 0),   // Orange
                new Color(156, 39, 176),  // Purple
                new Color(244, 67, 54),   // Red
                new Color(33, 150, 243)   // Blue
            };
            for (int i = 0; i < nutritionColors.length && i < renderer.getRowCount(); i++) {
                renderer.setSeriesPaint(i, nutritionColors[i]);
            }
        }
    }

    @Override
    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
        jfreeChart.setTitle(title);
    }

    @Override
    public void updateData(Map<String, Double> newData) {
        this.data = newData;
        DefaultCategoryDataset dataset = createBarDataset(newData);
        jfreeChart.getCategoryPlot().setDataset(dataset);
    }

    @Override
    public ChartType getType() {
        return ChartType.BAR;
    }
} 