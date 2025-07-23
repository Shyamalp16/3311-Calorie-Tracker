package gui;

import logic.FoodSwapEngine;
import models.Food;
import models.User;
import Database.FoodDAO;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class FoodSwapPanel extends JPanel {
    
    private User currentUser;
    private FoodSwapEngine swapEngine;
    private FoodDAO foodDAO;
    
    // UI Components
    private JComboBox<String> goal1Combo;
    private JComboBox<String> goal2Combo;
    private JComboBox<String> intensityCombo;
    private JPanel suggestedSwapsPanel;
    private JButton findSwapsButton;
    private JButton applySwapsButton;
    private JButton viewComparisonButton;
    
    // Styling constants matching Dashboard
    private final Color COLOR_PRIMARY = new Color(76, 175, 80);
    private final Color COLOR_SECONDARY = new Color(33, 150, 243);
    private final Color COLOR_BACKGROUND = new Color(245, 245, 245);
    private final Color COLOR_TEXT_LIGHT = Color.WHITE;
    private final Color COLOR_TEXT_DARK = new Color(51, 51, 51);
    private final Color COLOR_PANEL_BACKGROUND = new Color(245, 245, 245);
    private final Color COLOR_SWAP_HIGHLIGHT = new Color(255, 255, 0, 100); // Yellow highlight
    
    private final Font FONT_TITLE = new Font("Arial", Font.BOLD, 18);
    private final Font FONT_SUBTITLE = new Font("Arial", Font.BOLD, 16);
    private final Font FONT_NORMAL = new Font("Arial", Font.PLAIN, 14);
    private final Font FONT_SMALL = new Font("Arial", Font.PLAIN, 12);
    
    // Nutrition goals available
    private final String[] NUTRITION_GOALS = {
        "None", "Increase Fiber", "Reduce Calories", "Increase Protein", 
        "Reduce Sodium", "Increase Calcium", "Reduce Sugar", "Increase Iron"
    };
    
    private final String[] INTENSITY_LEVELS = {
        "Slightly more", "Moderately more", "Significantly more"
    };
    
    public FoodSwapPanel(User user) {
        this.currentUser = user;
        this.swapEngine = new FoodSwapEngine();
        this.foodDAO = new FoodDAO();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create main components
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createGoalsPanel(), BorderLayout.CENTER);
        add(createSuggestedSwapsPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Food Swap Goals");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(COLOR_TEXT_LIGHT);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        return headerPanel;
    }
    
    private JPanel createGoalsPanel() {
        JPanel goalsPanel = new JPanel(new BorderLayout(10, 15));
        goalsPanel.setBackground(Color.WHITE);
        goalsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Goals selection section
        JPanel selectionPanel = createGoalSelectionPanel();
        goalsPanel.add(selectionPanel, BorderLayout.NORTH);
        
        return goalsPanel;
    }
    
    private JPanel createGoalSelectionPanel() {
        JPanel selectionPanel = new JPanel(new GridBagLayout());
        selectionPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Select Your Goals (Max 2)");
        titleLabel.setFont(FONT_SUBTITLE);
        titleLabel.setForeground(COLOR_TEXT_DARK);
        selectionPanel.add(titleLabel, gbc);
        
        // Goal 1
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        JLabel goal1Label = new JLabel("Goal 1:");
        goal1Label.setFont(FONT_NORMAL);
        selectionPanel.add(goal1Label, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        goal1Combo = new JComboBox<>(NUTRITION_GOALS);
        goal1Combo.setFont(FONT_NORMAL);
        goal1Combo.setSelectedItem("Increase Fiber");
        goal1Combo.setPreferredSize(new Dimension(200, 30));
        selectionPanel.add(goal1Combo, gbc);
        
        // Intensity
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel intensityLabel = new JLabel("Intensity:");
        intensityLabel.setFont(FONT_NORMAL);
        selectionPanel.add(intensityLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        intensityCombo = new JComboBox<>(INTENSITY_LEVELS);
        intensityCombo.setFont(FONT_NORMAL);
        intensityCombo.setSelectedItem("Slightly more");
        intensityCombo.setPreferredSize(new Dimension(200, 30));
        selectionPanel.add(intensityCombo, gbc);
        
        // Goal 2 (Optional)
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel goal2Label = new JLabel("Goal 2 (Optional):");
        goal2Label.setFont(FONT_NORMAL);
        selectionPanel.add(goal2Label, gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        goal2Combo = new JComboBox<>(NUTRITION_GOALS);
        goal2Combo.setFont(FONT_NORMAL);
        goal2Combo.setSelectedItem("None");
        goal2Combo.setPreferredSize(new Dimension(200, 30));
        selectionPanel.add(goal2Combo, gbc);
        
        // Find Swaps Button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        findSwapsButton = new JButton("Find Swaps");
        styleButton(findSwapsButton, COLOR_SECONDARY);
        findSwapsButton.addActionListener(new FindSwapsActionListener());
        selectionPanel.add(findSwapsButton, gbc);
        
        return selectionPanel;
    }
    
    private JPanel createSuggestedSwapsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Suggested Swaps");
        titleLabel.setFont(FONT_SUBTITLE);
        titleLabel.setForeground(COLOR_TEXT_DARK);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Swaps container
        suggestedSwapsPanel = new JPanel();
        suggestedSwapsPanel.setLayout(new BoxLayout(suggestedSwapsPanel, BoxLayout.Y_AXIS));
        suggestedSwapsPanel.setBackground(Color.WHITE);
        
        // Add initial example swaps (as shown in the image)
        addExampleSwaps();
        
        JScrollPane scrollPane = new JScrollPane(suggestedSwapsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        applySwapsButton = new JButton("Apply Swaps");
        styleButton(applySwapsButton, COLOR_SECONDARY);
        applySwapsButton.addActionListener(new ApplySwapsActionListener());
        buttonPanel.add(applySwapsButton);
        
        viewComparisonButton = new JButton("View Comparison");
        styleButton(viewComparisonButton, COLOR_PRIMARY);
        viewComparisonButton.addActionListener(new ViewComparisonActionListener());
        buttonPanel.add(viewComparisonButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private void addExampleSwaps() {
        // Add example swaps as shown in the image
        addSwapItem("Replace in breakfast:", "White bread", "Whole grain bread", "+5g fiber, -20 calories");
        addSwapItem("Replace in snack:", "Regular milk", "Almond milk", "-50 calories, +Vitamin E");
    }
    
    private void addSwapItem(String mealContext, String originalFood, String replacementFood, String benefits) {
        JPanel swapPanel = new JPanel(new BorderLayout(10, 5));
        swapPanel.setBackground(COLOR_PANEL_BACKGROUND);
        swapPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        swapPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        // Main content
        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        contentPanel.setBackground(COLOR_PANEL_BACKGROUND);
        
        JLabel contextLabel = new JLabel(mealContext);
        contextLabel.setFont(FONT_NORMAL);
        contextLabel.setForeground(COLOR_TEXT_DARK);
        contentPanel.add(contextLabel);
        
        JLabel originalLabel = new JLabel(originalFood);
        originalLabel.setFont(FONT_NORMAL);
        originalLabel.setForeground(COLOR_TEXT_DARK);
        contentPanel.add(originalLabel);
        
        JLabel arrowLabel = new JLabel(" → ");
        arrowLabel.setFont(FONT_NORMAL);
        arrowLabel.setForeground(COLOR_TEXT_DARK);
        contentPanel.add(arrowLabel);
        
        JLabel replacementLabel = new JLabel(replacementFood);
        replacementLabel.setFont(FONT_NORMAL);
        replacementLabel.setForeground(COLOR_TEXT_DARK);
        replacementLabel.setOpaque(true);
        replacementLabel.setBackground(COLOR_SWAP_HIGHLIGHT);
        replacementLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        contentPanel.add(replacementLabel);
        
        swapPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Benefits
        JLabel benefitsLabel = new JLabel(benefits);
        benefitsLabel.setFont(FONT_SMALL);
        benefitsLabel.setForeground(new Color(102, 102, 102));
        swapPanel.add(benefitsLabel, BorderLayout.SOUTH);
        
        suggestedSwapsPanel.add(swapPanel);
        suggestedSwapsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }
    
    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(FONT_NORMAL);
        button.setBackground(backgroundColor);
        button.setForeground(COLOR_TEXT_LIGHT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    // Action Listeners
    private class FindSwapsActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String goal1 = (String) goal1Combo.getSelectedItem();
            String goal2 = (String) goal2Combo.getSelectedItem();
            String intensity = (String) intensityCombo.getSelectedItem();
            
            if ("None".equals(goal1)) {
                JOptionPane.showMessageDialog(FoodSwapPanel.this, 
                    "Please select at least one nutrition goal.", 
                    "No Goal Selected", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Clear previous suggestions
            suggestedSwapsPanel.removeAll();
            
            // Find swaps using the engine
            Map<String, List<Food>> swapSuggestions = swapEngine.findFoodSwaps(
                currentUser, goal1, goal2, intensity
            );
            
            if (swapSuggestions.isEmpty()) {
                JLabel noSwapsLabel = new JLabel("No suitable swaps found for your goals.");
                noSwapsLabel.setFont(FONT_NORMAL);
                noSwapsLabel.setForeground(Color.GRAY);
                suggestedSwapsPanel.add(noSwapsLabel);
            } else {
                // Display found swaps
                for (Map.Entry<String, List<Food>> entry : swapSuggestions.entrySet()) {
                    String mealType = entry.getKey();
                    List<Food> swaps = entry.getValue();
                    
                    for (int i = 0; i < swaps.size() - 1; i += 2) {
                        Food original = swaps.get(i);
                        Food replacement = swaps.get(i + 1);
                        String benefits = calculateBenefits(original, replacement, goal1, goal2);
                        
                        addSwapItem("Replace in " + mealType.toLowerCase() + ":", 
                                  original.getFoodDescription(), 
                                  replacement.getFoodDescription(), 
                                  benefits);
                    }
                }
            }
            
            suggestedSwapsPanel.revalidate();
            suggestedSwapsPanel.repaint();
        }
    }
    
    private class ApplySwapsActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Implementation for applying swaps to meal plan
            JOptionPane.showMessageDialog(FoodSwapPanel.this, 
                "Swaps applied to your meal plan!", 
                "Swaps Applied", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private class ViewComparisonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Implementation for viewing nutritional comparison
            showNutritionComparisonDialog();
        }
    }
    
    private String calculateBenefits(Food original, Food replacement, String goal1, String goal2) {
        StringBuilder benefits = new StringBuilder();
        
        // Calculate differences
        double calorieDiff = replacement.getCalories() - original.getCalories();
        double fiberDiff = replacement.getFiber() - original.getFiber();
        double proteinDiff = replacement.getProtein() - original.getProtein();
        
        if (Math.abs(calorieDiff) > 5) {
            benefits.append(String.format("%+.0f calories", calorieDiff));
        }
        
        if (Math.abs(fiberDiff) > 0.5) {
            if (benefits.length() > 0) benefits.append(", ");
            benefits.append(String.format("%+.1fg fiber", fiberDiff));
        }
        
        if (Math.abs(proteinDiff) > 1) {
            if (benefits.length() > 0) benefits.append(", ");
            benefits.append(String.format("%+.1fg protein", proteinDiff));
        }
        
        return benefits.toString();
    }
    
    private void showNutritionComparisonDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                   "Nutrition Comparison", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Nutritional Comparison - Before vs After Swaps");
        titleLabel.setFont(FONT_SUBTITLE);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Create comparison table
        String[] columnNames = {"Nutrient", "Before", "After", "Change"};
        Object[][] data = {
            {"Calories", "1,850", "1,780", "-70"},
            {"Fiber", "18g", "23g", "+5g"},
            {"Protein", "85g", "87g", "+2g"},
            {"Sodium", "2,100mg", "1,950mg", "-150mg"}
        };
        
        JTable table = new JTable(data, columnNames);
        table.setFont(FONT_NORMAL);
        table.setRowHeight(25);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close");
        styleButton(closeButton, COLOR_SECONDARY);
        closeButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
} 