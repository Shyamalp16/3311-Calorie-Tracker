package Database;

import models.UserSettings;
import models.UnitSystem;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

/**
 * Data Access Object for UserSettings
 * Handles database operations for user preferences and settings
 */
public class UserSettingsDAO extends AbstractDAO<UserSettings> {

    /**
     * Create or update user settings
     */
    public UserSettings saveSettings(UserSettings settings) {
        Optional<UserSettings> existing = getSettingsByUserId(settings.getUserId());
        
        if (existing.isPresent()) {
            return updateSettings(settings);
        } else {
            return createSettings(settings);
        }
    }
    
    /**
     * Create new user settings
     */
    private UserSettings createSettings(UserSettings settings) {
        String sql = """
            INSERT INTO user_settings (user_id, unit_system, theme, enable_notifications, 
                                     date_format, daily_goal_calories, last_updated) 
            VALUES (?, ?, ?, ?, ?, ?, NOW())
            """;
        
        try (var conn = DatabaseConnector.getInstance().getConnection();
             var stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setParameters(stmt, settings.getUserId(), settings.getUnitSystem().name(), 
                         settings.getTheme(), settings.isEnableNotifications(),
                         settings.getDateFormat(), settings.getDailyGoalCalories());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        settings.setSettingsId(generatedKeys.getInt(1));
                    }
                }
                return settings;
            }
            
        } catch (SQLException e) {
            handleSQLException(e, sql);
        }
        
        return null;
    }
    
    /**
     * Update existing user settings
     */
    private UserSettings updateSettings(UserSettings settings) {
        String sql = """
            UPDATE user_settings 
            SET unit_system = ?, theme = ?, enable_notifications = ?, 
                date_format = ?, daily_goal_calories = ?, last_updated = NOW()
            WHERE user_id = ?
            """;
        
        int rowsAffected = update(sql, settings.getUnitSystem().name(), settings.getTheme(),
                                 settings.isEnableNotifications(), settings.getDateFormat(),
                                 settings.getDailyGoalCalories(), settings.getUserId());
        
        if (rowsAffected > 0) {
            return settings;
        }
        
        return null;
    }
    
    /**
     * Get user settings by user ID
     */
    public Optional<UserSettings> getSettingsByUserId(int userId) {
        String sql = "SELECT * FROM user_settings WHERE user_id = ?";
        return findOne(sql, userId);
    }
    
    /**
     * Get user settings by settings ID
     */
    public Optional<UserSettings> getSettingsById(int settingsId) {
        String sql = "SELECT * FROM user_settings WHERE settings_id = ?";
        return findOne(sql, settingsId);
    }
    
    /**
     * Delete user settings
     */
    public boolean deleteSettings(int userId) {
        String sql = "DELETE FROM user_settings WHERE user_id = ?";
        int rowsAffected = update(sql, userId);
        return rowsAffected > 0;
    }
    
    /**
     * Get or create default settings for a user
     */
    public UserSettings getOrCreateDefaultSettings(int userId) {
        Optional<UserSettings> existing = getSettingsByUserId(userId);
        
        if (existing.isPresent()) {
            return existing.get();
        } else {
            UserSettings defaultSettings = new UserSettings(userId, UnitSystem.METRIC);
            return saveSettings(defaultSettings);
        }
    }
    
    /**
     * Create the user_settings table if it doesn't exist
     */
    public void createUserSettingsTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS user_settings (
                settings_id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL UNIQUE,
                unit_system VARCHAR(20) DEFAULT 'METRIC',
                theme VARCHAR(50) DEFAULT 'Default',
                enable_notifications BOOLEAN DEFAULT TRUE,
                date_format VARCHAR(20) DEFAULT 'yyyy-MM-dd',
                daily_goal_calories INT DEFAULT 2000,
                last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                INDEX idx_user_settings (user_id)
            )
            """;
        
        try (var conn = DatabaseConnector.getInstance().getConnection();
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
    protected UserSettings parseResultSet(ResultSet rs) throws SQLException {
        UserSettings settings = new UserSettings();
        settings.setSettingsId(rs.getInt("settings_id"));
        settings.setUserId(rs.getInt("user_id"));
        
        String unitSystemStr = rs.getString("unit_system");
        try {
            settings.setUnitSystem(UnitSystem.valueOf(unitSystemStr));
        } catch (IllegalArgumentException e) {
            settings.setUnitSystem(UnitSystem.METRIC); 
        }
        
        settings.setTheme(rs.getString("theme"));
        settings.setEnableNotifications(rs.getBoolean("enable_notifications"));
        settings.setDateFormat(rs.getString("date_format"));
        settings.setDailyGoalCalories(rs.getInt("daily_goal_calories"));
        settings.setLastUpdated(rs.getTimestamp("last_updated"));
        
        return settings;
    }
} 