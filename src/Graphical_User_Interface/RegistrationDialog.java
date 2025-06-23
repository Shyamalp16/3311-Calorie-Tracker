package Graphical_User_Interface;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import User_Profile_Management.AuthService;

public class RegistrationDialog extends AuthDialog {
 private JPasswordField confirmPasswordField;
 private JTextField emailField;
 
 public RegistrationDialog(JFrame parent) {
     super(parent, "NutriSci Registration");
 }
 
 @Override
 protected void addCustomFields(JPanel panel, GridBagConstraints gbc) {
     // Email field
     gbc.gridx = 0; gbc.gridy = 2;
     panel.add(new JLabel("Email:"), gbc);
     gbc.gridx = 1;
     emailField = new JTextField(15);
     panel.add(emailField, gbc);
     
     // Confirm password field
     gbc.gridx = 0; gbc.gridy = 3;
     panel.add(new JLabel("Confirm Password:"), gbc);
     gbc.gridx = 1;
     confirmPasswordField = new JPasswordField(15);
     panel.add(confirmPasswordField, gbc);
 }
 
 @Override
 protected JPanel createButtonPanel() {
     JPanel buttonPanel = new JPanel();
     JButton registerButton = new JButton("Register");
     JButton cancelButton = new JButton("Cancel");
     
     registerButton.addActionListener(e -> performAction());
     cancelButton.addActionListener(e -> dispose());
     
     buttonPanel.add(registerButton);
     buttonPanel.add(cancelButton);
     return buttonPanel;
 }
 
 @Override
 protected int getNextRow() {
     return 4;
 }
 
 @Override
 protected boolean validateInput() {
     if (!super.validateInput()) return false;
     
     if (emailField.getText().trim().isEmpty()) {
         showError("Email cannot be empty");
         return false;
     }
     
     char[] password = passwordField.getPassword();
     char[] confirmPassword = confirmPasswordField.getPassword();
     
     try {
         if (!Arrays.equals(password, confirmPassword)) {
             showError("Passwords do not match");
             return false;
         }
     } finally {
         Arrays.fill(password, '\0');
         Arrays.fill(confirmPassword, '\0');
     }
     
     return true;
 }
 
 @Override
 protected void performAction() {
     if (!validateInput()) return;
     
     char[] passwordChars = passwordField.getPassword();
     try {
         String username = usernameField.getText().trim();
         String password = new String(passwordChars);
         String email = emailField.getText().trim();
         
         userId = AuthService.register(username, password, email);
         
         if (userId != -1) {
             successful = true;
             JOptionPane.showMessageDialog(this, "Registration successful!");
             dispose();
         } else {
             showError("Registration failed. Username may already exist.");
         }
     } finally {
         Arrays.fill(passwordChars, '\0');
     }
 }
}


