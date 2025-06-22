package Nutrition_Analysis_Visualization;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import org.jfree.chart.JFreeChart;

@SuppressWarnings("unused")
public class ChartGenerator {
public JFreeChart generateChart(ChartData chartData) {
 switch (chartData.getChartType()) {
     case PIE:
         return createPieChart(chartData);
     case BAR:
         return createBarChart(chartData);
     case LINE:
         return createLineChart(chartData);
     default:
         return createPieChart(chartData);
 }
}

private JFreeChart createPieChart(ChartData chartData) {
 DefaultPieDataset dataset = new DefaultPieDataset();
 
 chartData.getData().forEach((key, value) -> {
     dataset.setValue(key, value);
 });
 
 JFreeChart chart = ChartFactory.createPieChart(
     chartData.getChartTitle(),
     dataset,
     true, // legend
     true, // tooltips
     false // URLs
 );
 
 PiePlot plot = (PiePlot) chart.getPlot();
 plot.setSectionOutlinesVisible(false);
 plot.setLabelGenerator(null);
 
 return chart;
}

private JFreeChart createBarChart(ChartData chartData) {
 DefaultCategoryDataset dataset = new DefaultCategoryDataset();
 
 chartData.getData().forEach((key, value) -> {
     dataset.addValue(value, "Value", key);
 });
 
 return ChartFactory.createBarChart(
     chartData.getChartTitle(),
     "Category",
     "Value",
     dataset,
     PlotOrientation.VERTICAL,
     true, // legend
     true, // tooltips
     false // URLs
 );
}

private JFreeChart createLineChart(ChartData chartData) {
 DefaultCategoryDataset dataset = new DefaultCategoryDataset();
 
 chartData.getData().forEach((key, value) -> {
     dataset.addValue(value, "Value", key);
 });
 
 return ChartFactory.createLineChart(
     chartData.getChartTitle(),
     "Category",
     "Value",
     dataset,
     PlotOrientation.VERTICAL,
     true, // legend
     true, // tooltips
     false // URLs
 );
}
}
