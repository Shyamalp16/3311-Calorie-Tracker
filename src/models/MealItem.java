package models;

public class MealItem {
    private int itemId;
    private int mealId;
    private int foodId;
    private double quantity;
    private String unit;
    private double calories;
    private double protein;
    private double carbs;
    private double fats;
    private double fiber;
    private double sodium;
    private double sugars;
    private double saturatedFats;
    private double iron;
    private double calcium;
    private double vitaminA;
    private double vitaminB;
    private double vitaminC;
    private double vitaminD;

    public MealItem() {}

    private MealItem(Builder builder) {
        this.itemId = builder.itemId;
        this.mealId = builder.mealId;
        this.foodId = builder.foodId;
        this.quantity = builder.quantity;
        this.unit = builder.unit;
        this.calories = builder.calories;
        this.protein = builder.protein;
        this.carbs = builder.carbs;
        this.fats = builder.fats;
        this.fiber = builder.fiber;
        this.sodium = builder.sodium;
        this.sugars = builder.sugars;
        this.saturatedFats = builder.saturatedFats;
        this.iron = builder.iron;
        this.calcium = builder.calcium;
        this.vitaminA = builder.vitaminA;
        this.vitaminB = builder.vitaminB;
        this.vitaminC = builder.vitaminC;
        this.vitaminD = builder.vitaminD;
    }

    public int getItemId() {
        return itemId;
    }

    public int getMealId() {
        return mealId;
    }

    public int getFoodId() {
        return foodId;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public double getCalories() {
        return calories;
    }

    public double getProtein() {
        return protein;
    }

    public double getCarbs() {
        return carbs;
    }

    public double getFats() {
        return fats;
    }

    public double getFiber() {
        return fiber;
    }

    public double getSodium() {
        return sodium;
    }

    public double getSugars() {
        return sugars;
    }

    public double getSaturatedFats() {
        return saturatedFats;
    }

    public double getIron() {
        return iron;
    }

    public double getCalcium() {
        return calcium;
    }

    public double getVitaminA() {
        return vitaminA;
    }

    public double getVitaminB() {
        return vitaminB;
    }

    public double getVitaminC() {
        return vitaminC;
    }

    public double getVitaminD() {
        return vitaminD;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public void setFats(double fats) {
        this.fats = fats;
    }

    public void setFiber(double fiber) {
        this.fiber = fiber;
    }

    public void setSodium(double sodium) {
        this.sodium = sodium;
    }

    public void setSugars(double sugars) {
        this.sugars = sugars;
    }

    public void setSaturatedFats(double saturatedFats) {
        this.saturatedFats = saturatedFats;
    }

    public void setIron(double iron) {
        this.iron = iron;
    }

    public void setCalcium(double calcium) {
        this.calcium = calcium;
    }

    public void setVitaminA(double vitaminA) {
        this.vitaminA = vitaminA;
    }

    public void setVitaminB(double vitaminB) {
        this.vitaminB = vitaminB;
    }

    public void setVitaminC(double vitaminC) {
        this.vitaminC = vitaminC;
    }

    public void setVitaminD(double vitaminD) {
        this.vitaminD = vitaminD;
    }

    public static class Builder {
        private int itemId;
        private int mealId;
        private int foodId;
        private double quantity;
        private String unit;
        private double calories;
        private double protein;
        private double carbs;
        private double fats;
        private double fiber;
        private double sodium;
        private double sugars;
        private double saturatedFats;
        private double iron;
        private double calcium;
        private double vitaminA;
        private double vitaminB;
        private double vitaminC;
        private double vitaminD;

        public Builder itemId(int itemId) {
            this.itemId = itemId;
            return this;
        }

        public Builder mealId(int mealId) {
            this.mealId = mealId;
            return this;
        }

        public Builder foodId(int foodId) {
            this.foodId = foodId;
            return this;
        }

        public Builder quantity(double quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder unit(String unit) {
            this.unit = unit;
            return this;
        }

        public Builder calories(double calories) {
            this.calories = calories;
            return this;
        }

        public Builder protein(double protein) {
            this.protein = protein;
            return this;
        }

        public Builder carbs(double carbs) {
            this.carbs = carbs;
            return this;
        }

        public Builder fats(double fats) {
            this.fats = fats;
            return this;
        }

        public Builder fiber(double fiber) {
            this.fiber = fiber;
            return this;
        }

        public Builder sodium(double sodium) {
            this.sodium = sodium;
            return this;
        }

        public Builder sugars(double sugars) {
            this.sugars = sugars;
            return this;
        }

        public Builder saturatedFats(double saturatedFats) {
            this.saturatedFats = saturatedFats;
            return this;
        }

        public Builder iron(double iron) {
            this.iron = iron;
            return this;
        }

        public Builder calcium(double calcium) {
            this.calcium = calcium;
            return this;
        }

        public Builder vitaminA(double vitaminA) {
            this.vitaminA = vitaminA;
            return this;
        }

        public Builder vitaminB(double vitaminB) {
            this.vitaminB = vitaminB;
            return this;
        }

        public Builder vitaminC(double vitaminC) {
            this.vitaminC = vitaminC;
            return this;
        }

        public Builder vitaminD(double vitaminD) {
            this.vitaminD = vitaminD;
            return this;
        }

        public MealItem build() {
            return new MealItem(this);
        }
    }
}
