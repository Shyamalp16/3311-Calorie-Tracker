package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public abstract class AbstractDAO<T> {

	protected abstract void setParameters(PreparedStatement pstmt, Object... params) throws SQLException;

	protected abstract T parseResultSet(ResultSet rs) throws SQLException;

	protected void handleSQLException(SQLException e, String sql) {
		System.err.println("Error executing SQL: " + sql);
		e.printStackTrace();
	}

	public List<T> findMany(String sql, Object... params) {
		List<T> results = new ArrayList<>();
		try (Connection conn = DatabaseConnector.getInstance().getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			setParameters(pstmt, params);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					results.add(parseResultSet(rs));
				}
			}
		} catch (SQLException e) {
			handleSQLException(e, sql);
		}
		return results;
	}

	public Optional<T> findOne(String sql, Object... params) {
		try (Connection conn = DatabaseConnector.getInstance().getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			setParameters(pstmt, params);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return Optional.of(parseResultSet(rs));
				}
			}
		} catch (SQLException e) {
			handleSQLException(e, sql);
		}
		return Optional.empty();
	}

	public int update(String sql, Object... params) {
		try (Connection conn = DatabaseConnector.getInstance().getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			setParameters(pstmt, params);
			return pstmt.executeUpdate();

		} catch (SQLException e) {
			handleSQLException(e, sql);
		}
		return 0;
	}
}
