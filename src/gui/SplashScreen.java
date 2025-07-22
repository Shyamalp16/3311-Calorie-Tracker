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
    private JComboBox<User> userDropdown; // Dropdown to select existing users
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
        
        // User selection section
        JLabel selectLabel = new JLabel("Select your profile or create a new one:");
        selectLabel.setFont(new Font("Arial", Font.BOLD, 14));
        selectLabel.setForeground(Color.WHITE);
        selectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Dropdown for existing users
        userDropdown = new JComboBox<>();
        userDropdown.setMaximumSize(new Dimension(300, 30));
        userDropdown.setAlignmentX(Component.CENTER_ALIGNMENT);
        refreshUserDropdown(); // Load users from database
        
        // Add all components with spacing
        panel.add(welcomeLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(descLabel);
        panel.add(Box.createVerticalStrut(40));
        panel.add(selectLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(userDropdown);
        
        return panel;
    }
    
    /**
     * Creates the footer with action buttons
     */
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panel.setBackground(new Color(45, 45, 45));
        
        // Button to create new user
        JButton newUserButton = new JButton("Create New Profile");
        newUserButton.setPreferredSize(new Dimension(150, 35));
        newUserButton.setBackground(new Color(34, 139, 34));
        newUserButton.setForeground(Color.WHITE);
        newUserButton.setFocusPainted(false); // Remove ugly focus border
        
        // What happens when "Create New Profile" is clicked
        newUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close this window and open user creation dialog
                dispose(); // Close splash screen
                new ProfileCreation(SplashScreen.this); // Open creation dialog
            }
        });
        
        // Button to continue with selected user
        JButton continueButton = new JButton("Continue");
        continueButton.setPreferredSize(new Dimension(150, 35));
        continueButton.setBackground(new Color(70, 130, 180)); // Steel blue
        continueButton.setForeground(Color.WHITE);
        continueButton.setFocusPainted(false);
        
        // What happens when "Continue" is clicked
        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User selectedUser = (User) userDropdown.getSelectedItem();
                if (selectedUser != null) {
                    // TODO: Open main application with selected user
                    JOptionPane.showMessageDialog(SplashScreen.this,
                        "Welcome back, " + selectedUser.getName() + "!\nMain app will open here.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(SplashScreen.this,
                        "Please select a user or create a new profile.",
                        "No User Selected",
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        panel.add(newUserButton);
        panel.add(continueButton);
        
        return panel;
    }
    
    /**
     * Loads all users from database into the dropdown menu
     */
    private void refreshUserDropdown() {
        userDropdown.removeAllItems(); // Clear existing items
        
        // Add placeholder option
        userDropdown.addItem(null);
        
        // Get all users from database
        List<User> users = userDAO.getAllUsers();
        
        // Add each user to dropdown
        for (User user : users) {
            userDropdown.addItem(user); // Uses User.toString() for display
        }
        
        // Set custom renderer to show placeholder text nicely
        userDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (value == null) {
                    setText("-- Select a user --");
                    setForeground(Color.GRAY);
                } else {
                    setText(value.toString());
                }
                
                return this;
            }
        });
    }
    
    /**
     * Called when a new user is created - refreshes the dropdown
     */
    public void onUserCreated() {
        refreshUserDropdown();
        setVisible(true); // Make splash screen visible again
    }
}