package com.smartbank.db;

import com.smartbank.utils.LoggerUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QueryExecutor {

    public static ResultSet executeQuery(String query, Object... params) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            setParameters(stmt, params);
            return stmt.executeQuery();
        } catch (SQLException e) {
            LoggerUtil.logError("Query execution failed: " + e.getMessage() + " | Query: " + query);
            return null;
        }
    }

    public static int executeUpdate(String query, Object... params) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            setParameters(stmt, params);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    return rs.getInt(1);
                }
            }
            return affectedRows;
        } catch (SQLException e) {
            LoggerUtil.logError("Update execution failed: " + e.getMessage() + " | Query: " + query);
            return -1;
        }
    }

    public static boolean execute(String query, Object... params) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            setParameters(stmt, params);
            return stmt.execute();
        } catch (SQLException e) {
            LoggerUtil.logError("Execute failed: " + e.getMessage() + " | Query: " + query);
            return false;
        }
    }

    private static void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }

    // Batch execution for multiple queries
    public static boolean executeBatch(List<String> queries) {
        try {
            Connection conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            for (String query : queries) {
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.execute();
            }

            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            LoggerUtil.logError("Batch execution failed: " + e.getMessage());
            return false;
        }
    }
}