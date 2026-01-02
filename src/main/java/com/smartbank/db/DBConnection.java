package com.smartbank.db;

import com.smartbank.utils.Config;
import com.smartbank.utils.LoggerUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(
                        Config.DB_URL,
                        Config.DB_USER,
                        Config.DB_PASSWORD
                );
                LoggerUtil.logInfo("Database connection established");
            }
        } catch (ClassNotFoundException e) {
            LoggerUtil.logError("MySQL JDBC Driver not found: " + e.getMessage());
            System.err.println("Please add MySQL JDBC driver to your classpath");
        } catch (SQLException e) {
            LoggerUtil.logError("Database connection failed: " + e.getMessage());
            System.err.println("Database error: " + e.getMessage());
        } catch (Exception e) {
            LoggerUtil.logError("Unexpected error during database connection: " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                LoggerUtil.logInfo("Database connection closed");
            }
        } catch (SQLException e) {
            LoggerUtil.logError("Error closing database connection: " + e.getMessage());
        }
    }

    // Test database connection
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}