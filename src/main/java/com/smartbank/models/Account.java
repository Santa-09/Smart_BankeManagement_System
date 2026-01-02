package com.smartbank.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Account {
    private int id;
    private String accountNumber;
    private int userId;
    private BigDecimal balance;
    private String accountType;
    private String bankName;
    private String status;
    private BigDecimal initialDeposit;
    private LocalDateTime createdAt;
    private User user; // Joined user data

    public Account() {}

    public Account(String accountNumber, int userId, String accountType) {
        this.accountNumber = accountNumber;
        this.userId = userId;
        this.balance = BigDecimal.ZERO;
        this.accountType = accountType;
        this.status = "PENDING";
        this.initialDeposit = BigDecimal.ZERO;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getInitialDeposit() { return initialDeposit; }
    public void setInitialDeposit(BigDecimal initialDeposit) { this.initialDeposit = initialDeposit; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    @Override
    public String toString() {
        return "Account{id=" + id + ", number=" + accountNumber + ", type=" + accountType + ", balance=" + balance + "}";
    }
}