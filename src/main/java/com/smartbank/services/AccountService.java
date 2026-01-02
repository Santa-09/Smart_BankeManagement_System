package com.smartbank.services;

import com.smartbank.db.QueryExecutor;
import com.smartbank.models.Account;
import com.smartbank.models.User;
import com.smartbank.utils.RandomGenerator;
import com.smartbank.utils.LoggerUtil;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AccountService {

    public static int createUser(User user) {
        try {
            // Check if email already exists
            if (AuthService.emailExists(user.getEmail())) {
                LoggerUtil.logError("Email already exists: " + user.getEmail());
                return -2; // Special code for duplicate email
            }

            String query = "INSERT INTO users (first_name, last_name, email, phone, address, date_of_birth, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
            int result = QueryExecutor.executeUpdate(query,
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getAddress(),
                    user.getDateOfBirth(),
                    user.getPassword()
            );

            if (result > 0) {
                LoggerUtil.logInfo("User created successfully: " + user.getEmail());
                return result;
            } else {
                LoggerUtil.logError("Failed to create user: " + user.getEmail());
                return -1;
            }

        } catch (Exception e) {
            LoggerUtil.logError("Failed to create user: " + e.getMessage());
            // Check if it's a duplicate entry error
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("duplicate")) {
                return -2;
            }
            return -1;
        }
    }

    public static int createAccount(Account account, String bankName) {
        try {
            // Generate unique account number
            String accountNumber;
            boolean isUnique = false;
            int attempts = 0;

            do {
                accountNumber = RandomGenerator.generateAccountNumber();
                // Check if account number already exists
                String checkQuery = "SELECT id FROM accounts WHERE account_number = ?";
                ResultSet rs = QueryExecutor.executeQuery(checkQuery, accountNumber);
                isUnique = rs == null || !rs.next();
                attempts++;

                // Close the result set
                if (rs != null) {
                    try { rs.close(); } catch (Exception e) {}
                }
            } while (!isUnique && attempts < 10);

            if (!isUnique) {
                LoggerUtil.logError("Failed to generate unique account number after " + attempts + " attempts");
                return -1;
            }

            account.setAccountNumber(accountNumber);

            String query = "INSERT INTO accounts (account_number, user_id, balance, account_type, bank_name, status, initial_deposit) VALUES (?, ?, ?, ?, ?, ?, ?)";
            int result = QueryExecutor.executeUpdate(query,
                    account.getAccountNumber(),
                    account.getUserId(),
                    account.getBalance(),
                    account.getAccountType(),
                    bankName,
                    "PENDING", // Fixed typo: was "PERDING", now "PENDING"
                    account.getInitialDeposit()
            );

            if (result > 0) {
                LoggerUtil.logInfo("Account created successfully: " + account.getAccountNumber() + " | Bank: " + bankName);

                // Send account creation email
                User user = getUserById(account.getUserId());
                if (user != null) {
                    MailService.sendAccountCreationEmail(
                            user.getEmail(),
                            user.getFullName(),
                            account.getAccountNumber(),
                            account.getAccountType(),
                            bankName,
                            account.getInitialDeposit().toString()
                    );
                }
                return result;
            } else {
                LoggerUtil.logError("Failed to create account for user: " + account.getUserId());
                return -1;
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to create account: " + e.getMessage());
            return -1;
        }
    }

    public static boolean updateAccountStatus(String accountNumber, String status) {
        try {
            String query = "UPDATE accounts SET status = ? WHERE account_number = ?";
            int result = QueryExecutor.executeUpdate(query, status, accountNumber);

            if (result > 0) {
                LoggerUtil.logInfo("Account status updated: " + accountNumber + " -> " + status);

                // If account is approved, send approval email
                if ("ACTIVE".equals(status)) {
                    Account account = getAccountByNumber(accountNumber);
                    if (account != null && account.getUser() != null) {
                        MailService.sendAccountApprovalEmail(
                                account.getUser().getEmail(),
                                account.getUser().getFullName(),
                                accountNumber,
                                account.getAccountType(),
                                account.getBankName()
                        );
                    }
                }
                return true;
            } else {
                LoggerUtil.logError("Failed to update account status: " + accountNumber);
                return false;
            }
        } catch (Exception e) {
            LoggerUtil.logError("Error updating account status: " + e.getMessage());
            return false;
        }
    }

    public static Account getAccountByNumber(String accountNumber) {
        try {
            String query = "SELECT a.*, u.first_name, u.last_name, u.email, u.phone, u.address, u.date_of_birth, u.password " +
                    "FROM accounts a JOIN users u ON a.user_id = u.id " +
                    "WHERE a.account_number = ?";
            ResultSet rs = QueryExecutor.executeQuery(query, accountNumber);
            if (rs != null && rs.next()) {
                return extractAccountFromResultSet(rs);
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get account: " + e.getMessage());
        }
        return null;
    }

    public static Account getAccountByUserId(int userId) {
        try {
            String query = "SELECT a.*, u.first_name, u.last_name, u.email, u.phone, u.address, u.date_of_birth, u.password " +
                    "FROM accounts a JOIN users u ON a.user_id = u.id " +
                    "WHERE a.user_id = ?";
            ResultSet rs = QueryExecutor.executeQuery(query, userId);
            if (rs != null && rs.next()) {
                return extractAccountFromResultSet(rs);
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get account by user ID: " + e.getMessage());
        }
        return null;
    }

    private static Account extractAccountFromResultSet(ResultSet rs) {
        try {
            Account account = new Account();
            account.setId(rs.getInt("id"));
            account.setAccountNumber(rs.getString("account_number"));
            account.setUserId(rs.getInt("user_id"));
            account.setBalance(rs.getBigDecimal("balance"));
            account.setAccountType(rs.getString("account_type"));
            account.setBankName(rs.getString("bank_name"));
            account.setStatus(rs.getString("status"));
            account.setInitialDeposit(rs.getBigDecimal("initial_deposit"));

            // Handle potential null for created_at
            if (rs.getTimestamp("created_at") != null) {
                account.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }

            User user = new User();
            user.setId(rs.getInt("user_id"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setEmail(rs.getString("email"));
            user.setPhone(rs.getString("phone"));
            user.setAddress(rs.getString("address"));

            // Handle potential null for date_of_birth
            if (rs.getDate("date_of_birth") != null) {
                user.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
            }

            user.setPassword(rs.getString("password"));
            account.setUser(user);

            return account;
        } catch (Exception e) {
            LoggerUtil.logError("Error extracting account from result set: " + e.getMessage());
            return null;
        }
    }

    public static boolean updateBalance(String accountNumber, BigDecimal newBalance) {
        try {
            String query = "UPDATE accounts SET balance = ? WHERE account_number = ?";
            int result = QueryExecutor.executeUpdate(query, newBalance, accountNumber);
            return result > 0;
        } catch (Exception e) {
            LoggerUtil.logError("Failed to update balance: " + e.getMessage());
            return false;
        }
    }

    public static List<Account> getPendingAccounts() {
        List<Account> pendingAccounts = new ArrayList<>();
        try {
            String query = "SELECT a.*, u.first_name, u.last_name, u.email, u.phone, u.address, u.date_of_birth, u.password " +
                    "FROM accounts a JOIN users u ON a.user_id = u.id " +
                    "WHERE a.status = 'PENDING' " +
                    "ORDER BY a.created_at ASC";
            ResultSet rs = QueryExecutor.executeQuery(query);

            if (rs != null) {
                while (rs.next()) {
                    Account account = extractAccountFromResultSet(rs);
                    if (account != null) {
                        pendingAccounts.add(account);
                    }
                }
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get pending accounts: " + e.getMessage());
        }
        return pendingAccounts;
    }

    public static List<Account> getAllActiveAccounts() {
        List<Account> accounts = new ArrayList<>();
        try {
            String query = "SELECT a.*, u.first_name, u.last_name, u.email, u.phone, u.address, u.date_of_birth, u.password " +
                    "FROM accounts a JOIN users u ON a.user_id = u.id " +
                    "WHERE a.status = 'ACTIVE' " +
                    "ORDER BY a.account_number ASC";
            ResultSet rs = QueryExecutor.executeQuery(query);

            if (rs != null) {
                while (rs.next()) {
                    Account account = extractAccountFromResultSet(rs);
                    if (account != null) {
                        accounts.add(account);
                    }
                }
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get all active accounts: " + e.getMessage());
        }
        return accounts;
    }

    public static int getTotalAccountsCount() {
        try {
            String query = "SELECT COUNT(*) as total FROM accounts WHERE status = 'ACTIVE'";
            ResultSet rs = QueryExecutor.executeQuery(query);
            if (rs != null && rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get total accounts count: " + e.getMessage());
        }
        return 0;
    }

    public static BigDecimal getTotalBalance() {
        try {
            String query = "SELECT SUM(balance) as total FROM accounts WHERE status = 'ACTIVE'";
            ResultSet rs = QueryExecutor.executeQuery(query);
            if (rs != null && rs.next()) {
                return rs.getBigDecimal("total");
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get total balance: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    public static int getPendingAccountsCount() {
        try {
            String query = "SELECT COUNT(*) as total FROM accounts WHERE status = 'PENDING'";
            ResultSet rs = QueryExecutor.executeQuery(query);
            if (rs != null && rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get pending accounts count: " + e.getMessage());
        }
        return 0;
    }

    // Helper method to get user by ID
    public static User getUserById(int userId) {
        try {
            String query = "SELECT * FROM users WHERE id = ?";
            ResultSet rs = QueryExecutor.executeQuery(query, userId);
            if (rs != null && rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
                user.setPassword(rs.getString("password"));
                return user;
            }
        } catch (Exception e) {
            LoggerUtil.logError("Failed to get user by ID: " + e.getMessage());
        }
        return null;
    }
}