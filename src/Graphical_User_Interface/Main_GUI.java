package Graphical_User_Interface;

import javax.swing.*;
import DatabaseConnector.DatabaseConnector;
import Meal_Logging_Calculation.MealService;
import Model.NutriSciApp;
import User_Profile_Management.ProfileManager;
import java.awt.*;

public class Main_GUI {
 private ProfileManagementGUI profileGUI;
 private MealLoggingGUI mealGUI;
 private VisualizationGUI vizGUI;
 private int currentUserId;
 private JFrame mainFrame;
 
 public Main_GUI() {
     initializeMainFrame();
     mainFrame.setVisible(false);
     
     if (!authenticateUser()) {
         System.exit(0);
     }
     
     initializeServices();
     mainFrame.setVisible(true);
 }
 
 private void initializeServices() {
     DatabaseConnector dbConnector = DatabaseConnector.getInstance();
     ProfileManager profileManager = new ProfileManager();
     MealService mealService = new MealService();
     
     profileGUI = new ProfileManagementGUI(profileManager, currentUserId);
     
     new NutriSciApp().run();
 }
 
 private boolean authenticateUser() {
     LoginDialog loginDialog = new LoginDialog(mainFrame);
     loginDialog.setVisible(true);
     
     if (loginDialog.isSuccessful()) {
         this.currentUserId = loginDialog.getUserId();
         return true;
     }
     return false;
 }
 
 private void initializeMainFrame() {
     mainFrame = new JFrame("NutriSci Main Menu");
     mainFrame.setSize(400, 300);
     mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     
     JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
     panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
     
     JButton profileBtn = new JButton("Profile Management");
     profileBtn.addActionListener(e -> showProfileGUI());
     
     JButton mealBtn = new JButton("Meal Logging");
     mealBtn.addActionListener(e -> showMealGUI());
     
     JButton vizBtn = new JButton("Nutrition Visualization");
     vizBtn.addActionListener(e -> showVisualizationGUI());
     
     panel.add(profileBtn);
     panel.add(mealBtn);
     panel.add(vizBtn);
     
     mainFrame.add(panel);
     mainFrame.setLocationRelativeTo(null);
     mainFrame.setVisible(true);
 }
 
 private void showProfileGUI() {
     if (profileGUI != null) {
         profileGUI.setVisible(true);
     }
 }
 
 private void showMealGUI() {
     if (mealGUI == null) {
         mealGUI = new MealLoggingGUI(currentUserId);
     }
     mealGUI.setVisible(true);
 }
 
 private void showVisualizationGUI() {
     if (vizGUI == null) {
         vizGUI = new VisualizationGUI(currentUserId);
     }
     vizGUI.setVisible(true);
 }
 
 public void show() {
     if (mainFrame != null) {
         mainFrame.setVisible(true);
     }
 }
}

