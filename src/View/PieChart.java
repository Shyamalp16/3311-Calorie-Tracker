package View;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * ConcreteProduct - PieChart
 * Implements the Chart interface for creating pie charts
 */
public class PieChart implements Chart {
    
    private static final Set<String> MAIN_MACROS = new HashSet<>(Arrays.asList("Protein", "Fats", "Carbohydrates", "Fiber"));
    private static final Set<String> VITAMINS = new HashSet<>(Arrays.asList("Vitamin A", "Vitamin B", "Vitamin C", "Vitamin D"));
    
    private String title;
    private JFreeChart jfreeChart;
    private ChartPanel chartPanel;
    private Map<String, Double> data;

    public PieChart(String title, Map<String, Double> data) {
        this.title = title;
        this.data = data;
        createPieChart();
    }

    private void createPieChart() {
        DefaultPieDataset dataset = createPieDataset(data);
        jfreeChart = org.jfree.chart.ChartFactory.createPieChart(title, dataset, true, false, false);
        stylePieChart();
        chartPanel = new ChartPanel(jfreeChart);
    }

    private DefaultPieDataset createPieDataset(Map<String, Double> data) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        
        boolean isCFGData = data.keySet().stream().anyMatch(key -> 
            key.equals("Vegetables & Fruits") || key.equals("Whole Grains") || 
            key.equals("Protein Foods") || key.equals("Dairy & Alternatives"));
        
        
        if (data.isEmpty() || data.values().stream().allMatch(v -> v == 0)) {
            dataset.setValue("No data", 1);
        } else if (isCFGData) {
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                if (entry.getValue() > 0) {
                    dataset.setValue(entry.getKey(), entry.getValue());
                }
            }
            if (dataset.getItemCount() == 0) {
                dataset.setValue("No data consumed", 1);
            }
        } else {
            double othersValue = 0;
            double vitaminsValue = 0;
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                String key = entry.getKey();
                if (MAIN_MACROS.contains(key)) {
                    dataset.setValue(key, entry.getValue());
                } else if (VITAMINS.contains(key)) {
                    vitaminsValue += entry.getValue();
                } else {
                    othersValue += entry.getValue();
                }
            }
            if (vitaminsValue > 0) {
                dataset.setValue("Vitamins", vitaminsValue);
            }
            if (othersValue > 0) {
                dataset.setValue("Others", othersValue);
            }
        }
        
        
        return dataset;
    }

    private void stylePieChart() {
        jfreeChart.setBackgroundPaint(Color.WHITE);
        PiePlot plot = (PiePlot) jfreeChart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setLabelGenerator(null);
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
        DefaultPieDataset dataset = createPieDataset(newData);
        ((PiePlot) jfreeChart.getPlot()).setDataset(dataset);
    }

    @Override
    public ChartType getType() {
        return ChartType.PIE;
    }
} 