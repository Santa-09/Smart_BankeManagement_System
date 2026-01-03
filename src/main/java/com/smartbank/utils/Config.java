package com.smartbank.utils;

public class Config {
    // Database Configuration
    public static final String DB_URL = "jdbc:mysql://localhost:3306/smartbank";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "Santanu@#2006";

    // Email Configuration
    public static final String SMTP_HOST = "smtp.gmail.com";
    public static final String SMTP_PORT = "587";
    public static final String EMAIL_USERNAME = "ENTER_YOUR_MAIL";
    public static final String EMAIL_PASSWORD = "ENTER_YOUR_SECURITY_KEY";

    // Application Settings
    public static final String APP_NAME = "SmartBank";
    public static final String VERSION = "2.0";
    public static final int MAX_LOGIN_ATTEMPTS = 3;

    // UI Settings - Modern Color Scheme
    public static final String PRIMARY_COLOR = "#2C3E50";    // Dark Blue
    public static final String SECONDARY_COLOR = "#34495E";  // Dark Gray
    public static final String ACCENT_COLOR = "#3498DB";     // Bright Blue
    public static final String SUCCESS_COLOR = "#27AE60";    // Green
    public static final String DANGER_COLOR = "#E74C3C";     // Red
    public static final String WARNING_COLOR = "#F39C12";    // Orange
    public static final String INFO_COLOR = "#17A2B8";

    // Bank Names
    public static final String[] BANK_NAMES = {
            "State Bank of India (SBI)",
            "Punjab National Bank (PNB)",
            "HDFC Bank",
            "ICICI Bank",
            "Axis Bank",
            "Bank of Baroda",
            "Canara Bank",
            "Union Bank of India"
    };

    // Account Types
    public static final String[] ACCOUNT_TYPES = {
            "SAVINGS ACCOUNT",
            "CURRENT ACCOUNT",
            "SALARY ACCOUNT",
            "FIXED DEPOSIT",
            "RECURRING DEPOSIT"
    };
}
