package models;

import java.sql.Timestamp;
import java.util.Date;

public class Meal {
    private int mealId;
    private int userId;
    private String mealType;
    private Date mealDate;
    private Timestamp createdAt;
    private double totalCalories;
    private double totalProtein;
    private double totalCarbs;
    private double totalFats;
    private double totalFiber;
    private double totalSodium;
    private double totalSugars;
    private double totalSaturatedFats;
    private double totalIron;
    private double totalCalcium;
    private double totalVitaminA;
    private double totalVitaminB;
    private double totalVitaminC;
    private double totalVitaminD;

    public Meal(int mealId, int userId, String mealType, Date mealDate, Timestamp createdAt) {
        this.mealId = mealId;
        this.userId = userId;
        this.mealType = mealType;
        this.mealDate = mealDate;
        this.createdAt = createdAt;
    }

    public Meal(int mealId, int userId, String mealType, Date mealDate, Timestamp createdAt, double totalCalories, double totalProtein, double totalCarbs, double totalFats, double totalFiber, double totalSodium, double totalSugars, double totalSaturatedFats, double totalIron, double totalCalcium, double totalVitaminA, double totalVitaminB, double totalVitaminC, double totalVitaminD) {
        this.mealId = mealId;
        this.userId = userId;
        this.mealType = mealType;
        this.mealDate = mealDate;
        this.createdAt = createdAt;
        this.totalCalories = totalCalories;
        this.totalProtein = totalProtein;
        this.totalCarbs = totalCarbs;
        this.totalFats = totalFats;
        this.totalFiber = totalFiber;
        this.totalSodium = totalSodium;
        this.totalSugars = totalSugars;
        this.totalSaturatedFats = totalSaturatedFats;
        this.totalIron = totalIron;
        this.totalCalcium = totalCalcium;
        this.totalVitaminA = totalVitaminA;
        this.totalVitaminB = totalVitaminB;
        this.totalVitaminC = totalVitaminC;
        this.totalVitaminD = totalVitaminD;
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

    public double getTotalCalories() {
        return totalCalories;
    }

    public double getTotalProtein() {
        return totalProtein;
    }

    public double getTotalCarbs() {
        return totalCarbs;
    }

    public double getTotalFats() {
        return totalFats;
    }

    public double getTotalFiber() {
        return totalFiber;
    }

    public double getTotalSodium() {
        return totalSodium;
    }

    public double getTotalSugars() {
        return totalSugars;
    }

    public double getTotalSaturatedFats() {
        return totalSaturatedFats;
    }

    public double getTotalIron() {
        return totalIron;
    }

    public double getTotalCalcium() {
        return totalCalcium;
    }

    public double getTotalVitaminA() {
        return totalVitaminA;
    }

    public double getTotalVitaminB() {
        return totalVitaminB;
    }

    public double getTotalVitaminC() {
        return totalVitaminC;
    }

    public double getTotalVitaminD() {
        return totalVitaminD;
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

    public void setTotalCalories(double totalCalories) {
        this.totalCalories = totalCalories;
    }

    public void setTotalProtein(double totalProtein) {
        this.totalProtein = totalProtein;
    }

    public void setTotalCarbs(double totalCarbs) {
        this.totalCarbs = totalCarbs;
    }

    public void setTotalFats(double totalFats) {
        this.totalFats = totalFats;
    }

    public void setTotalFiber(double totalFiber) {
        this.totalFiber = totalFiber;
    }

    public void setTotalSodium(double totalSodium) {
        this.totalSodium = totalSodium;
    }

    public void setTotalSugars(double totalSugars) {
        this.totalSugars = totalSugars;
    }

    public void setTotalSaturatedFats(double totalSaturatedFats) {
        this.totalSaturatedFats = totalSaturatedFats;
    }

    public void setTotalIron(double totalIron) {
        this.totalIron = totalIron;
    }

    public void setTotalCalcium(double totalCalcium) {
        this.totalCalcium = totalCalcium;
    }

    public void setTotalVitaminA(double totalVitaminA) {
        this.totalVitaminA = totalVitaminA;
    }

    public void setTotalVitaminB(double totalVitaminB) {
        this.totalVitaminB = totalVitaminB;
    }

    public void setTotalVitaminC(double totalVitaminC) {
        this.totalVitaminC = totalVitaminC;
    }

    public void setTotalVitaminD(double totalVitaminD) {
        this.totalVitaminD = totalVitaminD;
    }
}