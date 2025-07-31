package View;

import controller.DashboardController;
import controller.GoalsController;
import logic.facade.INutritionFacade;
import models.Goal;
import models.Meal;
import models.UserSettings;
import org.jfree.chart.ChartPanel;
import org.jfree.data.general.DefaultPieDataset;
import utils.UnitHelper;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class NutritionAnalysisPanel extends JPanel {

    private final Font FONT_TITLE = new Font("Arial", Font.BOLD, 18);
    private final Color COLOR_SECONDARY = new Color(33, 150, 243);
    private final Color COLOR_TEXT_LIGHT = Color.WHITE;
    private final Font FONT_NORMAL = new Font("Arial", Font.PLAIN, 14);

    private INutritionFacade nutritionFacade;
    private UserSettings currentSettings;
    private Frame parentFrame;
    private models.User currentUser;
    private JComboBox<String> timePeriodCombo;
    private JPanel leftColumn;
    private JPanel rightColumn;

    public NutritionAnalysisPanel(Frame parent, models.User currentUser, INutritionFacade nutritionFacade, UserSettings settings) {
        this.parentFrame = parent;
        this.currentUser = currentUser;
        this.nutritionFacade = nutritionFacade;
        this.currentSettings = settings;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Nutrition Analysis");
        titleLabel.setFont(FONT_TITLE);
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel timePeriodPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        timePeriodPanel.setBackground(Color.WHITE);
        timePeriodPanel.add(new JLabel("Time Period:"));
        timePeriodCombo = new JComboBox<>(new String[]{"Today", "Last 7 days", "Last 30 days", "Last 3 months"});
        timePeriodPanel.add(timePeriodCombo);
        topPanel.add(timePeriodPanel, BorderLayout.EAST);

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.setBackground(Color.WHITE);

        rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        rightColumn.setBackground(Color.WHITE);

        timePeriodCombo.addActionListener(e -> {
            updateNutritionAnalysisCharts(leftColumn, rightColumn, (String) timePeriodCombo.getSelectedItem());
        });

        add(topPanel, BorderLayout.NORTH);

        updateNutritionAnalysisCharts(leftColumn, rightColumn, (String) timePeriodCombo.getSelectedItem());

        contentPanel.add(leftColumn);
        contentPanel.add(rightColumn);

        add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        JButton goalsButton = new JButton("Set Nutrition Goals");
        styleButton(goalsButton);
        goalsButton.addActionListener(e -> {
            NutrientGoalsDialog goalsDialog = new NutrientGoalsDialog(parentFrame, currentUser, nutritionFacade, this::refresh);
            goalsDialog.setVisible(true);
        });
        buttonPanel.add(goalsButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void refresh() {
        if (timePeriodCombo != null && leftColumn != null && rightColumn != null) {
            String selectedPeriod = (String) timePeriodCombo.getSelectedItem();
            updateNutritionAnalysisCharts(leftColumn, rightColumn, selectedPeriod);
        }
    }

    private void updateNutritionAnalysisCharts(JPanel leftColumn, JPanel rightColumn, String timePeriod) {
        leftColumn.removeAll();
        rightColumn.removeAll();

        int days = 0;
        switch (timePeriod) {
            case "Today":
                days = 1;
                break;
            case "Last 7 days":
                days = 7;
                break;
            case "Last 30 days":
                days = 30;
                break;
            case "Last 3 months":
                days = 90;
                break;
        }

        Date endDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        cal.add(Calendar.DAY_OF_MONTH, -days + 1);
        Date startDate = cal.getTime();

        DashboardController.NutritionAnalysisData analysisData = nutritionFacade.getNutritionAnalysis(timePeriod);

        double totalProtein = analysisData.totalNutrition.totalProtein;
        double totalCarbs = analysisData.totalNutrition.totalCarbs;
        double totalFats = analysisData.totalNutrition.totalFats;
        double totalFiber = analysisData.totalNutrition.totalFiber;
        double totalVitaminA = analysisData.totalNutrition.totalVitaminA;
        double totalVitaminB = analysisData.totalNutrition.totalVitaminB;
        double totalVitaminC = analysisData.totalNutrition.totalVitaminC;
        double totalVitaminD = analysisData.totalNutrition.totalVitaminD;

        leftColumn.add(new JLabel("Nutrient Breakdown (Calories)"));
        DefaultPieDataset nutrientDataset = new DefaultPieDataset();
        nutrientDataset.setValue("Protein", totalProtein);
        nutrientDataset.setValue("Carbohydrates", totalCarbs);
        nutrientDataset.setValue("Fats", totalFats);
        nutrientDataset.setValue("Fiber", totalFiber);
        leftColumn.add(ChartFactory.createChart(Chart.ChartType.PIE, "Nutrient Breakdown", convertToMap(nutrientDataset)));

        leftColumn.add(Box.createVerticalStrut(20));
        leftColumn.add(new JLabel("Average Daily Intake"));
        Goal userGoals = analysisData.userGoals;
        double recommendedCalories = userGoals.getCalories();
        double recommendedProtein = userGoals.getProtein();
        double recommendedCarbs = userGoals.getCarbs();
        double recommendedFats = userGoals.getFats();
        double recommendedFiber = userGoals.getFiber();
        
        double avgCalories = analysisData.averageNutrition.totalCalories;
        leftColumn.add(new JLabel(String.format("Calories: %.0f / %.0f recommended", avgCalories, recommendedCalories)));
        
        double avgProtein = analysisData.averageNutrition.totalProtein;
        leftColumn.add(new JLabel(String.format("Protein: %s / %s recommended", 
            UnitHelper.formatFoodWeight(avgProtein, currentSettings), 
            UnitHelper.formatFoodWeight(recommendedProtein, currentSettings))));
            
        double avgCarbs = analysisData.averageNutrition.totalCarbs;
        leftColumn.add(new JLabel(String.format("Carbs: %s / %s recommended", 
            UnitHelper.formatFoodWeight(avgCarbs, currentSettings), 
            UnitHelper.formatFoodWeight(recommendedCarbs, currentSettings))));
            
        double avgFats = analysisData.averageNutrition.totalFats;
        leftColumn.add(new JLabel(String.format("Fats: %s / %s recommended", 
            UnitHelper.formatFoodWeight(avgFats, currentSettings), 
            UnitHelper.formatFoodWeight(recommendedFats, currentSettings))));
            
        double avgFiber = analysisData.averageNutrition.totalFiber;
        leftColumn.add(new JLabel(String.format("Fiber: %s / %s recommended", 
            UnitHelper.formatFoodWeight(avgFiber, currentSettings), 
            UnitHelper.formatFoodWeight(recommendedFiber, currentSettings))));


        List<Meal> meals = nutritionFacade.getMealsInDateRange(startDate, endDate);
        Map<java.time.LocalDate, Double> dailyTotals = new java.util.TreeMap<>();
        for (Meal meal : meals) {
            java.time.LocalDate mealDate = new java.sql.Date(meal.getMealDate().getTime()).toLocalDate();
            dailyTotals.put(mealDate, dailyTotals.getOrDefault(mealDate, 0.0) + meal.getTotalCalories());
        }

        Map<String, Double> calorieTrendData = new java.util.LinkedHashMap<>();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd-MMM");

        if (days <= 7) { 
            java.time.LocalDate current = new java.sql.Date(startDate.getTime()).toLocalDate();
            for (int i = 0; i < days; i++) {
                calorieTrendData.put(current.format(formatter), dailyTotals.getOrDefault(current, 0.0));
                current = current.plusDays(1);
            }
        } else { 
            Map<java.time.LocalDate, List<Double>> weeklyData = new java.util.TreeMap<>();
            java.time.LocalDate current = new java.sql.Date(startDate.getTime()).toLocalDate();
            for (int i = 0; i < days; i++) {
                java.time.LocalDate startOfWeek = current.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
                weeklyData.computeIfAbsent(startOfWeek, k -> new ArrayList<>()).add(dailyTotals.getOrDefault(current, 0.0));
                current = current.plusDays(1);
            }
            for (Map.Entry<java.time.LocalDate, List<Double>> entry : weeklyData.entrySet()) {
                double weeklyTotal = entry.getValue().stream().mapToDouble(Double::doubleValue).sum();
                int daysInWeek = entry.getValue().size();
                double weeklyAverage = (daysInWeek > 0) ? weeklyTotal / daysInWeek : 0.0;
                calorieTrendData.put(entry.getKey().format(formatter), weeklyAverage);
            }
        }
        
        rightColumn.add(new JLabel("Trends Over Time"));
        rightColumn.add(ChartFactory.createChart(Chart.ChartType.LINE, "Calorie Intake Trend", calorieTrendData));

        rightColumn.add(Box.createVerticalStrut(20));
        String weightUnit = UnitHelper.getFoodWeightUnitName(currentSettings);
        rightColumn.add(new JLabel("Total Nutrient Intake (" + weightUnit + ")"));
        Map<String, Double> topNutrientsData = new java.util.HashMap<>();
        
        topNutrientsData.put("Protein", UnitHelper.convertFoodWeightForDisplay(totalProtein, currentSettings));
        topNutrientsData.put("Carbohydrates", UnitHelper.convertFoodWeightForDisplay(totalCarbs, currentSettings));
        topNutrientsData.put("Fats", UnitHelper.convertFoodWeightForDisplay(totalFats, currentSettings));
        topNutrientsData.put("Fiber", UnitHelper.convertFoodWeightForDisplay(totalFiber, currentSettings));
        topNutrientsData.put("Vitamins", totalVitaminA + totalVitaminB + totalVitaminC + totalVitaminD);
        rightColumn.add(ChartFactory.createChart(Chart.ChartType.BAR, "Total Nutrient Intake", topNutrientsData));
        
        leftColumn.revalidate();
        leftColumn.repaint();
        rightColumn.revalidate();
        rightColumn.repaint();
    }

    private void styleButton(JButton button) {
        button.setFont(FONT_NORMAL);
        button.setBackground(COLOR_SECONDARY);
        button.setForeground(COLOR_TEXT_LIGHT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }

    private Map<String, Double> convertToMap(DefaultPieDataset dataset) {
        Map<String, Double> result = new java.util.HashMap<>();
        for (int i = 0; i < dataset.getItemCount(); i++) {
            Comparable key = dataset.getKey(i);
            Number value = dataset.getValue(i);
            result.put(key.toString(), value.doubleValue());
        }
        return result;
    }
}
