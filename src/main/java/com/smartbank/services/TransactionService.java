package com.smartbank.services;

import com.smartbank.db.QueryExecutor;
import com.smartbank.models.Transaction;
import com.smartbank.utils.LoggerUtil;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TransactionService {

    public static boolean transferMoney(String fromAccount, String toAccount, BigDecimal amount, String description) {
        try {
            // Start transaction
            String checkFromQuery = "SELECT balance FROM accounts WHERE account_number = ? AND status = 'ACTIVE'";
            ResultSet fromRs = QueryExecutor.executeQuery(checkFromQuery, fromAccount);
            if (fromRs == null || !fromRs.next()) {
                LoggerUtil.logError("Transfer failed: Sender account not found or inactive - " + fromAccount);
                return false;
            }

            BigDecimal fromBalance = fromRs.getBigDecimal("balance");
            if (fromBalance.compareTo(amount) < 0) {
                LoggerUtil.logError("Transfer failed: Insufficient funds - " + fromAccount);
                return false; // Insufficient funds
            }

            // Check recipient account
            String checkToQuery = "SELECT id FROM accounts WHERE account_number = ? AND status = 'ACTIVE'";
            ResultSet toRs = QueryExecutor.executeQuery(checkToQuery, toAccount);
            if (toRs == null || !toRs.next()) {
                LoggerUtil.logError("Transfer failed: Recipient account not found or inactive - " + toAccount);
                return false;
            }

            // Update sender balance
            String updateFromQuery = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
            int fromUpdate = QueryExecutor.executeUpdate(updateFromQuery, amount, fromAccount);
            if (fromUpdate <= 0) {
                LoggerUtil.logError("Transfer failed: Could not update sender balance - " + fromAccount);
                return false;
            }

            // Update receiver balance
            String updateToQuery = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
            int toUpdate = QueryExecutor.executeUpdate(updateToQuery, amount, toAccount);
            if (toUpdate <= 0) {
                // Rollback sender update
                QueryExecutor.executeUpdate(updateFromQuery, amount.negate(), fromAccount);
                LoggerUtil.logError("Transfer failed: Could not update recipient balance - " + toAccount);
                return false;
            }

            // Record transaction
            String transactionQuery = "INSERT INTO transactions (from_account, to_account, amount, type, description) VALUES (?, ?, ?, 'TRANSFER', ?)";
            int transactionId = QueryExecutor.executeUpdate(transactionQuery, fromAccount, toAccount, amount, description);

            if (transactionId > 0) {
                LoggerUtil.logInfo(String.format("Transfer successful: %s -> %s, Amount: %s, Description: %s",
                        fromAccount, toAccount, amount, description));
                return true;
            } else {
                // Rollback both balance updates
                QueryExecutor.executeUpdate(updateFromQuery, amount.negate(), fromAccount);
                QueryExecutor.executeUpdate(updateToQuery, amount.negate(), toAccount);
                LoggerUtil.logError("Transfer failed: Could not record transaction");
                return false;
            }

        } catch (Exception e) {
            LoggerUtil.logError("Transfer failed: " + e.getMessage());
            return false;
        }
    }

    public static List<Transaction> getTransactionsByAccount(String accountNumber) {
        List<Transaction> transactions = new ArrayList<>();
        try {
            String query = "SELECT * FROM transactions WHERE from_account = ? OR to_account = ? ORDER BY timestamp DESC LIMIT 50";
            ResultSet rs = QueryExecutor.executeQuery(query, accountNumber, accountNumber);
            if (rs != null) {
                while (rs.next()) {
                    Transaction transaction = new Transaction();
                    transaction.setId(rs.getInt("id"));
                    transaction.setFromAccount(rs.getString("from_account"));
                    transaction.setToAccount(rs.getString("to_account"));
                    transaction.setAmount(rs.getBigDecimal("amount"));
                    transaction.setType(rs.getString("type"));
                    transaction.setDescription(rs.getString("description"));
                    transaction.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                    transactions.add(transaction);
                }
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get transactions: " + e.getMessage());
        }
        return transactions;
    }

    public static int getTodayTransactionsCount() {
        try {
            String query = "SELECT COUNT(*) as total FROM transactions WHERE DATE(timestamp) = CURDATE()";
            ResultSet rs = QueryExecutor.executeQuery(query);
            if (rs != null && rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get today's transactions count: " + e.getMessage());
        }
        return 0;
    }

    public static List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        try {
            String query = "SELECT * FROM transactions ORDER BY timestamp DESC LIMIT 100";
            ResultSet rs = QueryExecutor.executeQuery(query);
            if (rs != null) {
                while (rs.next()) {
                    Transaction transaction = new Transaction();
                    transaction.setId(rs.getInt("id"));
                    transaction.setFromAccount(rs.getString("from_account"));
                    transaction.setToAccount(rs.getString("to_account"));
                    transaction.setAmount(rs.getBigDecimal("amount"));
                    transaction.setType(rs.getString("type"));
                    transaction.setDescription(rs.getString("description"));
                    transaction.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                    transactions.add(transaction);
                }
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get all transactions: " + e.getMessage());
        }
        return transactions;
    }
}