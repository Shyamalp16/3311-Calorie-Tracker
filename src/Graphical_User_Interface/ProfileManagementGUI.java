package Graphical_User_Interface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import DatabaseConnector.DatabaseConnector;
import User_Profile_Management.ProfileManager;
import User_Profile_Management.UserProfile;

public class ProfileManagementGUI extends JFrame implements ActionListener {
    private ProfileManager profileManager;
    private int currentUserId;
    
    // Form components
    private JTextField nameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> genderCombo;
    private JTextField birthDateField;
    private JTextField heightField;
    private JTextField weightField;
    private JComboBox<String> activityLevelCombo;
    private JButton createButton;
    private JButton updateButton;
    private JButton viewButton;
    private JTextArea resultArea;
    private JTextField userIdField;

    public ProfileManagementGUI(ProfileManager profileManager, int userId) {
        this.profileManager = profileManager;
        this.currentUserId = userId;
        initializeUI();
        loadCurrentUserProfile();
    }

    private void loadCurrentUserProfile() {
        if (currentUserId > 0) {
            UserProfile profile = profileManager.getProfile(currentUserId);
            if (profile != null) {
                userIdField.setText(String.valueOf(profile.getUserId()));
                nameField.setText(profile.getName());
                genderCombo.setSelectedItem(capitalize(profile.getGender()));
                birthDateField.setText(profile.getBirthDate().toString());
                heightField.setText(String.valueOf(profile.getHeight()));
                weightField.setText(String.valueOf(profile.getWeight()));
                activityLevelCombo.setSelectedItem(capitalize(profile.getActivityLevel()));
            }
        }
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private void initializeUI() {
        setTitle("NutriSci Profile Management");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(0, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Profile Information"));

        // User ID field
        formPanel.add(new JLabel("User ID (for update/view):"));
        userIdField = new JTextField();
        formPanel.add(userIdField);

        // Name field
        formPanel.add(new JLabel("Full Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        // Password fields (only for create/update)
        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        formPanel.add(new JLabel("Confirm Password:"));
        confirmPasswordField = new JPasswordField();
        formPanel.add(confirmPasswordField);

        // Gender combo box
        formPanel.add(new JLabel("Gender:"));
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        formPanel.add(genderCombo);

        // Birth date field
        formPanel.add(new JLabel("Birth Date (YYYY-MM-DD):"));
        birthDateField = new JTextField();
        formPanel.add(birthDateField);

        // Height field
        formPanel.add(new JLabel("Height (cm):"));
        heightField = new JTextField();
        formPanel.add(heightField);

        // Weight field
        formPanel.add(new JLabel("Weight (kg):"));
        weightField = new JTextField();
        formPanel.add(weightField);

        // Activity level combo box
        formPanel.add(new JLabel("Activity Level:"));
        activityLevelCombo = new JComboBox<>(new String[]{
            "Sedentary", 
            "Lightly Active", 
            "Moderately Active", 
            "Very Active", 
            "Extra Active"
        });
        formPanel.add(activityLevelCombo);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        createButton = new JButton("Create Profile");
        createButton.addActionListener(this);
        buttonPanel.add(createButton);

        updateButton = new JButton("Update Profile");
        updateButton.addActionListener(this);
        buttonPanel.add(updateButton);

        viewButton = new JButton("View Profile");
        viewButton.addActionListener(this);
        buttonPanel.add(viewButton);

        // Result area
        resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Add components to main panel
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == createButton) {
            createProfile();
        } else if (e.getSource() == updateButton) {
            updateProfile();
        } else if (e.getSource() == viewButton) {
            viewProfile();
        }
    }

    private void createProfile() {
        try {
            char[] passwordChars = passwordField.getPassword();
            char[] confirmChars = confirmPasswordField.getPassword();
            
            try {
                if (passwordChars.length == 0 || confirmChars.length == 0) {
                    resultArea.setText("Error: Password fields cannot be empty");
                    return;
                }
                
                if (!Arrays.equals(passwordChars, confirmChars)) {
                    resultArea.setText("Error: Passwords do not match");
                    return;
                }
                
                String password = new String(passwordChars);
                if (password.length() < 8) {
                    resultArea.setText("Error: Password must be at least 8 characters");
                    return;
                }

                UserProfile profile = createProfileFromInputs();
                UserProfile createdProfile = profileManager.createProfile(profile, password);
                
                if (createdProfile != null) {
                    resultArea.setText("Profile created successfully!\n\n" + profileToString(createdProfile));
                    currentUserId = createdProfile.getUserId();
                    userIdField.setText(String.valueOf(currentUserId));
                } else {
                    resultArea.setText("Failed to create profile.");
                }
            } finally {
                Arrays.fill(passwordChars, '\0');
                Arrays.fill(confirmChars, '\0');
            }
        } catch (IllegalArgumentException | DateTimeParseException ex) {
            resultArea.setText("Error: " + ex.getMessage());
        }
    }

    private void updateProfile() {
        try {
            int userId = Integer.parseInt(userIdField.getText().trim());
            UserProfile existingProfile = profileManager.getProfile(userId);
            
            if (existingProfile == null) {
                resultArea.setText("No profile found with ID: " + userId);
                return;
            }
            
            char[] passwordChars = passwordField.getPassword();
            try {
                String password = passwordChars.length > 0 ? new String(passwordChars) : null;
                
                // Update the existing profile with new values
                UserProfile updatedProfile = createProfileFromInputs();
                updatedProfile.setUserId(userId);
                
                boolean success = profileManager.updateProfile(updatedProfile, password);
                
                if (success) {
                    resultArea.setText("Profile updated successfully!\n\n" + profileToString(updatedProfile));
                } else {
                    resultArea.setText("Failed to update profile.");
                }
            } finally {
                Arrays.fill(passwordChars, '\0');
            }
        } catch (NumberFormatException ex) {
            resultArea.setText("Invalid User ID. Please enter a number.");
        } catch (IllegalArgumentException | DateTimeParseException ex) {
            resultArea.setText("Error: " + ex.getMessage());
        }
    }

    private void viewProfile() {
        try {
            int userId = Integer.parseInt(userIdField.getText().trim());
            UserProfile profile = profileManager.getProfile(userId);
            
            if (profile != null) {
                resultArea.setText(profileToString(profile));
                // Load the profile into form fields
                nameField.setText(profile.getName());
                genderCombo.setSelectedItem(capitalize(profile.getGender()));
                birthDateField.setText(profile.getBirthDate().toString());
                heightField.setText(String.valueOf(profile.getHeight()));
                weightField.setText(String.valueOf(profile.getWeight()));
                activityLevelCombo.setSelectedItem(capitalize(profile.getActivityLevel()));
            } else {
                resultArea.setText("No profile found with ID: " + userId);
            }
        } catch (NumberFormatException ex) {
            resultArea.setText("Invalid User ID. Please enter a number.");
        }
    }

    private UserProfile createProfileFromInputs() throws DateTimeParseException, IllegalArgumentException {
        UserProfile profile = new UserProfile();
        
        // Validate and set name
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        profile.setName(name);
        
        // Set gender
        profile.setGender(genderCombo.getSelectedItem().toString().toLowerCase());
        
        // Parse and set birth date
        String birthDateStr = birthDateField.getText().trim();
        LocalDate birthDate = LocalDate.parse(birthDateStr, DateTimeFormatter.ISO_DATE);
        profile.setBirthDate(birthDate);
        
        // Parse and set height
        double height = Double.parseDouble(heightField.getText().trim());
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be positive");
        }
        profile.setHeight(height);
        
        // Parse and set weight
        double weight = Double.parseDouble(weightField.getText().trim());
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }
        profile.setWeight(weight);
        
        // Set activity level
        profile.setActivityLevel(activityLevelCombo.getSelectedItem().toString().toLowerCase());
        
        return profile;
    }

    private String profileToString(UserProfile profile) {
        return String.format(
            "User ID: %d\nName: %s\nGender: %s\nBirth Date: %s\n" +
            "Height: %.1f cm\nWeight: %.1f kg\nActivity Level: %s\n" +
            "BMR: %.2f calories/day\nDaily Calorie Needs: %.2f calories/day",
            profile.getUserId(),
            profile.getName(),
            profile.getGender(),
            profile.getBirthDate().toString(),
            profile.getHeight(),
            profile.getWeight(),
            profile.getActivityLevel(),
            profile.calculateBMR(),
            profile.calculateDailyCalories()
        );
    }
    
    public UserProfile getCurrentProfile() {
        try {
            int userId = Integer.parseInt(userIdField.getText().trim());
            return profileManager.getProfile(userId);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}