package gui;

import models.User;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    private User currentUser;

    public Dashboard(User user) {
        this.currentUser = user;
        initUI();
    }

    private void initUI() {
        setTitle("NutriSci Dashboard");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Main panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome to your Dashboard, " + currentUser.getName() + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(welcomeLabel, BorderLayout.NORTH);

        // Add more components here in the center
        JTextArea mainContent = new JTextArea("This is where the main application content will go.");
        mainContent.setEditable(false);
        panel.add(new JScrollPane(mainContent), BorderLayout.CENTER);


        add(panel);
        setVisible(true);
    }
}
