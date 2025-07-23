//user data access object
package Database;

import java.sql.*;
import java.util.*;

/**
 * Data Access Object for nutrient-related database operations
 * Handles queries to nutrient_name, nutrient_amount and related tables
 */
public class NutrientDAO {
    
    /**
     * Gets all available nutrients from the nutrient_name table
     * @return List of nutrient names and their details
     */
    public List<Map<String, Object>> getAllNutrients() {
        List<Map<String, Object>> nutrients = new ArrayList<>();
        String sql = "SELECT NutrientNameID, NutrientCode, NutrientSymbol, Unit, NutrientName, NutrientNameF, Tagn FROM nutrient_name ORDER BY NutrientName";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> nutrient = new HashMap<>();
                nutrient.put("id", rs.getInt("NutrientNameID"));
                nutrient.put("code", rs.getInt("NutrientCode"));
                nutrient.put("symbol", rs.getString("NutrientSymbol"));
                nutrient.put("unit", rs.getString("Unit"));
                nutrient.put("name", rs.getString("NutrientName"));
                nutrient.put("nameFrench", rs.getString("NutrientNameF"));
                nutrient.put("tag", rs.getString("Tagn"));
                nutrients.add(nutrient);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching nutrients: " + e.getMessage());
            e.printStackTrace();
        }
        
        return nutrients;
    }
    
    /**
     * Gets nutrient amounts for a specific food item
     * @param foodId The food item ID
     * @return Map of nutrient name to amount
     */
    public Map<String, Double> getFoodNutrients(int foodId) {
        Map<String, Double> nutrients = new HashMap<>();
        String sql = """
            SELECT nn.NutrientName, nn.Unit, na.NutrientValue 
            FROM nutrient_amount na
            JOIN nutrient_name nn ON na.NutrientNameID = nn.NutrientNameID
            WHERE na.FoodID = ?
            """;
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, foodId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String nutrientName = rs.getString("NutrientName");
                double value = rs.getDouble("NutrientValue");
                nutrients.put(nutrientName, value);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching food nutrients: " + e.getMessage());
            e.printStackTrace();
        }
        
        return nutrients;
    }
    
    /**
     * Gets specific nutrient value for a food item
     * @param foodId The food item ID
     * @param nutrientName The nutrient name (e.g., "PROTEIN", "FAT", "ENERGY (KILOCALORIES)")
     * @return The nutrient value, or 0.0 if not found
     */
    public double getNutrientValue(int foodId, String nutrientName) {
        String sql = """
            SELECT na.NutrientValue 
            FROM nutrient_amount na
            JOIN nutrient_name nn ON na.NutrientNameID = nn.NutrientNameID
            WHERE na.FoodID = ? AND nn.NutrientName = ?
            """;
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, foodId);
            pstmt.setString(2, nutrientName);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("NutrientValue");
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching nutrient value: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0.0;
    }
    
    /**
     * Gets specific nutrient value by nutrient symbol
     * @param foodId The food item ID
     * @param nutrientSymbol The nutrient symbol (e.g., "PROT", "FAT", "KCAL")
     * @return The nutrient value, or 0.0 if not found
     */
    public double getNutrientValueBySymbol(int foodId, String nutrientSymbol) {
        String sql = """
            SELECT na.NutrientValue 
            FROM nutrient_amount na
            JOIN nutrient_name nn ON na.NutrientNameID = nn.NutrientNameID
            WHERE na.FoodID = ? AND nn.NutrientSymbol = ?
            """;
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, foodId);
            pstmt.setString(2, nutrientSymbol);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("NutrientValue");
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching nutrient value by symbol: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0.0;
    }
    
    /**
     * Searches for nutrients by name pattern
     * @param searchTerm The search term to match against nutrient names
     * @return List of matching nutrients
     */
    public List<Map<String, Object>> searchNutrients(String searchTerm) {
        List<Map<String, Object>> nutrients = new ArrayList<>();
        String sql = """
            SELECT NutrientNameID, NutrientCode, NutrientSymbol, Unit, NutrientName, NutrientNameF, Tagn 
            FROM nutrient_name 
            WHERE NutrientName LIKE ? OR NutrientSymbol LIKE ?
            ORDER BY NutrientName
            """;
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> nutrient = new HashMap<>();
                nutrient.put("id", rs.getInt("NutrientNameID"));
                nutrient.put("code", rs.getInt("NutrientCode"));
                nutrient.put("symbol", rs.getString("NutrientSymbol"));
                nutrient.put("unit", rs.getString("Unit"));
                nutrient.put("name", rs.getString("NutrientName"));
                nutrient.put("nameFrench", rs.getString("NutrientNameF"));
                nutrient.put("tag", rs.getString("Tagn"));
                nutrients.add(nutrient);
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching nutrients: " + e.getMessage());
            e.printStackTrace();
        }
        
        return nutrients;
    }
    
    /**
     * Gets foods that are high in a specific nutrient
     * @param nutrientName The nutrient name to search for
     * @param minimumValue The minimum value threshold
     * @return List of food IDs and their nutrient values
     */
    public List<Map<String, Object>> getFoodsHighInNutrient(String nutrientName, double minimumValue) {
        List<Map<String, Object>> foods = new ArrayList<>();
        String sql = """
            SELECT fn.FoodID, fn.FoodDescription, na.NutrientValue, nn.Unit
            FROM food_name fn
            JOIN nutrient_amount na ON fn.FoodID = na.FoodID
            JOIN nutrient_name nn ON na.NutrientNameID = nn.NutrientNameID
            WHERE nn.NutrientName = ? AND na.NutrientValue >= ?
            ORDER BY na.NutrientValue DESC
            LIMIT 50
            """;
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nutrientName);
            pstmt.setDouble(2, minimumValue);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> food = new HashMap<>();
                food.put("foodId", rs.getInt("FoodID"));
                food.put("foodDescription", rs.getString("FoodDescription"));
                food.put("nutrientValue", rs.getDouble("NutrientValue"));
                food.put("unit", rs.getString("Unit"));
                foods.add(food);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching foods high in nutrient: " + e.getMessage());
            e.printStackTrace();
        }
        
        return foods;
    }
    
    /**
     * Gets comprehensive nutrient profile for multiple foods
     * @param foodIds List of food IDs to get nutrients for
     * @return Map of food ID to nutrient profile
     */
    public Map<Integer, Map<String, Double>> getMultipleFoodNutrients(List<Integer> foodIds) {
        Map<Integer, Map<String, Double>> foodNutrients = new HashMap<>();
        
        if (foodIds.isEmpty()) {
            return foodNutrients;
        }
        
        // Create placeholders for the IN clause
        String placeholders = String.join(",", Collections.nCopies(foodIds.size(), "?"));
        String sql = String.format("""
            SELECT na.FoodID, nn.NutrientName, na.NutrientValue 
            FROM nutrient_amount na
            JOIN nutrient_name nn ON na.NutrientNameID = nn.NutrientNameID
            WHERE na.FoodID IN (%s)
            """, placeholders);
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Set parameters
            for (int i = 0; i < foodIds.size(); i++) {
                pstmt.setInt(i + 1, foodIds.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int foodId = rs.getInt("FoodID");
                String nutrientName = rs.getString("NutrientName");
                double value = rs.getDouble("NutrientValue");
                
                foodNutrients.computeIfAbsent(foodId, k -> new HashMap<>()).put(nutrientName, value);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching multiple food nutrients: " + e.getMessage());
            e.printStackTrace();
        }
        
        return foodNutrients;
    }
    
    /**
     * Gets the recommended daily values for nutrients (if available in database)
     * This would typically come from a separate recommendations table
     * @return Map of nutrient name to recommended daily value
     */
    public Map<String, Double> getRecommendedDailyValues() {
        // Since this table might not exist in the current schema,
        // return some common recommended values
        Map<String, Double> rdv = new HashMap<>();
        rdv.put("ENERGY (KILOCALORIES)", 2000.0);
        rdv.put("PROTEIN", 50.0);
        rdv.put("CARBOHYDRATE, TOTAL (BY DIFFERENCE)", 300.0);
        rdv.put("FAT (TOTAL LIPIDS)", 65.0);
        rdv.put("FIBER, TOTAL DIETARY", 25.0);
        rdv.put("SODIUM", 2300.0);
        rdv.put("CALCIUM", 1000.0);
        rdv.put("IRON", 18.0);
        return rdv;
    }
}


