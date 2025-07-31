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
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog for editing existing user profiles
 * Supports unit system changes and triggers observer notifications
 */
public class ProfileEditDialog extends JDialog {
    private User currentUser;
    private UserSettings currentSettings;
    private ProfileController profileController;
    private List<ProfileChangeListener> listeners;
    private ProgressMonitor progressMonitor;
    
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
    
    private final Color COLOR_PRIMARY = new Color(76, 175, 80);
    private final Color COLOR_BACKGROUND = new Color(245, 245, 245);
    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    private final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    
    public ProfileEditDialog(Frame parent, User user, UserSettings settings) {
        super(parent, "Edit Profile", true);
        this.currentUser = user;
        this.currentSettings = settings;
        this.profileController = new ProfileController();
        this.listeners = new ArrayList<>();
        this.progressMonitor = new ProgressDialog(parent);
        
        setupDialog();
        createForm();
        populateFields();
        pack();
        setLocationRelativeTo(parent);
    }
    
    /**
     * Add a listener for profile changes
     */
    public void addProfileChangeListener(ProfileChangeListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Remove a listener
     */
    public void removeProfileChangeListener(ProfileChangeListener listener) {
        listeners.remove(listener);
    }
    
    private void setupDialog() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(COLOR_BACKGROUND);
    }
    
    private void createForm() {
        setLayout(new BorderLayout());
        
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(COLOR_PRIMARY);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Edit Profile", SwingConstants.CENTER);
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        add(titlePanel, BorderLayout.NORTH);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_BACKGROUND);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        addSectionHeader(formPanel, "Personal Information", row++, gbc);
        
        addFormField(formPanel, "Name:", nameField = new JTextField(20), row++, gbc);
        
        addFormField(formPanel, "Username:", usernameField = new JTextField(20), row++, gbc);
        
        addFormField(formPanel, "Password:", passwordField = new JPasswordField(20), row++, gbc);
        
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        addFormField(formPanel, "Gender:", genderCombo, row++, gbc);
        
        addFormField(formPanel, "Birth Date (YYYY-MM-DD):", birthDateField = new JTextField(20), row++, gbc);
        
        addSectionHeader(formPanel, "Physical Information", row++, gbc);
        
        heightLabel = new JLabel("Height:");
        heightField = new JTextField(20);
        addFormField(formPanel, heightLabel, heightField, row++, gbc);
        
        weightLabel = new JLabel("Weight:");
        weightField = new JTextField(20);
        addFormField(formPanel, weightLabel, weightField, row++, gbc);
        
        activityCombo = new JComboBox<>(new String[]{
            "Sedentary", "Lightly Active", "Moderately Active", "Very Active", "Extremely Active"
        });
        addFormField(formPanel, "Activity Level:", activityCombo, row++, gbc);
        
        addSectionHeader(formPanel, "Preferences", row++, gbc);
        
        unitSystemCombo = new JComboBox<>(UnitSystem.values());
        unitSystemCombo.addActionListener(e -> updateUnitLabels());
        addFormField(formPanel, "Unit System:", unitSystemCombo, row++, gbc);
        
        
        add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(COLOR_BACKGROUND);
        
        JButton saveButton = createStyledButton("Save Changes", COLOR_PRIMARY);
        saveButton.addActionListener(e -> saveProfile());
        
        JButton cancelButton = createStyledButton("Cancel", new Color(108, 117, 125));
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void addSectionHeader(JPanel panel, String text, int row, GridBagConstraints gbc) {
        JLabel header = new JLabel(text);
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setForeground(COLOR_PRIMARY);
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 8, 5);
        panel.add(header, gbc);
        
        gbc.gridwidth = 1; 
        gbc.insets = new Insets(8, 5, 8, 5);
    }
    
    private void addFormField(JPanel panel, String labelText, JComponent field, int row, GridBagConstraints gbc) {
        JLabel label = new JLabel(labelText);
        addFormField(panel, label, field, row, gbc);
    }
    
    private void addFormField(JPanel panel, JLabel label, JComponent field, int row, GridBagConstraints gbc) {
        label.setFont(FONT_NORMAL);
        field.setFont(FONT_NORMAL);
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(field, gbc);
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(130, 35));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(FONT_NORMAL);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        return button;
    }
    
    private void populateFields() {
        nameField.setText(currentUser.getName());
        usernameField.setText(currentUser.getUsername());
        passwordField.setText(currentUser.getPassword());
        genderCombo.setSelectedItem(currentUser.getGender());
        
        if (currentUser.getBirthDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            birthDateField.setText(sdf.format(currentUser.getBirthDate()));
        }
        
        unitSystemCombo.setSelectedItem(currentSettings.getUnitSystem());
        updateUnitLabels();
        
        double displayHeight = currentSettings.convertHeightForDisplay(currentUser.getHeight());
        double displayWeight = currentSettings.convertWeightForDisplay(currentUser.getWeight());
        
        heightField.setText(String.valueOf(displayHeight));
        weightField.setText(String.valueOf(displayWeight));
        
        activityCombo.setSelectedItem(currentUser.getActivityLevel());
        
    }
    
    private void updateUnitLabels() {
        UnitSystem selectedSystem = (UnitSystem) unitSystemCombo.getSelectedItem();
        if (selectedSystem != null) {
            heightLabel.setText("Height (" + selectedSystem.getHeightUnit() + "):");
            weightLabel.setText("Weight (" + selectedSystem.getWeightUnit() + "):");
            
            if (selectedSystem != currentSettings.getUnitSystem()) {
                convertDisplayedValues(selectedSystem);
            }
        }
    }
    
    private void convertDisplayedValues(UnitSystem newSystem) {
        try {
            double currentHeightDisplay = Double.parseDouble(heightField.getText());
            double heightInCm = UnitSystem.convertHeight(currentHeightDisplay, currentSettings.getUnitSystem(), UnitSystem.METRIC);
            double newHeightDisplay = UnitSystem.convertHeight(heightInCm, UnitSystem.METRIC, newSystem);
            heightField.setText(String.format("%.1f", newHeightDisplay));
            
            double currentWeightDisplay = Double.parseDouble(weightField.getText());
            double weightInKg = UnitSystem.convertWeight(currentWeightDisplay, currentSettings.getUnitSystem(), UnitSystem.METRIC);
            double newWeightDisplay = UnitSystem.convertWeight(weightInKg, UnitSystem.METRIC, newSystem);
            weightField.setText(String.format("%.1f", newWeightDisplay));
            
        } catch (NumberFormatException e) {
        }
    }
    
    private void saveProfile() {
        progressMonitor.showProgress("Saving Profile", "Validating changes...");
        
        SwingWorker<Boolean, String> worker = new SwingWorker<Boolean, String>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    publish("Updating profile information...");
                    progressMonitor.updateProgress(25);
                    
                    User updatedUser = validateAndCreateUser();
                    if (updatedUser == null) return false;
                    
                    publish("Updating settings...");
                    progressMonitor.updateProgress(50);
                    
                    UserSettings updatedSettings = validateAndCreateSettings();
                    if (updatedSettings == null) return false;
                    
                    publish("Saving to database...");
                    progressMonitor.updateProgress(75);
                    
                    boolean success = profileController.updateUser(updatedUser, updatedSettings);
                    
                    if (success) {
                        publish("Notifying UI components...");
                        progressMonitor.updateProgress(90);
                        
                        currentUser = updatedUser;
                        UnitSystem oldUnitSystem = currentSettings.getUnitSystem();
                        currentSettings = updatedSettings;
                        
                        notifyListeners(updatedUser, updatedSettings, oldUnitSystem);
                        
                        progressMonitor.updateProgress(100);
                        return true;
                    }
                    
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        progressMonitor.setError("Error saving profile: " + e.getMessage());
                    });
                }
                
                return false;
            }
            
            @Override
            protected void process(java.util.List<String> chunks) {
                if (!chunks.isEmpty()) {
                    progressMonitor.updateMessage(chunks.get(chunks.size() - 1));
                }
            }
            
            @Override
            protected void done() {
                try {
                    Boolean success = get();
                    if (success) {
                        SwingUtilities.invokeLater(() -> {
                            progressMonitor.setCompleted("Profile updated successfully!");
                            Timer timer = new Timer(1000, e -> dispose());
                            timer.setRepeats(false);
                            timer.start();
                        });
                    }
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        progressMonitor.setError("Failed to save profile changes");
                    });
                }
            }
        };
        
        worker.execute();
    }
    
    private User validateAndCreateUser() {
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String gender = (String) genderCombo.getSelectedItem();
        String birthDateStr = birthDateField.getText().trim();
        String heightStr = heightField.getText().trim();
        String weightStr = weightField.getText().trim();
        String activityLevel = (String) activityCombo.getSelectedItem();
        
        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showError("Name, username, and password are required.");
            return null;
        }
        
        DateValidator.ValidationResult dateResult = DateValidator.validateBirthDate(birthDateStr);
        if (!dateResult.isValid()) {
            showError(dateResult.getErrorMessage());
            return null;
        }
        Date birthDate = new Date(dateResult.getParsedDate().getTime());
        
        double height, weight;
        try {
            UnitSystem currentUnitSystem = (UnitSystem) unitSystemCombo.getSelectedItem();
            
            double displayHeight = Double.parseDouble(heightStr);
            double displayWeight = Double.parseDouble(weightStr);
            
            height = UnitSystem.convertHeight(displayHeight, currentUnitSystem, UnitSystem.METRIC);
            weight = UnitSystem.convertWeight(displayWeight, currentUnitSystem, UnitSystem.METRIC);
            
        } catch (NumberFormatException e) {
            showError("Please enter valid numbers for height and weight.");
            return null;
        }
        
        User updatedUser = new User(name, username, password, gender, birthDate, height, weight, activityLevel);
        updatedUser.setUserId(currentUser.getUserId());
        updatedUser.setCreatedAt(currentUser.getCreatedAt());
        
        return updatedUser;
    }
    
    private UserSettings validateAndCreateSettings() {
        UnitSystem unitSystem = (UnitSystem) unitSystemCombo.getSelectedItem();
        
        UserSettings updatedSettings = new UserSettings();
        updatedSettings.setSettingsId(currentSettings.getSettingsId());
        updatedSettings.setUserId(currentSettings.getUserId());
        updatedSettings.setUnitSystem(unitSystem);
        updatedSettings.setTheme(currentSettings.getTheme());
        updatedSettings.setEnableNotifications(true);
        updatedSettings.setDateFormat(currentSettings.getDateFormat());
        updatedSettings.setDailyGoalCalories(currentSettings.getDailyGoalCalories());
        
        return updatedSettings;
    }
    
    private void notifyListeners(User user, UserSettings settings, UnitSystem oldUnitSystem) {
        SwingUtilities.invokeLater(() -> {
            for (ProfileChangeListener listener : listeners) {
                listener.onProfileAndSettingsUpdated(user, settings);
                
                if (oldUnitSystem != settings.getUnitSystem()) {
                    listener.onUnitSystemChanged(oldUnitSystem, settings.getUnitSystem());
                }
            }
        });
    }
    
    private void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
        });
    }
} 