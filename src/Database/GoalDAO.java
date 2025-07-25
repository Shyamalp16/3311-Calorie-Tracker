package Database;

import models.Goal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class GoalDAO extends AbstractDAO<Goal> {

    public void saveGoal(Goal goal) {
        String checkIfExistsSql = "SELECT COUNT(*) FROM goals WHERE user_id = ?";
        String insertSql = "INSERT INTO goals (user_id, calories, protein, carbs, fats, fiber) VALUES (?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE goals SET calories = ?, protein = ?, carbs = ?, fats = ?, fiber = ? WHERE user_id = ?";

        try (var conn = DatabaseConnector.getConnection();
             var checkStmt = conn.prepareStatement(checkIfExistsSql)) {
            checkStmt.setInt(1, goal.getUserId());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                update(updateSql, goal.getCalories(), goal.getProtein(), goal.getCarbs(), goal.getFats(), goal.getFiber(), goal.getUserId());
            } else {
                update(insertSql, goal.getUserId(), goal.getCalories(), goal.getProtein(), goal.getCarbs(), goal.getFats(), goal.getFiber());
            }
        } catch (SQLException e) {
            handleSQLException(e, "saveGoal");
        }
    }

    public Optional<Goal> getGoalByUserId(int userId) {
        String sql = "SELECT * FROM goals WHERE user_id = ?";
        return findOne(sql, userId);
    }

    @Override
    protected void setParameters(PreparedStatement pstmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
    }

    @Override
    protected Goal parseResultSet(ResultSet rs) throws SQLException {
        return new Goal(
            rs.getInt("user_id"),
            rs.getDouble("calories"),
            rs.getDouble("protein"),
            rs.getDouble("carbs"),
            rs.getDouble("fats"),
            rs.getDouble("fiber")
        );
    }
}