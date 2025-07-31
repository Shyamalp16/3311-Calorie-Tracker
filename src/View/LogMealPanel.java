package View;

import logic.facade.INutritionFacade;
import models.Food;
import models.MealItem;
import utils.DateValidator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class LogMealPanel extends JPanel {

    private final Font FONT_TITLE = new Font("Arial", Font.BOLD, 18);
    private final Font FONT_NORMAL = new Font("Arial", Font.PLAIN, 14);
    private final Color COLOR_SECONDARY = new Color(33, 150, 243);
    private final Color COLOR_TEXT_LIGHT = Color.WHITE;
    private final Color COLOR_TEXT_DARK = new Color(51, 51, 51);

    private INutritionFacade nutritionFacade;
    private DefaultTableModel mealTableModel;
    private JTextField searchFoodField;
    private JSpinner quantitySpinner;
    private JComboBox<String> unitComboBox;
    private JPopupMenu searchResultsPopup;
    private JList<String> searchResultsList;
    private List<Food> currentSearchResults;
    private List<MealItem> loggedMealItems;
    private JLabel totalCaloriesLabel;
    private Runnable onMealSavedCallback;

    public LogMealPanel(INutritionFacade nutritionFacade, Runnable onMealSavedCallback) {
        this.nutritionFacade = nutritionFacade;
        this.onMealSavedCallback = onMealSavedCallback;
        this.currentSearchResults = new ArrayList<>();
        this.loggedMealItems = new ArrayList<>();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Log Meal");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(COLOR_TEXT_DARK);
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(new JLabel("Date:"));
        JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 10);
        formPanel.add(dateField);

        formPanel.add(Box.createHorizontalStrut(20));

        formPanel.add(new JLabel("Meal Type:"));
        JComboBox<String> mealTypeCombo = new JComboBox<>(new String[]{"Breakfast", "Lunch", "Dinner", "Snack"});
        formPanel.add(mealTypeCombo);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(formPanel, BorderLayout.NORTH);

        String[] columnNames = {"Food Item", "Quantity", "Unit", "Calories"};
        mealTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(mealTableModel);
        table.setFont(FONT_NORMAL);
        table.setRowHeight(20);
        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout(10, 10));
        southPanel.setBackground(Color.WHITE);

        JPanel addFoodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addFoodPanel.setBackground(Color.WHITE);
        addFoodPanel.add(new JLabel("Add Food:"));
        searchFoodField = new JTextField("Search food items...", 20);

        setupSearchFunctionality();

        addFoodPanel.add(searchFoodField);

        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        quantitySpinner.setPreferredSize(new Dimension(80, 25));
        addFoodPanel.add(quantitySpinner);

        unitComboBox = new JComboBox<>();
        unitComboBox.setPreferredSize(new Dimension(100, 25));
        addFoodPanel.add(unitComboBox);

        JButton addItemButton = new JButton("Add Item");
        addItemButton.addActionListener(e -> addMealItem());
        styleButton(addItemButton);
        addFoodPanel.add(addItemButton);

        JButton removeItemButton = new JButton("Remove Selected");
        removeItemButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                mealTableModel.removeRow(selectedRow);
                loggedMealItems.remove(selectedRow);
                updateTotalCalories();
            }
        });
        styleButton(removeItemButton);
        addFoodPanel.add(removeItemButton);

        southPanel.add(addFoodPanel, BorderLayout.NORTH);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        actionsPanel.setBackground(Color.WHITE);
        totalCaloriesLabel = new JLabel("Total Calories: 0");
        actionsPanel.add(totalCaloriesLabel);
        JButton saveButton = new JButton("Save Meal");
        saveButton.addActionListener(e -> {
            DateValidator.ValidationResult dateResult = DateValidator.validateDate(dateField.getText());
            if (!dateResult.isValid()) {
                JOptionPane.showMessageDialog(this, dateResult.getErrorMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String mealType = (String) mealTypeCombo.getSelectedItem();
            Date mealDate = dateResult.getParsedDate();
            controller.MealController.SaveMealResult result = nutritionFacade.saveMeal(mealType, mealDate, loggedMealItems);

            if (result.success) {
                JOptionPane.showMessageDialog(this, result.message, "Success", JOptionPane.INFORMATION_MESSAGE);
                mealTableModel.setRowCount(0);
                loggedMealItems.clear();
                updateTotalCalories();
                if (onMealSavedCallback != null) {
                    onMealSavedCallback.run();
                }
            } else {
                JOptionPane.showMessageDialog(this, result.message, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        styleButton(saveButton);
        actionsPanel.add(saveButton);

        southPanel.add(actionsPanel, BorderLayout.SOUTH);

        contentPanel.add(southPanel, BorderLayout.SOUTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void setupSearchFunctionality() {
        searchResultsPopup = new JPopupMenu();
        searchResultsList = new JList<>();
        searchResultsPopup.add(new JScrollPane(searchResultsList));

        final boolean[] isAdjusting = {false};

        searchFoodField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }

            public void update() {
                if (isAdjusting[0]) return;
                SwingUtilities.invokeLater(() -> {
                    String searchTerm = searchFoodField.getText().trim();
                    if (searchTerm.length() > 2) {
                        currentSearchResults = nutritionFacade.searchFoods(searchTerm);
                        DefaultListModel<String> listModel = new DefaultListModel<>();
                        currentSearchResults.forEach(food -> listModel.addElement(food.getFoodDescription()));
                        searchResultsList.setModel(listModel);
                        if (!listModel.isEmpty()) {
                            if (!searchResultsPopup.isVisible()) {
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
                        e.consume();
                        break;
                    case KeyEvent.VK_UP:
                        searchResultsList.setSelectedIndex((selectedIndex - 1 + listSize) % listSize);
                        e.consume();
                        break;
                    case KeyEvent.VK_ENTER:
                        if (selectedIndex != -1) {
                            isAdjusting[0] = true;
                            searchFoodField.setText(searchResultsList.getSelectedValue());
                            isAdjusting[0] = false;
                            searchResultsPopup.setVisible(false);
                            updateUnitComboBoxForSelectedFood();
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

        searchResultsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 1 && searchResultsList.getSelectedIndex() != -1) {
                    isAdjusting[0] = true;
                    searchFoodField.setText(searchResultsList.getSelectedValue());
                    isAdjusting[0] = false;
                    searchResultsPopup.setVisible(false);
                    updateUnitComboBoxForSelectedFood();
                }
            }
        });
    }

    private void updateUnitComboBoxForSelectedFood() {
        int selectedIndex = searchResultsList.getSelectedIndex();
        if (selectedIndex != -1) {
            Food selectedFood = currentSearchResults.get(selectedIndex);
            Map<String, Integer> measures = nutritionFacade.getFoodMeasures(selectedFood.getFoodID());
            unitComboBox.removeAllItems();
            if (measures.isEmpty()) {
                unitComboBox.addItem("g");
            } else {
                measures.keySet().forEach(unitComboBox::addItem);
            }
        }
    }

    private void addMealItem() {
        int selectedIndex = searchResultsList.getSelectedIndex();
        if (selectedIndex != -1 && currentSearchResults != null && selectedIndex < currentSearchResults.size()) {
            Food selectedFoodSummary = currentSearchResults.get(selectedIndex);
            Food selectedFood = nutritionFacade.getFoodById(selectedFoodSummary.getFoodID());
            double quantity = ((Number) quantitySpinner.getValue()).doubleValue();
            String selectedUnit = (String) unitComboBox.getSelectedItem();

            if (selectedFood != null) {
                MealItem mealItem = nutritionFacade.calculateMealItem(selectedFood, quantity, selectedUnit);
                loggedMealItems.add(mealItem);
                mealTableModel.addRow(new Object[]{selectedFood.getFoodDescription(), quantity, selectedUnit, mealItem.getCalories()});
                updateTotalCalories();
                searchFoodField.setText("Search food items...");
                quantitySpinner.setValue(1);
                searchResultsPopup.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Could not retrieve full details for the selected food.", "Data Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateTotalCalories() {
        double total = loggedMealItems.stream().mapToDouble(MealItem::getCalories).sum();
        totalCaloriesLabel.setText(String.format("Total Calories: %.0f", total));
    }

    private void styleButton(JButton button) {
        button.setFont(FONT_NORMAL);
        button.setBackground(COLOR_SECONDARY);
        button.setForeground(COLOR_TEXT_LIGHT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }
}
