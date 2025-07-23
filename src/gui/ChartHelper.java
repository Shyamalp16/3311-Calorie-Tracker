package gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.PlotOrientation;
import java.awt.Color;
import java.util.Map;
import java.util.List;
import models.Meal;

public class ChartHelper {

    public static ChartPanel createPieChart(String title, Map<String, Double> data) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        if (data.isEmpty() || data.values().stream().allMatch(v -> v == 0)) {
            dataset.setValue("No data", 1);
        } else {
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                dataset.setValue(entry.getKey(), entry.getValue());
            }
        }

        JFreeChart pieChart = ChartFactory.createPieChart(title, dataset, true, true, false);
        pieChart.setBackgroundPaint(Color.WHITE);
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setLabelGenerator(null);

        return new ChartPanel(pieChart);
    }

    public static ChartPanel createLineChart(String title, String xAxisLabel, String yAxisLabel, Map<String, Double> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (data.isEmpty()) {
            dataset.addValue(0, "No data", "");
        } else {
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                dataset.addValue(entry.getValue(), title, entry.getKey());
            }
        }

        JFreeChart lineChart = ChartFactory.createLineChart(title, xAxisLabel, yAxisLabel, dataset,
                PlotOrientation.VERTICAL, true, true, false);
        lineChart.setBackgroundPaint(Color.WHITE);
        lineChart.getCategoryPlot().setBackgroundPaint(Color.WHITE);

        return new ChartPanel(lineChart);
    }

    public static ChartPanel createBarChart(String title, String xAxisLabel, String yAxisLabel, Map<String, Double> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (data.isEmpty()) {
            dataset.addValue(0, "No data", "");
        } else {
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                dataset.addValue(entry.getValue(), entry.getKey(), "");
            }
        }

        JFreeChart barChart = ChartFactory.createBarChart(title, xAxisLabel, yAxisLabel, dataset,
                PlotOrientation.VERTICAL, true, true, false);
        barChart.setBackgroundPaint(Color.WHITE);
        barChart.getCategoryPlot().setBackgroundPaint(Color.WHITE);

        return new ChartPanel(barChart);
    }
}
