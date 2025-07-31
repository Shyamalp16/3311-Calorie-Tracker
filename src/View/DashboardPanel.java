package View;

import controller.DashboardController;
import logic.facade.INutritionFacade;
import models.Meal;
import models.MealItem;
import models.UserSettings;
import org.jfree.chart.ChartPanel;
import org.jfree.data.general.DefaultPieDataset;
import utils.UnitHelper;
import utils.DateValidator;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import View.Main_Application_GUI;

public class DashboardPanel extends JPanel {

    private final Font FONT_TITLE = new Font("Arial", Font.BOLD, 18);
    private final Font FONT_NORMAL = new Font("Arial", Font.PLAIN, 14);
    private final Color COLOR_BACKGROUND = new Color(245, 245, 245);
    private final Color COLOR_TEXT_DARK = new Color(51, 51, 51);
    private final Color COLOR_PANEL_BACKGROUND = new Color(245, 245, 245);
    private final Color COLOR_SECONDARY = new Color(33, 150, 243);
    private final Color COLOR_TEXT_LIGHT = Color.WHITE;

    private INutritionFacade nutritionFacade;
    private UserSettings currentSettings;
    private Frame parentFrame;
    private models.User currentUser;

    private JTextField dashboardDateField;
    private JPanel leftColumnPanel;
    private ChartPanel nutritionChartPanel;
    private ChartPanel cfgComplianceChartPanel;

    public DashboardPanel(Frame parent, models.User currentUser, INutritionFacade nutritionFacade, UserSettings settings) {
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
        topPanel.setBackground(COLOR_BACKGROUND);

        JLabel titleLabel = new JLabel("Main Application - Dashboard");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(COLOR_TEXT_DARK);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(COLOR_BACKGROUND);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        datePanel.setBackground(COLOR_BACKGROUND);

        JPanel userActionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userActionsPanel.setBackground(COLOR_BACKGROUND);

        JButton editProfileButton = new JButton("Edit Profile");
        styleButton(editProfileButton);
        editProfileButton.addActionListener(e -> {
            ProfileEditDialog dialog = new ProfileEditDialog(parentFrame, currentUser, currentSettings);
            dialog.addProfileChangeListener((ProfileChangeListener) parentFrame);
            dialog.setVisible(true);
        });
        userActionsPanel.add(editProfileButton);

        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton);
        logoutButton.addActionListener(e -> {
            // Dispose the current Dashboard frame
            if (parentFrame instanceof JFrame) {
                ((JFrame) parentFrame).dispose();
            }
            // Show the Main_Application_GUI (splash screen)
            Main_Application_GUI.getInstance().setVisible(true);
        });
        userActionsPanel.add(logoutButton);

        datePanel.add(new JLabel("View Date:"));
        dashboardDateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 10);
        
        // Add Enter key support to date field
        dashboardDateField.addActionListener(e -> refresh());
        
        datePanel.add(dashboardDateField);
        JButton viewDateButton = new JButton("Go");
        styleButton(viewDateButton);
        viewDateButton.addActionListener(e -> refresh());
        datePanel.add(viewDateButton);

        JPanel rightSidePanel = new JPanel();
        rightSidePanel.setLayout(new BoxLayout(rightSidePanel, BoxLayout.Y_AXIS));
        rightSidePanel.setBackground(COLOR_BACKGROUND);
        rightSidePanel.add(userActionsPanel);
        rightSidePanel.add(datePanel);

        topPanel.add(rightSidePanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        DashboardController.NutritionSummary todaysNutrition = nutritionFacade.getNutritionSummaryForDate(new Date());
        leftColumnPanel = createLeftColumn(todaysNutrition, new Date());
        contentPanel.add(leftColumnPanel);
        
        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        rightColumn.setBackground(Color.WHITE);
        rightColumn.add(new JLabel("Daily Nutrition Chart"));
        nutritionChartPanel = createPieChartPlaceholder(todaysNutrition);
        rightColumn.add(nutritionChartPanel);
        rightColumn.add(Box.createRigidArea(new Dimension(0, 20)));
        JLabel cfgLabel = new JLabel("Food Guide Adherence");
        cfgLabel.setFont(FONT_NORMAL);
        rightColumn.add(cfgLabel);
        cfgComplianceChartPanel = createCFGComplianceChart();
        cfgComplianceChartPanel.setPreferredSize(new Dimension(400, 250));
        rightColumn.add(cfgComplianceChartPanel);
        contentPanel.add(rightColumn);

        add(contentPanel, BorderLayout.CENTER);
    }

    public void refresh() {
        DateValidator.ValidationResult result = DateValidator.validateDate(dashboardDateField.getText());
        if (!result.isValid()) {
            JOptionPane.showMessageDialog(this, result.getErrorMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Date selectedDate = result.getParsedDate();
        DashboardController.NutritionSummary nutrition = nutritionFacade.getNutritionSummaryForDate(selectedDate);
        updateDashboardPanels(nutrition, selectedDate);
    }

    private void updateDashboardPanels(DashboardController.NutritionSummary nutrition, Date selectedDate) {
        JPanel newLeftColumn = createLeftColumn(nutrition, selectedDate);
        
        Container parent = leftColumnPanel.getParent();
        parent.remove(leftColumnPanel);
        leftColumnPanel = newLeftColumn;
        parent.add(leftColumnPanel, 0); 
        
        ChartPanel newChartPanel = createPieChartPlaceholder(nutrition);
        Container chartParent = nutritionChartPanel.getParent();
        chartParent.remove(nutritionChartPanel);
        nutritionChartPanel = newChartPanel;
        chartParent.add(newChartPanel, 1);
        
        ChartPanel newCFGChart = createCFGComplianceChartForDate(selectedDate);
        Container cfgChartParent = cfgComplianceChartPanel.getParent();
        cfgChartParent.remove(cfgComplianceChartPanel);
        cfgComplianceChartPanel = newCFGChart;
        cfgChartParent.add(newCFGChart, 3);
        
        revalidate();
        repaint();
    }

    private JPanel createLeftColumn(DashboardController.NutritionSummary nutrition, Date selectedDate) {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);

        JPanel summaryPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Today's Summary"));

        summaryPanel.add(new JLabel("Total Calories:"));
        summaryPanel.add(new JLabel(String.format("%.0f", nutrition.totalCalories)));
        summaryPanel.add(new JLabel("Protein:"));
        summaryPanel.add(new JLabel(UnitHelper.formatFoodWeight(nutrition.totalProtein, currentSettings)));
        summaryPanel.add(new JLabel("Carbs:"));
        summaryPanel.add(new JLabel(UnitHelper.formatFoodWeight(nutrition.totalCarbs, currentSettings)));
        summaryPanel.add(new JLabel("Fat:"));
        summaryPanel.add(new JLabel(UnitHelper.formatFoodWeight(nutrition.totalFats, currentSettings)));
        // Add other nutrients...
        leftPanel.add(summaryPanel);

        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel recentMealsPanel = new JPanel();
        recentMealsPanel.setLayout(new BoxLayout(recentMealsPanel, BoxLayout.Y_AXIS));
        recentMealsPanel.setBackground(Color.WHITE);
        recentMealsPanel.setBorder(BorderFactory.createTitledBorder("Meals for Selected Date"));

        List<Meal> selectedDateMeals = nutritionFacade.getMealsForDate(selectedDate);
        
        if (selectedDateMeals.isEmpty()) {
            recentMealsPanel.add(new JLabel("No meals logged for this date."));
        } else {
            Map<String, List<Meal>> groupedMeals = new java.util.HashMap<>();
            for (Meal meal : selectedDateMeals) {
                groupedMeals.computeIfAbsent(meal.getMealType(), k -> new ArrayList<>()).add(meal);
            }

            java.util.List<String> mealOrder = java.util.Arrays.asList("Breakfast", "Lunch", "Dinner", "Snack");
            for (String mealType : mealOrder) {
                if (groupedMeals.containsKey(mealType)) {
                    List<Meal> mealsOfType = groupedMeals.get(mealType);
                    double totalCalories = mealsOfType.stream().mapToDouble(Meal::getTotalCalories).sum();
                    String description = String.format("%s - %.0f cal", mealType, totalCalories);
                    recentMealsPanel.add(createMealItem(description, mealsOfType));
                    recentMealsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
        }
        leftPanel.add(recentMealsPanel);

        return leftPanel;
    }

    private JPanel createMealItem(String description, List<Meal> meals) {
        JPanel mealPanel = new JPanel(new BorderLayout(10, 0));
        mealPanel.setBackground(COLOR_PANEL_BACKGROUND);
        mealPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(FONT_NORMAL);
        mealPanel.add(descriptionLabel, BorderLayout.CENTER);

        JButton detailsButton = new JButton("View Nutrition Details");
        detailsButton.addActionListener(e -> {
            List<MealItem> allItems = new ArrayList<>();
            for (Meal meal : meals) {
                allItems.addAll(nutritionFacade.getMealItemsByMealId(meal.getMealId()));
            }
            MealDetailsDialog dialog = new MealDetailsDialog(parentFrame, meals.get(0), allItems, nutritionFacade);
            dialog.setVisible(true);
        });
        styleButton(detailsButton);
        mealPanel.add(detailsButton, BorderLayout.EAST);

        return mealPanel;
    }

    private ChartPanel createPieChartPlaceholder(DashboardController.NutritionSummary nutrition) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        if (nutrition.totalProtein > 0) dataset.setValue("Protein", nutrition.totalProtein);
        if (nutrition.totalCarbs > 0) dataset.setValue("Carbohydrates", nutrition.totalCarbs);
        if (nutrition.totalFats > 0) dataset.setValue("Fats", nutrition.totalFats);
        if (nutrition.totalFiber > 0) dataset.setValue("Fiber", nutrition.totalFiber);
        
        if (dataset.getItemCount() == 0) {
            dataset.setValue("No Data", 1);
        }
        
        return ChartFactory.createChart(Chart.ChartType.PIE, "Daily Nutrition Breakdown", convertToMap(dataset));
    }

    private JPanel createChartPlaceholder(String text) {
        JPanel placeholder = new JPanel(new GridBagLayout());
        placeholder.setBackground(new Color(249, 249, 249));
        placeholder.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        placeholder.add(new JLabel(text));
        placeholder.setMinimumSize(new Dimension(200, 200));
        placeholder.setPreferredSize(new Dimension(200, 200));
        return placeholder;
    }

    private void styleButton(JButton button) {
        button.setFont(FONT_NORMAL);
        button.setBackground(COLOR_SECONDARY);
        button.setForeground(COLOR_TEXT_LIGHT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }

    private ChartPanel createCFGComplianceChart() {
        return createCFGComplianceChartForDate(new Date());
    }
    
    private ChartPanel createCFGComplianceChartForDate(Date date) {
        // For now, we'll use "Today" for the selected date
        // You could extend this to use date ranges if needed
        Map<String, Double> cfgData = nutritionFacade.calculateUserPlateData("Today");
        return ChartFactory.createChart(Chart.ChartType.BAR, "Food Guide Compliance (%)", cfgData);
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