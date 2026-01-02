package com.smartbank.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerUtil {
    private static final String LOG_FILE = "logs/activity_log.txt";
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static {
        // Ensure logs directory exists
        new java.io.File("logs").mkdirs();
    }

    public static void logInfo(String message) {
        log("INFO", message);
    }

    public static void logError(String message) {
        log("ERROR", message);
    }

    public static void logWarning(String message) {
        log("WARNING", message);
    }

    public static void logDebug(String message) {
        log("DEBUG", message);
    }

    private static void log(String level, String message) {
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            String timestamp = LocalDateTime.now().format(formatter);
            out.printf("[%s] %s - %s%n", timestamp, level, message);
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }

    public static void logTransaction(String fromAccount, String toAccount, String amount, String type) {
        logInfo(String.format("TRANSACTION: %s -> %s | Amount: %s | Type: %s",
                fromAccount, toAccount, amount, type));
    }

    public static void logUserActivity(String email, String activity) {
        logInfo(String.format("USER ACTIVITY: %s - %s", email, activity));
    }

    public static void logAdminActivity(String username, String activity) {
        logInfo(String.format("ADMIN ACTIVITY: %s - %s", username, activity));
    }
}