package com.smartbank.main;

import com.smartbank.gui.LoginFrame;
import com.smartbank.utils.LoggerUtil;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Set system look and feel for native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Set modern UI improvements
            setupModernUI();

        } catch (Exception e) {
            System.err.println("Failed to set system look and feel: " + e.getMessage());
        }

        // Start the application on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Show splash screen or loading message
                showStartupMessage();

                // Initialize and show login frame
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);

                LoggerUtil.logInfo("=== SmartBank Application Started ===");
                LoggerUtil.logInfo("System initialized successfully");

            } catch (Exception e) {
                LoggerUtil.logError("Application startup failed: " + e.getMessage());
                showErrorDialog("Failed to start application: " + e.getMessage());
            }
        });
    }

    private static void setupModernUI() {
        // Modern UI settings for better appearance
        UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("TextArea.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("ComboBox.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 12));
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));

        // Better table row height
        UIManager.put("Table.rowHeight", 28);

        // Improved button appearance
        UIManager.put("Button.background", Color.decode("#3498DB"));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.focus", UIManager.get("Button.background"));

        // Improved panel backgrounds
        UIManager.put("Panel.background", Color.WHITE);
        UIManager.put("OptionPane.background", Color.WHITE);
    }

    private static void showStartupMessage() {
        System.out.println("=========================================");
        System.out.println("        🏦 SmartBank System v1.0");
        System.out.println("     Professional Banking Solution");
        System.out.println("=========================================");
        System.out.println("Initializing database connection...");
        System.out.println("Loading modern user interface...");
        System.out.println("Starting application services...");
        System.out.println("=========================================");
    }

    private static void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(
                null,
                message,
                "Application Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}