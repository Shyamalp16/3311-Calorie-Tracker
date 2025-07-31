package models;

import java.sql.Timestamp;

public class SwapHistory {
    private int historyId;
    private int userId;
    private int mealId;
    private int originalFoodId;
    private int swappedFoodId;
    private double quantity;
    private String unit;
    private String swapReason;
    private Timestamp appliedAt;
    private boolean isActive; 

    public SwapHistory(int historyId, int userId, int mealId, int originalFoodId, 
                      int swappedFoodId, double quantity, String unit, String swapReason, 
                      Timestamp appliedAt, boolean isActive) {
        this.historyId = historyId;
        this.userId = userId;
        this.mealId = mealId;
        this.originalFoodId = originalFoodId;
        this.swappedFoodId = swappedFoodId;
        this.quantity = quantity;
        this.unit = unit;
        this.swapReason = swapReason;
        this.appliedAt = appliedAt;
        this.isActive = isActive;
    }

    public SwapHistory(int userId, int mealId, int originalFoodId, int swappedFoodId, 
                      double quantity, String unit, String swapReason, boolean isActive) {
        this(0, userId, mealId, originalFoodId, swappedFoodId, quantity, unit, 
             swapReason, new Timestamp(System.currentTimeMillis()), isActive);
    }

    public int getHistoryId() { return historyId; }
    public int getUserId() { return userId; }
    public int getMealId() { return mealId; }
    public int getOriginalFoodId() { return originalFoodId; }
    public int getSwappedFoodId() { return swappedFoodId; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public String getSwapReason() { return swapReason; }
    public Timestamp getAppliedAt() { return appliedAt; }
    public boolean isActive() { return isActive; }

    public void setHistoryId(int historyId) { this.historyId = historyId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setMealId(int mealId) { this.mealId = mealId; }
    public void setOriginalFoodId(int originalFoodId) { this.originalFoodId = originalFoodId; }
    public void setSwappedFoodId(int swappedFoodId) { this.swappedFoodId = swappedFoodId; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setSwapReason(String swapReason) { this.swapReason = swapReason; }
    public void setAppliedAt(Timestamp appliedAt) { this.appliedAt = appliedAt; }
    public void setActive(boolean active) { isActive = active; }
} 