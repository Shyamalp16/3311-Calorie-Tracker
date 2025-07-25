package gui;

import models.Goal;
import models.User;
import Database.GoalDAO;

import javax.swing.*;
import java.awt.*;

public class NutrientGoalsDialog extends JDialog {

    private User currentUser;
    private GoalDAO goalDAO;

    private JTextField caloriesField;
    private JTextField proteinField;
    private JTextField carbsField;
    private JTextField fatsField;
    private JTextField fiberField;

    public NutrientGoalsDialog(Frame owner, User user) {
        super(owner, "Set Nutrient Goals", true);
        this.currentUser = user;
        this.goalDAO = new GoalDAO();
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

        // Labels
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Calories (kcal):"), gbc);
        gbc.gridy++;
        formPanel.add(new JLabel("Protein (g):"), gbc);
        gbc.gridy++;
        formPanel.add(new JLabel("Carbohydrates (g):"), gbc);
        gbc.gridy++;
        formPanel.add(new JLabel("Fats (g):"), gbc);
        gbc.gridy++;
        formPanel.add(new JLabel("Fiber (g):"), gbc);

        // Fields
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

        // Buttons
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
        Goal existingGoal = goalDAO.getGoalByUserId(currentUser.getUserId()).orElse(null);
        if (existingGoal != null) {
            caloriesField.setText(String.valueOf(existingGoal.getCalories()));
            proteinField.setText(String.valueOf(existingGoal.getProtein()));
            carbsField.setText(String.valueOf(existingGoal.getCarbs()));
            fatsField.setText(String.valueOf(existingGoal.getFats()));
            fiberField.setText(String.valueOf(existingGoal.getFiber()));
        }
    }

    private void saveGoals() {
        try {
            double calories = Double.parseDouble(caloriesField.getText());
            double protein = Double.parseDouble(proteinField.getText());
            double carbs = Double.parseDouble(carbsField.getText());
            double fats = Double.parseDouble(fatsField.getText());
            double fiber = Double.parseDouble(fiberField.getText());

            Goal goal = new Goal(currentUser.getUserId(), calories, protein, carbs, fats, fiber);
            goalDAO.saveGoal(goal);

            JOptionPane.showMessageDialog(this, "Goals saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            setVisible(false);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
