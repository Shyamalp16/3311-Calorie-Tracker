package gui;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.Color;
import java.util.Map;

public class ChartFactory {

    public enum ChartType {
        PIE,
        LINE,
        BAR
    }

    public static ChartPanel createChart(ChartType type, String title, Map<String, Double> data) {
        switch (type) {
            case PIE:
                return createPieChart(title, data);
            case LINE:
                // Using default labels, can be customized if needed
                return createLineChart(title, "Category", "Value", data);
            case BAR:
                // Using default labels, can be customized if needed
                return createBarChart(title, "Category", "Value", data);
            default:
                throw new IllegalArgumentException("Unsupported chart type: " + type);
        }
    }

    private static ChartPanel createPieChart(String title, Map<String, Double> data) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        if (data.isEmpty() || data.values().stream().allMatch(v -> v == 0)) {
            dataset.setValue("No data", 1);
        } else {
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                dataset.setValue(entry.getKey(), entry.getValue());
            }
        }

        // Use fully qualified name to avoid conflict
        JFreeChart pieChart = org.jfree.chart.ChartFactory.createPieChart(title, dataset, true, true, false);
        pieChart.setBackgroundPaint(Color.WHITE);
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setLabelGenerator(null);

        return new ChartPanel(pieChart);
    }

    private static ChartPanel createLineChart(String title, String xAxisLabel, String yAxisLabel, Map<String, Double> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (data.isEmpty()) {
            dataset.addValue(0, "No data", "");
        } else {
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                dataset.addValue(entry.getValue(), title, entry.getKey());
            }
        }

        // Use fully qualified name to avoid conflict
        JFreeChart lineChart = org.jfree.chart.ChartFactory.createLineChart(title, xAxisLabel, yAxisLabel, dataset,
                PlotOrientation.VERTICAL, true, true, false);
        lineChart.setBackgroundPaint(Color.WHITE);
        lineChart.getCategoryPlot().setBackgroundPaint(Color.WHITE);

        return new ChartPanel(lineChart);
    }

    private static ChartPanel createBarChart(String title, String xAxisLabel, String yAxisLabel, Map<String, Double> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (data.isEmpty()) {
            dataset.addValue(0, "No data", "");
        } else {
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                dataset.addValue(entry.getValue(), entry.getKey(), "");
            }
        }

        // Use fully qualified name to avoid conflict
        JFreeChart barChart = org.jfree.chart.ChartFactory.createBarChart(title, xAxisLabel, yAxisLabel, dataset,
                PlotOrientation.VERTICAL, true, true, false);
        barChart.setBackgroundPaint(Color.WHITE);
        barChart.getCategoryPlot().setBackgroundPaint(Color.WHITE);

        return new ChartPanel(barChart);
    }
}