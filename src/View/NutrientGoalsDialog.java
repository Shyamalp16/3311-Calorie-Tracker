package View;

import models.User;
import models.UserSettings;
import utils.UnitHelper;
import logic.facade.INutritionFacade;
import controller.GoalsController; // Keep this for GoalsForDisplay

import javax.swing.*;
import java.awt.*;

public class NutrientGoalsDialog extends JDialog {

    private User currentUser;
    private INutritionFacade nutritionFacade;
    private UserSettings userSettings;
    private Runnable onGoalsSavedCallback;

    private JTextField caloriesField;
    private JTextField proteinField;
    private JTextField carbsField;
    private JTextField fatsField;
    private JTextField fiberField;

    public NutrientGoalsDialog(Frame owner, User user, INutritionFacade nutritionFacade) {
        this(owner, user, nutritionFacade, null);
    }

    public NutrientGoalsDialog(Frame owner, User user, INutritionFacade nutritionFacade, Runnable onGoalsSavedCallback) {
        super(owner, "Set Nutrient Goals", true);
        this.currentUser = user;
        this.nutritionFacade = nutritionFacade;
        this.userSettings = nutritionFacade.getUserSettings(); // Get settings from facade
        this.onGoalsSavedCallback = onGoalsSavedCallback;

        initUI();
        loadExistingGoals();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        String weightUnit = UnitHelper.getFoodWeightUnit(userSettings); // Corrected call
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Calories (kcal):"), gbc);
        gbc.gridy++;
        formPanel.add(new JLabel("Protein (" + weightUnit + "):"), gbc);
        gbc.gridy++;
        formPanel.add(new JLabel("Carbohydrates (" + weightUnit + "):"), gbc);
        gbc.gridy++;
        formPanel.add(new JLabel("Fats (" + weightUnit + "):"), gbc);
        gbc.gridy++;
        formPanel.add(new JLabel("Fiber (" + weightUnit + "):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        caloriesField = new JTextField(10);
        formPanel.add(caloriesField, gbc);
        gbc.gridy++;
        proteinField = new JTextField(10);
        formPanel.add(proteinField, gbc);
        gbc.gridy++;
        carbsField = new JTextField(10);
        formPanel.add(carbsField, gbc);
        gbc.gridy++;
        fatsField = new JTextField(10);
        formPanel.add(fatsField, gbc);
        gbc.gridy++;
        fiberField = new JTextField(10);
        formPanel.add(fiberField, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveGoals());
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> setVisible(false));
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadExistingGoals() {
        GoalsController.GoalsForDisplay goals = nutritionFacade.getUserGoals();
        caloriesField.setText(String.valueOf(goals.calories));
        proteinField.setText(String.format("%.1f", goals.protein));
        carbsField.setText(String.format("%.1f", goals.carbs));
        fatsField.setText(String.format("%.1f", goals.fats));
        fiberField.setText(String.format("%.1f", goals.fiber));
    }

    private void saveGoals() {
        try {
            double calories = Double.parseDouble(caloriesField.getText());
            double protein = Double.parseDouble(proteinField.getText());
            double carbs = Double.parseDouble(carbsField.getText());
            double fats = Double.parseDouble(fatsField.getText());
            double fiber = Double.parseDouble(fiberField.getText());

            GoalsController.GoalsForDisplay goalsToSave = new GoalsController.GoalsForDisplay(calories, protein, carbs, fats, fiber);
            nutritionFacade.saveGoals(goalsToSave);
            
            JOptionPane.showMessageDialog(this, "Goals saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            setVisible(false);
            
            if (onGoalsSavedCallback != null) {
                onGoalsSavedCallback.run();
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
