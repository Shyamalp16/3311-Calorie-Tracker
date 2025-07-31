package View;

import logic.facade.INutritionFacade;
import org.jfree.chart.ChartPanel;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CanadaFoodGuidePanel extends JPanel {

    private final Font FONT_TITLE = new Font("Arial", Font.BOLD, 18);
    private final Font FONT_NORMAL = new Font("Arial", Font.PLAIN, 14);

    private INutritionFacade nutritionFacade;
    private JPanel cfgLeftColumn;
    private JPanel cfgRightColumn;
    private JPanel cfgBottomPanel;

    public CanadaFoodGuidePanel(INutritionFacade nutritionFacade) {
        this.nutritionFacade = nutritionFacade;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Canada Food Guide Adherence");
        titleLabel.setFont(FONT_TITLE);
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel timePeriodPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        timePeriodPanel.setBackground(Color.WHITE);

        JLabel timePeriodLabel = new JLabel("Analysis Period:");
        timePeriodLabel.setFont(FONT_NORMAL);

        JComboBox<String> cfgTimePeriodCombo = new JComboBox<>(new String[]{"All Time", "Last 30 Days", "Last 7 Days", "Today"});
        cfgTimePeriodCombo.setFont(FONT_NORMAL);
        cfgTimePeriodCombo.addActionListener(e -> {
            String selectedPeriod = (String) cfgTimePeriodCombo.getSelectedItem();
            updateCFGAnalysisForPeriod(selectedPeriod);
        });

        timePeriodPanel.add(timePeriodLabel);
        timePeriodPanel.add(cfgTimePeriodCombo);
        topPanel.add(timePeriodPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        JPanel mainContentPanel = new JPanel(new BorderLayout(20, 20));
        mainContentPanel.setBackground(Color.WHITE);

        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        chartsPanel.setBackground(Color.WHITE);

        cfgLeftColumn = new JPanel(new BorderLayout(15, 15));
        cfgLeftColumn.setBackground(Color.WHITE);
        
        cfgRightColumn = new JPanel(new BorderLayout(15, 15));
        cfgRightColumn.setBackground(Color.WHITE);

        updateCanadaFoodGuideCharts("All Time");

        chartsPanel.add(cfgLeftColumn);
        chartsPanel.add(cfgRightColumn);

        mainContentPanel.add(chartsPanel, BorderLayout.CENTER);

        cfgBottomPanel = createBottomInfoPanel("All Time");
        mainContentPanel.add(cfgBottomPanel, BorderLayout.SOUTH);

        add(mainContentPanel, BorderLayout.CENTER);
    }

    private void updateCanadaFoodGuideCharts(String timePeriod) {
        cfgLeftColumn.removeAll();
        cfgRightColumn.removeAll();

        JLabel leftLabel = new JLabel("Your Average Plate");
        leftLabel.setFont(FONT_NORMAL);
        leftLabel.setHorizontalAlignment(JLabel.CENTER);
        cfgLeftColumn.add(leftLabel, BorderLayout.NORTH);

        JLabel rightLabel = new JLabel("CFG Recommended Plate");
        rightLabel.setFont(FONT_NORMAL);
        rightLabel.setHorizontalAlignment(JLabel.CENTER);
        cfgRightColumn.add(rightLabel, BorderLayout.NORTH);

        Map<String, Double> userPlateData = nutritionFacade.calculateUserPlateData(timePeriod);
        
        DefaultPieDataset userDataset = new DefaultPieDataset();
        for (Map.Entry<String, Double> entry : userPlateData.entrySet()) {
            if (entry.getValue() > 0) {
                userDataset.setValue(entry.getKey(), entry.getValue());
            }
        }
        ChartPanel userChart = ChartFactory.createChart(Chart.ChartType.PIE, "Your Average Plate", convertToMap(userDataset));
        userChart.setPreferredSize(new java.awt.Dimension(300, 300));
        userChart.setMinimumSize(new java.awt.Dimension(300, 300));
        cfgLeftColumn.add(userChart, BorderLayout.CENTER);

        Map<String, Double> cfgPlateData = new HashMap<>();
        cfgPlateData.put("Vegetables & Fruits", 50.0);
        cfgPlateData.put("Whole Grains", 25.0);
        cfgPlateData.put("Protein Foods", 17.5);
        cfgPlateData.put("Dairy & Alternatives", 7.5);
        
        DefaultPieDataset cfgDataset = new DefaultPieDataset();
        for (Map.Entry<String, Double> entry : cfgPlateData.entrySet()) {
            if (entry.getValue() > 0) {
                cfgDataset.setValue(entry.getKey(), entry.getValue());
            }
        }
        ChartPanel cfgChart = ChartFactory.createChart(Chart.ChartType.PIE, "CFG Recommended Plate", convertToMap(cfgDataset));
        cfgChart.setPreferredSize(new java.awt.Dimension(300, 300));
        cfgChart.setMinimumSize(new java.awt.Dimension(300, 300));
        cfgRightColumn.add(cfgChart, BorderLayout.CENTER);

        cfgLeftColumn.revalidate();
        cfgLeftColumn.repaint();
        cfgRightColumn.revalidate();
        cfgRightColumn.repaint();
    }

    private JPanel createBottomInfoPanel(String timePeriod) {
        JPanel bottomPanel = new JPanel(new BorderLayout(20, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JPanel comparisonSection = new JPanel(new BorderLayout(10, 10));
        comparisonSection.setBackground(Color.WHITE);

        JLabel comparisonTitle = new JLabel("Nutritional Breakdown Comparison");
        comparisonTitle.setFont(FONT_NORMAL.deriveFont(Font.BOLD));
        comparisonTitle.setHorizontalAlignment(JLabel.CENTER);
        comparisonSection.add(comparisonTitle, BorderLayout.NORTH);

        String[] columnNames = {"Food Group", "Your %", "CFG Recommended"};
        Object[][] data = createComparisonTableData(timePeriod);
        
        JTable comparisonTable = new JTable(data, columnNames);
        comparisonTable.setRowHeight(25);
        comparisonTable.setFont(FONT_NORMAL);
        comparisonTable.getTableHeader().setFont(FONT_NORMAL.deriveFont(Font.BOLD));
        comparisonTable.setBackground(Color.WHITE);
        comparisonTable.setGridColor(Color.LIGHT_GRAY);
        
        JScrollPane tableScrollPane = new JScrollPane(comparisonTable);
        tableScrollPane.setPreferredSize(new java.awt.Dimension(500, 100));
        tableScrollPane.setBackground(Color.WHITE);
        
        JPanel tableWrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tableWrapperPanel.setBackground(Color.WHITE);
        tableWrapperPanel.add(tableScrollPane);
        
        comparisonSection.add(tableWrapperPanel, BorderLayout.CENTER);

        bottomPanel.add(comparisonSection, BorderLayout.CENTER);

        JPanel recommendationsPanel = createCFGRecommendationsPanel(timePeriod);
        bottomPanel.add(recommendationsPanel, BorderLayout.SOUTH);

        return bottomPanel;
    }

    private Object[][] createComparisonTableData(String timePeriod) {
        Map<String, Double> userPlateData = nutritionFacade.calculateUserPlateData(timePeriod);
        
        Object[][] data = {
                {"Vegetables & Fruits", 
                 String.format("%.0f%%", userPlateData.getOrDefault("Vegetables & Fruits", 0.0)), 
                 "50%"},
                {"Whole Grains", 
                 String.format("%.0f%%", userPlateData.getOrDefault("Whole Grains", 0.0)), 
                 "25%"},
                {"Protein Foods", 
                 String.format("%.0f%%", userPlateData.getOrDefault("Protein Foods", 0.0)), 
                 "17.5%"},
                {"Dairy & Alternatives", 
                 String.format("%.0f%%", userPlateData.getOrDefault("Dairy & Alternatives", 0.0)), 
                 "7.5%"},
        };
        return data;
    }

    private JPanel createCFGRecommendationsPanel(String timePeriod) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JLabel titleLabel = new JLabel("CFG Compliance Recommendations");
        titleLabel.setFont(FONT_NORMAL.deriveFont(Font.BOLD));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        String recommendations = nutritionFacade.generateCFGRecommendations(timePeriod);
        
        JTextArea recommendationsArea = new JTextArea(recommendations);
        recommendationsArea.setFont(FONT_NORMAL);
        recommendationsArea.setBackground(new Color(248, 248, 248));
        recommendationsArea.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        recommendationsArea.setEditable(false);
        recommendationsArea.setLineWrap(true);
        recommendationsArea.setWrapStyleWord(true);
        recommendationsArea.setRows(4);
        
        JScrollPane scrollPane = new JScrollPane(recommendationsArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(600, 100));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(scrollPane);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private void updateCFGAnalysisForPeriod(String timePeriod) {
        updateCanadaFoodGuideCharts(timePeriod);
        
        JPanel parentPanel = (JPanel) cfgBottomPanel.getParent();
        parentPanel.remove(cfgBottomPanel);
        cfgBottomPanel = createBottomInfoPanel(timePeriod);
        parentPanel.add(cfgBottomPanel, BorderLayout.SOUTH);
        
        parentPanel.revalidate();
        parentPanel.repaint();
    }

    public void refresh() {
        Component topPanel = getComponent(0);
        if (topPanel instanceof JPanel) {
            JPanel top = (JPanel) topPanel;
            Component timePeriodPanel = top.getComponent(1);
            if (timePeriodPanel instanceof JPanel) {
                for (Component c : ((JPanel) timePeriodPanel).getComponents()) {
                    if (c instanceof JComboBox) {
                        @SuppressWarnings("unchecked")
                        JComboBox<String> timePeriodCombo = (JComboBox<String>) c;
                        String selectedPeriod = (String) timePeriodCombo.getSelectedItem();
                        if (selectedPeriod != null) {
                            updateCFGAnalysisForPeriod(selectedPeriod);
                        }
                        break;
                    }
                }
            }
        }
    }

    private Map<String, Double> convertToMap(DefaultPieDataset dataset) {
        Map<String, Double> result = new HashMap<>();
        for (int i = 0; i < dataset.getItemCount(); i++) {
            Comparable key = dataset.getKey(i);
            Number value = dataset.getValue(i);
            result.put(key.toString(), value.doubleValue());
        }
        return result;
    }
}
