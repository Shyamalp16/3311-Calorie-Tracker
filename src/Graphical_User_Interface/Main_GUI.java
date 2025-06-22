package Graphical_User_Interface;

import javax.swing.*;

import DatabaseConnector.DatabaseConnector;
import Meal_Logging_Calculation.MealService;
import User_Profile_Management.ProfileManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main_GUI {
    private JFrame mainFrame;
    private ProfileManagementGUI profileGUI;
    private MealLoggingGUI mealGUI;
    private VisualizationGUI vizGUI;
    
    public Main_GUI() {
        // Initialize core services
        DatabaseConnector dbConnector = DatabaseConnector.getInstance();
        ProfileManager profileManager = new ProfileManager();
        MealService mealService = new MealService();
        
        // Create GUI components
        profileGUI = new ProfileManagementGUI(profileManager);
        initializeMainFrame();
    }
    
    private void initializeMainFrame() {
        mainFrame = new JFrame("NutriSci Main Menu");
        mainFrame.setSize(400, 300);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton profileBtn = new JButton("Profile Management");
        profileBtn.addActionListener(e -> profileGUI.setVisible(true));
        
        JButton mealBtn = new JButton("Meal Logging");
        //mealBtn.addActionListener(e -> mealGUI.setVisible(true));
        
        JButton vizBtn = new JButton("Nutrition Visualization");
        //vizBtn.addActionListener(e -> vizGUI.setVisible(true));
        
        panel.add(profileBtn);
        panel.add(mealBtn);
        panel.add(vizBtn);
        
        mainFrame.add(panel);
        mainFrame.setLocationRelativeTo(null);
    }
    
    public void show() {
        mainFrame.setVisible(true);
    }
    
}