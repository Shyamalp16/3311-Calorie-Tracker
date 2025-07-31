package View;

import Database.UserDAO;
import Database.UserSettingsDAO;
import models.User;
import models.UserSettings;
import models.UnitSystem;
import controller.ProfileController;
import utils.DateValidator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Dialog window for creating new user profiles
 * Contains form fields for all user information
 */
public class ProfileCreation extends JDialog {
    private Main_Application_GUI parentWindow;
    private ProfileController profileController;
    
    private JTextField nameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> genderCombo;
    private JTextField birthDateField;
    private JTextField heightField;
    private JTextField weightField;
    private JComboBox<String> activityCombo;
    private JComboBox<UnitSystem> unitSystemCombo;
    
    private JLabel heightLabel;
    private JLabel weightLabel;
    
    public ProfileCreation(Main_Application_GUI parent) {
        super(parent, "Create New Profile", true); 
        this.parentWindow = parent;
        this.profileController = new ProfileController();
        
        setupDialog();
        createForm();
        pack();
        setLocationRelativeTo(parent); 
        setVisible(true);
    }
    
    /**
     * Sets up dialog window properties
     */
    private void setupDialog() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); 
        setResizable(false);
        getContentPane().setBackground(new Color(45, 45, 45));
    }
    
    /**
     * Creates the form with input fields
     */
    private void createForm() {
        setLayout(new BorderLayout());
        
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(34, 139, 34));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel headerLabel = new JLabel("Create Your Profile");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        
        add(headerPanel, BorderLayout.NORTH);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(45, 45, 45));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5); 
        gbc.anchor = GridBagConstraints.WEST;
        
        addFormRow(formPanel, gbc, 0, "Name:", nameField = new JTextField(20));

        addFormRow(formPanel, gbc, 1, "Username:", usernameField = new JTextField(20));

        addFormRow(formPanel, gbc, 2, "Password:", passwordField = new JPasswordField(20));
        
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        addFormRow(formPanel, gbc, 3, "Gender:", genderCombo);
        
        birthDateField = new JTextField(20);
        JPanel birthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        birthPanel.setBackground(new Color(45, 45, 45));
        birthPanel.add(birthDateField);
        JLabel formatLabel = new JLabel(" (YYYY-MM-DD)");
        formatLabel.setForeground(Color.GRAY);
        formatLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        birthPanel.add(formatLabel);
        addFormRow(formPanel, gbc, 4, "Birth Date:", birthPanel);
        
        unitSystemCombo = new JComboBox<>(UnitSystem.values());
        unitSystemCombo.addActionListener(e -> updateUnitsDisplay());
        addFormRow(formPanel, gbc, 5, "Unit System:", unitSystemCombo);
        
        JPanel heightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        heightPanel.setBackground(new Color(45, 45, 45));
        heightField = new JTextField(10);
        heightPanel.add(heightField);
        heightLabel = new JLabel(" cm");
        heightLabel.setForeground(Color.WHITE);
        heightPanel.add(heightLabel);
        addFormRow(formPanel, gbc, 6, "Height:", heightPanel);
        
        JPanel weightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        weightPanel.setBackground(new Color(45, 45, 45));
        weightField = new JTextField(10);
        weightPanel.add(weightField);
        weightLabel = new JLabel(" kg");
        weightLabel.setForeground(Color.WHITE);
        weightPanel.add(weightLabel);
        addFormRow(formPanel, gbc, 7, "Weight:", weightPanel);
        
        activityCombo = new JComboBox<>(new String[]{
            "Sedentary", "Lightly Active", "Moderately Active", "Very Active", "Extremely Active"
        });
        addFormRow(formPanel, gbc, 8, "Activity Level:", activityCombo);
        
        add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(45, 45, 45));
        
        JButton createButton = new JButton("Create Profile");
        createButton.setPreferredSize(new Dimension(120, 35));
        createButton.setBackground(new Color(34, 139, 34));
        createButton.setForeground(Color.WHITE);
        createButton.setFocusPainted(false);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.setBackground(new Color(128, 128, 128));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createUserProfile();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); 
                parentWindow.setVisible(true);
            }
        });
        
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Helper method to add a row to the form (label + input field)
     */
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, Component inputComponent) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(inputComponent, gbc);
    }
    
    /**
     * Updates unit labels when unit system changes
     */
    private void updateUnitsDisplay() {
        UnitSystem selectedSystem = (UnitSystem) unitSystemCombo.getSelectedItem();
        if (selectedSystem != null) {
            heightLabel.setText(" " + selectedSystem.getHeightUnit());
            weightLabel.setText(" " + selectedSystem.getWeightUnit());
            
            convertExistingValues(selectedSystem);
        }
    }
    
    /**
     * Convert existing field values when unit system changes
     */
    private void convertExistingValues(UnitSystem newSystem) {
        try {
            String heightText = heightField.getText().trim();
            String weightText = weightField.getText().trim();
            
            if (!heightText.isEmpty()) {
                double currentHeight = Double.parseDouble(heightText);
                double convertedHeight = UnitSystem.convertHeight(currentHeight, UnitSystem.METRIC, newSystem);
                heightField.setText(String.format("%.1f", convertedHeight));
            }
            
            if (!weightText.isEmpty()) {
                double currentWeight = Double.parseDouble(weightText);
                double convertedWeight = UnitSystem.convertWeight(currentWeight, UnitSystem.METRIC, newSystem);
                weightField.setText(String.format("%.1f", convertedWeight));
            }
            
        } catch (NumberFormatException e) {
        }
    }
    
    /**
     * Validates form input and creates new user profile
     */
    private void createUserProfile() {
        try {
            String name = nameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String gender = (String) genderCombo.getSelectedItem();
            String birthDateStr = birthDateField.getText().trim();
            String heightStr = heightField.getText().trim();
            String weightStr = weightField.getText().trim();
            String activityLevel = (String) activityCombo.getSelectedItem();
            UnitSystem selectedUnitSystem = (UnitSystem) unitSystemCombo.getSelectedItem();

            if (name.isEmpty() || username.isEmpty() || password.isEmpty() || birthDateStr.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty()) {
                showError("All fields are required.");
                return;
            }

            // Validate birth date format and reasonableness
            if (!isValidBirthDate(birthDateStr)) {
                return; // Error message shown in isValidBirthDate method
            }

            double height = Double.parseDouble(heightStr);
            double weight = Double.parseDouble(weightStr);

            User savedUser = profileController.createUser(name, username, password, gender, birthDateStr, height, weight, activityLevel, selectedUnitSystem);

            if (savedUser != null) {
                JOptionPane.showMessageDialog(this,
                    "Profile created successfully with " + selectedUnitSystem.getDisplayName() + " units!");
                dispose();
                new Dashboard(savedUser);
            } else {
                showError("Failed to create profile. Please try again.");
            }
        } catch (NumberFormatException e) {
            showError("Please enter valid numbers for height and weight.");
        } catch (Exception e) {
            showError("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Validates birth date format and reasonableness
     */
    private boolean isValidBirthDate(String birthDateStr) {
        DateValidator.ValidationResult result = DateValidator.validateBirthDate(birthDateStr);
        if (!result.isValid()) {
            showError(result.getErrorMessage());
        }
        return result.isValid();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }
}