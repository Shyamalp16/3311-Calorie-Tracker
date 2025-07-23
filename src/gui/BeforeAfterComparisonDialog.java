package gui;

import models.FoodSwapRecommendation;
import logic.SwapApplicationService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Date;

public class BeforeAfterComparisonDialog extends JDialog {
    
    private List<FoodSwapRecommendation> swaps;
    private SwapApplicationService swapApplicationService;
    private int userId;
    private final Color COLOR_PRIMARY = new Color(76, 175, 80);
    private final Color COLOR_SECONDARY = new Color(33, 150, 243);
    private final Font FONT_TITLE = new Font("Arial", Font.BOLD, 18);
    private final Font FONT_SUBTITLE = new Font("Arial", Font.BOLD, 16);
    private final Font FONT_NORMAL = new Font("Arial", Font.PLAIN, 14);
    
    public BeforeAfterComparisonDialog(JFrame parent, List<FoodSwapRecommendation> swaps) {
        super(parent, "Before/After Comparison", true);
        this.swaps = swaps;
        this.swapApplicationService = new SwapApplicationService();
        this.userId = 1; // In a real app, get this from the parent or session
        initUI();
    }
    
    private void initUI() {
        setSize(1000, 700);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Before/After Comparison");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setBackground(COLOR_PRIMARY);
        titleLabel.setOpaque(true);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Main comparison panel
        JPanel comparisonPanel = createComparisonPanel();
        mainPanel.add(comparisonPanel, BorderLayout.CENTER);
        
        // Bottom buttons
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createComparisonPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setBackground(Color.WHITE);
        
        // Original Meal Panel
        JPanel originalPanel = createMealPanel("Original Meal", true);
        panel.add(originalPanel);
        
        // Modified Meal Panel
        JPanel modifiedPanel = createMealPanel("Modified Meal", false);
        panel.add(modifiedPanel);
        
        return panel;
    }
    
    private JPanel createMealPanel(String title, boolean isOriginal) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_SUBTITLE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Meal items
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(Color.WHITE);
        
        double totalCalories = 0, totalProtein = 0, totalFiber = 0;
        
        for (FoodSwapRecommendation swap : swaps) {
            JPanel itemPanel = new JPanel(new BorderLayout(5, 5));
            itemPanel.setBackground(Color.WHITE);
            itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            
            String foodName = isOriginal ? swap.getOriginalFood().getFoodDescription() 
                                        : swap.getRecommendedFood().getFoodDescription();
            double calories = isOriginal ? swap.getOriginalFood().getCalories() 
                                        : swap.getRecommendedFood().getCalories();
            double protein = isOriginal ? swap.getOriginalFood().getProtein() 
                                       : swap.getRecommendedFood().getProtein();
            double fiber = isOriginal ? swap.getOriginalFood().getFiber() 
                                     : swap.getRecommendedFood().getFiber();
            
            totalCalories += calories;
            totalProtein += protein;
            totalFiber += fiber;
            
            JLabel nameLabel = new JLabel(String.format("%s: %.0f cal", 
                truncateText(foodName, 30), calories));
            nameLabel.setFont(FONT_NORMAL);
            
            JLabel detailLabel = new JLabel(String.format("%.0fg protein, %.0fg fiber", 
                protein, fiber));
            detailLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            detailLabel.setForeground(Color.GRAY);
            
            itemPanel.add(nameLabel, BorderLayout.NORTH);
            itemPanel.add(detailLabel, BorderLayout.SOUTH);
            
            itemsPanel.add(itemPanel);
        }
        
        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Totals
        JPanel totalsPanel = new JPanel();
        totalsPanel.setLayout(new BoxLayout(totalsPanel, BoxLayout.Y_AXIS));
        totalsPanel.setBackground(Color.WHITE);
        totalsPanel.setBorder(BorderFactory.createTitledBorder("Total"));
        
        totalsPanel.add(new JLabel(String.format("Calories: %.0f", totalCalories)));
        totalsPanel.add(new JLabel(String.format("Protein: %.0fg", totalProtein)));
        totalsPanel.add(new JLabel(String.format("Fiber: %.0fg", totalFiber)));
        
        panel.add(totalsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private String truncateText(String text, int maxLength) {
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
    
    private JPanel createNutrientChangesPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Nutrient Changes"));
        
        // Create table data
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
        double originalCals = 0, originalProtein = 0, originalFiber = 0;
        double modifiedCals = 0, modifiedProtein = 0, modifiedFiber = 0;
        
        for (FoodSwapRecommendation swap : swaps) {
            originalCals += swap.getOriginalFood().getCalories();
            originalProtein += swap.getOriginalFood().getProtein();
            originalFiber += swap.getOriginalFood().getFiber();
            
            modifiedCals += swap.getRecommendedFood().getCalories();
            modifiedProtein += swap.getRecommendedFood().getProtein();
            modifiedFiber += swap.getRecommendedFood().getFiber();
        }
        
        return new Object[][] {
            {"Calories", String.format("%.0f", originalCals), 
             String.format("%.0f", modifiedCals), 
             formatChange(modifiedCals - originalCals, false)},
            {"Protein", String.format("%.1fg", originalProtein), 
             String.format("%.1fg", modifiedProtein), 
             formatChange(modifiedProtein - originalProtein, true)},
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
        
        // Add nutrient changes table
        JPanel changesPanel = createNutrientChangesPanel();
        panel.add(changesPanel, BorderLayout.CENTER);
        
        // Buttons
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
            boolean success = swapApplicationService.applySwapsToCurrentMeal(swaps, userId);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Swaps applied to your current meals!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh parent dashboard if it's a Dashboard
                if (getParent() instanceof gui.Dashboard) {
                    ((gui.Dashboard) getParent()).refreshDashboard();
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
        // Show date range dialog for retroactive application
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
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(new JLabel("<html><i>Note: This will apply swaps to all qualifying meals in the date range</i></html>"), gbc);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Apply Swaps Over Time", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate = sdf.parse(startDateField.getText());
                Date endDate = sdf.parse(endDateField.getText());
                
                boolean success = swapApplicationService.applySwapsToDateRange(swaps, userId, startDate, endDate);
                
                if (success) {
                    SwapApplicationService.SwapEffectSummary summary = 
                        swapApplicationService.calculateSwapEffects(userId, startDate, endDate);
                    
                    String message = String.format(
                        "Swaps applied successfully to meals from %s to %s!\n\n" +
                        "Total cumulative effect:\n" +
                        "Calories: %+.0f\n" +
                        "Protein: %+.1fg\n" +
                        "Fiber: %+.1fg\n" +
                        "Fat: %+.1fg\n" +
                        "Carbs: %+.1fg\n\n" +
                        "Total swaps applied: %d",
                        sdf.format(startDate), sdf.format(endDate),
                        summary.calorieChange,
                        summary.proteinChange,
                        summary.fiberChange,
                        summary.fatChange,
                        summary.carbChange,
                        summary.totalSwaps
                    );
                    
                    JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh parent dashboard if it's a Dashboard
                    if (getParent() instanceof gui.Dashboard) {
                        ((gui.Dashboard) getParent()).refreshDashboard();
                    }
                    
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Some swaps could not be applied. Check console for details.",
                        "Partial Success",
                        JOptionPane.WARNING_MESSAGE);
                }
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid date format. Please use YYYY-MM-DD.",
                    "Date Format Error",
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error applying swaps: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private void tryDifferentSwaps(ActionEvent e) {
        dispose(); // Close this dialog and return to main swap interface
    }
} 