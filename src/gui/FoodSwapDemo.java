package gui;

import models.User;
import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.sql.Timestamp;

public class FoodSwapDemo extends JFrame {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Use default look and feel
            }
            
            new FoodSwapDemo().setVisible(true);
        });
    }
    
    public FoodSwapDemo() {
        initializeDemo();
    }
    
    private void initializeDemo() {
        setTitle("Food Swap Feature Demo - ClearTracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Create a dummy user for the demo
        User demoUser = new User();
        demoUser.setUserId(1);
        demoUser.setName("Demo User");
        demoUser.setGender("Other");
        demoUser.setBirthDate(new Date(System.currentTimeMillis()));
        demoUser.setHeight(170.0);
        demoUser.setWeight(70.0);
        demoUser.setActivityLevel("Moderate");
        demoUser.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        
        // Create the main panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Add our Food Swap panel
        FoodSwapPanel foodSwapPanel = new FoodSwapPanel(demoUser);
        tabbedPane.addTab("Food Swap Goals", foodSwapPanel);
        
        // Add a simple info panel
        JPanel infoPanel = createInfoPanel();
        tabbedPane.addTab("Demo Info", infoPanel);
        
        add(tabbedPane);
        
        // Set the Food Swap tab as active
        tabbedPane.setSelectedIndex(0);
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("Food Swap Feature Demo");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(76, 175, 80));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        
        String[] features = {
            "✓ Select up to 2 nutrition goals (Increase Fiber, Reduce Calories, etc.)",
            "✓ Choose intensity level (Slightly more, Moderately more, Significantly more)",
            "✓ Get personalized food swap suggestions",
            "✓ View nutritional benefits of each swap",
            "✓ Compare before and after nutrition profiles",
            "✓ Apply swaps to your meal plan"
        };
        
        for (String feature : features) {
            JLabel featureLabel = new JLabel(feature);
            featureLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            featureLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            contentPanel.add(featureLabel);
        }
        
        contentPanel.add(Box.createVerticalStrut(20));
        
        JLabel instructionLabel = new JLabel("<html><b>Instructions:</b><br>" +
                "1. Click on the 'Food Swap Goals' tab to see the interface<br>" +
                "2. Select your nutrition goal and intensity<br>" +
                "3. Click 'Find Swaps' to see suggestions<br>" +
                "4. Use 'View Comparison' to see nutritional differences</html>");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        instructionLabel.setVerticalAlignment(SwingConstants.TOP);
        contentPanel.add(instructionLabel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
} 