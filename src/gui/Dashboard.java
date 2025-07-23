package gui;

import models.User;
import org.jfree.chart.ChartFactory;
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
import java.util.ArrayList;
import java.sql.Timestamp;
import javax.swing.table.DefaultTableModel;

public class Dashboard extends JFrame {

    private User currentUser;
    private final Color COLOR_TAB_INACTIVE = new Color(224, 224, 224);
    private final Color COLOR_TEXT_INACTIVE = new Color(51, 51, 51);
    private DefaultTableModel mealTableModel;
    private JTextField searchFoodField;
    private JSpinner quantitySpinner;
    private JPopupMenu searchResultsPopup;
    private JList<String> searchResultsList;
    private List<models.Food> currentSearchResults;
    private Database.FoodDAO foodDAO;
    private Database.MealDAO mealDAO;
    private JLabel totalCaloriesLabel;

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
        setTitle("Main Application - Dashboard");
        setSize(1200, 800); // Increased size for better layout
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(COLOR_BACKGROUND);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_NORMAL);

        // Add tabs
        tabbedPane.addTab("Dashboard", createDashboardPanel());
        tabbedPane.addTab("Log Meal", createLogMealPanel());
        tabbedPane.addTab("Food Swaps", createPlaceholderPanel("Food Swaps"));
        tabbedPane.addTab("Nutrition Analysis", createNutritionAnalysisPanel());
        tabbedPane.addTab("Canada Food Guide", createCanadaFoodGuidePanel());

        // Add change listener for dynamic tab highlighting
        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateTabStyles(tabbedPane);
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

        // Screen Title
        JLabel titleLabel = new JLabel("Main Application - Dashboard");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(COLOR_TEXT_LIGHT);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(COLOR_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        dashboardPanel.add(titleLabel, BorderLayout.NORTH);

        // Main content with two columns
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        contentPanel.add(createLeftColumn());
        contentPanel.add(createRightColumn());

        dashboardPanel.add(contentPanel, BorderLayout.CENTER);

        return dashboardPanel;
    }

    private JPanel createLeftColumn() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);

        // Today's Summary
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Today's Summary"));
        summaryPanel.add(new JLabel("Total Calories: 1,850 / 2,000"));
        summaryPanel.add(new JLabel("Protein: 85g"));
        summaryPanel.add(new JLabel("Carbs: 220g"));
        summaryPanel.add(new JLabel("Fat: 65g"));
        leftPanel.add(summaryPanel);

        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Recent Meals
        JPanel recentMealsPanel = new JPanel();
        recentMealsPanel.setLayout(new BoxLayout(recentMealsPanel, BoxLayout.Y_AXIS));
        recentMealsPanel.setBackground(Color.WHITE);
        recentMealsPanel.setBorder(BorderFactory.createTitledBorder("Recent Meals"));

        recentMealsPanel.add(createMealItem("Breakfast: Oatmeal with berries - 350 cal"));
        recentMealsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        recentMealsPanel.add(createMealItem("Lunch: Chicken salad - 450 cal"));
        recentMealsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        recentMealsPanel.add(createMealItem("Dinner: Pasta with vegetables - 650 cal"));
        leftPanel.add(recentMealsPanel);

        return leftPanel;
    }

    private JPanel createMealItem(String description) {
        JPanel mealPanel = new JPanel(new BorderLayout(10, 0));
        mealPanel.setBackground(COLOR_PANEL_BACKGROUND);
        mealPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(FONT_NORMAL);
        mealPanel.add(descriptionLabel, BorderLayout.CENTER);

        JButton detailsButton = new JButton("View Nutrition Details");
        styleButton(detailsButton);
        mealPanel.add(detailsButton, BorderLayout.EAST);

        return mealPanel;
    }

    private JPanel createRightColumn() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);

        // Daily Nutrition Chart
        rightPanel.add(new JLabel("Daily Nutrition Chart"));
        rightPanel.add(createPieChartPlaceholder());

        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Food Guide Adherence
        rightPanel.add(new JLabel("Food Guide Adherence"));
        rightPanel.add(createChartPlaceholder("[Bar Chart: CFG Compliance]"));

        return rightPanel;
    }

    private ChartPanel createPieChartPlaceholder() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Protein 20%", 20);
        dataset.setValue("Carbs 55%", 55);
        dataset.setValue("Fat 25%", 25);

        JFreeChart pieChart = ChartFactory.createPieChart(
                null, dataset, false, true, false);

        pieChart.setBackgroundPaint(Color.WHITE);
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setLabelGenerator(null);
        plot.setSectionPaint("Protein 20%", new Color(255, 105, 97)); // Red
        plot.setSectionPaint("Carbs 55%", new Color(97, 168, 255));  // Blue
        plot.setSectionPaint("Fat 25%", new Color(255, 214, 97));   // Yellow

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
        JTextField dateField = new JTextField("2025-07-24", 10);
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
        String[] columnNames = {"Food Item", "Quantity", "Unit", "Calories", "FoodObject"}; // Added hidden column for Food object
        mealTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells uneditable
            }
        };
        JTable table = new JTable(mealTableModel);
        foodDAO = new Database.FoodDAO();
        mealDAO = new Database.MealDAO();
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
        searchFoodField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchTerm = searchFoodField.getText().trim();
                if (searchTerm.length() > 2) { // Only search if at least 3 characters are typed
                    currentSearchResults = foodDAO.searchFoodByName(searchTerm);
                    DefaultListModel<String> listModel = new DefaultListModel<>();
                    for (models.Food food : currentSearchResults) {
                        listModel.addElement(food.getFoodDescription());
                    }
                    searchResultsList.setModel(listModel);
                    if (!currentSearchResults.isEmpty()) {
                        searchResultsPopup.show(searchFoodField, 0, searchFoodField.getHeight());
                    } else {
                        searchResultsPopup.setVisible(false);
                    }
                } else {
                    searchResultsPopup.setVisible(false);
                }
            }
        });
        addFoodPanel.add(searchFoodField);

        searchResultsPopup = new JPopupMenu();
        searchResultsList = new JList<>();
        searchResultsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                searchFoodField.setText(searchResultsList.getSelectedValue());
                searchResultsPopup.setVisible(false);
            }
        });
        searchResultsPopup.add(new JScrollPane(searchResultsList));
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        quantitySpinner.setPreferredSize(new Dimension(80, 25)); // Adjust size as needed
        addFoodPanel.add(quantitySpinner);
        JButton addItemButton = new JButton("Add Item");
        addItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = searchResultsList.getSelectedIndex();
                if (selectedIndex != -1 && currentSearchResults != null && selectedIndex < currentSearchResults.size()) {
                    models.Food selectedFood = currentSearchResults.get(selectedIndex);
                    int quantity = (int) quantitySpinner.getValue();
                    double calories = selectedFood.getCalories() * quantity;
                    mealTableModel.addRow(new Object[]{selectedFood.getFoodDescription(), quantity, "g", calories, selectedFood}); // Store the Food object
                    updateTotalCalories();
                    searchFoodField.setText("Search food items...");
                    quantitySpinner.setValue(1);
                    searchResultsPopup.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(Dashboard.this, "Please select a food item to add.", "No Food Selected", JOptionPane.WARNING_MESSAGE);
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
                    updateTotalCalories();
                } else {
                    JOptionPane.showMessageDialog(Dashboard.this, "Please select a row to remove.", "No Row Selected", JOptionPane.WARNING_MESSAGE);
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

                    double totalCalories = 0;
                    double totalProtein = 0;
                    double totalCarbs = 0;
                    double totalFats = 0;
                    double totalFiber = 0;

                    for (int i = 0; i < mealTableModel.getRowCount(); i++) {
                        models.Food foodFromTable = (models.Food) mealTableModel.getValueAt(i, 4);
                        int quantity = (int) mealTableModel.getValueAt(i, 1);
                        totalCalories += foodFromTable.getCalories() * quantity;
                        totalProtein += foodFromTable.getProtein() * quantity;
                        totalCarbs += foodFromTable.getCarbs() * quantity;
                        totalFats += foodFromTable.getFats() * quantity;
                        totalFiber += foodFromTable.getFiber() * quantity;
                    }

                    models.Meal meal = new models.Meal(0, currentUser.getUserId(), mealType, mealDate, new Timestamp(System.currentTimeMillis()), totalCalories, totalProtein, totalCarbs, totalFats, totalFiber);
                    int mealId = mealDAO.saveMeal(meal);

                    if (mealId != -1) {
                        List<models.MealItem> mealItems = new ArrayList<>();
                        for (int i = 0; i < mealTableModel.getRowCount(); i++) {
                            String foodDescription = (String) mealTableModel.getValueAt(i, 0);
                            int quantity = (int) mealTableModel.getValueAt(i, 1);
                            models.Food foodFromTable = (models.Food) mealTableModel.getValueAt(i, 4); // Retrieve the stored Food object
                            int foodId = foodFromTable.getFoodID();
                            mealItems.add(new models.MealItem(0, mealId, foodId, quantity, "g", foodFromTable.getCalories(), foodFromTable.getProtein(), foodFromTable.getCarbs(), foodFromTable.getFats(), foodFromTable.getFiber()));
                        }
                        mealDAO.saveMealItems(mealId, mealItems);
                        JOptionPane.showMessageDialog(Dashboard.this, "Meal saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        mealTableModel.setRowCount(0); // Clear the table
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
        JButton detailsButton = new JButton("View Nutrition Details");
        styleButton(detailsButton);
        actionsPanel.add(detailsButton);

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
        topPanel.add(new JComboBox<>(new String[]{"Last 7 days", "Last 30 days", "Last 3 months"}));
        panel.add(topPanel, BorderLayout.NORTH);

        // Main content with two columns
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        // Left Column
        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.setBackground(Color.WHITE);
        leftColumn.add(new JLabel("Daily Nutrient Breakdown"));
        leftColumn.add(createChartPlaceholder("[Pie Chart]"));
        leftColumn.add(Box.createVerticalStrut(20));
        leftColumn.add(new JLabel("Average Daily Intake"));
        leftColumn.add(new JLabel("Calories: 1,850 / 2,000 recommended"));
        leftColumn.add(new JLabel("<html>Protein: 85g / 75g recommended <font color='green'>✓</font></html>"));
        leftColumn.add(new JLabel("<html>Fiber: 18g / 25g recommended <font color='orange'>⚠️</font></html>"));
        leftColumn.add(new JLabel("<html>Sodium: 2,100mg / 2,300mg recommended <font color='green'>✓</font></html>"));
        contentPanel.add(leftColumn);

        // Right Column
        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        rightColumn.setBackground(Color.WHITE);
        rightColumn.add(new JLabel("Trends Over Time"));
        rightColumn.add(createChartPlaceholder("[Line Chart]"));
        rightColumn.add(Box.createVerticalStrut(20));
        rightColumn.add(new JLabel("Top 10 Nutrients"));
        rightColumn.add(createChartPlaceholder("[Bar Chart]"));
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
        buttonPanel.add(goalsButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
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
        leftColumn.add(createChartPlaceholder("[Pie Chart]"), BorderLayout.CENTER);
        String[] columnNames = {"Food Group", "Your %", "CFG Recommended"};
        Object[][] data = {
                {"Vegetables & Fruits", "35%", "50%"},
                {"Whole Grains", "30%", "25%"},
                {"Protein Foods", "25%", "25%"},
                {"Dairy", "10%", "Include daily"}
        };
        JTable table = new JTable(data, columnNames);
        leftColumn.add(new JScrollPane(table), BorderLayout.SOUTH);
        contentPanel.add(leftColumn);

        // Right Column
        JPanel rightColumn = new JPanel(new BorderLayout(10, 10));
        rightColumn.setBackground(Color.WHITE);
        rightColumn.add(new JLabel("CFG Recommended Plate"), BorderLayout.NORTH);
        rightColumn.add(createChartPlaceholder("[Pie Chart]"), BorderLayout.CENTER);
        JPanel recommendationsPanel = new JPanel();
        recommendationsPanel.setLayout(new BoxLayout(recommendationsPanel, BoxLayout.Y_AXIS));
        recommendationsPanel.setBackground(Color.WHITE);
        recommendationsPanel.add(new JLabel("<html><b>⚠️ Increase vegetables and fruits</b><br><small>Add 15% more to reach CFG guidelines</small></html>"));
        recommendationsPanel.add(Box.createVerticalStrut(10));
        recommendationsPanel.add(new JLabel("<html><b>✓ Protein intake is good</b><br><small>You're meeting the recommended amount</small></html>"));
        recommendationsPanel.add(Box.createVerticalStrut(10));
        recommendationsPanel.add(new JLabel("<html><b>⚠️ Reduce grain portions slightly</b><br><small>Currently 5% above recommendations</small></html>"));
        rightColumn.add(recommendationsPanel, BorderLayout.SOUTH);
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

    private void updateTotalCalories() {
        double totalCalories = 0;
        for (int i = 0; i < mealTableModel.getRowCount(); i++) {
            totalCalories += (double) mealTableModel.getValueAt(i, 3); // Calories are in the 4th column (index 3)
        }
        totalCaloriesLabel.setText(String.format("Total Calories: %.0f", totalCalories));
    }
}
