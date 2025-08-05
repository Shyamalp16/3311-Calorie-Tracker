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

    public Meal() {}

    private Meal(Builder builder) {
        this.mealId = builder.mealId;
        this.userId = builder.userId;
        this.mealType = builder.mealType;
        this.mealDate = builder.mealDate;
        this.createdAt = builder.createdAt;
        this.totalCalories = builder.totalCalories;
        this.totalProtein = builder.totalProtein;
        this.totalCarbs = builder.totalCarbs;
        this.totalFats = builder.totalFats;
        this.totalFiber = builder.totalFiber;
        this.totalSodium = builder.totalSodium;
        this.totalSugars = builder.totalSugars;
        this.totalSaturatedFats = builder.totalSaturatedFats;
        this.totalIron = builder.totalIron;
        this.totalCalcium = builder.totalCalcium;
        this.totalVitaminA = builder.totalVitaminA;
        this.totalVitaminB = builder.totalVitaminB;
        this.totalVitaminC = builder.totalVitaminC;
        this.totalVitaminD = builder.totalVitaminD;
    }

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

    public void setTotalSugars(double sugars) {
        this.totalSugars = sugars;
    }

    public void setTotalSaturatedFats(double saturatedFats) {
        this.totalSaturatedFats = saturatedFats;
    }

    public void setTotalIron(double iron) {
        this.totalIron = iron;
    }

    public void setTotalCalcium(double calcium) {
        this.totalCalcium = calcium;
    }

    public void setTotalVitaminA(double vitaminA) {
        this.totalVitaminA = vitaminA;
    }

    public void setTotalVitaminB(double vitaminB) {
        this.totalVitaminB = vitaminB;
    }

    public void setTotalVitaminC(double vitaminC) {
        this.totalVitaminC = vitaminC;
    }

    public void setTotalVitaminD(double vitaminD) {
        this.totalVitaminD = vitaminD;
    }

    public static class Builder {
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

        public Builder mealId(int mealId) {
            this.mealId = mealId;
            return this;
        }

        public Builder userId(int userId) {
            this.userId = userId;
            return this;
        }

        public Builder mealType(String mealType) {
            this.mealType = mealType;
            return this;
        }

        public Builder mealDate(Date mealDate) {
            this.mealDate = mealDate;
            return this;
        }

        public Builder createdAt(Timestamp createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder totalCalories(double totalCalories) {
            this.totalCalories = totalCalories;
            return this;
        }

        public Builder totalProtein(double totalProtein) {
            this.totalProtein = totalProtein;
            return this;
        }

        public Builder totalCarbs(double totalCarbs) {
            this.totalCarbs = totalCarbs;
            return this;
        }

        public Builder totalFats(double totalFats) {
            this.totalFats = totalFats;
            return this;
        }

        public Builder totalFiber(double totalFiber) {
            this.totalFiber = totalFiber;
            return this;
        }

        public Builder totalSodium(double totalSodium) {
            this.totalSodium = totalSodium;
            return this;
        }

        public Builder totalSugars(double totalSugars) {
            this.totalSugars = totalSugars;
            return this;
        }

        public Builder totalSaturatedFats(double totalSaturatedFats) {
            this.totalSaturatedFats = totalSaturatedFats;
            return this;
        }

        public Builder totalIron(double totalIron) {
            this.totalIron = totalIron;
            return this;
        }

        public Builder totalCalcium(double totalCalcium) {
            this.totalCalcium = totalCalcium;
            return this;
        }

        public Builder totalVitaminA(double totalVitaminA) {
            this.totalVitaminA = totalVitaminA;
            return this;
        }

        public Builder totalVitaminB(double totalVitaminB) {
            this.totalVitaminB = totalVitaminB;
            return this;
        }

        public Builder totalVitaminC(double totalVitaminC) {
            this.totalVitaminC = totalVitaminC;
            return this;
        }

        public Builder totalVitaminD(double totalVitaminD) {
            this.totalVitaminD = totalVitaminD;
            return this;
        }

        public Meal build() {
            return new Meal(this);
        }
    }
}
