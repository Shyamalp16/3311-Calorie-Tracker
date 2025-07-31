package View;

import logic.facade.INutritionFacade;
import models.FoodSwapGoal;
import models.FoodSwapRecommendation;
import models.UserSettings;
import utils.UnitHelper;
import utils.DateValidator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class FoodSwapsPanel extends JPanel {

    private final Font FONT_TITLE = new Font("Arial", Font.BOLD, 18);
    private final Font FONT_SUBTITLE = new Font("Arial", Font.BOLD, 16);
    private final Font FONT_NORMAL = new Font("Arial", Font.PLAIN, 14);
    private final Color COLOR_PRIMARY = new Color(76, 175, 80);
    private final Color COLOR_SECONDARY = new Color(33, 150, 243);
    private final Color COLOR_TEXT_LIGHT = Color.WHITE;

    private INutritionFacade nutritionFacade;
    private UserSettings currentSettings;
    private Frame parentFrame;
    private int currentUserId;

    private JComboBox<String> goal1Combo;
    private JComboBox<String> intensityCombo;
    private JComboBox<String> goal2Combo;
    private JComboBox<String> intensity2Combo;
    private JPanel swapResultsPanel;
    private List<FoodSwapRecommendation> currentSwaps;
    private JTextField swapDateField;

    public FoodSwapsPanel(Frame parent, int userId, INutritionFacade nutritionFacade, UserSettings settings) {
        this.parentFrame = parent;
        this.currentUserId = userId;
        this.nutritionFacade = nutritionFacade;
        this.currentSettings = settings;
        this.currentSwaps = new ArrayList<>();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Food Swap Goals");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setBackground(COLOR_PRIMARY);
        titleLabel.setOpaque(true);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(Color.WHITE);

        JPanel goalsPanel = createGoalsSelectionPanel();
        contentPanel.add(goalsPanel, BorderLayout.NORTH);

        swapResultsPanel = new JPanel(new BorderLayout());
        swapResultsPanel.setBackground(Color.WHITE);
        contentPanel.add(swapResultsPanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createGoalsSelectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel("Select Your Goals (Max 2)");
        titleLabel.setFont(FONT_SUBTITLE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        panel.add(titleLabel, gbc);

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

        gbc.gridx = 2;
        panel.add(new JLabel("Intensity:"), gbc);
        
        intensityCombo = new JComboBox<>();
        intensityCombo.setFont(FONT_NORMAL);
        gbc.gridx = 3;
        panel.add(intensityCombo, gbc);

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

        gbc.gridx = 2;
        JLabel intensity2Label = new JLabel("Intensity 2:");
        panel.add(intensity2Label, gbc);
        
        intensity2Combo = new JComboBox<>();
        intensity2Combo.setFont(FONT_NORMAL);
        gbc.gridx = 3;
        panel.add(intensity2Combo, gbc);

        goal1Combo.addActionListener(e -> updateIntensityOptions(goal1Combo, intensityCombo));
        goal2Combo.addActionListener(e -> {
            boolean isGoalSelected = !"None".equals(goal2Combo.getSelectedItem());
            intensity2Label.setVisible(isGoalSelected);
            intensity2Combo.setVisible(isGoalSelected);
            if (isGoalSelected) {
                updateIntensityOptions(goal2Combo, intensity2Combo);
            }
        });

        updateIntensityOptions(goal1Combo, intensityCombo);
        intensity2Label.setVisible(false);
        intensity2Combo.setVisible(false);

        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Date for Swaps:"), gbc);
        
        swapDateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 10);
        swapDateField.setFont(FONT_NORMAL);
        gbc.gridx = 1;
        panel.add(swapDateField, gbc);

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
        findAndDisplaySwaps(false); 
    }

    private void findAndDisplaySwaps(boolean findDifferent) {
        DateValidator.ValidationResult dateResult = validateSwapDate();
        if (!dateResult.isValid()) {
            return; // Error message already shown
        }
        Date date = dateResult.getParsedDate();

        List<FoodSwapGoal> goals = createGoalsFromUI();
        
        if (goals.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please select at least one goal.", 
                "No Goals Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<FoodSwapRecommendation> exclusions = findDifferent ? currentSwaps : Collections.emptyList();
        List<FoodSwapRecommendation> newSwaps = nutritionFacade.findFoodSwaps(date, goals, exclusions);
        
        if (newSwaps.isEmpty()) {
            if (findDifferent) {
                JOptionPane.showMessageDialog(this, "No alternative swaps could be found.", "No More Swaps", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No suitable swaps found for the selected goals.", "No Swaps Found", JOptionPane.INFORMATION_MESSAGE);
            }
            return;
        }
        
        currentSwaps = newSwaps;
        
        displaySwapResults();
    }

    private List<FoodSwapGoal> createGoalsFromUI() {
        List<FoodSwapGoal> goals = new ArrayList<>();
        
        String goal1 = (String) goal1Combo.getSelectedItem();
        String intensity = (String) intensityCombo.getSelectedItem();
        
        FoodSwapGoal.NutrientType nutrientType1 = parseNutrientType(goal1);
        FoodSwapGoal.IntensityLevel intensityLevel = parseIntensityLevel(intensity);
        
        if (nutrientType1 != null && intensityLevel != null) {
            goals.add(new FoodSwapGoal(nutrientType1, intensityLevel));
        }
        
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
            JPanel suggestedPanel = createSuggestedSwapsPanel();
            swapResultsPanel.add(suggestedPanel, BorderLayout.CENTER);
        }
        
        swapResultsPanel.revalidate();
        swapResultsPanel.repaint();
    }

    private JPanel createSuggestedSwapsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Suggested Swaps");
        titleLabel.setFont(FONT_SUBTITLE);
        panel.add(titleLabel, BorderLayout.NORTH);

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

        // Only keep View Swap History button as a global action
        JPanel globalButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        globalButtonPanel.setBackground(Color.WHITE);
        
        JButton viewHistoryButton = new JButton("View Swap History");
        styleButton(viewHistoryButton);
        viewHistoryButton.addActionListener(this::viewSwapHistoryAction);
        globalButtonPanel.add(viewHistoryButton);
        
        panel.add(globalButtonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSwapItemPanel(FoodSwapRecommendation swap) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        String swapText = String.format("Replace in %s: %s → %s", 
            swap.getMealType(),
            swap.getOriginalFood().getFoodDescription(),
            swap.getRecommendedFood().getFoodDescription());
        
        JLabel swapLabel = new JLabel(swapText);
        swapLabel.setFont(FONT_NORMAL);
        panel.add(swapLabel, BorderLayout.NORTH);

        String unitSymbol = UnitHelper.getFoodWeightUnit(currentSettings);
        String impactText = String.format("Cal: %+.0f, Prot: %+.1f%s, Carb: %+.1f%s, Fat: %+.1f%s, Fib: %+.1f%s",
            swap.getCalorieChange(),
            UnitHelper.convertFoodWeightForDisplay(swap.getProteinChange(), currentSettings), unitSymbol,
            UnitHelper.convertFoodWeightForDisplay(swap.getCarbsChange(), currentSettings), unitSymbol,
            UnitHelper.convertFoodWeightForDisplay(swap.getFatsChange(), currentSettings), unitSymbol,
            UnitHelper.convertFoodWeightForDisplay(swap.getFiberChange(), currentSettings), unitSymbol);
        
        JLabel impactLabel = new JLabel(impactText);
        impactLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        impactLabel.setForeground(Color.GRAY);
        panel.add(impactLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        // Apply This Swap button
        JButton applyButton = new JButton("Apply");
        applyButton.setFont(new Font("Arial", Font.PLAIN, 11));
        applyButton.setBackground(COLOR_PRIMARY);
        applyButton.setForeground(COLOR_TEXT_LIGHT);
        applyButton.setFocusPainted(false);
        applyButton.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        applyButton.addActionListener(e -> applyIndividualSwap(swap));
        buttonPanel.add(applyButton);
        
        // Apply Over Time button
        JButton applyOverTimeButton = new JButton("Apply Over Time");
        applyOverTimeButton.setFont(new Font("Arial", Font.PLAIN, 11));
        applyOverTimeButton.setBackground(COLOR_SECONDARY);
        applyOverTimeButton.setForeground(COLOR_TEXT_LIGHT);
        applyOverTimeButton.setFocusPainted(false);
        applyOverTimeButton.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        applyOverTimeButton.addActionListener(e -> applyIndividualSwapOverTime(swap));
        buttonPanel.add(applyOverTimeButton);
        
        // View Comparison button
        JButton viewComparisonButton = new JButton("View Comparison");
        viewComparisonButton.setFont(new Font("Arial", Font.PLAIN, 11));
        viewComparisonButton.setBackground(new Color(156, 39, 176));
        viewComparisonButton.setForeground(COLOR_TEXT_LIGHT);
        viewComparisonButton.setFocusPainted(false);
        viewComparisonButton.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        viewComparisonButton.addActionListener(e -> viewIndividualComparison(swap));
        buttonPanel.add(viewComparisonButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void applyIndividualSwap(FoodSwapRecommendation swap) {
        DateValidator.ValidationResult dateResult = validateSwapDate();
        if (!dateResult.isValid()) {
            return; // Error message already shown
        }
        Date swapDate = dateResult.getParsedDate();

        List<FoodSwapRecommendation> singleSwap = Collections.singletonList(swap);
        boolean success = nutritionFacade.applyFoodSwaps(swapDate, singleSwap);
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                "Swap applied successfully!\n" + 
                swap.getOriginalFood().getFoodDescription() + " → " + 
                swap.getRecommendedFood().getFoodDescription(), 
                "Swap Applied", JOptionPane.INFORMATION_MESSAGE);
            
            currentSwaps.remove(swap);
            displaySwapResults();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to apply swap. Please try again.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyIndividualSwapOverTime(FoodSwapRecommendation swap) {
        DateValidator.ValidationResult dateResult = validateSwapDate();
        if (!dateResult.isValid()) {
            return; // Error message already shown
        }
        Date swapDate = dateResult.getParsedDate();

        // Show dialog to choose date range or apply to all
        ApplyOverTimeDialog dialog = new ApplyOverTimeDialog((JFrame)SwingUtilities.getWindowAncestor(this), swapDate);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Date startDate = dialog.getStartDate();
            Date endDate = dialog.getEndDate();
            boolean isApplyToAll = dialog.isApplyToAll();
            
            // Show confirmation dialog
            String confirmMessage;
            if (isApplyToAll) {
                confirmMessage = String.format("This will apply the swap (%s → %s) to ALL meals in your entire history where this food exists.\n" +
                               "Are you sure you want to continue?",
                               swap.getOriginalFood().getFoodDescription(),
                               swap.getRecommendedFood().getFoodDescription());
            } else {
                confirmMessage = String.format("This will apply the swap (%s → %s) to all meals between %s and %s where this food exists.\n" +
                               "Are you sure you want to continue?", 
                               swap.getOriginalFood().getFoodDescription(),
                               swap.getRecommendedFood().getFoodDescription(),
                               new SimpleDateFormat("yyyy-MM-dd").format(startDate),
                               new SimpleDateFormat("yyyy-MM-dd").format(endDate));
            }
            
            int result = JOptionPane.showConfirmDialog(this,
                confirmMessage,
                "Apply Swap Over Time",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (result != JOptionPane.YES_OPTION) {
                return;
            }

            List<FoodSwapRecommendation> singleSwap = Collections.singletonList(swap);
            boolean success = nutritionFacade.applyFoodSwapsToDateRange(startDate, endDate, singleSwap);
            
            if (success) {
                String successMessage = isApplyToAll ? 
                    "Swap applied to all meals successfully!" :
                    "Swap applied to meals in date range successfully!";
                    
                JOptionPane.showMessageDialog(this, 
                    successMessage + "\n" + swap.getOriginalFood().getFoodDescription() + " → " + swap.getRecommendedFood().getFoodDescription(), 
                    "Swap Applied", JOptionPane.INFORMATION_MESSAGE);
                
                currentSwaps.remove(swap);
                displaySwapResults();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to apply swap over time. Please try again.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewIndividualComparison(FoodSwapRecommendation swap) {
        DateValidator.ValidationResult dateResult = validateSwapDate();
        if (!dateResult.isValid()) {
            return; // Error message already shown
        }
        Date swapDate = dateResult.getParsedDate();

        List<FoodSwapRecommendation> singleSwap = Collections.singletonList(swap);
        BeforeAfterComparisonDialog dialog = new BeforeAfterComparisonDialog(
            (JFrame)parentFrame, 
            singleSwap, 
            currentUserId, 
            swapDate,
            () -> findAndDisplaySwaps(true) 
        );
        dialog.setVisible(true);
    }

    
    private void viewSwapHistoryAction(ActionEvent e) {
        SwapHistoryDialog dialog = new SwapHistoryDialog((JFrame)parentFrame, currentUserId);
        dialog.setVisible(true);
    }

    private DateValidator.ValidationResult validateSwapDate() {
        DateValidator.ValidationResult result = DateValidator.validateDate(swapDateField.getText());
        if (!result.isValid()) {
            JOptionPane.showMessageDialog(this, result.getErrorMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
        return result;
    }

    private void styleButton(JButton button) {
        button.setFont(FONT_NORMAL);
        button.setBackground(COLOR_SECONDARY);
        button.setForeground(COLOR_TEXT_LIGHT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }
}
