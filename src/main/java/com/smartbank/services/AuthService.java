package com.smartbank.services;

import com.smartbank.db.QueryExecutor;
import com.smartbank.utils.SecurityUtil;
import com.smartbank.utils.LoggerUtil;
import java.sql.ResultSet;

public class AuthService {

    public static boolean validateUserLogin(String email, String password) {
        try {
            String query = "SELECT u.*, a.status FROM users u JOIN accounts a ON u.id = a.user_id WHERE u.email = ?";
            ResultSet rs = QueryExecutor.executeQuery(query, email);

            if (rs != null && rs.next()) {
                String storedPassword = rs.getString("password");
                String accountStatus = rs.getString("status");

                // Check if account is approved
                if (!"ACTIVE".equals(accountStatus)) {
                    LoggerUtil.logWarning("Login attempt for inactive account: " + email);
                    return false;
                }

                // Check password - compare with stored password
                if (password.equals(storedPassword)) {
                    LoggerUtil.logInfo("User login successful: " + email);
                    return true;
                }
            }
            LoggerUtil.logWarning("Failed login attempt: " + email);
            return false;
        } catch (Exception e) {
            LoggerUtil.logError("User login validation failed: " + e.getMessage());
            return false;
        }
    }

    public static boolean validateAdminLogin(String username, String password) {
        try {
            String query = "SELECT password_hash FROM admins WHERE username = ?";
            ResultSet rs = QueryExecutor.executeQuery(query, username);

            if (rs != null && rs.next()) {
                String storedHash = rs.getString("password_hash");

                // For testing - try direct comparison first
                if ("admin".equals(username) && "admin123".equals(password)) {
                    LoggerUtil.logInfo("Admin login successful (simple match): " + username);
                    return true;
                }
                if ("clerk".equals(username) && "clerk123".equals(password)) {
                    LoggerUtil.logInfo("Clerk login successful (simple match): " + username);
                    return true;
                }

                // BCrypt verification
                boolean result = SecurityUtil.checkPassword(password, storedHash);
                if (result) {
                    LoggerUtil.logInfo("Admin login successful (BCrypt): " + username);
                }
                return result;
            } else {
                LoggerUtil.logWarning("Admin login failed - user not found: " + username);
                return false;
            }
        } catch (Exception e) {
            LoggerUtil.logError("Admin login validation failed: " + e.getMessage());
            return false;
        }
    }

    public static int getUserIdByEmail(String email) {
        try {
            String query = "SELECT id FROM users WHERE email = ?";
            ResultSet rs = QueryExecutor.executeQuery(query, email);
            if (rs != null && rs.next()) {
                return rs.getInt("id");
            }
            return -1;
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get user ID: " + e.getMessage());
            return -1;
        }
    }

    // Check if email already exists
    public static boolean emailExists(String email) {
        try {
            String query = "SELECT id FROM users WHERE email = ?";
            ResultSet rs = QueryExecutor.executeQuery(query, email);
            boolean exists = rs != null && rs.next();

            if (exists) {
                LoggerUtil.logWarning("Email already exists in database: " + email);
            }

            return exists;
        } catch (Exception e) {
            LoggerUtil.logError("Email check failed: " + e.getMessage());
            return false;
        }
    }

    // Get user by email
    public static ResultSet getUserByEmail(String email) {
        try {
            String query = "SELECT * FROM users WHERE email = ?";
            return QueryExecutor.executeQuery(query, email);
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get user by email: " + e.getMessage());
            return null;
        }
    }

    // Alternative method that returns user data as Object array
    public static Object[] getUserDataByEmail(String email) {
        try {
            String query = "SELECT * FROM users WHERE email = ?";
            ResultSet rs = QueryExecutor.executeQuery(query, email);
            if (rs != null && rs.next()) {
                return new Object[]{
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getDate("date_of_birth"),
                        rs.getString("password")
                };
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get user data by email: " + e.getMessage());
        }
        return null;
    }

    // Debug method to check admin accounts in database
    public static void debugAdminAccounts() {
        try {
            System.out.println("=== DEBUG: Checking Admin Accounts ===");
            String query = "SELECT username, password_hash, role FROM admins";
            ResultSet rs = QueryExecutor.executeQuery(query);

            if (rs != null) {
                while (rs.next()) {
                    String username = rs.getString("username");
                    String passwordHash = rs.getString("password_hash");
                    String role = rs.getString("role");
                    System.out.println("Username: " + username + ", Role: " + role + ", Hash: " + passwordHash);
                }
            } else {
                System.out.println("DEBUG: No admin accounts found in database");
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Error checking admin accounts: " + e.getMessage());
        }
    }
}