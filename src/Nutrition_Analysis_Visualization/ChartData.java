package Nutrition_Analysis_Visualization;

import java.util.Map;

public class ChartData {
private String chartTitle;
private Map<String, Double> data;
private ChartType chartType;

public ChartData(String chartTitle, Map<String, Double> data, ChartType chartType) {
   this.chartTitle = chartTitle;
   this.data = data;
   this.chartType = chartType;
}

// Getters
public String getChartTitle() { return chartTitle; }
public Map<String, Double> getData() { return data; }
public ChartType getChartType() { return chartType; }

public enum ChartType {
   PIE, BAR, LINE
}
}

