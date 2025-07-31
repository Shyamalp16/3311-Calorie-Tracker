package View;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import java.awt.Color;
import java.util.Map;

/**
 * ConcreteProduct - LineChart
 * Implements the Chart interface for creating line charts
 */
public class LineChart implements Chart {
    
    private String title;
    private JFreeChart jfreeChart;
    private ChartPanel chartPanel;
    private Map<String, Double> data;
    private String xAxisLabel;
    private String yAxisLabel;

    public LineChart(String title, Map<String, Double> data) {
        this(title, "Category", "Value", data);
    }

    public LineChart(String title, String xAxisLabel, String yAxisLabel, Map<String, Double> data) {
        this.title = title;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.data = data;
        createLineChart();
    }

    private void createLineChart() {
        DefaultCategoryDataset dataset = createLineDataset(data);
        jfreeChart = org.jfree.chart.ChartFactory.createLineChart(title, xAxisLabel, yAxisLabel, dataset,
                PlotOrientation.VERTICAL, true, false, false);
        styleLineChart();
        chartPanel = new ChartPanel(jfreeChart);
    }

    private DefaultCategoryDataset createLineDataset(Map<String, Double> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (data.isEmpty()) {
            dataset.addValue(0, "No data", "");
        } else {
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                dataset.addValue(entry.getValue(), title, entry.getKey());
            }
        }
        return dataset;
    }

    private void styleLineChart() {
        jfreeChart.setBackgroundPaint(Color.WHITE);
        jfreeChart.getCategoryPlot().setBackgroundPaint(Color.WHITE);
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
        DefaultCategoryDataset dataset = createLineDataset(newData);
        jfreeChart.getCategoryPlot().setDataset(dataset);
    }

    @Override
    public ChartType getType() {
        return ChartType.LINE;
    }
} 