package Database;

import models.SwapHistory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

public class SwapHistoryDAO extends AbstractDAO<SwapHistory> {

    public int saveSwapHistory(SwapHistory swapHistory) {
        String sql = """
            INSERT INTO swap_history (user_id, meal_id, original_food_id, swapped_food_id, 
                                    quantity, unit, swap_reason, applied_at, is_active) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (var conn = DatabaseConnector.getConnection();
             var pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setParameters(pstmt, swapHistory.getUserId(), swapHistory.getMealId(), swapHistory.getOriginalFoodId(), 
                        swapHistory.getSwappedFoodId(), swapHistory.getQuantity(), swapHistory.getUnit(), 
                        swapHistory.getSwapReason(), swapHistory.getAppliedAt(), swapHistory.isActive());
            
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

    public List<SwapHistory> getSwapHistoryForUser(int userId) {
        String sql = "SELECT * FROM swap_history WHERE user_id = ? ORDER BY applied_at DESC";
        return findMany(sql, userId);
    }

    public List<SwapHistory> getActiveSwapsForMeal(int mealId) {
        String sql = "SELECT * FROM swap_history WHERE meal_id = ? AND is_active = true";
        return findMany(sql, mealId);
    }

    public void deactivateSwap(int historyId) {
        String sql = "UPDATE swap_history SET is_active = false WHERE history_id = ?";
        update(sql, historyId);
    }

    public List<SwapHistory> getSwapHistoryForPeriod(int userId, Date startDate, Date endDate) {
        String sql = """
            SELECT sh.* 
            FROM swap_history sh
            INNER JOIN meals m ON sh.meal_id = m.meal_id
            WHERE sh.user_id = ? AND m.meal_date BETWEEN ? AND ?
            ORDER BY sh.applied_at DESC
            """;
        return findMany(sql, userId, new java.sql.Date(startDate.getTime()), new java.sql.Date(endDate.getTime()));
    }

    public void createSwapHistoryTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS swap_history (
                history_id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL,
                meal_id INT NOT NULL,
                original_food_id INT NOT NULL,
                swapped_food_id INT NOT NULL,
                quantity DOUBLE NOT NULL,
                unit VARCHAR(10) NOT NULL,
                swap_reason TEXT,
                applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                is_active BOOLEAN DEFAULT TRUE,
                FOREIGN KEY (meal_id) REFERENCES meals(meal_id),
                INDEX idx_user_date (user_id, applied_at),
                INDEX idx_meal_active (meal_id, is_active)
            )
            """;
        
        try (var conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            handleSQLException(e, sql);
        }
    }

    @Override
    protected void setParameters(PreparedStatement pstmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
    }

    @Override
    protected SwapHistory parseResultSet(ResultSet rs) throws SQLException {
        return new SwapHistory(
            rs.getInt("history_id"),
            rs.getInt("user_id"),
            rs.getInt("meal_id"),
            rs.getInt("original_food_id"),
            rs.getInt("swapped_food_id"),
            rs.getDouble("quantity"),
            rs.getString("unit"),
            rs.getString("swap_reason"),
            rs.getTimestamp("applied_at"),
            rs.getBoolean("is_active")
        );
    }
}
