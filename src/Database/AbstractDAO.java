package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * An abstract base class for Data Access Objects that uses the Template Method pattern.
 * This class provides the skeleton for database query operations, leaving specific
 * implementation details to the concrete subclasses.
 *
 * @param <T> The type of the model object this DAO handles.
 */
public abstract class AbstractDAO<T> {

    /**
     * Template Method: Defines the algorithm for finding multiple records.
     *
     * @param sql The SQL query to execute.
     * @param params The parameters to be set on the PreparedStatement.
     * @return A list of objects parsed from the ResultSet.
     */
    public List<T> findMany(String sql, Object... params) {
        List<T> results = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
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

    /**
     * Template Method: Defines the algorithm for finding a single record.
     *
     * @param sql The SQL query to execute.
     * @param params The parameters to be set on the PreparedStatement.
     * @return An Optional containing the object if found, otherwise empty.
     */
    public Optional<T> findOne(String sql, Object... params) {
        try (Connection conn = DatabaseConnector.getConnection();
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

    /**
     * Template Method: Defines the algorithm for update/insert/delete operations.
     *
     * @param sql The SQL statement to execute.
     * @param params The parameters to be set on the PreparedStatement.
     * @return The number of rows affected.
     */
    public int update(String sql, Object... params) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setParameters(pstmt, params);
            return pstmt.executeUpdate();

        } catch (SQLException e) {
            handleSQLException(e, sql);
        }
        return 0;
    }

    /**
     * Abstract method for setting parameters on a PreparedStatement.
     * Concrete subclasses must implement this to map parameters to the query.
     *
     * @param pstmt The PreparedStatement to set parameters on.
     * @param params The parameters to be set.
     * @throws SQLException if a database access error occurs.
     */
    protected abstract void setParameters(PreparedStatement pstmt, Object... params) throws SQLException;

    /**
     * Abstract method for parsing a ResultSet row into an object.
     * Concrete subclasses must implement this to map database rows to model objects.
     *
     * @param rs The ResultSet to parse.
     * @return The parsed object of type T.
     * @throws SQLException if a database access error occurs.
     */
    protected abstract T parseResultSet(ResultSet rs) throws SQLException;

    /**
     * Centralized exception handler. Can be overridden for more specific handling.
     *
     * @param e The SQLException that occurred.
     * @param sql The SQL that was being executed.
     */
    protected void handleSQLException(SQLException e, String sql) {
        System.err.println("Error executing SQL: " + sql);
        e.printStackTrace();
    }
}
