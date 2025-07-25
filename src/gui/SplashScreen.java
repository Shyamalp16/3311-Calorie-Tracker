package gui;

import Database.DatabaseConnector;
import Database.UserDAO;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * SplashScreen - The first window users see
 * Shows app title, logo, and lets users select existing profile or create new one
 */
public class SplashScreen extends JFrame {
	DatabaseConnector DatabaseConnector;
    private UserDAO userDAO; // Database operations for users
    
    public SplashScreen() {
        userDAO = new UserDAO();

        
        setupWindow();
        createComponents();
        setVisible(true); // Make window visible
    }
    
    /**
     * Sets up the main window properties
     */
    private void setupWindow() {
        setTitle("NutriSci: SwEATch to Better!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close app when window closes
        setSize(800, 600); // Window size
        setLocationRelativeTo(null); // Center on screen
        setResizable(false); // Don't let user resize
        
        // Set background color
        getContentPane().setBackground(new Color(45, 45, 45)); // Dark gray
    }
    
    /**
     * Creates and arranges all the visual components
     */
    private void createComponents() {
        // Main container with border layout
        setLayout(new BorderLayout());
        
        // Create header panel (top section)
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create center panel (main content)
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
        
        // Create footer panel (bottom buttons)
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates the header with app title and subtitle
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(34, 139, 34)); // Forest green
        panel.setPreferredSize(new Dimension(800, 120));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Main title
        JLabel titleLabel = new JLabel("NutriSci", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("SwEATch to Better!", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        subtitleLabel.setForeground(new Color(200, 255, 200)); // Light green
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10)); // Add space between labels
        panel.add(subtitleLabel);
        
        return panel;
    }
    
    /**
     * Creates the center panel with welcome message and user selection
     */
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(45, 45, 45));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome to your nutrition tracking companion!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Description
        JLabel descLabel = new JLabel("<html><center>Track your meals, analyze nutrition, and get smart food recommendations<br>to achieve your health goals with the Canadian Nutrient File.</center></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setForeground(new Color(200, 200, 200));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add all components with spacing
        panel.add(welcomeLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(descLabel);

        return panel;
    }
    
    /**
     * Creates the footer with action buttons
     */
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(45, 45, 45));

        // Initial buttons panel
        JPanel initialButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        initialButtonsPanel.setBackground(new Color(45, 45, 45));

        JButton newUserButton = new JButton("Create New Profile");
        styleButton(newUserButton, new Color(34, 139, 34));
        newUserButton.addActionListener(e -> {
            dispose();
            new ProfileCreation(SplashScreen.this);
        });

        JButton loginButton = new JButton("Login");
        styleButton(loginButton, new Color(70, 130, 180));

        initialButtonsPanel.add(newUserButton);
        initialButtonsPanel.add(loginButton);

        // Login fields and buttons panel (initially hidden)
        JPanel loginContainerPanel = new JPanel(new GridBagLayout());
        loginContainerPanel.setBackground(new Color(45, 45, 45));
        loginContainerPanel.setVisible(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        loginContainerPanel.add(userLabel, gbc);

        JTextField userField = new JTextField(20);
        gbc.gridy = 1;
        loginContainerPanel.add(userField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        gbc.gridy = 2;
        loginContainerPanel.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(20);
        gbc.gridy = 3;
        loginContainerPanel.add(passField, gbc);

        // Panel for Continue and Back buttons
        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        actionButtonsPanel.setBackground(new Color(45, 45, 45));

        JButton backButton = new JButton("Back");
        styleButton(backButton, new Color(108, 117, 125));

        JButton continueButton = new JButton("Continue");
        styleButton(continueButton, new Color(70, 130, 180));

        actionButtonsPanel.add(backButton);
        actionButtonsPanel.add(continueButton);

        gbc.gridy = 4;
        gbc.insets = new Insets(15, 5, 5, 5); // Add top margin
        loginContainerPanel.add(actionButtonsPanel, gbc);

        // Add panels to the main footer panel
        panel.add(initialButtonsPanel, BorderLayout.NORTH);
        panel.add(loginContainerPanel, BorderLayout.CENTER);

        // Action Listeners
        loginButton.addActionListener(e -> {
            initialButtonsPanel.setVisible(false);
            loginContainerPanel.setVisible(true);
        });

        backButton.addActionListener(e -> {
            loginContainerPanel.setVisible(false);
            initialButtonsPanel.setVisible(true);
        });

        continueButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            User user = userDAO.login(username, password).orElse(null);

            if (user != null) {
                new Dashboard(user);
                dispose();
            } else {
                JOptionPane.showMessageDialog(SplashScreen.this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private void styleButton(JButton button, Color color) {
        button.setPreferredSize(new Dimension(150, 35));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }
}
