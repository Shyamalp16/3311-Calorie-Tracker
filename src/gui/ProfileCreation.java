package gui;

import Database.UserDAO;
import models.User;

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
    private SplashScreen parentWindow;
    private UserDAO userDAO;
    
    // Form input fields
    private JTextField nameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> genderCombo;
    private JTextField birthDateField;
    private JTextField heightField;
    private JTextField weightField;
    private JComboBox<String> activityCombo;
    
    public ProfileCreation(SplashScreen parent) {
        super(parent, "Create New Profile", true); // Modal dialog
        this.parentWindow = parent;
        this.userDAO = new UserDAO();
        
        setupDialog();
        createForm();
        pack(); // Size window to fit components
        setLocationRelativeTo(parent); // Center on parent window
        setVisible(true);
    }
    
    /**
     * Sets up dialog window properties
     */
    private void setupDialog() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Close only this dialog
        setResizable(false);
        getContentPane().setBackground(new Color(45, 45, 45));
    }
    
    /**
     * Creates the form with input fields
     */
    private void createForm() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(34, 139, 34));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel headerLabel = new JLabel("Create Your Profile");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Form fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(45, 45, 45));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5); // Padding around components
        gbc.anchor = GridBagConstraints.WEST; // Align left
        
        // Name field
        addFormRow(formPanel, gbc, 0, "Name:", nameField = new JTextField(20));

        // Username field
        addFormRow(formPanel, gbc, 1, "Username:", usernameField = new JTextField(20));

        // Password field
        addFormRow(formPanel, gbc, 2, "Password:", passwordField = new JPasswordField(20));
        
        // Gender dropdown
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        addFormRow(formPanel, gbc, 3, "Gender:", genderCombo);
        
        // Birth date field with format hint
        birthDateField = new JTextField(20);
        JPanel birthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        birthPanel.setBackground(new Color(45, 45, 45));
        birthPanel.add(birthDateField);
        JLabel formatLabel = new JLabel(" (YYYY-MM-DD)");
        formatLabel.setForeground(Color.GRAY);
        formatLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        birthPanel.add(formatLabel);
        addFormRow(formPanel, gbc, 4, "Birth Date:", birthPanel);
        
        // Height field with unit
        JPanel heightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        heightPanel.setBackground(new Color(45, 45, 45));
        heightField = new JTextField(10);
        heightPanel.add(heightField);
        JLabel heightUnit = new JLabel(" cm");
        heightUnit.setForeground(Color.WHITE);
        heightPanel.add(heightUnit);
        addFormRow(formPanel, gbc, 5, "Height:", heightPanel);
        
        // Weight field with unit
        JPanel weightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        weightPanel.setBackground(new Color(45, 45, 45));
        weightField = new JTextField(10);
        weightPanel.add(weightField);
        JLabel weightUnit = new JLabel(" kg");
        weightUnit.setForeground(Color.WHITE);
        weightPanel.add(weightUnit);
        addFormRow(formPanel, gbc, 6, "Weight:", weightPanel);
        
        // Activity level dropdown
        activityCombo = new JComboBox<>(new String[]{
            "Sedentary", "Lightly Active", "Moderately Active", "Very Active", "Extremely Active"
        });
        addFormRow(formPanel, gbc, 7, "Activity Level:", activityCombo);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Buttons
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
        
        // What happens when Create is clicked
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createUserProfile();
            }
        });
        
        // What happens when Cancel is clicked
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close dialog
                parentWindow.setVisible(true); // Show splash screen again
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
        // Label column
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(label, gbc);
        
        // Input column
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(inputComponent, gbc);
    }
    
    /**
     * Validates form input and creates new user profile
     */
    private void createUserProfile() {
        try {
            // Get values from form fields
            String name = nameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String gender = (String) genderCombo.getSelectedItem();
            String birthDateStr = birthDateField.getText().trim();
            String heightStr = heightField.getText().trim();
            String weightStr = weightField.getText().trim();
            String activityLevel = (String) activityCombo.getSelectedItem();
            
            // Validate required fields
            if (name.isEmpty()) {
                showError("Name is required.");
                return;
            }

            if (username.isEmpty()) {
                showError("Username is required.");
                return;
            }

            if (password.isEmpty()) {
                showError("Password is required.");
                return;
            }
            
            if (birthDateStr.isEmpty()) {
                showError("Birth date is required.");
                return;
            }
            
            if (heightStr.isEmpty()) {
                showError("Height is required.");
                return;
            }
            
            if (weightStr.isEmpty()) {
                showError("Weight is required.");
                return;
            }
            
            // Parse and validate birth date
            Date birthDate;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false); // Strict date parsing
                java.util.Date parsedDate = sdf.parse(birthDateStr);
                birthDate = new Date(parsedDate.getTime()); // Convert to SQL Date
            } catch (ParseException e) {
                showError("Invalid date format. Please use YYYY-MM-DD (e.g., 1995-03-15).");
                return;
            }
            
            // Parse and validate height
            double height;
            try {
                height = Double.parseDouble(heightStr);
                if (height <= 0 || height > 300) { // Reasonable height range
                    showError("Please enter a valid height between 1 and 300 cm.");
                    return;
                }
            } catch (NumberFormatException e) {
                showError("Please enter a valid number for height.");
                return;
            }
            
            // Parse and validate weight
            double weight;
            try {
                weight = Double.parseDouble(weightStr);
                if (weight <= 0 || weight > 500) { // Reasonable weight range
                    showError("Please enter a valid weight between 1 and 500 kg.");
                    return;
                }
            } catch (NumberFormatException e) {
                showError("Please enter a valid number for weight.");
                return;
            }
            
            // Create User object with validated data
            User newUser = new User(name, username, password, gender, birthDate, height, weight, activityLevel);
            
            // Save to database
            User savedUser = userDAO.createUser(newUser);
            
            if (savedUser != null) {
                // Success! Show confirmation and close dialog
                JOptionPane.showMessageDialog(this,
                    "Profile created successfully!");
                dispose();
                new Dashboard(savedUser);
            }
        } catch (Exception e) {
            showError("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }
}