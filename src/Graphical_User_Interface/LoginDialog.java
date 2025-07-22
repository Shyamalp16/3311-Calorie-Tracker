package Graphical_User_Interface;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import User_Profile_Management.AuthService;

public class LoginDialog extends AuthDialog {
 
 public LoginDialog(JFrame parent) {
     super(parent, "NutriSci Login");
     passwordField.addActionListener(e -> performAction());
 }
 
 @Override
 protected void addCustomFields(JPanel panel, GridBagConstraints gbc) {
 }
 
 @Override
 protected JPanel createButtonPanel() {
     JPanel buttonPanel = new JPanel();
     JButton loginButton = new JButton("Login");
     JButton registerButton = new JButton("Register");
     
     loginButton.addActionListener(e -> performAction());
     registerButton.addActionListener(e -> showRegistrationDialog());
     
     buttonPanel.add(loginButton);
     buttonPanel.add(registerButton);
     return buttonPanel;
 }
 
 @Override
 protected int getNextRow() {
     return 2;
 }
 
 @Override
 protected void performAction() {
     if (!validateInput()) return;
     
     char[] passwordChars = passwordField.getPassword();
     try {
         String username = usernameField.getText().trim();
         String password = new String(passwordChars);
         
         userId = AuthService.login(username, password);
         
         if (userId != -1) {
             successful = true;
             dispose();
         } else {
             showError("Invalid username or password");
         }
     } finally {
         Arrays.fill(passwordChars, '\0');
     }
 }
 
 private void showRegistrationDialog() {
     RegistrationDialog regDialog = new RegistrationDialog((JFrame)getOwner());
     setVisible(false);
     regDialog.setVisible(true);
     
     if (regDialog.isSuccessful()) {
         userId = regDialog.getUserId();
         successful = true;
         dispose();
     } else {
         setVisible(true); 
     }
 }
}