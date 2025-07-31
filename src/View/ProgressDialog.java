package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Concrete implementation of ProgressMonitor interface
 * Provides a modal dialog with progress bar and cancellation support
 */
public class ProgressDialog extends JDialog implements ProgressMonitor {
    
    private JProgressBar progressBar;
    private JLabel messageLabel;
    private JButton cancelButton;
    private boolean cancelled = false;
    private boolean completed = false;
    
    private final Color COLOR_PRIMARY = new Color(76, 175, 80);
    private final Color COLOR_BACKGROUND = new Color(245, 245, 245);
    private final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    
    public ProgressDialog(Frame parent) {
        super(parent, true);
        initializeComponents();
        setupDialog();
    }
    
    private void initializeComponents() {
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("0%");
        progressBar.setFont(FONT_NORMAL);
        progressBar.setForeground(COLOR_PRIMARY);
        progressBar.setPreferredSize(new Dimension(300, 25));
        
        messageLabel = new JLabel("Please wait...", SwingConstants.CENTER);
        messageLabel.setFont(FONT_BOLD);
        messageLabel.setPreferredSize(new Dimension(350, 30));
        
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(FONT_NORMAL);
        cancelButton.setPreferredSize(new Dimension(100, 30));
        cancelButton.setBackground(new Color(244, 67, 54));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelled = true;
                hideProgress();
            }
        });
    }
    
    private void setupDialog() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(COLOR_BACKGROUND);
        
        setLayout(new BorderLayout(10, 10));
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(COLOR_BACKGROUND);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        centerPanel.add(messageLabel);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(progressBar);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(cancelButton);
        
        add(centerPanel, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(getParent());
    }
    
    @Override
    public void showProgress(String title, String message) {
        SwingUtilities.invokeLater(() -> {
            setTitle(title);
            messageLabel.setText(message);
            progressBar.setValue(0);
            progressBar.setString("0%");
            cancelled = false;
            completed = false;
            cancelButton.setEnabled(true);
            setVisible(true);
        });
    }
    
    @Override
    public void updateProgress(int percentage) {
        SwingUtilities.invokeLater(() -> {
            if (!completed && !cancelled) {
                progressBar.setValue(Math.max(0, Math.min(100, percentage)));
                progressBar.setString(percentage + "%");
                
                if (percentage >= 100) {
                    Timer timer = new Timer(500, e -> {
                        if (!cancelled) {
                            setCompleted("Operation completed successfully!");
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            }
        });
    }
    
    @Override
    public void updateMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            if (!completed && !cancelled) {
                messageLabel.setText(message);
            }
        });
    }
    
    @Override
    public void updateProgress(int percentage, String message) {
        SwingUtilities.invokeLater(() -> {
            updateProgress(percentage);
            updateMessage(message);
        });
    }
    
    @Override
    public void hideProgress() {
        SwingUtilities.invokeLater(() -> {
            setVisible(false);
            dispose();
        });
    }
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public void setCompleted(String message) {
        SwingUtilities.invokeLater(() -> {
            completed = true;
            messageLabel.setText(message);
            progressBar.setValue(100);
            progressBar.setString("Complete");
            progressBar.setForeground(COLOR_PRIMARY);
            cancelButton.setText("Close");
            cancelButton.setBackground(COLOR_PRIMARY);
            
            Timer timer = new Timer(1500, e -> hideProgress());
            timer.setRepeats(false);
            timer.start();
        });
    }
    
    @Override
    public void setError(String errorMessage) {
        SwingUtilities.invokeLater(() -> {
            completed = true;
            messageLabel.setText(errorMessage);
            progressBar.setString("Error");
            progressBar.setForeground(new Color(244, 67, 54));
            cancelButton.setText("Close");
            cancelButton.setBackground(new Color(244, 67, 54));
        });
    }
    
    /**
     * Simulate a background task with progress updates
     * Useful for testing and demonstrating the progress dialog
     */
    public void simulateTask(String taskName, int durationMs) {
        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                int steps = 10;
                int stepDelay = durationMs / steps;
                
                for (int i = 0; i <= steps; i++) {
                    if (isCancelled()) {
                        return null;
                    }
                    
                    int progress = (i * 100) / steps;
                    String message = taskName + " (" + i + "/" + steps + ")";
                    
                    publish(progress);
                    updateMessage(message);
                    
                    Thread.sleep(stepDelay);
                }
                return null;
            }
            
            @Override
            protected void process(java.util.List<Integer> chunks) {
                if (!chunks.isEmpty()) {
                    updateProgress(chunks.get(chunks.size() - 1));
                }
            }
            
            @Override
            protected void done() {
                if (!ProgressDialog.this.isCancelled()) {
                    setCompleted(taskName + " completed successfully!");
                }
            }
        };
        
        worker.execute();
    }
} 