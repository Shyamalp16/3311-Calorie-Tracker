package Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import models.User;
public class UserDAO extends AbstractDAO<User> {

	@Override
	protected void setParameters(PreparedStatement pstmt, Object... params) throws SQLException {
		for (int i = 0; i < params.length; i++) {
			pstmt.setObject(i + 1, params[i]);
		}
	}

	@Override
	protected User parseResultSet(ResultSet rs) throws SQLException {
		User user = new User();
		user.setUserId(rs.getInt("user_id"));
		user.setName(rs.getString("name"));
		user.setUsername(rs.getString("username"));
		user.setPassword(rs.getString("password"));
		user.setGender(rs.getString("gender"));
		user.setBirthDate(rs.getDate("birth_date"));
		user.setHeight(rs.getDouble("height"));
		user.setWeight(rs.getDouble("weight"));
		user.setActivityLevel(rs.getString("activity_level"));
		user.setCreatedAt(rs.getTimestamp("created_at"));
		return user;
	}

	public User createUser(User user) {
		String sql = "INSERT INTO users (name, username, password, gender, birth_date, height, weight, activity_level, created_at) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";

		try (var conn = DatabaseConnector.getInstance().getConnection();
				var stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			setParameters(stmt, user.getName(), user.getUsername(), user.getPassword(), user.getGender(),
					user.getBirthDate(), user.getHeight(), user.getWeight(), user.getActivityLevel());

			int rowsAffected = stmt.executeUpdate();

			if (rowsAffected > 0) {
				try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						user.setUserId(generatedKeys.getInt(1));
					}
				}
				return user;
			}

		} catch (SQLException e) {
			handleSQLException(e, sql);
		}

		return null;
	}

	public List<User> getAllUsers() {
		String sql = "SELECT * FROM users ORDER BY name";
		return findMany(sql);
	}

	public Optional<User> getUserById(int userId) {
		String sql = "SELECT * FROM users WHERE user_id = ?";
		return findOne(sql, userId);
	}

	public Optional<User> login(String username, String password) {
		String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
		return findOne(sql, username, password);
	}

	/**
	 * Update existing user information
	 * @param user The user object with updated information
	 * @return Updated user object if successful, null otherwise
	 */
	public User updateUser(User user) {
		String sql = "UPDATE users SET name = ?, username = ?, password = ?, gender = ?, " +
				"birth_date = ?, height = ?, weight = ?, activity_level = ? WHERE user_id = ?";

		try (var conn = DatabaseConnector.getInstance().getConnection();
				var stmt = conn.prepareStatement(sql)) {

			setParameters(stmt, user.getName(), user.getUsername(), user.getPassword(), 
					user.getGender(), user.getBirthDate(), user.getHeight(), user.getWeight(), 
					user.getActivityLevel(), user.getUserId());

			int rowsAffected = stmt.executeUpdate();

			if (rowsAffected > 0) {
				return user;
			}

		} catch (SQLException e) {
			handleSQLException(e, sql);
		}

		return null;
	}

}
