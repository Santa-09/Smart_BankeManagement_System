package com.smartbank.services;

import com.smartbank.db.QueryExecutor;
import com.smartbank.utils.LoggerUtil;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class ReportService {

    public static Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            // Total accounts
            stats.put("totalAccounts", AccountService.getTotalAccountsCount());

            // Total transactions today
            stats.put("todayTransactions", TransactionService.getTodayTransactionsCount());

            // Total balance
            stats.put("totalBalance", AccountService.getTotalBalance());

            // Pending accounts
            stats.put("pendingAccounts", AccountService.getPendingAccountsCount());

            // Additional stats
            stats.put("totalUsers", getTotalUsersCount());
            stats.put("totalTransactions", getTotalTransactionsCount());

        } catch (Exception e) {
            LoggerUtil.logError("Failed to get dashboard stats: " + e.getMessage());
            // Set default values
            stats.put("totalAccounts", 0);
            stats.put("todayTransactions", 0);
            stats.put("totalBalance", 0.0);
            stats.put("pendingAccounts", 0);
            stats.put("totalUsers", 0);
            stats.put("totalTransactions", 0);
        }
        return stats;
    }

    public static int getTotalUsersCount() {
        try {
            String query = "SELECT COUNT(*) as total FROM users";
            ResultSet rs = QueryExecutor.executeQuery(query);
            if (rs != null && rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get total users count: " + e.getMessage());
        }
        return 0;
    }

    public static int getTotalTransactionsCount() {
        try {
            String query = "SELECT COUNT(*) as total FROM transactions";
            ResultSet rs = QueryExecutor.executeQuery(query);
            if (rs != null && rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get total transactions count: " + e.getMessage());
        }
        return 0;
    }

    public static Map<String, Object> getBankWiseStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            String query = "SELECT bank_name, COUNT(*) as count, SUM(balance) as total_balance " +
                    "FROM accounts WHERE status = 'ACTIVE' GROUP BY bank_name";
            ResultSet rs = QueryExecutor.executeQuery(query);

            if (rs != null) {
                while (rs.next()) {
                    String bankName = rs.getString("bank_name");
                    int count = rs.getInt("count");
                    double totalBalance = rs.getDouble("total_balance");
                    stats.put(bankName + "_count", count);
                    stats.put(bankName + "_balance", totalBalance);
                }
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get bank-wise stats: " + e.getMessage());
        }
        return stats;
    }
}