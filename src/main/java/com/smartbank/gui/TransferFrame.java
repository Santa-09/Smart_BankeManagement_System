package com.smartbank.gui;

import com.smartbank.models.Account;
import com.smartbank.services.AccountService;
import com.smartbank.threads.TransferThread;
import com.smartbank.utils.Config;
import com.smartbank.utils.LoggerUtil;
import com.smartbank.utils.Validator;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class TransferFrame extends JFrame {
    private int userId;
    private Account userAccount;

    private JTextField toAccountField, amountField, descriptionField;
    private JButton transferButton, backButton;
    private JLabel balanceLabel, feeLabel;
    private JCheckBox agreeCheckbox;

    public TransferFrame(int userId) {
        this.userId = userId;
        this.userAccount = AccountService.getAccountByUserId(userId);
        initializeUI();
    }

    private void initializeUI() {
        setTitle(Config.APP_NAME + " - Transfer Money");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(650, 750);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with modern gradient
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                Color color1 = new Color(248, 249, 250);
                Color color2 = new Color(233, 236, 239);
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Top panel with header and balance card
        JPanel topPanel = new JPanel(new BorderLayout(0, 20));
        topPanel.setOpaque(false);

        // Header
        JLabel headerLabel = new JLabel("💸 Transfer Money", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        headerLabel.setForeground(Color.decode(Config.PRIMARY_COLOR));

        // Balance info card
        JPanel balanceCard = createInfoCard();

        topPanel.add(headerLabel, BorderLayout.NORTH);
        topPanel.add(balanceCard, BorderLayout.CENTER);

        // Center panel with form and terms
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);

        // Form panel
        JPanel formPanel = createFormPanel();

        // Terms and conditions
        JPanel termsPanel = createTermsPanel();

        centerPanel.add(formPanel, BorderLayout.CENTER);
        centerPanel.add(termsPanel, BorderLayout.SOUTH);

        // Buttons panel
        JPanel buttonPanel = createButtonPanel();

        // Add all to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Event listeners
        setupEventListeners();
    }

    private JPanel createInfoCard() {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode(Config.SUCCESS_COLOR), 3),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        balanceLabel = new JLabel("Available Balance: $" + String.format("%.2f", userAccount.getBalance()), JLabel.CENTER);
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        balanceLabel.setForeground(Color.decode(Config.SUCCESS_COLOR));

        JLabel accountLabel = new JLabel("From: " + userAccount.getAccountNumber() + " | " + userAccount.getBankName(), JLabel.CENTER);
        accountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        accountLabel.setForeground(new Color(100, 100, 100));

        card.add(balanceLabel, BorderLayout.CENTER);
        card.add(accountLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Create white container for form fields
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode(Config.PRIMARY_COLOR), 2),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        // Recipient Account
        JPanel toAccountPanel = createFormField("🏦 To Account Number:", toAccountField = new JTextField());
        toAccountField.setToolTipText("Enter the recipient's account number");

        // Amount
        JPanel amountPanel = createFormField("💰 Amount ($):", amountField = new JTextField());
        amountField.setToolTipText("Enter transfer amount (excluding fees)");

        // Transaction Fee Display
        JPanel feePanel = new JPanel(new BorderLayout(10, 0));
        feePanel.setOpaque(false);
        feePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        feePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel feeBox = new JPanel(new BorderLayout());
        feeBox.setBackground(new Color(255, 243, 224));
        feeBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 193, 7), 2),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        JLabel feeTitle = new JLabel("💸 Transaction Fee:");
        feeTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        feeTitle.setForeground(new Color(60, 60, 60));

        feeLabel = new JLabel("$1.50 (Standard banking fee)");
        feeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        feeLabel.setForeground(new Color(230, 81, 0));

        feeBox.add(feeTitle, BorderLayout.WEST);
        feeBox.add(feeLabel, BorderLayout.EAST);
        feePanel.add(feeBox, BorderLayout.CENTER);

        // Description
        JPanel descPanel = createFormField("📝 Description (Optional):", descriptionField = new JTextField());
        descriptionField.setText("Fund Transfer");
        descriptionField.setToolTipText("Add a note for this transaction");

        // Add all fields to container
        formContainer.add(toAccountPanel);
        formContainer.add(Box.createVerticalStrut(15));
        formContainer.add(amountPanel);
        formContainer.add(Box.createVerticalStrut(10));
        formContainer.add(feePanel);
        formContainer.add(Box.createVerticalStrut(5));
        formContainer.add(descPanel);

        formPanel.add(formContainer);
        return formPanel;
    }

    private JPanel createFormField(String labelText, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(new Color(50, 50, 50));

        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setPreferredSize(new Dimension(0, 45));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Add focus effects
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.decode(Config.PRIMARY_COLOR), 2),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
        });

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTermsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // Create styled checkbox panel
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        checkboxPanel.setBackground(new Color(255, 255, 255, 230));
        checkboxPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        agreeCheckbox = new JCheckBox();
        agreeCheckbox.setOpaque(false);
        agreeCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel termsLabel = new JLabel("<html>I agree to the terms and conditions and understand that<br>transactions are final and cannot be reversed</html>");
        termsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        termsLabel.setForeground(new Color(80, 80, 80));

        checkboxPanel.add(agreeCheckbox);
        checkboxPanel.add(Box.createHorizontalStrut(10));
        checkboxPanel.add(termsLabel);

        panel.add(checkboxPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setOpaque(false);

        backButton = createModernButton("← Back", Config.DANGER_COLOR, 140);
        transferButton = createModernButton("💸 Transfer Now", Config.SUCCESS_COLOR, 180);

        panel.add(backButton);
        panel.add(transferButton);

        return panel;
    }

    private JButton createModernButton(String text, String color, int minWidth) {
        JButton button = new JButton(text);
        button.setBackground(Color.decode(color));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(minWidth, 50));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode(color).darker(), 2),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = Color.decode(color);
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });

        return button;
    }

    private void setupEventListeners() {
        transferButton.addActionListener(e -> handleTransfer());
        backButton.addActionListener(e -> {
            new DashboardFrame(userId).setVisible(true);
            dispose();
        });

        // Real-time validation
        amountField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateFee(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateFee(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateFee(); }
        });
    }

    private void updateFee() {
        // You can implement dynamic fee calculation here
        feeLabel.setText("$1.50 (Standard banking fee)");
    }

    private void handleTransfer() {
        String toAccount = toAccountField.getText().trim();
        String amountStr = amountField.getText().trim();
        String description = descriptionField.getText().trim();

        // Validation
        if (toAccount.isEmpty()) {
            showError("Please enter recipient account number");
            return;
        }

        if (!Validator.isValidAmount(amountStr)) {
            showError("Please enter a valid amount");
            return;
        }

        if (description.isEmpty()) {
            description = "Fund Transfer";
        }

        if (!agreeCheckbox.isSelected()) {
            showError("Please agree to the terms and conditions");
            return;
        }

        BigDecimal amount = new BigDecimal(amountStr);
        BigDecimal fee = new BigDecimal("1.50"); // Standard fee
        BigDecimal totalAmount = amount.add(fee);

        // Check if transferring to own account
        if (toAccount.equals(userAccount.getAccountNumber())) {
            showError("Cannot transfer to your own account");
            return;
        }

        // Check balance including fee
        if (userAccount.getBalance().compareTo(totalAmount) < 0) {
            showError(String.format("Insufficient balance. Required: $%.2f (Amount: $%.2f + Fee: $%.2f)",
                    totalAmount, amount, fee));
            return;
        }

        // Check if recipient account exists and is active
        Account recipientAccount = AccountService.getAccountByNumber(toAccount);
        if (recipientAccount == null) {
            showError("Recipient account not found");
            return;
        }

        if (!recipientAccount.getStatus().equals("ACTIVE")) {
            showError("Recipient account is not active");
            return;
        }

        // Confirm transfer
        String confirmMessage = String.format(
                "<html><div style='padding: 20px; font-family: Segoe UI;'>" +
                        "<h2 style='color: %s; text-align: center; margin-bottom: 20px;'>💸 Confirm Transfer</h2>" +
                        "<div style='background: #F8F9FA; padding: 20px; border-radius: 8px; border-left: 4px solid %s;'>" +
                        "<table style='width: 100%%; font-size: 14px;'>" +
                        "<tr><td style='padding: 8px 0; color: #666;'>From Account:</td><td style='text-align: right; font-weight: bold;'>%s</td></tr>" +
                        "<tr><td style='padding: 8px 0; color: #666;'>Bank:</td><td style='text-align: right;'>%s</td></tr>" +
                        "<tr><td style='padding: 8px 0; color: #666;'>To Account:</td><td style='text-align: right; font-weight: bold;'>%s</td></tr>" +
                        "<tr><td style='padding: 8px 0; color: #666;'>Recipient Bank:</td><td style='text-align: right;'>%s</td></tr>" +
                        "<tr><td colspan='2' style='padding-top: 15px; border-top: 2px solid #ddd;'></td></tr>" +
                        "<tr><td style='padding: 8px 0; color: #666;'>Transfer Amount:</td><td style='text-align: right; font-size: 16px; font-weight: bold; color: %s;'>$%.2f</td></tr>" +
                        "<tr><td style='padding: 8px 0; color: #666;'>Transaction Fee:</td><td style='text-align: right; color: #E65100;'>$%.2f</td></tr>" +
                        "<tr><td colspan='2' style='padding-top: 10px; border-top: 1px solid #ddd;'></td></tr>" +
                        "<tr><td style='padding: 8px 0; font-weight: bold; color: #333;'>Total Debit:</td><td style='text-align: right; font-size: 18px; font-weight: bold; color: %s;'>$%.2f</td></tr>" +
                        "<tr><td style='padding: 8px 0; color: #666;'>Description:</td><td style='text-align: right; font-style: italic;'>%s</td></tr>" +
                        "</table>" +
                        "</div>" +
                        "<p style='text-align: center; margin-top: 20px; color: #666; font-size: 13px;'>Are you sure you want to proceed with this transfer?</p>" +
                        "</div></html>",
                Config.PRIMARY_COLOR, Config.PRIMARY_COLOR,
                userAccount.getAccountNumber(), userAccount.getBankName(),
                toAccount, recipientAccount.getBankName(),
                Config.SUCCESS_COLOR, amount, fee,
                Config.DANGER_COLOR, totalAmount, description
        );

        int confirm = JOptionPane.showConfirmDialog(this,
                confirmMessage,
                "Confirm Transfer",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Disable UI during transfer
            setUIEnabled(false);
            transferButton.setText("⏳ Processing...");

            // Perform transfer in background thread
            TransferThread transferThread = new TransferThread(
                    userAccount.getAccountNumber(),
                    toAccount,
                    amount,
                    description,
                    new TransferThread.TransferCallback() {
                        @Override
                        public void onTransferComplete(boolean success, String message) {
                            SwingUtilities.invokeLater(() -> {
                                setUIEnabled(true);
                                transferButton.setText("💸 Transfer Now");

                                if (success) {
                                    showSuccess(message);
                                    LoggerUtil.logInfo("Transfer completed: " +
                                            userAccount.getAccountNumber() + " -> " + toAccount + " Amount: " + amount);
                                } else {
                                    showError(message);
                                }
                            });
                        }
                    });

            transferThread.start();
        }
    }

    private void setUIEnabled(boolean enabled) {
        transferButton.setEnabled(enabled);
        backButton.setEnabled(enabled);
        toAccountField.setEnabled(enabled);
        amountField.setEnabled(enabled);
        descriptionField.setEnabled(enabled);
        agreeCheckbox.setEnabled(enabled);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this,
                "<html><div style='padding: 20px; text-align: center; font-family: Segoe UI;'>" +
                        "<div style='font-size: 48px; margin-bottom: 15px;'>✅</div>" +
                        "<h2 style='color: " + Config.SUCCESS_COLOR + "; margin-bottom: 10px;'>Transfer Successful!</h2>" +
                        "<p style='font-size: 14px; color: #666;'>" + message + "</p>" +
                        "<p style='font-size: 12px; color: #999; margin-top: 15px;'>Transaction has been processed successfully</p>" +
                        "</div></html>",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        // Update balance display
        userAccount = AccountService.getAccountByUserId(userId);
        balanceLabel.setText("Available Balance: $" + String.format("%.2f", userAccount.getBalance()));

        // Clear fields
        toAccountField.setText("");
        amountField.setText("");
        descriptionField.setText("Fund Transfer");
        agreeCheckbox.setSelected(false);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                "<html><div style='padding: 20px; text-align: center; font-family: Segoe UI;'>" +
                        "<div style='font-size: 48px; color: " + Config.DANGER_COLOR + "; margin-bottom: 15px;'>⚠</div>" +
                        "<h3 style='color: " + Config.DANGER_COLOR + "; margin-bottom: 10px;'>Transfer Error</h3>" +
                        "<p style='font-size: 14px; color: #666;'>" + message + "</p>" +
                        "</div></html>",
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}