package gui;

import models.User;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import Database.FoodDAO;
import Database.GoalDAO;
import Database.MealDAO;
import logic.FoodSwapEngine;
import logic.SwapApplicationService;
import models.FoodSwapGoal;
import models.FoodSwapRecommendation;
import gui.MealDetailsDialog;
import gui.NutrientGoalsDialog;
import static gui.ChartFactory.ChartType;
import static models.FoodSwapGoal.IntensityLevel;


public class Dashboard extends JFrame {

    private User currentUser;
    private final Color COLOR_TAB_INACTIVE = new Color(224, 224, 224);
    private final Color COLOR_TEXT_INACTIVE = new Color(51, 51, 51);
    private DefaultTableModel mealTableModel;
    private JTextField searchFoodField;
    private JSpinner quantitySpinner;
    private JComboBox<String> unitComboBox; // Add this line
    private JPopupMenu searchResultsPopup;
    private JList<String> searchResultsList;
    private List<models.Food> currentSearchResults;
    private Database.FoodDAO foodDAO;
    private Database.MealDAO mealDAO;
    private Database.GoalDAO goalDAO;
    private JLabel totalCaloriesLabel;
    private FoodSwapEngine swapEngine;
    private SwapApplicationService swapApplicationService;
    
    // Food Swaps UI components
    private JComboBox<String> goal1Combo;
    private JComboBox<String> intensityCombo;
    private JComboBox<String> goal2Combo;
    private JComboBox<String> intensity2Combo;
    private JPanel swapResultsPanel;
    private List<FoodSwapRecommendation> currentSwaps;
    private JTextField swapDateField;
    private List<models.MealItem> loggedMealItems;
    private JTextField dashboardDateField;
    private JTabbedPane tabbedPane;

    // Dashboard panels for refresh
    private JPanel leftColumnPanel;
    private ChartPanel nutritionChartPanel;

    // Styling constants from the mockup
    private final Color COLOR_PRIMARY = new Color(76, 175, 80);
    private final Color COLOR_SECONDARY = new Color(33, 150, 243);
    private final Color COLOR_BACKGROUND = new Color(245, 245, 245);
    private final Color COLOR_TEXT_LIGHT = Color.WHITE;
    private final Color COLOR_TEXT_DARK = new Color(51, 51, 51);
    private final Color COLOR_PANEL_BACKGROUND = new Color(245, 245, 245);

    private final Font FONT_TITLE = new Font("Arial", Font.BOLD, 18);
    private final Font FONT_SUBTITLE = new Font("Arial", Font.BOLD, 16);
    private final Font FONT_NORMAL = new Font("Arial", Font.PLAIN, 14);

    public Dashboard(User user) {
        this.currentUser = user;
        initUI();
    }

    private void initUI() {
        currentSearchResults = new ArrayList<>();
        foodDAO = new Database.FoodDAO(); // Initialize here
        mealDAO = new Database.MealDAO(); // Initialize here
        goalDAO = new Database.GoalDAO(); // Initialize here
        swapEngine = new FoodSwapEngine(); // Initialize swap engine
        swapApplicationService = new SwapApplicationService(); // Initialize swap application service
        currentSwaps = new ArrayList<>();
        loggedMealItems = new ArrayList<>();
        setTitle("Main Application - Dashboard");
        setSize(1200, 800); // Increased size for better layout
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(COLOR_BACKGROUND);

        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_NORMAL);

        // Add tabs
        tabbedPane.addTab("Dashboard", createDashboardPanel());
        tabbedPane.addTab("Log Meal", createLogMealPanel());
        tabbedPane.addTab("Food Swaps", createFoodSwapsPanel());
        tabbedPane.addTab("Nutrition Analysis", createNutritionAnalysisPanel());
        tabbedPane.addTab("Canada Food Guide", createCanadaFoodGuidePanel());

        // Add change listener for dynamic tab highlighting
        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateTabStyles(tabbedPane);
                if (tabbedPane.getSelectedIndex() == 0) { // Assuming Dashboard is the first tab
                    refreshDashboard();
                }
            }
        });

        // Initial style update
        updateTabStyles(tabbedPane);


        add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
    }

    private void updateTabStyles(JTabbedPane tabbedPane) {
        int selectedIndex = tabbedPane.getSelectedIndex();
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (i == selectedIndex) {
                tabbedPane.setBackgroundAt(i, COLOR_PRIMARY);
                tabbedPane.setForegroundAt(i, COLOR_TEXT_LIGHT);
            } else {
                tabbedPane.setBackgroundAt(i, COLOR_TAB_INACTIVE);
                tabbedPane.setForegroundAt(i, COLOR_TEXT_INACTIVE);
            }
        }
    }

    private JPanel createDashboardPanel() {
        JPanel dashboardPanel = new JPanel(new BorderLayout(20, 20));
        dashboardPanel.setBackground(Color.WHITE);
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel for title and date controls
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(COLOR_BACKGROUND);

        // Screen Title
        JLabel titleLabel = new JLabel("Main Application - Dashboard");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(COLOR_TEXT_DARK);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(COLOR_BACKGROUND);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // Date selection panel
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        datePanel.setBackground(COLOR_BACKGROUND);
        datePanel.add(new JLabel("View Date:"));
        dashboardDateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 10);
        datePanel.add(dashboardDateField);
        JButton viewDateButton = new JButton("Go");
        styleButton(viewDateButton);
        viewDateButton.addActionListener(e -> refreshDashboard());
        datePanel.add(viewDateButton);
        topPanel.add(datePanel, BorderLayout.EAST);

        dashboardPanel.add(topPanel, BorderLayout.NORTH);

        // Main content with two columns
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        List<models.Meal> todaysMeals = mealDAO.getMealsForUserAndDate(currentUser.getUserId(), new java.sql.Date(new Date().getTime()));
        leftColumnPanel = createLeftColumn(todaysMeals);
        contentPanel.add(leftColumnPanel);
        contentPanel.add(createRightColumn(todaysMeals));

        dashboardPanel.add(contentPanel, BorderLayout.CENTER);

        return dashboardPanel;
    }

    private JPanel createLeftColumn(List<models.Meal> todaysMeals) {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);

        // Today's Summary
        JPanel summaryPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Today's Summary"));

        double dailyTotalCalories = 0;
        double dailyTotalProtein = 0;
        double dailyTotalCarbs = 0;
        double dailyTotalFats = 0;
        double dailyTotalFiber = 0;

        for (models.Meal meal : todaysMeals) {
            dailyTotalCalories += meal.getTotalCalories();
            dailyTotalProtein += meal.getTotalProtein();
            dailyTotalCarbs += meal.getTotalCarbs();
            dailyTotalFats += meal.getTotalFats();
            dailyTotalFiber += meal.getTotalFiber();
        }

        summaryPanel.add(new JLabel("Total Calories:"));
        summaryPanel.add(new JLabel(String.format("%.0f", dailyTotalCalories)));
        summaryPanel.add(new JLabel("Protein:"));
        summaryPanel.add(new JLabel(String.format("%.0fg", dailyTotalProtein)));
        summaryPanel.add(new JLabel("Carbs:"));
        summaryPanel.add(new JLabel(String.format("%.0fg", dailyTotalCarbs)));
        summaryPanel.add(new JLabel("Fat:"));
        summaryPanel.add(new JLabel(String.format("%.0fg", dailyTotalFats)));
        summaryPanel.add(new JLabel("Fiber:"));
        summaryPanel.add(new JLabel(String.format("%.0fg", dailyTotalFiber)));
        leftPanel.add(summaryPanel);

        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Recent Meals
        JPanel recentMealsPanel = new JPanel();
        recentMealsPanel.setLayout(new BoxLayout(recentMealsPanel, BoxLayout.Y_AXIS));
        recentMealsPanel.setBackground(Color.WHITE);
        recentMealsPanel.setBorder(BorderFactory.createTitledBorder("Recent Meals"));

        if (todaysMeals.isEmpty()) {
            recentMealsPanel.add(new JLabel("No meals logged today."));
        } else {
            // Group meals by type
            java.util.Map<String, java.util.List<models.Meal>> groupedMeals = new java.util.HashMap<>();
            for (models.Meal meal : todaysMeals) {
                groupedMeals.computeIfAbsent(meal.getMealType(), k -> new java.util.ArrayList<>()).add(meal);
            }

            java.util.List<String> mealOrder = java.util.Arrays.asList("Breakfast", "Lunch", "Dinner", "Snack");
            for (String mealType : mealOrder) {
                if (groupedMeals.containsKey(mealType)) {
                    java.util.List<models.Meal> mealsOfType = groupedMeals.get(mealType);
                    double totalCalories = mealsOfType.stream().mapToDouble(models.Meal::getTotalCalories).sum();
                    String description = String.format("%s - %.0f cal", mealType, totalCalories);
                    recentMealsPanel.add(createMealItem(description, mealsOfType));
                    recentMealsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
        }
        leftPanel.add(recentMealsPanel);

        return leftPanel;
    }

    private JPanel createMealItem(String description, java.util.List<models.Meal> meals) {
        JPanel mealPanel = new JPanel(new BorderLayout(10, 0));
        mealPanel.setBackground(COLOR_PANEL_BACKGROUND);
        mealPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(FONT_NORMAL);
        mealPanel.add(descriptionLabel, BorderLayout.CENTER);

        JButton detailsButton = new JButton("View Nutrition Details");
        detailsButton.addActionListener(e -> {
            java.util.List<models.MealItem> allItems = new java.util.ArrayList<>();
            for (models.Meal meal : meals) {
                allItems.addAll(mealDAO.getMealItemsByMealId(meal.getMealId()));
            }
            MealDetailsDialog dialog = new MealDetailsDialog(this, meals.get(0), allItems);
            dialog.setVisible(true);
        });
        styleButton(detailsButton);
        mealPanel.add(detailsButton, BorderLayout.EAST);

        return mealPanel;
    }

    private JPanel createRightColumn(List<models.Meal> todaysMeals) {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);

        // Daily Nutrition Chart
        rightPanel.add(new JLabel("Daily Nutrition Chart"));
        nutritionChartPanel = createPieChartPlaceholder(todaysMeals);
        rightPanel.add(nutritionChartPanel);

        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Food Guide Adherence
        rightPanel.add(new JLabel("Food Guide Adherence"));
        rightPanel.add(createChartPlaceholder("[Bar Chart: CFG Compliance]"));

        return rightPanel;
    }

    private ChartPanel createPieChartPlaceholder(List<models.Meal> todaysMeals) {
        // Fetch today's meals to get dynamic data for the chart

        double dailyTotalProtein = 0;
        double dailyTotalCarbs = 0;
        double dailyTotalFats = 0;

        for (models.Meal meal : todaysMeals) {
            dailyTotalProtein += meal.getTotalProtein();
            dailyTotalCarbs += meal.getTotalCarbs();
            dailyTotalFats += meal.getTotalFats();
        }

        DefaultPieDataset dataset = new DefaultPieDataset();
        if (dailyTotalProtein > 0 || dailyTotalCarbs > 0 || dailyTotalFats > 0) {
            dataset.setValue(String.format("Protein %.0fg", dailyTotalProtein), dailyTotalProtein);
            dataset.setValue(String.format("Carbs %.0fg", dailyTotalCarbs), dailyTotalCarbs);
            dataset.setValue(String.format("Fat %.0fg", dailyTotalFats), dailyTotalFats);
        } else {
            // Default values if no data is available
            dataset.setValue("Protein 0g", 1);
            dataset.setValue("Carbs 0g", 1);
            dataset.setValue("Fat 0g", 1);
        }

        JFreeChart pieChart = org.jfree.chart.ChartFactory.createPieChart(
                null, dataset, true, true, false);

        pieChart.setBackgroundPaint(Color.WHITE);
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setLabelGenerator(null);

        // Set colors dynamically based on dataset keys
        if (dataset.getItemCount() > 0) {
            int i = 0;
            for (Object key : dataset.getKeys()) {
                if (key.toString().startsWith("Protein")) {
                    plot.setSectionPaint(key.toString(), new Color(255, 105, 97)); // Red
                } else if (key.toString().startsWith("Carbs")) {
                    plot.setSectionPaint(key.toString(), new Color(97, 168, 255));  // Blue
                } else if (key.toString().startsWith("Fat")) {
                    plot.setSectionPaint(key.toString(), new Color(255, 214, 97));   // Yellow
                }
                i++;
            }
        }

        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return chartPanel;
    }

    private JPanel createChartPlaceholder(String text) {
        JPanel placeholder = new JPanel(new GridBagLayout());
        placeholder.setBackground(new Color(249, 249, 249));
        Border line = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        Border empty = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        placeholder.setBorder(BorderFactory.createCompoundBorder(line, empty));
        placeholder.add(new JLabel(text));
        placeholder.setMinimumSize(new Dimension(200, 200));
        placeholder.setPreferredSize(new Dimension(200, 200));
        return placeholder;
    }

    private JPanel createLogMealPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Log Meal");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(COLOR_TEXT_DARK);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Form for Date and Meal Type
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(new JLabel("Date:"));
        JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 10);
        formPanel.add(dateField);

        formPanel.add(Box.createHorizontalStrut(20));

        formPanel.add(new JLabel("Meal Type:"));
        JComboBox<String> mealTypeCombo = new JComboBox<>(new String[]{"Breakfast", "Lunch", "Dinner", "Snack"});
        formPanel.add(mealTypeCombo);

        // Main content area
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(formPanel, BorderLayout.NORTH);

        // Food Items Table
        String[] columnNames = {"Food Item", "Quantity", "Unit", "Calories"}; // Removed hidden column
        mealTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells uneditable
            }
        };
        JTable table = new JTable(mealTableModel);
        table.setFont(FONT_NORMAL);
        table.setRowHeight(20);
        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Add/Remove and Totals Panel
        JPanel southPanel = new JPanel(new BorderLayout(10, 10));
        southPanel.setBackground(Color.WHITE);

        // Add food panel
        JPanel addFoodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addFoodPanel.setBackground(Color.WHITE);
        addFoodPanel.add(new JLabel("Add Food:"));
        searchFoodField = new JTextField("Search food items...", 20);
        // --- Autocomplete Search Field Setup ---

        // This flag prevents the DocumentListener from firing when we programmatically set the text
        final boolean[] isAdjusting = {false};

        // 1. DocumentListener: Updates the suggestion list as the user types.
        searchFoodField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }

            public void update() {
                if (isAdjusting[0]) return;

                SwingUtilities.invokeLater(() -> {
                    String searchTerm = searchFoodField.getText().trim();
                    if (searchTerm.length() > 2) {
                        currentSearchResults = foodDAO.searchFoodByName(searchTerm);
                        DefaultListModel<String> listModel = new DefaultListModel<>();
                        for (models.Food food : currentSearchResults) {
                            listModel.addElement(food.getFoodDescription());
                        }
                        searchResultsList.setModel(listModel);

                        if (!listModel.isEmpty()) {
                            if (!searchResultsPopup.isVisible()) {
                                // This needs to be focusable=false to prevent stealing focus
                                searchResultsPopup.setFocusable(false);
                                searchResultsPopup.show(searchFoodField, 0, searchFoodField.getHeight());
                            }
                        } else {
                            searchResultsPopup.setVisible(false);
                        }
                    } else {
                        searchResultsPopup.setVisible(false);
                    }
                });
            }
        });

        // 2. KeyListener: Handles navigation (Up, Down, Enter, Escape).
        searchFoodField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!searchResultsPopup.isVisible()) return;

                int listSize = searchResultsList.getModel().getSize();
                if (listSize == 0) return;

                int selectedIndex = searchResultsList.getSelectedIndex();

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_DOWN:
                        searchResultsList.setSelectedIndex((selectedIndex + 1) % listSize);
                        searchResultsList.ensureIndexIsVisible(searchResultsList.getSelectedIndex());
                        e.consume();
                        break;
                    case KeyEvent.VK_UP:
                        searchResultsList.setSelectedIndex((selectedIndex - 1 + listSize) % listSize);
                        searchResultsList.ensureIndexIsVisible(searchResultsList.getSelectedIndex());
                        e.consume();
                        break;
                    case KeyEvent.VK_ENTER:
                        if (searchResultsList.getSelectedIndex() != -1) {
                            isAdjusting[0] = true;
                            searchFoodField.setText(searchResultsList.getSelectedValue());
                            isAdjusting[0] = false;
                            searchResultsPopup.setVisible(false);

                            // Populate measures
                            models.Food selectedFood = currentSearchResults.get(searchResultsList.getSelectedIndex());
                            Map<String, Integer> measures = foodDAO.getMeasuresForFood(selectedFood.getFoodID());
                            unitComboBox.removeAllItems();
                            if (measures.isEmpty()) {
                                unitComboBox.addItem("g");
                            } else {
                                for (String measureName : measures.keySet()) {
                                    unitComboBox.addItem(measureName);
                                }
                            }
                        }
                        e.consume();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        searchResultsPopup.setVisible(false);
                        e.consume();
                        break;
                }
            }
        });
        addFoodPanel.add(searchFoodField);

        searchResultsPopup = new JPopupMenu();
        searchResultsList = new JList<>();

        // 3. MouseListener: Handles selecting an item with a click.
        searchResultsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 1 && searchResultsList.getSelectedIndex() != -1) {
                    isAdjusting[0] = true;
                    searchFoodField.setText(searchResultsList.getSelectedValue());
                    isAdjusting[0] = false;
                    searchResultsPopup.setVisible(false);

                    // Populate measures
                    models.Food selectedFood = currentSearchResults.get(searchResultsList.getSelectedIndex());
                    Map<String, Integer> measures = foodDAO.getMeasuresForFood(selectedFood.getFoodID());
                    unitComboBox.removeAllItems();
                    if (measures.isEmpty()) {
                        unitComboBox.addItem("g");
                    } else {
                        for (String measureName : measures.keySet()) {
                            unitComboBox.addItem(measureName);
                        }
                    }
                }
            }
        });
        searchResultsPopup.add(new JScrollPane(searchResultsList));
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        quantitySpinner.setPreferredSize(new Dimension(80, 25)); // Adjust size as needed
        addFoodPanel.add(quantitySpinner);

        unitComboBox = new JComboBox<>();
        unitComboBox.setPreferredSize(new Dimension(100, 25));
        addFoodPanel.add(unitComboBox);

        JButton addItemButton = new JButton("Add Item");
        addItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = searchResultsList.getSelectedIndex();
                if (selectedIndex != -1 && currentSearchResults != null && selectedIndex < currentSearchResults.size()) {
                    models.Food selectedFoodSummary = currentSearchResults.get(selectedIndex);
                    models.Food selectedFood = foodDAO.getFoodById(selectedFoodSummary.getFoodID()).orElse(null);
                    int quantity = (int) quantitySpinner.getValue();
                    String selectedUnit = (String) unitComboBox.getSelectedItem();

                    if (selectedFood != null) {
                        // Get the measure ID for the selected unit
                        Map<String, Integer> measures = foodDAO.getMeasuresForFood(selectedFood.getFoodID());
                        int measureId = measures.getOrDefault(selectedUnit, -1);

                        // Get the conversion factor
                        double conversionFactor = 1.0;
                        if (measureId != -1) {
                            conversionFactor = foodDAO.getConversionFactor(selectedFood.getFoodID(), measureId);
                        } else if (selectedUnit.equals("g")) {
                            // Special handling for 'g' if it's not in the measures map, assume 100g base
                            conversionFactor = 1.0 / 100.0;
                        }

                        System.out.println("UI: Food details fetched for " + selectedFood.getFoodDescription());
                        System.out.println("UI: Calories=" + selectedFood.getCalories() + ", Protein=" + selectedFood.getProtein());

                        // Apply the conversion factor to the nutrient values from the direct getters
                        double calories = selectedFood.getCalories() * quantity * conversionFactor;
                        double protein = selectedFood.getProtein() * quantity * conversionFactor;
                        double carbs = selectedFood.getCarbs() * quantity * conversionFactor;
                        double fats = selectedFood.getFats() * quantity * conversionFactor;
                        double fiber = selectedFood.getFiber() * quantity * conversionFactor;

                        models.MealItem mealItem = new models.MealItem(0, 0, selectedFood.getFoodID(), quantity, selectedUnit, calories, protein, carbs, fats, fiber);
                        loggedMealItems.add(mealItem); // Add to our backing list

                        mealTableModel.addRow(new Object[]{selectedFood.getFoodDescription(), quantity, selectedUnit, calories}); // Add only visible data
                        updateTotalCalories();
                        searchFoodField.setText("Search food items...");
                        quantitySpinner.setValue(1);
                        searchResultsPopup.setVisible(false);
                    } else {
                        JOptionPane.showMessageDialog(Dashboard.this, "Could not retrieve full details for the selected food.", "Data Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        styleButton(addItemButton);
        addFoodPanel.add(addItemButton);
        JButton removeItemButton = new JButton("Remove Selected");
        removeItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    mealTableModel.removeRow(selectedRow);
                    loggedMealItems.remove(selectedRow); // Remove from backing list
                    updateTotalCalories();
                }
            }
        });
        styleButton(removeItemButton);
        addFoodPanel.add(removeItemButton);

        southPanel.add(addFoodPanel, BorderLayout.NORTH);

        // Totals and Actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        actionsPanel.setBackground(Color.WHITE);
        totalCaloriesLabel = new JLabel("Total Calories: 0");
        actionsPanel.add(totalCaloriesLabel);
        JButton saveButton = new JButton("Save Meal");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String mealType = (String) mealTypeCombo.getSelectedItem();
                    Date mealDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateField.getText());

                    if (!mealType.equals("Snack")) {
                        List<models.Meal> existingMeals = mealDAO.getMealsForUserAndDate(currentUser.getUserId(), mealDate);
                        for (models.Meal existingMeal : existingMeals) {
                            if (existingMeal.getMealType().equals(mealType)) {
                                JOptionPane.showMessageDialog(Dashboard.this, "You have already logged a " + mealType + " for this date.", "Duplicate Meal", JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                        }
                    }

                    // Use the backing list of MealItem objects
                    List<models.MealItem> mealItems = new ArrayList<>(loggedMealItems);

                    // Calculate total nutrients from the meal items
                    double totalCalories = mealItems.stream().mapToDouble(models.MealItem::getCalories).sum();
                    double totalProtein = mealItems.stream().mapToDouble(models.MealItem::getProtein).sum();
                    double totalCarbs = mealItems.stream().mapToDouble(models.MealItem::getCarbs).sum();
                    double totalFats = mealItems.stream().mapToDouble(models.MealItem::getFats).sum();
                    double totalFiber = mealItems.stream().mapToDouble(models.MealItem::getFiber).sum();

                    // Create and save the meal
                    models.Meal meal = new models.Meal(0, currentUser.getUserId(), mealType, mealDate, new Timestamp(System.currentTimeMillis()), totalCalories, totalProtein, totalCarbs, totalFats, totalFiber);
                    int mealId = mealDAO.saveMeal(meal);

                    if (mealId != -1) {
                        // Save the meal items with the correct mealId
                        for (models.MealItem item : mealItems) {
                            item.setMealId(mealId);
                        }
                        mealDAO.saveMealItems(mealId, mealItems);
                        JOptionPane.showMessageDialog(Dashboard.this, "Meal saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        
                        // Refresh all charts and clear the current log
                        refreshAllTabs();
                        mealTableModel.setRowCount(0);
                        loggedMealItems.clear();
                        updateTotalCalories();
                    } else {
                        JOptionPane.showMessageDialog(Dashboard.this, "Failed to save meal.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(Dashboard.this, "Invalid date format. Please use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(Dashboard.this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        styleButton(saveButton);
        actionsPanel.add(saveButton);
        //JButton detailsButton = new JButton("View Nutrition Details");
        //styleButton(detailsButton);
        ///actionsPanel.add(detailsButton);

        southPanel.add(actionsPanel, BorderLayout.SOUTH);

        contentPanel.add(southPanel, BorderLayout.SOUTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createNutritionAnalysisPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Nutrition Analysis");
        titleLabel.setFont(FONT_TITLE);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Top panel for controls
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(new JLabel("Time Period:"));
        JComboBox<String> timePeriodCombo = new JComboBox<>(new String[]{"Today", "Last 7 days", "Last 30 days", "Last 3 months"});
        topPanel.add(timePeriodCombo);
        

        // Main content with two columns
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        // Left Column
        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.setBackground(Color.WHITE);
        
        // Right Column
        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        rightColumn.setBackground(Color.WHITE);
        
        timePeriodCombo.addActionListener(e -> {
            updateNutritionAnalysisCharts(leftColumn, rightColumn, (String) timePeriodCombo.getSelectedItem());
        });


        panel.add(topPanel, BorderLayout.NORTH);
        
        updateNutritionAnalysisCharts(leftColumn, rightColumn, (String) timePeriodCombo.getSelectedItem());

        contentPanel.add(leftColumn);
        contentPanel.add(rightColumn);

        panel.add(contentPanel, BorderLayout.CENTER);

        // Bottom buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        JButton exportButton = new JButton("Export Report");
        styleButton(exportButton);
        buttonPanel.add(exportButton);
        JButton goalsButton = new JButton("Set Nutrition Goals");
        styleButton(goalsButton);
        goalsButton.addActionListener(e -> {
            NutrientGoalsDialog goalsDialog = new NutrientGoalsDialog(this, currentUser);
            goalsDialog.setVisible(true);
        });
        buttonPanel.add(goalsButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
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
        cal.add(Calendar.DAY_OF_MONTH, -days + 1); // Adjust to get the correct start date
        Date startDate = cal.getTime();

        List<models.Meal> meals = mealDAO.getMealsInDateRange(currentUser.getUserId(), startDate, endDate);

        // --- Left Column: Pie Chart and Averages ---
        double totalCalories = meals.stream().mapToDouble(models.Meal::getTotalCalories).sum();
        double totalProtein = meals.stream().mapToDouble(models.Meal::getTotalProtein).sum();
        double totalCarbs = meals.stream().mapToDouble(models.Meal::getTotalCarbs).sum();
        double totalFats = meals.stream().mapToDouble(models.Meal::getTotalFats).sum();
        double totalFiber = meals.stream().mapToDouble(models.Meal::getTotalFiber).sum();

        // Daily Nutrient Breakdown (Pie Chart)
        Map<String, Double> nutrientData = new java.util.HashMap<>();
        nutrientData.put("Protein", totalProtein * 4); // 4 calories per gram
        nutrientData.put("Carbs", totalCarbs * 4); // 4 calories per gram
        nutrientData.put("Fats", totalFats * 9); // 9 calories per gram
        leftColumn.add(new JLabel("Nutrient Breakdown (Calories)"));
        leftColumn.add(ChartFactory.createChart(ChartType.PIE, "Nutrient Breakdown", nutrientData));

        // Average Daily Intake
        leftColumn.add(Box.createVerticalStrut(20));
        leftColumn.add(new JLabel("Average Daily Intake"));
        models.Goal userGoals = goalDAO.getGoalByUserId(currentUser.getUserId()).orElse(null);
        double recommendedCalories = (userGoals != null) ? userGoals.getCalories() : 2000;
        double recommendedProtein = (userGoals != null) ? userGoals.getProtein() : 75;
        double recommendedFiber = (userGoals != null) ? userGoals.getFiber() : 25;
        double avgCalories = totalCalories / (days > 0 ? days : 1);
        leftColumn.add(new JLabel(String.format("Calories: %.0f / %.0f recommended", avgCalories, recommendedCalories)));
        double avgProtein = totalProtein / (days > 0 ? days : 1);
        leftColumn.add(new JLabel(String.format("Protein: %.0fg / %.0fg recommended", avgProtein, recommendedProtein)));
        double avgFiber = totalFiber / (days > 0 ? days : 1);
        leftColumn.add(new JLabel(String.format("Fiber: %.0fg / %.0fg recommended", avgFiber, recommendedFiber)));

        // --- Right Column: Line and Bar Charts ---

        // First, calculate total calories per day
        Map<java.time.LocalDate, Double> dailyTotals = new java.util.TreeMap<>();
        for (models.Meal meal : meals) {
            java.time.LocalDate mealDate = new java.sql.Date(meal.getMealDate().getTime()).toLocalDate();
            dailyTotals.put(mealDate, dailyTotals.getOrDefault(mealDate, 0.0) + meal.getTotalCalories());
        }

        // Then, create the trend data with formatted labels based on the selected time period
        Map<String, Double> calorieTrendData = new java.util.LinkedHashMap<>();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd-MMM");

        if (days <= 7) { // Daily view for 7 days or less
            java.time.LocalDate current = new java.sql.Date(startDate.getTime()).toLocalDate();
            for (int i = 0; i < days; i++) {
                calorieTrendData.put(current.format(formatter), dailyTotals.getOrDefault(current, 0.0));
                current = current.plusDays(1);
            }
        } else { // Weekly average view for 30 or 90 days
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
                // Correctly average over the number of days present in that week for the period
                double weeklyAverage = (daysInWeek > 0) ? weeklyTotal / daysInWeek : 0.0;
                calorieTrendData.put(entry.getKey().format(formatter), weeklyAverage);
            }
        }
        
        rightColumn.add(new JLabel("Trends Over Time"));
        rightColumn.add(ChartFactory.createChart(ChartType.LINE, "Calorie Intake", calorieTrendData));

        // Top Nutrients (Bar Chart)
        rightColumn.add(Box.createVerticalStrut(20));
        rightColumn.add(new JLabel("Total Nutrient Intake (grams)"));
        Map<String, Double> topNutrientsData = new java.util.HashMap<>();
        topNutrientsData.put("Protein", totalProtein);
        topNutrientsData.put("Carbs", totalCarbs);
        topNutrientsData.put("Fats", totalFats);
        topNutrientsData.put("Fiber", totalFiber);
        rightColumn.add(ChartFactory.createChart(ChartType.BAR, "Total Nutrient Intake", topNutrientsData));
        
        leftColumn.revalidate();
        leftColumn.repaint();
        rightColumn.revalidate();
        rightColumn.repaint();
    }

    private JPanel createCanadaFoodGuidePanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Canada Food Guide Adherence");
        titleLabel.setFont(FONT_TITLE);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Main content with two columns
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        // Left Column
        JPanel leftColumn = new JPanel(new BorderLayout(10, 10));
        leftColumn.setBackground(Color.WHITE);
        leftColumn.add(new JLabel("Your Average Plate"), BorderLayout.NORTH);
        
        // Right Column
        JPanel rightColumn = new JPanel(new BorderLayout(10, 10));
        rightColumn.setBackground(Color.WHITE);
        rightColumn.add(new JLabel("CFG Recommended Plate"), BorderLayout.NORTH);
        
        updateCanadaFoodGuideCharts(leftColumn, rightColumn);

        contentPanel.add(leftColumn);
        contentPanel.add(rightColumn);

        panel.add(contentPanel, BorderLayout.CENTER);

        // Bottom button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        JButton dashboardButton = new JButton("Dashboard");
        styleButton(dashboardButton);
        buttonPanel.add(dashboardButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void updateCanadaFoodGuideCharts(JPanel leftColumn, JPanel rightColumn) {
        leftColumn.removeAll();
        rightColumn.removeAll();
        
        leftColumn.add(new JLabel("Your Average Plate"), BorderLayout.NORTH);
        rightColumn.add(new JLabel("CFG Recommended Plate"), BorderLayout.NORTH);

        // User's Plate
        Map<String, Double> userPlateData = new java.util.HashMap<>();
        List<models.Meal> allMeals = mealDAO.getMealsInDateRange(currentUser.getUserId(), new Date(0), new Date());
        Map<String, Integer> foodGroupCounts = new java.util.HashMap<>();
        int totalItems = 0;

        for (models.Meal meal : allMeals) {
            List<models.MealItem> items = mealDAO.getMealItemsByMealId(meal.getMealId());
            for (models.MealItem item : items) {
                models.Food food = foodDAO.getFoodById(item.getFoodId()).orElse(null);
                if (food != null) {
                    String foodGroup = foodDAO.getFoodGroupById(food.getFoodID());
                    foodGroupCounts.put(foodGroup, foodGroupCounts.getOrDefault(foodGroup, 0) + 1);
                    totalItems++;
                }
            }
        }

        if (totalItems > 0) {
            for (Map.Entry<String, Integer> entry : foodGroupCounts.entrySet()) {
                userPlateData.put(entry.getKey(), (double) entry.getValue() / totalItems * 100);
            }
        }
        
        leftColumn.add(ChartFactory.createChart(ChartType.PIE, "Your Average Plate", userPlateData), BorderLayout.CENTER);

        // CFG Recommended Plate
        Map<String, Double> cfgPlateData = new java.util.HashMap<>();
        cfgPlateData.put("Vegetables & Fruits", 50.0);
        cfgPlateData.put("Whole Grains", 25.0);
        cfgPlateData.put("Protein Foods", 25.0);
        rightColumn.add(ChartFactory.createChart(ChartType.PIE, "CFG Recommended Plate", cfgPlateData), BorderLayout.CENTER);

        // Comparison Table
        String[] columnNames = {"Food Group", "Your %", "CFG Recommended"};
        Object[][] data = {
                {"Vegetables & Fruits", String.format("%.0f%%", userPlateData.getOrDefault("Vegetables and Fruit", 0.0)), "50%"},
                {"Whole Grains", String.format("%.0f%%", userPlateData.getOrDefault("Grain Products", 0.0)), "25%"},
                {"Protein Foods", String.format("%.0f%%", userPlateData.getOrDefault("Meat and Alternatives", 0.0) + userPlateData.getOrDefault("Dairy and Egg Products", 0.0)), "25%"},
        };
        JTable table = new JTable(data, columnNames);
        leftColumn.add(new JScrollPane(table), BorderLayout.SOUTH);
        
        // Recommendations
        JPanel recommendationsPanel = new JPanel();
        recommendationsPanel.setLayout(new BoxLayout(recommendationsPanel, BoxLayout.Y_AXIS));
        recommendationsPanel.setBackground(Color.WHITE);
        double vegFruitDiff = 50.0 - userPlateData.getOrDefault("Vegetables and Fruit", 0.0);
        if (vegFruitDiff > 5) {
            recommendationsPanel.add(new JLabel("<html><b>⚠️ Increase vegetables and fruits</b><br><small>Add " + String.format("%.0f%%", vegFruitDiff) + " more to reach CFG guidelines</small></html>"));
        } else {
            recommendationsPanel.add(new JLabel("<html><b>✓ Vegetables and fruits intake is good</b></html>"));
        }
        recommendationsPanel.add(Box.createVerticalStrut(10));
        double proteinDiff = 25.0 - (userPlateData.getOrDefault("Meat and Alternatives", 0.0) + userPlateData.getOrDefault("Dairy and Egg Products", 0.0));
        if (proteinDiff > 5) {
            recommendationsPanel.add(new JLabel("<html><b>⚠️ Increase protein foods</b><br><small>Add " + String.format("%.0f%%", proteinDiff) + " more to reach CFG guidelines</small></html>"));
        } else if (proteinDiff < -5){
            recommendationsPanel.add(new JLabel("<html><b>⚠️ Reduce protein portions slightly</b><br><small>Currently " + String.format("%.0f%%", -proteinDiff) + " above recommendations</small></html>"));
        } else {
            recommendationsPanel.add(new JLabel("<html><b>✓ Protein intake is good</b></html>"));
        }
        recommendationsPanel.add(Box.createVerticalStrut(10));
        double grainDiff = 25.0 - userPlateData.getOrDefault("Grain Products", 0.0);
        if (grainDiff > 5) {
            recommendationsPanel.add(new JLabel("<html><b>⚠️ Increase whole grains</b><br><small>Add " + String.format("%.0f%%", grainDiff) + " more to reach CFG guidelines</small></html>"));
        } else if (grainDiff < -5){
            recommendationsPanel.add(new JLabel("<html><b>⚠️ Reduce grain portions slightly</b><br><small>Currently " + String.format("%.0f%%", -grainDiff) + " above recommendations</small></html>"));
        } else {
            recommendationsPanel.add(new JLabel("<html><b>✓ Whole grains intake is good</b></html>"));
        }
        rightColumn.add(recommendationsPanel, BorderLayout.SOUTH);


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

    private JPanel createPlaceholderPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel(title + " - Content Coming Soon");
        label.setFont(FONT_TITLE);
        label.setForeground(COLOR_TEXT_DARK);
        panel.add(label);
        return panel;
    }

    private JPanel createFoodSwapsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Food Swap Goals");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setBackground(COLOR_PRIMARY);
        titleLabel.setOpaque(true);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(Color.WHITE);

        // Goals selection panel
        JPanel goalsPanel = createGoalsSelectionPanel();
        contentPanel.add(goalsPanel, BorderLayout.NORTH);

        // Results panel (initially empty)
        swapResultsPanel = new JPanel(new BorderLayout());
        swapResultsPanel.setBackground(Color.WHITE);
        contentPanel.add(swapResultsPanel, BorderLayout.CENTER);

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createGoalsSelectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        JLabel titleLabel = new JLabel("Select Your Goals (Max 2)");
        titleLabel.setFont(FONT_SUBTITLE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        panel.add(titleLabel, gbc);

        // --- Goal 1 ---
        gbc.gridwidth = 1; gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Goal 1:"), gbc);
        
        goal1Combo = new JComboBox<>(new String[]{
            "Increase Fiber", "Reduce Calories", "Increase Protein", 
            "Reduce Fat", "Reduce Carbs", "Increase Carbs"
        });
        goal1Combo.setFont(FONT_NORMAL);
        gbc.gridx = 1;
        panel.add(goal1Combo, gbc);

        // --- Intensity 1 ---
        gbc.gridx = 2;
        panel.add(new JLabel("Intensity:"), gbc);
        
        intensityCombo = new JComboBox<>();
        intensityCombo.setFont(FONT_NORMAL);
        gbc.gridx = 3;
        panel.add(intensityCombo, gbc);

        // --- Goal 2 (Optional) ---
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Goal 2 (Optional):"), gbc);
        
        goal2Combo = new JComboBox<>(new String[]{
            "None", "Increase Fiber", "Reduce Calories", "Increase Protein", 
            "Reduce Fat", "Reduce Carbs", "Increase Carbs"
        });
        goal2Combo.setFont(FONT_NORMAL);
        gbc.gridx = 1;
        panel.add(goal2Combo, gbc);

        // --- Intensity 2 ---
        gbc.gridx = 2;
        JLabel intensity2Label = new JLabel("Intensity 2:");
        panel.add(intensity2Label, gbc);
        
        intensity2Combo = new JComboBox<>();
        intensity2Combo.setFont(FONT_NORMAL);
        gbc.gridx = 3;
        panel.add(intensity2Combo, gbc);

        // --- Action Listeners for Dynamic UI ---
        goal1Combo.addActionListener(e -> updateIntensityOptions(goal1Combo, intensityCombo));
        goal2Combo.addActionListener(e -> {
            boolean isGoalSelected = !"None".equals(goal2Combo.getSelectedItem());
            intensity2Label.setVisible(isGoalSelected);
            intensity2Combo.setVisible(isGoalSelected);
            if (isGoalSelected) {
                updateIntensityOptions(goal2Combo, intensity2Combo);
            }
        });

        // --- Initial UI State ---
        updateIntensityOptions(goal1Combo, intensityCombo);
        intensity2Label.setVisible(false);
        intensity2Combo.setVisible(false);

        // --- Date selection ---
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Date for Swaps:"), gbc);
        
        swapDateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 10);
        swapDateField.setFont(FONT_NORMAL);
        gbc.gridx = 1;
        panel.add(swapDateField, gbc);

        // --- Find Swaps button ---
        JButton findSwapsButton = new JButton("Find Swaps");
        styleButton(findSwapsButton);
        findSwapsButton.addActionListener(this::findSwapsAction);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(findSwapsButton, gbc);

        return panel;
    }

    private void updateIntensityOptions(JComboBox<String> goalCombo, JComboBox<String> intensityCombo) {
        String selectedGoal = (String) goalCombo.getSelectedItem();
        intensityCombo.removeAllItems();

        if (selectedGoal == null) return;

        if (selectedGoal.startsWith("Increase")) {
            intensityCombo.addItem("Slightly more");
            intensityCombo.addItem("Moderately more");
            intensityCombo.addItem("Significantly more");
        } else if (selectedGoal.startsWith("Reduce")) {
            intensityCombo.addItem("Slightly less");
            intensityCombo.addItem("Moderately less");
            intensityCombo.addItem("Significantly less");
        }
    }

    private void findSwapsAction(ActionEvent e) {
        findAndDisplaySwaps(false); // false means it's a new search, not a "try different"
    }

    private void findAndDisplaySwaps(boolean findDifferent) {
        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(swapDateField.getText());
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get meal items from the selected date
        List<models.MealItem> mealItems = getMealItemsForDate(date);
        
        if (mealItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No meals found for the selected date. Please log some meals first.", 
                "No Meals", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create goals from UI selections
        List<FoodSwapGoal> goals = createGoalsFromUI();
        
        if (goals.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please select at least one goal.", 
                "No Goals Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Find swaps, excluding current ones if requested
        List<FoodSwapRecommendation> exclusions = findDifferent ? currentSwaps : Collections.emptyList();
        List<FoodSwapRecommendation> newSwaps = swapEngine.findFoodSwaps(mealItems, goals, exclusions);
        
        if (newSwaps.isEmpty()) {
            if (findDifferent) {
                JOptionPane.showMessageDialog(this, "No alternative swaps could be found.", "No More Swaps", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No suitable swaps found for the selected goals.", "No Swaps Found", JOptionPane.INFORMATION_MESSAGE);
            }
            return;
        }
        
        currentSwaps = newSwaps;
        
        // Display results
        displaySwapResults();
    }

    private List<models.MealItem> getMealItemsForDate(Date date) {
        List<models.MealItem> allItems = new ArrayList<>();
        // Get meals for the selected date for the current user
        List<models.Meal> meals = mealDAO.getMealsForUserAndDate(currentUser.getUserId(), date);
        
        for (models.Meal meal : meals) {
            List<models.MealItem> mealItems = mealDAO.getMealItemsByMealId(meal.getMealId());
            allItems.addAll(mealItems);
        }
        
        return allItems;
    }

    private List<FoodSwapGoal> createGoalsFromUI() {
        List<FoodSwapGoal> goals = new ArrayList<>();
        
        // Goal 1
        String goal1 = (String) goal1Combo.getSelectedItem();
        String intensity = (String) intensityCombo.getSelectedItem();
        
        FoodSwapGoal.NutrientType nutrientType1 = parseNutrientType(goal1);
        FoodSwapGoal.IntensityLevel intensityLevel = parseIntensityLevel(intensity);
        
        if (nutrientType1 != null && intensityLevel != null) {
            goals.add(new FoodSwapGoal(nutrientType1, intensityLevel));
        }
        
        // Goal 2 (if not "None")
        String goal2 = (String) goal2Combo.getSelectedItem();
        if (!"None".equals(goal2)) {
            FoodSwapGoal.NutrientType nutrientType2 = parseNutrientType(goal2);
            FoodSwapGoal.IntensityLevel intensityLevel2 = parseIntensityLevel((String) intensity2Combo.getSelectedItem());
            if (nutrientType2 != null && intensityLevel2 != null) {
                goals.add(new FoodSwapGoal(nutrientType2, intensityLevel2));
            }
        }
        
        return goals;
    }

    private FoodSwapGoal.NutrientType parseNutrientType(String display) {
        return switch (display) {
            case "Increase Fiber" -> FoodSwapGoal.NutrientType.INCREASE_FIBER;
            case "Reduce Calories" -> FoodSwapGoal.NutrientType.REDUCE_CALORIES;
            case "Increase Protein" -> FoodSwapGoal.NutrientType.INCREASE_PROTEIN;
            case "Reduce Fat" -> FoodSwapGoal.NutrientType.REDUCE_FAT;
            case "Reduce Carbs" -> FoodSwapGoal.NutrientType.REDUCE_CARBS;
            case "Increase Carbs" -> FoodSwapGoal.NutrientType.INCREASE_CARBS;
            default -> null;
        };
    }

    private FoodSwapGoal.IntensityLevel parseIntensityLevel(String display) {
        return switch (display) {
            case "Slightly more", "Slightly less" -> FoodSwapGoal.IntensityLevel.SLIGHTLY_MORE;
            case "Moderately more", "Moderately less" -> FoodSwapGoal.IntensityLevel.MODERATELY_MORE;
            case "Significantly more", "Significantly less" -> FoodSwapGoal.IntensityLevel.SIGNIFICANTLY_MORE;
            default -> null;
        };
    }

    private void displaySwapResults() {
        swapResultsPanel.removeAll();
        
        if (currentSwaps.isEmpty()) {
            JLabel noSwapsLabel = new JLabel("No suitable swaps found. Try different goals or log more meals.");
            noSwapsLabel.setFont(FONT_NORMAL);
            noSwapsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            swapResultsPanel.add(noSwapsLabel, BorderLayout.CENTER);
        } else {
            // Create suggested swaps panel
            JPanel suggestedPanel = createSuggestedSwapsPanel();
            swapResultsPanel.add(suggestedPanel, BorderLayout.CENTER);
        }
        
        swapResultsPanel.revalidate();
        swapResultsPanel.repaint();
    }

    private JPanel createSuggestedSwapsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Suggested Swaps");
        titleLabel.setFont(FONT_SUBTITLE);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Swaps list
        JPanel swapsListPanel = new JPanel();
        swapsListPanel.setLayout(new BoxLayout(swapsListPanel, BoxLayout.Y_AXIS));
        swapsListPanel.setBackground(Color.WHITE);

        for (FoodSwapRecommendation swap : currentSwaps) {
            JPanel swapPanel = createSwapItemPanel(swap);
            swapsListPanel.add(swapPanel);
            swapsListPanel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scrollPane = new JScrollPane(swapsListPanel);
        scrollPane.setPreferredSize(new Dimension(600, 200));
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton applyButton = new JButton("Apply Swaps");
        styleButton(applyButton);
        applyButton.addActionListener(this::applySwapsAction);
        buttonPanel.add(applyButton);
        
        JButton viewComparisonButton = new JButton("View Comparison");
        styleButton(viewComparisonButton);
        viewComparisonButton.addActionListener(this::viewComparisonAction);
        buttonPanel.add(viewComparisonButton);
        
        JButton viewHistoryButton = new JButton("View Swap History");
        styleButton(viewHistoryButton);
        viewHistoryButton.addActionListener(this::viewSwapHistoryAction);
        buttonPanel.add(viewHistoryButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSwapItemPanel(FoodSwapRecommendation swap) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Swap description
        String swapText = String.format("Replace in %s: %s → %s", 
            getSwapContext(swap),
            swap.getOriginalFood().getFoodDescription(),
            swap.getRecommendedFood().getFoodDescription());
        
        JLabel swapLabel = new JLabel(swapText);
        swapLabel.setFont(FONT_NORMAL);
        panel.add(swapLabel, BorderLayout.NORTH);

        // Nutritional impact
        String impactText = String.format("Cal: %s, Prot: %s, Carb: %s, Fat: %s, Fib: %s",
            swap.getCalorieChangeDisplay(),
            swap.getProteinChangeDisplay(),
            swap.getCarbsChangeDisplay(),
            swap.getFatsChangeDisplay(),
            swap.getFiberChangeDisplay());
        
        JLabel impactLabel = new JLabel(impactText);
        impactLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        impactLabel.setForeground(Color.GRAY);
        panel.add(impactLabel, BorderLayout.SOUTH);

        return panel;
    }

    private String getSwapContext(FoodSwapRecommendation swap) {
        // This could be enhanced to show meal type (breakfast, lunch, etc.)
        // For now, just return a generic context
        return "meal";
    }

    private void applySwapsAction(ActionEvent e) {
        if (currentSwaps.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No swaps to apply.", 
                "No Swaps", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Show options for applying swaps
        String[] options = {"Current Meals Only", "Apply to Date Range", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this,
            "How would you like to apply " + currentSwaps.size() + " food swaps?",
            "Apply Swaps Options",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        if (choice == 0) { // Current Meals Only
            applySwapsToCurrentMeals();
        } else if (choice == 1) { // Apply to Date Range
            showDateRangeDialog();
        }
        // Choice 2 is Cancel, do nothing
    }
    
    private void applySwapsToCurrentMeals() {
        Date swapDate;
        try {
            swapDate = new SimpleDateFormat("yyyy-MM-dd").parse(swapDateField.getText());
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean success = swapApplicationService.applySwapsToCurrentMeal(currentSwaps, currentUser.getUserId(), swapDate);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Swaps applied successfully to the selected date's meals!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh all application charts to show updated data
                refreshAllTabs();
                
                // Clear current swaps since they've been applied
                currentSwaps.clear();
                displaySwapResults();
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
    
    private void showDateRangeDialog() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Start Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        JTextField startDateField = new JTextField(10);
        startDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        panel.add(startDateField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("End Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        JTextField endDateField = new JTextField(10);
        endDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        panel.add(endDateField, gbc);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Select Date Range for Retroactive Swaps", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate = sdf.parse(startDateField.getText());
                Date endDate = sdf.parse(endDateField.getText());
                
                applySwapsToDateRange(startDate, endDate);
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid date format. Please use YYYY-MM-DD.",
                    "Date Format Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void applySwapsToDateRange(Date startDate, Date endDate) {
        try {
            boolean success = swapApplicationService.applySwapsToDateRange(
                currentSwaps, currentUser.getUserId(), startDate, endDate);
            
            if (success) {
                SwapApplicationService.SwapEffectSummary summary = 
                    swapApplicationService.calculateSwapEffects(currentUser.getUserId(), startDate, endDate);
                
                String message = String.format(
                    "Swaps applied successfully to meals from %s to %s\n\n" +
                    "Total effect over this period:\n" +
                    "Calories: %+.0f\n" +
                    "Protein: %+.1fg\n" +
                    "Fiber: %+.1fg\n" +
                    "Fat: %+.1fg\n" +
                    "Carbs: %+.1fg",
                    new SimpleDateFormat("yyyy-MM-dd").format(startDate),
                    new SimpleDateFormat("yyyy-MM-dd").format(endDate),
                    summary.calorieChange,
                    summary.proteinChange,
                    summary.fiberChange,
                    summary.fatChange,
                    summary.carbChange
                );
                
                JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh all application charts to show updated data
                refreshAllTabs();
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
    
    public void refreshDashboard() {
        Date selectedDate;
        try {
            selectedDate = new SimpleDateFormat("yyyy-MM-dd").parse(dashboardDateField.getText());
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<models.Meal> mealsForDate = mealDAO.getMealsForUserAndDate(currentUser.getUserId(), new java.sql.Date(selectedDate.getTime()));
        updateDashboardPanels(mealsForDate);
    }

    public void refreshAllTabs() {
        // Refresh the main dashboard tab
        refreshDashboard();

        // Re-create and replace the other tabs to ensure they have the latest data
        if (tabbedPane != null && tabbedPane.getTabCount() > 4) {
            tabbedPane.setComponentAt(3, createNutritionAnalysisPanel());
            tabbedPane.setComponentAt(4, createCanadaFoodGuidePanel());
        }
    }

    private void updateDashboardPanels(List<models.Meal> todaysMeals) {
        // Refresh the left column (Today's Summary and Recent Meals)
        JPanel newLeftColumn = createLeftColumn(todaysMeals);
        
        // Replace the old left column
        Container parent = leftColumnPanel.getParent();
        parent.remove(leftColumnPanel);
        leftColumnPanel = newLeftColumn;
        parent.add(leftColumnPanel, 0); // Add at the same position
        
        // Refresh the nutrition chart
        ChartPanel newChartPanel = createPieChartPlaceholder(todaysMeals);
        Container chartParent = nutritionChartPanel.getParent();
        chartParent.remove(nutritionChartPanel);
        nutritionChartPanel = newChartPanel;
        chartParent.add(nutritionChartPanel, 1); // Add at the chart position
        
        // Refresh the UI
        revalidate();
        repaint();
    }

    private void viewComparisonAction(ActionEvent e) {
        Date swapDate;
        try {
            swapDate = new SimpleDateFormat("yyyy-MM-dd").parse(swapDateField.getText());
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Create and show the before/after comparison dialog, passing a callback
        // to the "try different" functionality.
        BeforeAfterComparisonDialog dialog = new BeforeAfterComparisonDialog(
            this, 
            currentSwaps, 
            currentUser.getUserId(), 
            swapDate,
            () -> findAndDisplaySwaps(true) // This is the callback
        );
        dialog.setVisible(true);
    }
    
    private void viewSwapHistoryAction(ActionEvent e) {
        // Create and show the swap history dialog
        SwapHistoryDialog dialog = new SwapHistoryDialog(this, currentUser.getUserId());
        dialog.setVisible(true);
    }

    private void updateTotalCalories() {
        double totalCalories = 0;
        for (int i = 0; i < mealTableModel.getRowCount(); i++) {
            totalCalories += (double) mealTableModel.getValueAt(i, 3); // Calories are in the 4th column (index 3)
        }
        totalCaloriesLabel.setText(String.format("Total Calories: %.0f", totalCalories));
    }
}