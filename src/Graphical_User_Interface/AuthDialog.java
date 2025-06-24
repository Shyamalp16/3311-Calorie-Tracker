package Graphical_User_Interface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class AuthDialog extends JDialog {
protected JTextField usernameField;
protected JPasswordField passwordField;
protected boolean successful = false;
protected int userId = -1;

public AuthDialog(JFrame parent, String title) {
   super(parent, title, true);
   initializeCommonUI();
}

protected void initializeCommonUI() {
   JPanel panel = new JPanel(new GridBagLayout());
   GridBagConstraints gbc = new GridBagConstraints();
   gbc.insets = new Insets(5, 5, 5, 5);
   gbc.fill = GridBagConstraints.HORIZONTAL;
   
   gbc.gridx = 0; gbc.gridy = 0;
   panel.add(new JLabel("Username:"), gbc);
   gbc.gridx = 1;
   usernameField = new JTextField(15);
   panel.add(usernameField, gbc);
  
   gbc.gridx = 0; gbc.gridy = 1;
   panel.add(new JLabel("Password:"), gbc);
   gbc.gridx = 1;
   passwordField = new JPasswordField(15);
   panel.add(passwordField, gbc);
   
   addCustomFields(panel, gbc);
   
   JPanel buttonPanel = createButtonPanel();
   gbc.gridx = 0; gbc.gridy = getNextRow();
   gbc.gridwidth = 2;
   panel.add(buttonPanel, gbc);
   
   add(panel);
   pack();
   setLocationRelativeTo(getParent());
}

protected abstract void addCustomFields(JPanel panel, GridBagConstraints gbc);
protected abstract JPanel createButtonPanel();
protected abstract int getNextRow();
protected abstract void performAction();

protected boolean validateInput() {
   if (usernameField.getText().trim().isEmpty() || 
       passwordField.getPassword().length == 0) {
       showError("Username and password cannot be empty");
       return false;
   }
   return true;
}

protected void showError(String message) {
   JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
}

public boolean isSuccessful() { return successful; }
public int getUserId() { return userId; }
}