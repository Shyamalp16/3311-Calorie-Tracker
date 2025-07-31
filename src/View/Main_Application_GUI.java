package View;

import Database.DatabaseConnector;
import controller.ProfileController;
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
public class Main_Application_GUI extends JFrame {
	private static Main_Application_GUI instance;
    private ProfileController profileController; 
    
    private Main_Application_GUI() {
        profileController = new ProfileController();
        setupWindow();
        createComponents();
    }
    
    public static Main_Application_GUI getInstance() {
    	
    	if(instance == null) {
    		instance = new Main_Application_GUI();	
    	}
    	
    	return instance;
    }
    
    /**
     * Sets up the main window properties
     */
    private void setupWindow() {
        setTitle("NutriSci: SwEATch to Better!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setSize(800, 600); 
        setLocationRelativeTo(null);
        setResizable(false); 
        
        getContentPane().setBackground(new Color(45, 45, 45)); // Dark gray
    }
    
    /**
     * Creates and arranges all the visual components
     */
    private void createComponents() {
        setLayout(new BorderLayout());
        
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
        
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates the header with app title and subtitle
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(46, 160, 46), 0, getHeight(), new Color(34, 139, 34));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setPreferredSize(new Dimension(800, 140));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));
        
        // Main title
        JLabel titleLabel = new JLabel("NutriSci", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("SwEATch to Better!", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 20));
        subtitleLabel.setForeground(new Color(220, 255, 220)); // Lighter green
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(Box.createVerticalGlue());
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(subtitleLabel);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    /**
     * Creates the center panel with welcome message and user selection
     */
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(45, 45, 45));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 60, 50, 60));

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome to your nutrition tracking companion!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Description with better line height
        JLabel descLabel = new JLabel("<html><div style='text-align: center; width: 520px; line-height: 1.6;'>Track your meals, analyze nutrition, and get smart food recommendations<br>to achieve your health goals with the Canadian Nutrient File.</div></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        descLabel.setForeground(new Color(220, 220, 220));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Add all components with spacing
        panel.add(Box.createVerticalGlue());
        panel.add(welcomeLabel);
        panel.add(Box.createVerticalStrut(25));
        panel.add(descLabel);
        panel.add(Box.createVerticalGlue());

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
            new ProfileCreation(Main_Application_GUI.this);
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
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 0;
        loginContainerPanel.add(userLabel, gbc);

        JTextField userField = new JTextField(20);
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        gbc.gridy = 1;
        loginContainerPanel.add(userField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 2;
        loginContainerPanel.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(20);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
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
            User user = profileController.login(username, password);

            if (user != null) {
                new Dashboard(user);
                dispose();
            } else {
                JOptionPane.showMessageDialog(Main_Application_GUI.this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        java.awt.event.KeyListener enterKeyListener = new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    continueButton.doClick();
                }
            }
        };
        
        userField.addKeyListener(enterKeyListener);
        passField.addKeyListener(enterKeyListener);

        return panel;
    }

    private void styleButton(JButton button, Color color) {
        button.setPreferredSize(new Dimension(170, 42));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        Color originalColor = color;
        Color hoverColor = color.brighter();
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });
    }
}
