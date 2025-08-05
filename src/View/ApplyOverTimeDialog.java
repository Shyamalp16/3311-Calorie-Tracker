package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ApplyOverTimeDialog extends JDialog {
    private final Font FONT_NORMAL = new Font("Arial", Font.PLAIN, 14);
    private final Font FONT_TITLE = new Font("Arial", Font.BOLD, 16);
    private final Color COLOR_PRIMARY = new Color(76, 175, 80);
    private final Color COLOR_SECONDARY = new Color(33, 150, 243);
    private final Color COLOR_TEXT_LIGHT = Color.WHITE;

    private boolean confirmed = false;
    private boolean applyToAll = false;
    private Date startDate;
    private Date endDate;
    
    private JRadioButton dateRangeOption;
    private JRadioButton applyToAllOption;
    private JTextField startDateField;
    private JTextField endDateField;
    private JPanel dateRangePanel;

    public ApplyOverTimeDialog(JFrame parent, Date currentDate) {
        super(parent, "Apply Swaps Over Time", true);
        
        // Initialize default date range (30 days before and after current date)
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DAY_OF_MONTH, -30);
        this.startDate = cal.getTime();
        
        cal.setTime(currentDate);
        cal.add(Calendar.DAY_OF_MONTH, 30);
        this.endDate = cal.getTime();
        
        initUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Choose Application Scope");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        add(titleLabel, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        contentPanel.setBackground(Color.WHITE);

        // Radio button group
        ButtonGroup optionGroup = new ButtonGroup();
        
        // Date range option
        dateRangeOption = new JRadioButton("Apply to specific date range", true);
        dateRangeOption.setFont(FONT_NORMAL);
        dateRangeOption.setBackground(Color.WHITE);
        dateRangeOption.addActionListener(e -> toggleDateRangeFields(true));
        optionGroup.add(dateRangeOption);
        contentPanel.add(dateRangeOption);
        contentPanel.add(Box.createVerticalStrut(10));

        // Date range input panel
        dateRangePanel = new JPanel(new GridBagLayout());
        dateRangePanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        dateRangePanel.add(new JLabel("Start Date:"), gbc);
        
        startDateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(startDate), 10);
        startDateField.setFont(FONT_NORMAL);
        gbc.gridx = 1;
        dateRangePanel.add(startDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dateRangePanel.add(new JLabel("End Date:"), gbc);
        
        endDateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(endDate), 10);
        endDateField.setFont(FONT_NORMAL);
        gbc.gridx = 1;
        dateRangePanel.add(endDateField, gbc);

        contentPanel.add(dateRangePanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Apply to all option
        applyToAllOption = new JRadioButton("Apply to ALL meals (entire history)");
        applyToAllOption.setFont(FONT_NORMAL);
        applyToAllOption.setBackground(Color.WHITE);
        applyToAllOption.addActionListener(e -> toggleDateRangeFields(false));
        optionGroup.add(applyToAllOption);
        contentPanel.add(applyToAllOption);

        add(contentPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        buttonPanel.setBackground(Color.WHITE);

        JButton okButton = new JButton("OK");
        styleButton(okButton);
        okButton.addActionListener(this::okAction);
        buttonPanel.add(okButton);

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton);
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void toggleDateRangeFields(boolean enable) {
        startDateField.setEnabled(enable);
        endDateField.setEnabled(enable);
        for (Component comp : dateRangePanel.getComponents()) {
            if (comp instanceof JLabel) {
                comp.setEnabled(enable);
            }
        }
    }

    private void okAction(ActionEvent e) {
        if (dateRangeOption.isSelected()) {
            if (!validateAndParseDateRange()) {
                return; // Stop if validation fails
            }
            applyToAll = false;
        } else {
            applyToAll = true;
            startDate = new Date(0); // Unix epoch start
            endDate = new Date(Long.MAX_VALUE); // Far future
        }

        confirmed = true;
        dispose();
    }

    private boolean validateAndParseDateRange() {
        try {
            String startText = startDateField.getText().trim();
            String endText = endDateField.getText().trim();

            if (isDateEmpty(startText, endText) || !parseDates(startText, endText) || !isDateRangeValid() || !confirmLargeDateRange() || !confirmFutureDate()) {
                return false;
            }

            return true;
        } catch (java.text.ParseException ex) {
            handleParseException(ex);
            return false;
        } catch (Exception ex) {
            handleGenericException(ex);
            return false;
        }
    }

    private boolean isDateEmpty(String startText, String endText) {
        if (startText.isEmpty() || endText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Both start date and end date are required.\nPlease use YYYY-MM-DD format.",
                "Missing Date Information",
                JOptionPane.ERROR_MESSAGE);
            return true;
        }
        return false;
    }

    private boolean parseDates(String startText, String endText) throws java.text.ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        startDate = sdf.parse(startText);
        endDate = sdf.parse(endText);
        return true;
    }

    private boolean isDateRangeValid() {
        if (startDate.after(endDate)) {
            JOptionPane.showMessageDialog(this,
                "Start date must be before or equal to end date.",
                "Invalid Date Range",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean confirmLargeDateRange() {
        long daysDifference = (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
        if (daysDifference > (5 * 365)) {
            int result = JOptionPane.showConfirmDialog(this,
                "You've selected a very large date range (" + daysDifference + " days).\n" +
                "This may take a long time to process and affect many meals.\n" +
                "Are you sure you want to continue?",
                "Large Date Range Warning",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            return result == JOptionPane.YES_OPTION;
        }
        return true;
    }

    private boolean confirmFutureDate() {
        Date now = new Date();
        long futureLimit = now.getTime() + (365L * 24 * 60 * 60 * 1000); // 1 year from now
        if (endDate.getTime() > futureLimit) {
            int result = JOptionPane.showConfirmDialog(this,
                "End date is more than 1 year in the future.\n" +
                "This may not find any existing meals to modify.\n" +
                "Are you sure you want to continue?",
                "Future Date Warning",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            return result == JOptionPane.YES_OPTION;
        }
        return true;
    }

    private void handleParseException(java.text.ParseException ex) {
        String errorMessage = ex.getMessage().contains("Unparseable")
            ? "Invalid date format detected.\nPlease use exactly YYYY-MM-DD format."
            : "Invalid date values. Please check that the dates are valid.";
        JOptionPane.showMessageDialog(this, errorMessage, "Date Format Error", JOptionPane.ERROR_MESSAGE);
    }

    private void handleGenericException(Exception ex) {
        JOptionPane.showMessageDialog(this,
            "Unexpected error processing dates:\n" + ex.getMessage(),
            "Input Error",
            JOptionPane.ERROR_MESSAGE);
    }

    private void styleButton(JButton button) {
        button.setFont(FONT_NORMAL);
        button.setBackground(COLOR_SECONDARY);
        button.setForeground(COLOR_TEXT_LIGHT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public boolean isApplyToAll() {
        return applyToAll;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }
}