package models;

import java.sql.Timestamp;
import java.util.Date;

public class Meal {
    private int mealId;
    private int userId;
    private String mealType;
    private Date mealDate;
    private Timestamp createdAt;

    public Meal(int mealId, int userId, String mealType, Date mealDate, Timestamp createdAt) {
        this.mealId = mealId;
        this.userId = userId;
        this.mealType = mealType;
        this.mealDate = mealDate;
        this.createdAt = createdAt;
    }

    // Getters
    public int getMealId() {
        return mealId;
    }

    public int getUserId() {
        return userId;
    }

    public String getMealType() {
        return mealType;
    }

    public Date getMealDate() {
        return mealDate;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public void setMealDate(Date mealDate) {
        this.mealDate = mealDate;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}