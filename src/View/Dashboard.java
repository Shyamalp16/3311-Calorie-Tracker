package View;

import models.User;
import models.UserSettings;
import models.UnitSystem;
import Database.UserSettingsDAO;
import controller.*;
import logic.facade.INutritionFacade;
import logic.facade.NutritionFacade;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.ChartPanel;

public class Dashboard extends JFrame implements ProfileChangeListener {

    private User currentUser;
    private UserSettings currentSettings;
    
    private INutritionFacade nutritionFacade;

    private JTabbedPane tabbedPane;
    private DashboardPanel dashboardPanel;
    private LogMealPanel logMealPanel;
    private FoodSwapsPanel foodSwapsPanel;
    private NutritionAnalysisPanel nutritionAnalysisPanel;
    private CanadaFoodGuidePanel canadaFoodGuidePanel;

    private final Color COLOR_PRIMARY = new Color(76, 175, 80);
    private final Color COLOR_TAB_INACTIVE = new Color(224, 224, 224);
    private final Color COLOR_TEXT_INACTIVE = new Color(51, 51, 51);
    private final Color COLOR_TEXT_LIGHT = Color.WHITE;
    private final Color COLOR_BACKGROUND = new Color(245, 245, 245);
    private final Color COLOR_SECONDARY = new Color(100, 181, 246);
    private final Color COLOR_TEXT_DARK = new Color(33, 33, 33); 
    private final Font FONT_NORMAL = new Font("Arial", Font.PLAIN, 14);

    public Dashboard(User user) {
        this.currentUser = user;
        
        UserSettingsDAO settingsDAO = new UserSettingsDAO();
        settingsDAO.createUserSettingsTable();
        this.currentSettings = settingsDAO.getOrCreateDefaultSettings(user.getUserId());
        
        this.nutritionFacade = new NutritionFacade(user);
        
        initUI();
    }

    private void initUI() {
        setTitle("Main Application - Dashboard");
        setSize(1200, 800); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(COLOR_BACKGROUND);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_NORMAL);

        dashboardPanel = new DashboardPanel(this, currentUser, nutritionFacade, currentSettings);
        logMealPanel = new LogMealPanel(nutritionFacade, this::refreshAllTabs);
        foodSwapsPanel = new FoodSwapsPanel(this, currentUser.getUserId(), nutritionFacade, currentSettings);
        nutritionAnalysisPanel = new NutritionAnalysisPanel(this, currentUser, nutritionFacade, currentSettings);
        canadaFoodGuidePanel = new CanadaFoodGuidePanel(nutritionFacade);

        tabbedPane.addTab("Dashboard", dashboardPanel);
        tabbedPane.addTab("Log Meal", logMealPanel);
        tabbedPane.addTab("Food Swaps", foodSwapsPanel);
        tabbedPane.addTab("Nutrition Analysis", nutritionAnalysisPanel);
        tabbedPane.addTab("Canada Food Guide", canadaFoodGuidePanel);

        tabbedPane.addChangeListener(e -> {
            updateTabStyles(tabbedPane);
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex == 0) { 
                dashboardPanel.refresh();
            } else if (selectedIndex == 3) {
                nutritionAnalysisPanel.refresh();
            } else if (selectedIndex == 4) {
                canadaFoodGuidePanel.refresh();
            }
        });

        updateTabStyles(tabbedPane);

        add(tabbedPane, BorderLayout.CENTER);
        
        setVisible(true);
    }

    private void updateTabStyles(JTabbedPane tabbedPane) {
        int selectedIndex = tabbedPane.getSelectedIndex();
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (i == selectedIndex) {
                tabbedPane.setBackgroundAt(i, COLOR_PRIMARY);
                tabbedPane.setForegroundAt(i, COLOR_TEXT_LIGHT);
            } else {
                tabbedPane.setBackgroundAt(i, COLOR_TAB_INACTIVE);
                tabbedPane.setForegroundAt(i, COLOR_TEXT_INACTIVE);
            }
        }
    }

    public void refreshAllTabs() {
        dashboardPanel.refresh();
        nutritionAnalysisPanel.refresh();
        canadaFoodGuidePanel.refresh();
    }
    
    public void refreshNutritionalAnalysisTab() {
        if (nutritionAnalysisPanel != null) {
            nutritionAnalysisPanel.refresh();
        }
    }

    public void refreshDashboard() {
        if (dashboardPanel != null) {
            dashboardPanel.refresh();
        }
    }

    @Override
    public void onProfileUpdated(User updatedUser) {
        this.currentUser = updatedUser;
    }

    @Override
    public void onSettingsUpdated(UserSettings updatedSettings) {
        this.currentSettings = updatedSettings;
    }

    @Override
    public void onProfileAndSettingsUpdated(User user, UserSettings settings) {
        this.currentUser = user;
        this.currentSettings = settings;
        JOptionPane.showMessageDialog(this, "Profile updated. Please restart the dashboard to see all changes.");
        dispose();
        new Dashboard(user);
    }

    @Override
    public void onUnitSystemChanged(UnitSystem oldSystem, UnitSystem newSystem) {
        refreshAllTabs();
    }
}

    
