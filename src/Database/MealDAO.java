package Database;

import models.Meal;
import models.MealItem;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MealDAO extends AbstractDAO<Meal> {

    public int saveMeal(Meal meal) {
        String sql = "INSERT INTO meals (user_id, meal_type, meal_date, created_at, total_calories, total_protein, total_carbs, total_fat, total_fiber, total_sodium, total_sugars, total_saturated_fats, total_iron, total_calcium, total_vitaminA, total_vitaminB, total_vitaminC, total_vitaminD) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (var conn = DatabaseConnector.getConnection();
             var pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setParameters(pstmt, meal.getUserId(), meal.getMealType(), new java.sql.Date(meal.getMealDate().getTime()), 
                        meal.getCreatedAt(), meal.getTotalCalories(), meal.getTotalProtein(), meal.getTotalCarbs(), 
                        meal.getTotalFats(), meal.getTotalFiber(), meal.getTotalSodium(), meal.getTotalSugars(), 
                        meal.getTotalSaturatedFats(), meal.getTotalIron(), meal.getTotalCalcium(),
                        meal.getTotalVitaminA(), meal.getTotalVitaminB(), meal.getTotalVitaminC(), meal.getTotalVitaminD());
            
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            handleSQLException(e, sql);
        }
        return -1;
    }

    public void saveMealItems(int mealId, List<MealItem> mealItems) {
        String sql = "INSERT INTO meal_items (meal_id, food_id, quantity, unit, calories, protein, carbs, fats, fiber, sodium, sugars, saturated_fats, iron, calcium, vitaminA, vitaminB, vitaminC, vitaminD) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (var conn = DatabaseConnector.getConnection();
             var pstmt = conn.prepareStatement(sql)) {
            for (MealItem item : mealItems) {
                setParameters(pstmt, mealId, item.getFoodId(), item.getQuantity(), item.getUnit(), item.getCalories(), 
                            item.getProtein(), item.getCarbs(), item.getFats(), item.getFiber(), item.getSodium(), 
                            item.getSugars(), item.getSaturatedFats(), item.getIron(), item.getCalcium(),
                            item.getVitaminA(), item.getVitaminB(), item.getVitaminC(), item.getVitaminD());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            handleSQLException(e, sql);
        }
    }

    public List<Meal> getMealsForUserAndDate(int userId, Date date) {
        String sql = "SELECT * FROM meals WHERE user_id = ? AND meal_date = ?";
        return findMany(sql, userId, new java.sql.Date(date.getTime()));
    }

    public List<MealItem> getMealItemsByMealId(int mealId) {
        String sql = "SELECT * FROM meal_items WHERE meal_id = ?";
        List<MealItem> mealItems = new java.util.ArrayList<>();
        try (var conn = DatabaseConnector.getConnection();
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, mealId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                mealItems.add(new MealItem(
                    rs.getInt("item_id"),
                    rs.getInt("meal_id"),
                    rs.getInt("food_id"),
                    rs.getDouble("quantity"),
                    rs.getString("unit"),
                    rs.getDouble("calories"),
                    rs.getDouble("protein"),
                    rs.getDouble("carbs"),
                    rs.getDouble("fats"),
                    rs.getDouble("fiber"),
                    rs.getDouble("sodium"),
                    rs.getDouble("sugars"),
                    rs.getDouble("saturated_fats"),
                    rs.getDouble("iron"),
                    rs.getDouble("calcium"),
                    rs.getDouble("vitaminA"),
                    rs.getDouble("vitaminB"),
                    rs.getDouble("vitaminC"),
                    rs.getDouble("vitaminD")
                ));
            }
        } catch (SQLException e) {
            handleSQLException(e, sql);
        }
        return mealItems;
    }

    public boolean updateMealItem(int mealId, int originalFoodId, int newFoodId, double newQuantity, String newUnit) {
        FoodDAO foodDAO = new FoodDAO();
        models.Food newFood = foodDAO.getFoodById(newFoodId).orElse(null);

        if (newFood == null) {
            System.err.println("Error: Could not retrieve details for the new food item.");
            return false;
        }

        Map<String, Integer> measures = foodDAO.getMeasuresForFood(newFoodId);
        int measureId = measures.getOrDefault(newUnit, -1);

        double conversionFactor = 1.0;
        if (measureId != -1) {
            conversionFactor = foodDAO.getConversionFactor(newFoodId, measureId);
        } else if (newUnit.equals("g")) {
            conversionFactor = 1.0 / 100.0;
        }

        double calories = newFood.getCalories() * newQuantity * conversionFactor;
        double protein = newFood.getProtein() * newQuantity * conversionFactor;
        double carbs = newFood.getCarbs() * newQuantity * conversionFactor;
        double fats = newFood.getFats() * newQuantity * conversionFactor;
        double fiber = newFood.getFiber() * newQuantity * conversionFactor;
        double sodium = newFood.getSodium() * newQuantity * conversionFactor;
        double sugars = newFood.getSugars() * newQuantity * conversionFactor;
        double saturated_fats = newFood.getSaturatedFats() * newQuantity * conversionFactor;
        double iron = newFood.getIron() * newQuantity * conversionFactor;
        double calcium = newFood.getCalcium() * newQuantity * conversionFactor;
        double vitaminA = newFood.getVitaminA() * newQuantity * conversionFactor;
        double vitaminB = newFood.getVitaminB() * newQuantity * conversionFactor;
        double vitaminC = newFood.getVitaminC() * newQuantity * conversionFactor;
        double vitaminD = newFood.getVitaminD() * newQuantity * conversionFactor;

        String sql = "UPDATE meal_items SET food_id = ?, quantity = ?, unit = ?, calories = ?, protein = ?, carbs = ?, fats = ?, fiber = ?, sodium = ?, sugars = ?, saturated_fats = ?, iron = ?, calcium = ?, vitaminA = ?, vitaminB = ?, vitaminC = ?, vitaminD = ? WHERE meal_id = ? AND food_id = ?";
        int rowsAffected = update(sql, newFoodId, newQuantity, newUnit, calories, protein, carbs, fats, fiber, sodium, sugars, saturated_fats, iron, calcium, vitaminA, vitaminB, vitaminC, vitaminD, mealId, originalFoodId);
        
        if (rowsAffected > 0) {
            updateMealTotals(mealId);
            return true;
        }
        return false;
    }

    public void updateMealTotals(int mealId) {
        String sql = "UPDATE meals SET total_calories = (SELECT COALESCE(SUM(calories), 0) FROM meal_items WHERE meal_id = ?), total_protein = (SELECT COALESCE(SUM(protein), 0) FROM meal_items WHERE meal_id = ?), total_carbs = (SELECT COALESCE(SUM(carbs), 0) FROM meal_items WHERE meal_id = ?), total_fat = (SELECT COALESCE(SUM(fats), 0) FROM meal_items WHERE meal_id = ?), total_fiber = (SELECT COALESCE(SUM(fiber), 0) FROM meal_items WHERE meal_id = ?), total_sodium = (SELECT COALESCE(SUM(sodium), 0) FROM meal_items WHERE meal_id = ?), total_sugars = (SELECT COALESCE(SUM(sugars), 0) FROM meal_items WHERE meal_id = ?), total_saturated_fats = (SELECT COALESCE(SUM(saturated_fats), 0) FROM meal_items WHERE meal_id = ?), total_iron = (SELECT COALESCE(SUM(iron), 0) FROM meal_items WHERE meal_id = ?), total_calcium = (SELECT COALESCE(SUM(calcium), 0) FROM meal_items WHERE meal_id = ?), total_vitaminA = (SELECT COALESCE(SUM(vitaminA), 0) FROM meal_items WHERE meal_id = ?), total_vitaminB = (SELECT COALESCE(SUM(vitaminB), 0) FROM meal_items WHERE meal_id = ?), total_vitaminC = (SELECT COALESCE(SUM(vitaminC), 0) FROM meal_items WHERE meal_id = ?), total_vitaminD = (SELECT COALESCE(SUM(vitaminD), 0) FROM meal_items WHERE meal_id = ?) WHERE meal_id = ?";
        update(sql, mealId, mealId, mealId, mealId, mealId, mealId, mealId, mealId, mealId, mealId, mealId, mealId, mealId, mealId, mealId);
    }


    public List<Meal> getMealsInDateRange(int userId, Date startDate, Date endDate) {
        String sql = "SELECT * FROM meals WHERE user_id = ? AND meal_date BETWEEN ? AND ? ORDER BY meal_date DESC, created_at DESC";
        return findMany(sql, userId, new java.sql.Date(startDate.getTime()), new java.sql.Date(endDate.getTime()));
    }

    public boolean hasMealItemWithFood(int mealId, int foodId) {
        String sql = "SELECT COUNT(*) FROM meal_items WHERE meal_id = ? AND food_id = ?";
        try (var conn = DatabaseConnector.getConnection(); var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, mealId);
            pstmt.setInt(2, foodId);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            handleSQLException(e, sql);
        }
        return false;
    }

    @Override
    protected void setParameters(PreparedStatement pstmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
    }

    @Override
    protected Meal parseResultSet(ResultSet rs) throws SQLException {
        return new Meal(
            rs.getInt("meal_id"),
            rs.getInt("user_id"),
            rs.getString("meal_type"),
            rs.getDate("meal_date"),
            rs.getTimestamp("created_at"),
            rs.getDouble("total_calories"),
            rs.getDouble("total_protein"),
            rs.getDouble("total_carbs"),
            rs.getDouble("total_fat"),
            rs.getDouble("total_fiber"),
            rs.getDouble("total_sodium"),
            rs.getDouble("total_sugars"),
            rs.getDouble("total_saturated_fats"),
            rs.getDouble("total_iron"),
            rs.getDouble("total_calcium"),
            rs.getDouble("total_vitaminA"),
            rs.getDouble("total_vitaminB"),
            rs.getDouble("total_vitaminC"),
            rs.getDouble("total_vitaminD")
        );
    }
}
