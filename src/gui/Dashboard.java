package gui;

import models.User;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    private User currentUser;

    // Colors and Fonts
    private final Color bgColor = new Color(45, 45, 45);
    private final Color primaryColor = new Color(34, 139, 34);
    private final Color textColor = Color.WHITE;
    private final Font titleFont = new Font("Arial", Font.BOLD, 24);
    private final Font labelFont = new Font("Arial", Font.PLAIN, 16);

    public Dashboard(User user) {
        this.currentUser = user;
        initUI();
    }

    private void initUI() {
        setTitle("CleanTracker Dashboard");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(bgColor);

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getName() + "!");
        welcomeLabel.setFont(titleFont);
        welcomeLabel.setForeground(textColor);
        headerPanel.add(welcomeLabel);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(bgColor);
        tabbedPane.setForeground(textColor);
        tabbedPane.setFont(labelFont);

        // Add tabs
        tabbedPane.addTab("Summary", createSummaryPanel());
        tabbedPane.addTab("Log Meal", createLogMealPanel());
        tabbedPane.addTab("Nutrition Analysis", createAnalysisPanel());

        // Main layout
        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Consumed Calories
        JLabel consumedLabel = new JLabel("Consumed Calories:");
        consumedLabel.setFont(labelFont);
        consumedLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(consumedLabel, gbc);

        JLabel consumedValue = new JLabel("1200 kcal"); // Placeholder
        consumedValue.setFont(labelFont);
        consumedValue.setForeground(primaryColor);
        gbc.gridx = 1;
        panel.add(consumedValue, gbc);

        // Target Calories
        JLabel targetLabel = new JLabel("Target Calories:");
        targetLabel.setFont(labelFont);
        targetLabel.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(targetLabel, gbc);

        JLabel targetValue = new JLabel("2000 kcal"); // Placeholder
        targetValue.setFont(labelFont);
        targetValue.setForeground(primaryColor);
        gbc.gridx = 1;
        panel.add(targetValue, gbc);

        return panel;
    }

    private JPanel createLogMealPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.add(new JLabel("Log Meal Tab - Content Here", SwingConstants.CENTER), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAnalysisPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10)); // Grid for multiple charts
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add different charts
        panel.add(createPieChartPanel());
        panel.add(createBarChartPanel());
        panel.add(createLineChartPanel());

        return panel;
    }

    private ChartPanel createPieChartPanel() {
        // Dataset for macronutrient breakdown
        org.jfree.data.general.DefaultPieDataset dataset = new org.jfree.data.general.DefaultPieDataset();
        dataset.setValue("Protein (40g)", 160);
        dataset.setValue("Carbs (150g)", 600);
        dataset.setValue("Fat (50g)", 450);

        // Create Pie Chart
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Macronutrient Split (Calories)",
                dataset,
                true, true, false);

        // Styling
        pieChart.setBackgroundPaint(bgColor);
        pieChart.getTitle().setPaint(textColor);
        pieChart.getLegend().setBackgroundPaint(bgColor);
        pieChart.getLegend().setItemPaint(textColor);
        org.jfree.chart.plot.PiePlot plot = (org.jfree.chart.plot.PiePlot) pieChart.getPlot();
        plot.setBackgroundPaint(bgColor);
        plot.setLabelBackgroundPaint(bgColor);
        plot.setLabelPaint(textColor);
        plot.setSimpleLabels(true);

        return new ChartPanel(pieChart);
    }

    private ChartPanel createBarChartPanel() {
        // Dataset for calories per meal
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(350, "Calories", "Breakfast");
        dataset.addValue(550, "Calories", "Lunch");
        dataset.addValue(600, "Calories", "Dinner");
        dataset.addValue(200, "Calories", "Snacks");

        // Create Bar Chart
        JFreeChart barChart = ChartFactory.createBarChart(
                "Calories by Meal",
                "Meal Type", "Calories",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        // Styling
        styleChart(barChart);

        return new ChartPanel(barChart);
    }

    private ChartPanel createLineChartPanel() {
        // Dataset for weekly calorie trend
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1800, "Calories", "Mon");
        dataset.addValue(2000, "Calories", "Tue");
        dataset.addValue(1900, "Calories", "Wed");
        dataset.addValue(2100, "Calories", "Thu");
        dataset.addValue(1700, "Calories", "Fri");
        dataset.addValue(2300, "Calories", "Sat");
        dataset.addValue(2200, "Calories", "Sun");

        // Create Line Chart
        JFreeChart lineChart = ChartFactory.createLineChart(
                "Weekly Calorie Trend",
                "Day", "Calories",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        // Styling
        styleChart(lineChart);

        return new ChartPanel(lineChart);
    }

    /**
     * Helper method to apply common styling to charts
     */
    private void styleChart(JFreeChart chart) {
        chart.setBackgroundPaint(bgColor);
        chart.getTitle().setPaint(textColor);
        
        // A chart might not have a legend, so check for null
        if (chart.getLegend() != null) {
            chart.getLegend().setBackgroundPaint(bgColor);
            chart.getLegend().setItemPaint(textColor);
        }

        org.jfree.chart.plot.CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(bgColor);
        plot.setRangeGridlinePaint(Color.DARK_GRAY);
        plot.getDomainAxis().setLabelPaint(textColor);
        plot.getDomainAxis().setTickLabelPaint(textColor);
        plot.getRangeAxis().setLabelPaint(textColor);
        plot.getRangeAxis().setTickLabelPaint(textColor);
    }
}
