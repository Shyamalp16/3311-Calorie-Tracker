package gui;

import javax.swing.*;
import java.awt.*;

public class SplashPageold extends JFrame {

    public SplashPageold() {
    	//basic setup
    	setTitle("NutriSci: SwEATch to better!");
        setSize(500, 400);
//        FIX LATER:not necessary
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //Gradient creation
        JPanel splashPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(76, 175, 80);   
                Color color2 = new Color(33, 150, 243);  
                // Create a linear gradient paint from top-left to bottom-right
                GradientPaint gp = new GradientPaint(
                    0, 0, color1,         // Start point + colour
                    getWidth(), getHeight(), color2 // End point  + colour
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        //Vertical orientation
        splashPanel.setLayout(new BoxLayout(splashPanel, BoxLayout.Y_AXIS));
        
        //Padding
        splashPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        //Title
        JLabel titleLabel = new JLabel("NutriSci");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));  
        titleLabel.setForeground(Color.WHITE);                     
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);      
        
        //Subtitle
        JLabel subtitleLabel = new JLabel("SwEATch to better!");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));  
        subtitleLabel.setForeground(Color.WHITE);                      
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);       

        // drop down and panel panel
        JPanel controlPanel = new JPanel();
        // Make background transparent so gradient shows through
        controlPanel.setOpaque(false);
        // 3row
        controlPanel.setLayout(new GridLayout(3, 1, 10, 10)); 
        // Dropdown menu (JComboBox) to select profile
//FIX LATER: Make it so that you can actually edit with user.names
        JComboBox<String> profileSelect = new JComboBox<>(new String[]{"John Doe", "Jane Smith"});
        JButton enterButton = new JButton("Enter Application");
        JButton createButton = new JButton("Create New Profile...");
        controlPanel.add(profileSelect);
        controlPanel.add(enterButton);
        controlPanel.add(createButton);

        // Layout and spacing control
        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setOpaque(false); 
        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.Y_AXIS));
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0)); 
        controlPanel.setMaximumSize(new Dimension(300, 100));
        wrapperPanel.add(controlPanel);
        wrapperPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        splashPanel.add(titleLabel);
        splashPanel.add(Box.createVerticalStrut(20)); 
        splashPanel.add(subtitleLabel);
        splashPanel.add(Box.createVerticalStrut(30)); 
        splashPanel.add(wrapperPanel);

        add(splashPanel);
    }
}