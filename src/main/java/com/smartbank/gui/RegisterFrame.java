package com.smartbank.gui;

import com.smartbank.models.Account;
import com.smartbank.models.User;
import com.smartbank.services.AccountService;
import com.smartbank.services.AuthService;
import com.smartbank.utils.Config;
import com.smartbank.utils.LoggerUtil;
import com.smartbank.utils.Validator;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.math.BigDecimal;

public class RegisterFrame extends JFrame {
    private JTextField firstNameField, lastNameField, emailField, phoneField, addressField, dobField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<String> bankComboBox, accountTypeComboBox;
    private JTextField initialDepositField;
    private JButton registerButton, backButton, clearButton;

    public RegisterFrame() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle(Config.APP_NAME + " - New Account Registration");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        mainPanel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = createHeaderPanel();

        // Form panel with scrolling
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Button panel
        JPanel buttonPanel = createButtonPanel();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setupEventListeners();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel(" Create New Bank Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.decode(Config.PRIMARY_COLOR));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Fill in your details to open a new bank account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(subtitleLabel);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Personal Information Section
        panel.add(createSectionLabel(" Personal Information"));
        panel.add(createInputField("First Name *", firstNameField = new JTextField()));
        panel.add(createInputField("Last Name *", lastNameField = new JTextField()));
        panel.add(createInputField("Email *", emailField = new JTextField()));
        panel.add(createInputField("Phone *", phoneField = new JTextField()));
        panel.add(createInputField("Address *", addressField = new JTextField()));
        panel.add(createInputField("Date of Birth (YYYY-MM-DD) *", dobField = new JTextField()));

        // Account Information Section
        panel.add(Box.createVerticalStrut(15));
        panel.add(createSectionLabel(" Account Information"));

        // Bank Selection
        JPanel bankPanel = new JPanel(new BorderLayout(10, 0));
        bankPanel.setBackground(Color.WHITE);
        bankPanel.setMaximumSize(new Dimension(600, 60));

        JLabel bankLabel = new JLabel("Select Bank *");
        bankLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bankLabel.setForeground(new Color(60, 60, 60));

        bankComboBox = new JComboBox<>(Config.BANK_NAMES);
        bankComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bankComboBox.setBackground(Color.WHITE);
        bankComboBox.setMaximumSize(new Dimension(600, 40));

        bankPanel.add(bankLabel, BorderLayout.NORTH);
        bankPanel.add(bankComboBox, BorderLayout.CENTER);
        panel.add(bankPanel);
        panel.add(Box.createVerticalStrut(10));

        // Account Type
        JPanel typePanel = new JPanel(new BorderLayout(10, 0));
        typePanel.setBackground(Color.WHITE);
        typePanel.setMaximumSize(new Dimension(600, 60));

        JLabel typeLabel = new JLabel("Account Type *");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        typeLabel.setForeground(new Color(60, 60, 60));

        accountTypeComboBox = new JComboBox<>(Config.ACCOUNT_TYPES);
        accountTypeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        accountTypeComboBox.setBackground(Color.WHITE);
        accountTypeComboBox.setMaximumSize(new Dimension(600, 40));

        typePanel.add(typeLabel, BorderLayout.NORTH);
        typePanel.add(accountTypeComboBox, BorderLayout.CENTER);
        panel.add(typePanel);
        panel.add(Box.createVerticalStrut(10));

        // Initial Deposit
        panel.add(createInputField("Initial Deposit ($) *", initialDepositField = new JTextField()));

        return panel;
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(Color.decode(Config.PRIMARY_COLOR));
        label.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createInputField(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(600, 60));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(60, 60, 60));

        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        textField.setMaximumSize(new Dimension(600, 40));

        panel.add(label, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        backButton = createStyledButton("← Back to Login", Config.SECONDARY_COLOR);
        clearButton = createStyledButton("️ Clear Form", Config.WARNING_COLOR);
        registerButton = createStyledButton(" Register Account", Config.SUCCESS_COLOR);

        panel.add(backButton);
        panel.add(clearButton);
        panel.add(registerButton);

        return panel;
    }

    private JButton createStyledButton(String text, String color) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(180, 45));
        button.setBackground(Color.decode(color));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.decode(color).brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.decode(color));
            }
        });

        return button;
    }

    private void setupEventListeners() {
        backButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        clearButton.addActionListener(e -> clearForm());

        registerButton.addActionListener(e -> registerAccount());
    }

    private void clearForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
        dobField.setText("");
        initialDepositField.setText("");
        bankComboBox.setSelectedIndex(0);
        accountTypeComboBox.setSelectedIndex(0);
    }

    private void registerAccount() {
        try {
            // Validate all fields
            if (!validateForm()) {
                return;
            }

            // Create user object
            User user = new User();
            user.setFirstName(firstNameField.getText().trim());
            user.setLastName(lastNameField.getText().trim());
            user.setEmail(emailField.getText().trim().toLowerCase());
            user.setPhone(phoneField.getText().trim());
            user.setAddress(addressField.getText().trim());

            // Parse date of birth and set as password
            try {
                LocalDate dob = LocalDate.parse(dobField.getText().trim());
                user.setDateOfBirth(dob);
                // Set password as date of birth in YYYY-MM-DD format
                user.setPassword(dob.toString());
            } catch (DateTimeParseException e) {
                showError("Invalid date format. Please use YYYY-MM-DD format.");
                return;
            }

            // Create account object
            Account account = new Account();
            account.setAccountType((String) accountTypeComboBox.getSelectedItem());

            try {
                BigDecimal initialDeposit = new BigDecimal(initialDepositField.getText().trim());
                account.setBalance(initialDeposit);
                account.setInitialDeposit(initialDeposit);
            } catch (NumberFormatException e) {
                showError("Please enter a valid amount for initial deposit");
                return;
            }

            account.setStatus("PENDING");

            // Show confirmation dialog
            int confirm = JOptionPane.showConfirmDialog(this,
                    createConfirmationMessage(user, account, (String) bankComboBox.getSelectedItem()),
                    "Confirm Registration",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                // Disable register button during processing
                registerButton.setEnabled(false);
                registerButton.setText("⏳ Processing...");

                // Perform registration in background thread
                new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() {
                        try {
                            // Create user
                            int userId = AccountService.createUser(user);
                            if (userId > 0) {
                                // Set user ID for account
                                account.setUserId(userId);

                                // Create account
                                int accountResult = AccountService.createAccount(account, (String) bankComboBox.getSelectedItem());
                                return accountResult > 0;
                            } else if (userId == -2) {
                                // Email already exists
                                SwingUtilities.invokeLater(() ->
                                        showError("Email address already exists. Please use a different email."));
                                return false;
                            } else {
                                return false;
                            }
                        } catch (Exception e) {
                            LoggerUtil.logError("Registration failed: " + e.getMessage());
                            return false;
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            boolean success = get();
                            if (success) {
                                showSuccess("Account registration successful!\n\n" +
                                        "Your account is pending approval.\n" +
                                        "You will receive an email once your account is approved.\n\n" +
                                        "Please note: Your temporary password is your date of birth (YYYY-MM-DD format)");
                                clearForm();
                                new LoginFrame().setVisible(true);
                                dispose();
                            } else {
                                showError("Registration failed. Please try again.");
                            }
                        } catch (Exception e) {
                            showError("Registration error: " + e.getMessage());
                        } finally {
                            registerButton.setEnabled(true);
                            registerButton.setText("✅ Register Account");
                        }
                    }
                }.execute();
            }

        } catch (Exception e) {
            showError("Registration error: " + e.getMessage());
            LoggerUtil.logError("Registration error: " + e.getMessage());
        }
    }

    private boolean validateForm() {
        // Validate required fields
        if (firstNameField.getText().trim().isEmpty()) {
            showError("First name is required");
            firstNameField.requestFocus();
            return false;
        }

        if (lastNameField.getText().trim().isEmpty()) {
            showError("Last name is required");
            lastNameField.requestFocus();
            return false;
        }

        if (emailField.getText().trim().isEmpty()) {
            showError("Email is required");
            emailField.requestFocus();
            return false;
        }

        if (!Validator.isValidEmail(emailField.getText().trim())) {
            showError("Please enter a valid email address");
            emailField.requestFocus();
            return false;
        }

        if (phoneField.getText().trim().isEmpty()) {
            showError("Phone number is required");
            phoneField.requestFocus();
            return false;
        }

        if (!Validator.isValidPhone(phoneField.getText().trim())) {
            showError("Please enter a valid 10-digit phone number");
            phoneField.requestFocus();
            return false;
        }

        if (addressField.getText().trim().isEmpty()) {
            showError("Address is required");
            addressField.requestFocus();
            return false;
        }

        if (dobField.getText().trim().isEmpty()) {
            showError("Date of birth is required");
            dobField.requestFocus();
            return false;
        }

        // Validate date format
        try {
            LocalDate.parse(dobField.getText().trim());
        } catch (DateTimeParseException e) {
            showError("Invalid date format. Please use YYYY-MM-DD format");
            dobField.requestFocus();
            return false;
        }

        if (initialDepositField.getText().trim().isEmpty()) {
            showError("Initial deposit is required");
            initialDepositField.requestFocus();
            return false;
        }

        try {
            double deposit = Double.parseDouble(initialDepositField.getText().trim());
            if (deposit <= 0) {
                showError("Initial deposit must be greater than 0");
                initialDepositField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid amount for initial deposit");
            initialDepositField.requestFocus();
            return false;
        }

        return true;
    }

    private String createConfirmationMessage(User user, Account account, String bankName) {
        return "<html><div style='width: 400px; padding: 15px; font-family: Segoe UI;'>" +
                "<h2 style='color: #2C3E50; margin: 0 0 15px 0; text-align: center;'>Confirm Registration</h2>" +
                "<div style='background: #F8F9FA; padding: 15px; border-radius: 8px; margin: 10px 0;'>" +
                "<h3 style='color: #3498DB; margin: 0 0 10px 0;'>Personal Information</h3>" +
                "<p><b>Name:</b> " + user.getFullName() + "</p>" +
                "<p><b>Email:</b> " + user.getEmail() + "</p>" +
                "<p><b>Phone:</b> " + user.getPhone() + "</p>" +
                "<p><b>Date of Birth:</b> " + user.getDateOfBirth() + "</p>" +
                "</div>" +
                "<div style='background: #E8F5E9; padding: 15px; border-radius: 8px; margin: 10px 0;'>" +
                "<h3 style='color: #27AE60; margin: 0 0 10px 0;'>Account Information</h3>" +
                "<p><b>Bank:</b> " + bankName + "</p>" +
                "<p><b>Account Type:</b> " + account.getAccountType() + "</p>" +
                "<p><b>Initial Deposit:</b> $" + account.getInitialDeposit() + "</p>" +
                "</div>" +
                "<p style='color: #7F8C8D; font-size: 12px; text-align: center; margin: 15px 0 0 0;'>" +
                "Your account will be pending approval. You'll receive an email once approved." +
                "</p>" +
                "</div></html>";
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                "<html><div style='text-align: center; padding: 20px; font-family: Segoe UI;'>" +
                        "<h2 style='color: #E74C3C; margin: 0 0 10px 0;'>❌ Error</h2>" +
                        "<p style='color: #2C3E50;'>" + message + "</p>" +
                        "</div></html>",
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this,
                "<html><div style='text-align: center; padding: 20px; font-family: Segoe UI;'>" +
                        "<h2 style='color: #27AE60; margin: 0 0 10px 0;'>✅ Success</h2>" +
                        "<p style='color: #2C3E50;'>" + message.replace("\n", "<br>") + "</p>" +
                        "</div></html>",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }
}