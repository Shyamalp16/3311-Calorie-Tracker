package View;

import models.FoodSwapRecommendation;
import models.UserSettings;
import models.UnitSystem;
import models.Meal;
import models.MealItem;
import models.Food;
import Database.UserSettingsDAO;
import Database.MealDAO;
import Database.FoodDAO;
import logic.SwapApplicationService;
import utils.UnitHelper;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import org.jfree.chart.ChartPanel;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

public class BeforeAfterComparisonDialog extends JDialog {
    
    private List<FoodSwapRecommendation> swaps;
    private SwapApplicationService swapApplicationService;
    private int userId;
    private Date swapDate;
    private Runnable onTryDifferent;
    private UserSettings userSettings;
    private MealDAO mealDAO;
    private FoodDAO foodDAO;
    private final Color COLOR_PRIMARY = new Color(76, 175, 80);
    private final Color COLOR_SECONDARY = new Color(33, 150, 243);
    private final Font FONT_TITLE = new Font("Arial", Font.BOLD, 18);
    private final Font FONT_SUBTITLE = new Font("Arial", Font.BOLD, 16);
    private final Font FONT_NORMAL = new Font("Arial", Font.PLAIN, 14);
    
    public BeforeAfterComparisonDialog(JFrame parent, List<FoodSwapRecommendation> swaps, int userId, Date swapDate, Runnable onTryDifferent) {
        super(parent, "Before/After Comparison", true);
        this.swaps = swaps;
        this.swapApplicationService = new SwapApplicationService();
        this.userId = userId;
        this.swapDate = swapDate;
        this.onTryDifferent = onTryDifferent;
        
        UserSettingsDAO settingsDAO = new UserSettingsDAO();
        this.userSettings = settingsDAO.getOrCreateDefaultSettings(userId);
        this.mealDAO = new MealDAO();
        this.foodDAO = new FoodDAO();
        
        initUI();
    }
    
    private void initUI() {
        setSize(1200, 900);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Before/After Comparison");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setBackground(COLOR_PRIMARY);
        titleLabel.setOpaque(true);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel comparisonPanel = createComparisonPanel();
        mainPanel.add(comparisonPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createComparisonPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        
        // Top section: Side-by-side meal comparison
        JPanel mealsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mealsPanel.setBackground(Color.WHITE);
        
        JPanel originalPanel = createMealPanel("Original Meal", true);
        mealsPanel.add(originalPanel);
        
        JPanel modifiedPanel = createMealPanel("Modified Meal", false);
        mealsPanel.add(modifiedPanel);
        
        mainPanel.add(mealsPanel, BorderLayout.CENTER);
        
        // Bottom section: Pie charts
        JPanel chartsPanel = createChartsPanel();
        mainPanel.add(chartsPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createMealPanel(String title, boolean isOriginal) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_SUBTITLE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(Color.WHITE);
        
        double totalCalories = 0, totalProtein = 0, totalCarbs = 0, totalFats = 0, totalFiber = 0;
        
        // Get all meals for the user on the specified date
        java.sql.Date sqlDate = new java.sql.Date(swapDate.getTime());
        List<Meal> mealsOnDate = mealDAO.getMealsForUserAndDate(userId, sqlDate);
        
        // Create a map of food swaps for quick lookup
        Map<Integer, Food> swapMap = new HashMap<>();
        for (FoodSwapRecommendation swap : swaps) {
            if (isOriginal) {
                swapMap.put(swap.getOriginalFood().getFoodID(), swap.getOriginalFood());
            } else {
                swapMap.put(swap.getOriginalFood().getFoodID(), swap.getRecommendedFood());
            }
        }
        
        // Display all meal items from all meals on this date
        for (Meal meal : mealsOnDate) {
            List<MealItem> mealItems = mealDAO.getMealItemsByMealId(meal.getMealId());
            
            for (MealItem mealItem : mealItems) {
                JPanel itemPanel = new JPanel(new BorderLayout(5, 5));
                itemPanel.setBackground(Color.WHITE);
                itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
                
                // Get food description
                Food food = foodDAO.getFoodById(mealItem.getFoodId()).orElse(null);
                String foodName = "Unknown Food";
                if (food != null) {
                    foodName = food.getFoodDescription();
                }
                
                // For modified meal, check if this food should be swapped
                if (!isOriginal && swapMap.containsKey(mealItem.getFoodId())) {
                    Food swappedFood = swapMap.get(mealItem.getFoodId());
                    foodName = swappedFood.getFoodDescription();
                    
                    // Calculate nutrition values for swapped food with same quantities
                    double quantity = mealItem.getQuantity();
                    double calories = swappedFood.getCalories() * quantity / 100.0;
                    double protein = swappedFood.getProtein() * quantity / 100.0;
                    double carbs = swappedFood.getCarbs() * quantity / 100.0;
                    double fats = swappedFood.getFats() * quantity / 100.0;
                    double fiber = swappedFood.getFiber() * quantity / 100.0;
                    
                    totalCalories += calories;
                    totalProtein += protein;
                    totalCarbs += carbs;
                    totalFats += fats;
                    totalFiber += fiber;
                    
                    JLabel nameLabel = new JLabel(String.format("%s: %.0f cal (%.1f%s)", 
                        truncateText(foodName, 25), calories, quantity, mealItem.getUnit()));
                    nameLabel.setFont(FONT_NORMAL);
                    
                    String unitSymbol = UnitHelper.getFoodWeightUnit(userSettings);
                    JLabel detailLabel = new JLabel(String.format("P: %.1f%s, C: %.1f%s, F: %.1f%s, Fib: %.1f%s",
                        UnitHelper.convertFoodWeightForDisplay(protein, userSettings), unitSymbol,
                        UnitHelper.convertFoodWeightForDisplay(carbs, userSettings), unitSymbol,
                        UnitHelper.convertFoodWeightForDisplay(fats, userSettings), unitSymbol,
                        UnitHelper.convertFoodWeightForDisplay(fiber, userSettings), unitSymbol));
                    detailLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                    detailLabel.setForeground(Color.BLUE); // Highlight swapped items in blue
                    
                    itemPanel.add(nameLabel, BorderLayout.NORTH);
                    itemPanel.add(detailLabel, BorderLayout.SOUTH);
                } else {
                    // Use actual meal item nutrition values (these are already calculated with quantities)
                    double calories = mealItem.getCalories();
                    double protein = mealItem.getProtein();
                    double carbs = mealItem.getCarbs();
                    double fats = mealItem.getFats();
                    double fiber = mealItem.getFiber();
                    
                    totalCalories += calories;
                    totalProtein += protein;
                    totalCarbs += carbs;
                    totalFats += fats;
                    totalFiber += fiber;
                    
                    JLabel nameLabel = new JLabel(String.format("%s: %.0f cal (%.1f%s)", 
                        truncateText(foodName, 25), calories, mealItem.getQuantity(), mealItem.getUnit()));
                    nameLabel.setFont(FONT_NORMAL);
                    
                    String unitSymbol = UnitHelper.getFoodWeightUnit(userSettings);
                    JLabel detailLabel = new JLabel(String.format("P: %.1f%s, C: %.1f%s, F: %.1f%s, Fib: %.1f%s",
                        UnitHelper.convertFoodWeightForDisplay(protein, userSettings), unitSymbol,
                        UnitHelper.convertFoodWeightForDisplay(carbs, userSettings), unitSymbol,
                        UnitHelper.convertFoodWeightForDisplay(fats, userSettings), unitSymbol,
                        UnitHelper.convertFoodWeightForDisplay(fiber, userSettings), unitSymbol));
                    detailLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                    detailLabel.setForeground(Color.GRAY);
                    
                    itemPanel.add(nameLabel, BorderLayout.NORTH);
                    itemPanel.add(detailLabel, BorderLayout.SOUTH);
                }
                
                itemsPanel.add(itemPanel);
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel totalsPanel = new JPanel();
        totalsPanel.setLayout(new BoxLayout(totalsPanel, BoxLayout.Y_AXIS));
        totalsPanel.setBackground(Color.WHITE);
        totalsPanel.setBorder(BorderFactory.createTitledBorder("Total"));
        
        totalsPanel.add(new JLabel(String.format("Calories: %.0f", totalCalories)));
        totalsPanel.add(new JLabel(String.format("Protein: %s", UnitHelper.formatFoodWeight(totalProtein, userSettings))));
        totalsPanel.add(new JLabel(String.format("Carbs: %s", UnitHelper.formatFoodWeight(totalCarbs, userSettings))));
        totalsPanel.add(new JLabel(String.format("Fats: %s", UnitHelper.formatFoodWeight(totalFats, userSettings))));
        totalsPanel.add(new JLabel(String.format("Fiber: %s", UnitHelper.formatFoodWeight(totalFiber, userSettings))));
        
        panel.add(totalsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createChartsPanel() {
        JPanel chartsPanel = new JPanel(new BorderLayout(10, 10));
        chartsPanel.setBackground(Color.WHITE);
        chartsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), 
            "Nutrient Distribution Charts",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            FONT_SUBTITLE
        ));
        
        // Calculate nutrition data for charts
        Map<String, Double> originalNutrients = calculateOriginalNutrients();
        Map<String, Double> modifiedNutrients = calculateModifiedNutrients();
        
        // Create side-by-side pie charts
        JPanel chartContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        chartContainer.setBackground(Color.WHITE);
        
        // Original meal pie chart
        ChartPanel originalChartPanel = ChartFactory.createChart(
            Chart.ChartType.PIE, 
            "Original Meal Nutrients", 
            originalNutrients
        );
        originalChartPanel.setPreferredSize(new Dimension(300, 250));
        chartContainer.add(originalChartPanel);
        
        // Modified meal pie chart  
        ChartPanel modifiedChartPanel = ChartFactory.createChart(
            Chart.ChartType.PIE, 
            "Modified Meal Nutrients", 
            modifiedNutrients
        );
        modifiedChartPanel.setPreferredSize(new Dimension(300, 250));
        chartContainer.add(modifiedChartPanel);
        
        chartsPanel.add(chartContainer, BorderLayout.CENTER);
        
        return chartsPanel;
    }
    
    private Map<String, Double> calculateOriginalNutrients() {
        Map<String, Double> nutrients = new HashMap<>();
        
        // Get all meals for the user on the specified date
        java.sql.Date sqlDate = new java.sql.Date(swapDate.getTime());
        List<Meal> mealsOnDate = mealDAO.getMealsForUserAndDate(userId, sqlDate);
        
        double totalProtein = 0, totalCarbs = 0, totalFats = 0, totalFiber = 0;
        
        // Calculate totals from actual meal items
        for (Meal meal : mealsOnDate) {
            List<MealItem> mealItems = mealDAO.getMealItemsByMealId(meal.getMealId());
            
            for (MealItem mealItem : mealItems) {
                totalProtein += mealItem.getProtein();
                totalCarbs += mealItem.getCarbs();
                totalFats += mealItem.getFats();
                totalFiber += mealItem.getFiber();
            }
        }
        
        // Convert to user's preferred units
        nutrients.put("Protein", UnitHelper.convertFoodWeightForDisplay(totalProtein, userSettings));
        nutrients.put("Carbohydrates", UnitHelper.convertFoodWeightForDisplay(totalCarbs, userSettings));
        nutrients.put("Fats", UnitHelper.convertFoodWeightForDisplay(totalFats, userSettings));
        nutrients.put("Fiber", UnitHelper.convertFoodWeightForDisplay(totalFiber, userSettings));
        
        return nutrients;
    }
    
    private Map<String, Double> calculateModifiedNutrients() {
        Map<String, Double> nutrients = new HashMap<>();
        
        // Get all meals for the user on the specified date
        java.sql.Date sqlDate = new java.sql.Date(swapDate.getTime());
        List<Meal> mealsOnDate = mealDAO.getMealsForUserAndDate(userId, sqlDate);
        
        // Create a map of food swaps for quick lookup
        Map<Integer, Food> swapMap = new HashMap<>();
        for (FoodSwapRecommendation swap : swaps) {
            swapMap.put(swap.getOriginalFood().getFoodID(), swap.getRecommendedFood());
        }
        
        double totalProtein = 0, totalCarbs = 0, totalFats = 0, totalFiber = 0;
        
        // Calculate totals with swaps applied
        for (Meal meal : mealsOnDate) {
            List<MealItem> mealItems = mealDAO.getMealItemsByMealId(meal.getMealId());
            
            for (MealItem mealItem : mealItems) {
                // Check if this food should be swapped
                if (swapMap.containsKey(mealItem.getFoodId())) {
                    Food swappedFood = swapMap.get(mealItem.getFoodId());
                    double quantity = mealItem.getQuantity();
                    
                    // Calculate nutrition values for swapped food with same quantities
                    totalProtein += swappedFood.getProtein() * quantity / 100.0;
                    totalCarbs += swappedFood.getCarbs() * quantity / 100.0;
                    totalFats += swappedFood.getFats() * quantity / 100.0;
                    totalFiber += swappedFood.getFiber() * quantity / 100.0;
                } else {
                    // No swap for this food - use original values
                    totalProtein += mealItem.getProtein();
                    totalCarbs += mealItem.getCarbs();
                    totalFats += mealItem.getFats();
                    totalFiber += mealItem.getFiber();
                }
            }
        }
        
        // Convert to user's preferred units
        nutrients.put("Protein", UnitHelper.convertFoodWeightForDisplay(totalProtein, userSettings));
        nutrients.put("Carbohydrates", UnitHelper.convertFoodWeightForDisplay(totalCarbs, userSettings));
        nutrients.put("Fats", UnitHelper.convertFoodWeightForDisplay(totalFats, userSettings));
        nutrients.put("Fiber", UnitHelper.convertFoodWeightForDisplay(totalFiber, userSettings));
        
        return nutrients;
    }
    
    private String truncateText(String text, int maxLength) {
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
    
    private JPanel createNutrientChangesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Nutrient Changes"));
        
        String[] columnNames = {"Nutrient", "Before", "After", "Change"};
        Object[][] data = calculateNutrientChanges();
        
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(tableModel);
        table.setFont(FONT_NORMAL);
        table.getTableHeader().setFont(FONT_NORMAL);
        table.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(400, 120));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private Object[][] calculateNutrientChanges() {
        double originalCals = 0, originalProtein = 0, originalCarbs = 0, originalFats = 0, originalFiber = 0;
        double modifiedCals = 0, modifiedProtein = 0, modifiedCarbs = 0, modifiedFats = 0, modifiedFiber = 0;

        // Get all meals for the user on the specified date
        java.sql.Date sqlDate = new java.sql.Date(swapDate.getTime());
        List<Meal> mealsOnDate = mealDAO.getMealsForUserAndDate(userId, sqlDate);
        
        // Create a map of food swaps for quick lookup
        Map<Integer, Food> swapMap = new HashMap<>();
        for (FoodSwapRecommendation swap : swaps) {
            swapMap.put(swap.getOriginalFood().getFoodID(), swap.getRecommendedFood());
        }
        
        // Calculate totals for original and modified meals
        for (Meal meal : mealsOnDate) {
            List<MealItem> mealItems = mealDAO.getMealItemsByMealId(meal.getMealId());
            
            for (MealItem mealItem : mealItems) {
                // Original values (actual meal item nutrition)
                originalCals += mealItem.getCalories();
                originalProtein += mealItem.getProtein();
                originalCarbs += mealItem.getCarbs();
                originalFats += mealItem.getFats();
                originalFiber += mealItem.getFiber();
                
                // Modified values - check if this food should be swapped
                if (swapMap.containsKey(mealItem.getFoodId())) {
                    Food swappedFood = swapMap.get(mealItem.getFoodId());
                    double quantity = mealItem.getQuantity();
                    
                    // Calculate nutrition values for swapped food with same quantities
                    modifiedCals += swappedFood.getCalories() * quantity / 100.0;
                    modifiedProtein += swappedFood.getProtein() * quantity / 100.0;
                    modifiedCarbs += swappedFood.getCarbs() * quantity / 100.0;
                    modifiedFats += swappedFood.getFats() * quantity / 100.0;
                    modifiedFiber += swappedFood.getFiber() * quantity / 100.0;
                } else {
                    // No swap for this food - use original values
                    modifiedCals += mealItem.getCalories();
                    modifiedProtein += mealItem.getProtein();
                    modifiedCarbs += mealItem.getCarbs();
                    modifiedFats += mealItem.getFats();
                    modifiedFiber += mealItem.getFiber();
                }
            }
        }

        return new Object[][] {
            {"Calories", String.format("%.0f", originalCals),
             String.format("%.0f", modifiedCals),
             formatChange(modifiedCals - originalCals, false)},
            {"Protein", String.format("%.1fg", originalProtein),
             String.format("%.1fg", modifiedProtein),
             formatChange(modifiedProtein - originalProtein, true)},
            {"Carbs", String.format("%.1fg", originalCarbs),
             String.format("%.1fg", modifiedCarbs),
             formatChange(modifiedCarbs - originalCarbs, true)},
            {"Fats", String.format("%.1fg", originalFats),
             String.format("%.1fg", modifiedFats),
             formatChange(modifiedFats - originalFats, true)},
            {"Fiber", String.format("%.1fg", originalFiber),
             String.format("%.1fg", modifiedFiber),
             formatChange(modifiedFiber - originalFiber, true)}
        };
    }
    
    private String formatChange(double change, boolean showGrams) {
        String unit = showGrams ? "g" : "";
        if (change > 0) {
            return String.format("<html><font color='green'>+%.1f%s</font></html>", change, unit);
        } else if (change < 0) {
            return String.format("<html><font color='red'>%.1f%s</font></html>", change, unit);
        } else {
            return String.format("%.1f%s", change, unit);
        }
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JPanel changesPanel = createNutrientChangesPanel();
        panel.add(changesPanel, BorderLayout.CENTER);
        
        JPanel buttonSubPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonSubPanel.setBackground(Color.WHITE);
        
        JButton applyButton = new JButton("Apply Swaps");
        styleButton(applyButton);
        applyButton.addActionListener(this::applySwaps);
        
        JButton applyOverTimeButton = new JButton("Apply Swaps Over Time");
        styleButton(applyOverTimeButton);
        applyOverTimeButton.addActionListener(this::applySwapsOverTime);
        
        JButton tryDifferentButton = new JButton("Try Different Swaps");
        styleButton(tryDifferentButton);
        tryDifferentButton.addActionListener(this::tryDifferentSwaps);
        
        buttonSubPanel.add(applyButton);
        buttonSubPanel.add(applyOverTimeButton);
        buttonSubPanel.add(tryDifferentButton);
        
        panel.add(buttonSubPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void styleButton(JButton button) {
        button.setFont(FONT_NORMAL);
        button.setBackground(COLOR_SECONDARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }
    
    private void applySwaps(ActionEvent e) {
        try {
            boolean success = swapApplicationService.applySwapsToCurrentMeal(swaps, userId, swapDate);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Swaps applied to your current meals!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                if (getParent() instanceof View.Dashboard) {
                    ((View.Dashboard) getParent()).refreshDashboard();
                }
                
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Some swaps could not be applied. Check console for details.",
                    "Partial Success",
                    JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error applying swaps: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void applySwapsOverTime(ActionEvent e) {
        // Show dialog to choose date range or apply to all
        ApplyOverTimeDialog dialog = new ApplyOverTimeDialog((JFrame)getParent(), swapDate);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Date startDate = dialog.getStartDate();
            Date endDate = dialog.getEndDate();
            boolean isApplyToAll = dialog.isApplyToAll();
            
            // Show confirmation dialog
            String confirmMessage;
            if (isApplyToAll) {
                confirmMessage = String.format("This will apply all swaps (%d swaps) to ALL meals in your entire history where these foods exist.\n" +
                               "Are you sure you want to continue?", swaps.size());
            } else {
                confirmMessage = String.format("This will apply all swaps (%d swaps) to all meals between %s and %s where these foods exist.\n" +
                               "Are you sure you want to continue?", 
                               swaps.size(),
                               new SimpleDateFormat("yyyy-MM-dd").format(startDate),
                               new SimpleDateFormat("yyyy-MM-dd").format(endDate));
            }
            
            int result = JOptionPane.showConfirmDialog(this,
                confirmMessage,
                "Apply Swaps Over Time",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (result != JOptionPane.YES_OPTION) {
                return;
            }

            boolean success = swapApplicationService.applySwapsToDateRange(swaps, userId, startDate, endDate);
            
            if (success) {
                SwapApplicationService.SwapEffectSummary summary = 
                    swapApplicationService.calculateSwapEffects(userId, startDate, endDate);
                
                String successMessage = isApplyToAll ? 
                    "Swaps applied to all meals successfully!" :
                    "Swaps applied to meals in date range successfully!";
                
                String message = String.format(
                    successMessage + "\n\n" +
                    "Date Range: %s to %s\n\n" +
                    "Total cumulative effect:\n" +
                    "Calories: %+.0f\n" +
                    "Protein: %+.1fg\n" +
                    "Fiber: %+.1fg\n" +
                    "Fat: %+.1fg\n" +
                    "Carbs: %+.1fg\n\n" +
                    "Total swaps applied: %d",
                    new SimpleDateFormat("yyyy-MM-dd").format(startDate), 
                    new SimpleDateFormat("yyyy-MM-dd").format(endDate),
                    summary.calorieChange,
                    summary.proteinChange,
                    summary.fiberChange,
                    summary.fatChange,
                    summary.carbChange,
                    summary.totalSwaps
                );
                
                JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
                
                if (getParent() instanceof View.Dashboard) {
                    ((View.Dashboard) getParent()).refreshDashboard();
                }
                
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Some swaps could not be applied. Check console for details.",
                    "Partial Success",
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    private void tryDifferentSwaps(ActionEvent e) {
        if (onTryDifferent != null) {
            onTryDifferent.run();
        }
        dispose();
    }
} 