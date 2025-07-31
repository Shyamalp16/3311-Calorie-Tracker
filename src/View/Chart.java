package View;

import org.jfree.chart.ChartPanel;
import java.util.Map;

/**
 * Product Interface for Factory Method Pattern
 * Defines the common interface that all concrete chart products must implement
 */
public interface Chart {
    ChartPanel getChartPanel();
    String getTitle();
    void setTitle(String title);
    void updateData(Map<String, Double> data);
    ChartType getType();
    
    enum ChartType {
        PIE("Pie Chart"),
        LINE("Line Chart"), 
        BAR("Bar Chart");
        
        private final String displayName;
        
        ChartType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
} 