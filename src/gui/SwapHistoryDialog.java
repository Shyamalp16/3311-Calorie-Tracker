package gui;

import models.SwapHistory;
import models.Food;
import logic.SwapApplicationService;
import Database.FoodDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.List;

public class SwapHistoryDialog extends JDialog {
    
    private SwapApplicationService swapApplicationService;
    private FoodDAO foodDAO;
    private int userId;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    
    private final Color COLOR_PRIMARY = new Color(76, 175, 80);
    private final Color COLOR_SECONDARY = new Color(33, 150, 243);
    private final Font FONT_TITLE = new Font("Arial", Font.BOLD, 18);
    private final Font FONT_NORMAL = new Font("Arial", Font.PLAIN, 14);
    
    public SwapHistoryDialog(JFrame parent, int userId) {
        super(parent, "Swap History", true);
        this.userId = userId;
        this.swapApplicationService = new SwapApplicationService();
        this.foodDAO = new FoodDAO();
        initUI();
        loadSwapHistory();
    }
    
    private void initUI() {
        setSize(800, 600);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Food Swap History");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setBackground(COLOR_PRIMARY);
        titleLabel.setOpaque(true);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        createHistoryTable();
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setPreferredSize(new Dimension(750, 400));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void createHistoryTable() {
        String[] columnNames = {"Date Applied", "Original Food", "Swapped To", "Quantity", "Reason", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        historyTable = new JTable(tableModel);
        historyTable.setFont(FONT_NORMAL);
        historyTable.setRowHeight(25);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set column widths
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        historyTable.getColumnModel().getColumn(5).setPreferredWidth(80);
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);
        
        JButton revertButton = new JButton("Revert Selected Swap");
        styleButton(revertButton);
        revertButton.addActionListener(this::revertSwap);
        panel.add(revertButton);
        
        JButton refreshButton = new JButton("Refresh");
        styleButton(refreshButton);
        refreshButton.addActionListener(e -> loadSwapHistory());
        panel.add(refreshButton);
        
        JButton closeButton = new JButton("Close");
        styleButton(closeButton);
        closeButton.addActionListener(e -> dispose());
        panel.add(closeButton);
        
        return panel;
    }
    
    private void styleButton(JButton button) {
        button.setFont(FONT_NORMAL);
        button.setBackground(COLOR_SECONDARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }
    
    private void loadSwapHistory() {
        tableModel.setRowCount(0); // Clear existing data
        
        List<SwapHistory> swapHistories = swapApplicationService.getSwapHistory(userId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        for (SwapHistory swap : swapHistories) {
            Food originalFood = foodDAO.getFoodById(swap.getOriginalFoodId());
            Food swappedFood = foodDAO.getFoodById(swap.getSwappedFoodId());
            
            String originalName = (originalFood != null) ? originalFood.getFoodDescription() : "Unknown Food";
            String swappedName = (swappedFood != null) ? swappedFood.getFoodDescription() : "Unknown Food";
            
            // Truncate long food names
            if (originalName.length() > 30) {
                originalName = originalName.substring(0, 27) + "...";
            }
            if (swappedName.length() > 30) {
                swappedName = swappedName.substring(0, 27) + "...";
            }
            
            Object[] row = {
                sdf.format(swap.getAppliedAt()),
                originalName,
                swappedName,
                String.format("%.1f %s", swap.getQuantity(), swap.getUnit()),
                swap.getSwapReason() != null ? swap.getSwapReason() : "No reason",
                swap.isActive() ? "Active" : "Reverted"
            };
            
            tableModel.addRow(row);
        }
    }
    
    private void revertSwap(ActionEvent e) {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a swap to revert.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the swap history for the selected row
        List<SwapHistory> swapHistories = swapApplicationService.getSwapHistory(userId);
        if (selectedRow >= swapHistories.size()) {
            JOptionPane.showMessageDialog(this, 
                "Invalid selection.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        SwapHistory selectedSwap = swapHistories.get(selectedRow);
        
        if (!selectedSwap.isActive()) {
            JOptionPane.showMessageDialog(this, 
                "This swap has already been reverted.",
                "Already Reverted",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to revert this swap?\n" +
            "This will change the food back to its original state.",
            "Confirm Revert",
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                boolean success = swapApplicationService.revertSwap(selectedSwap.getHistoryId());
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Swap reverted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh the history table
                    loadSwapHistory();
                    
                    // Refresh parent dashboard if it's a Dashboard
                    if (getParent() instanceof gui.Dashboard) {
                        ((gui.Dashboard) getParent()).refreshDashboard();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to revert the swap. Check console for details.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error reverting swap: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
} 