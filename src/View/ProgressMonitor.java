package View;

/**
 * Interface for monitoring progress of long-running operations
 * Provides abstraction for progress indicators and loading bars
 */
public interface ProgressMonitor {
    
    void showProgress(String title, String message);
    
    void updateProgress(int percentage);
    
    void updateMessage(String message);
    
    void updateProgress(int percentage, String message);
    
    void hideProgress();
    
    boolean isCancelled();
    
    void setCompleted(String message);
    
    void setError(String errorMessage);
} 