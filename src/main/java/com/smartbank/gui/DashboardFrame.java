package com.smartbank.gui;

import com.smartbank.models.Account;
import com.smartbank.services.AccountService;
import com.smartbank.utils.Config;
import com.smartbank.utils.LoggerUtil;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DashboardFrame extends JFrame {
    private int userId;
    private Account userAccount;

    private JLabel welcomeLabel, balanceLabel, accountNumberLabel, accountTypeLabel, statusLabel, bankNameLabel, depositLabel, timeLabel;
    private JButton transferButton, historyButton, detailsButton, logoutButton;
    private JPanel mainPanel;

    public DashboardFrame(int userId) {
        this.userId = userId;
        loadUserData();
        initializeUI();
    }

    private void loadUserData() {
        userAccount = AccountService.getAccountByUserId(userId);
        if (userAccount == null) {
            JOptionPane.showMessageDialog(this,
                    "<html><div style='padding: 15px; text-align: center; font-family: Segoe UI;'>" +
                            "<h3 style='color: #E74C3C; margin: 0 0 10px 0;'>❌ Account Not Found</h3>" +
                            "<p style='color: #7F8C8D;'>Please contact administrator for assistance.</p>" +
                            "</div></html>",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new LoginFrame().setVisible(true);
            dispose();
            return;
        }
    }

    private void initializeUI() {
        setTitle(Config.APP_NAME + " - Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 850);
        setLocationRelativeTo(null);
        setResizable(true);

        // Main panel with modern gradient
        mainPanel = new JPanel(new BorderLayout(0, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                Color color1 = new Color(240, 242, 245);
                Color color2 = new Color(250, 251, 252);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Header
        JPanel headerPanel = createHeaderPanel();

        // Content panel with stats and actions
        JPanel contentPanel = new JPanel(new BorderLayout(0, 25));
        contentPanel.setOpaque(false);

        JPanel statsPanel = createStatsPanel();
        JPanel actionsPanel = createActionsPanel();

        contentPanel.add(statsPanel, BorderLayout.CENTER);
        contentPanel.add(actionsPanel, BorderLayout.SOUTH);

        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Event listeners
        setupEventListeners();

        updateDashboard();
        startAutoRefresh();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Left side - Welcome message
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);

        welcomeLabel = new JLabel("Welcome back!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcomeLabel.setForeground(new Color(33, 37, 41));

        JLabel subtitleLabel = new JLabel("Manage your finances with ease");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        leftPanel.add(welcomeLabel, BorderLayout.NORTH);
        leftPanel.add(subtitleLabel, BorderLayout.CENTER);

        // Right side - Date and Time
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        rightPanel.setOpaque(false);

        JLabel dateLabel = new JLabel(java.time.LocalDate.now().format(
                java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")), JLabel.RIGHT);
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        dateLabel.setForeground(new Color(73, 80, 87));

        timeLabel = new JLabel(java.time.LocalTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("hh:mm:ss a")), JLabel.RIGHT);
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        timeLabel.setForeground(new Color(134, 142, 150));

        rightPanel.add(dateLabel);
        rightPanel.add(timeLabel);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 25, 25));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Balance card - Make it prominent
        JPanel balanceCard = createStatCard("Current Balance", "", Config.SUCCESS_COLOR, true);
        balanceLabel = (JLabel) balanceCard.getComponent(1);

        // Account number card
        JPanel accountCard = createStatCard("Account Number", "", Config.PRIMARY_COLOR, false);
        accountNumberLabel = (JLabel) accountCard.getComponent(1);

        // Account type card
        JPanel typeCard = createStatCard("Account Type", "", Config.SECONDARY_COLOR, false);
        accountTypeLabel = (JLabel) typeCard.getComponent(1);

        // Status card
        JPanel statusCard = createStatCard("Account Status", "", Config.INFO_COLOR, false);
        statusLabel = (JLabel) statusCard.getComponent(1);

        // Bank name card
        JPanel bankCard = createStatCard("Bank Name", "", Config.ACCENT_COLOR, false);
        bankNameLabel = (JLabel) bankCard.getComponent(1);

        // Initial deposit card
        JPanel depositCard = createStatCard("Initial Deposit", "", Config.WARNING_COLOR, false);
        depositLabel = (JLabel) depositCard.getComponent(1);

        panel.add(balanceCard);
        panel.add(accountCard);
        panel.add(typeCard);
        panel.add(statusCard);
        panel.add(bankCard);
        panel.add(depositCard);

        return panel;
    }

    private JPanel createStatCard(String title, String emoji, String colorHex, boolean isLarge) {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(Color.WHITE);

        // Add shadow effect with rounded border
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(12, new Color(233, 236, 239)),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        // Top section - emoji and title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));

        topPanel.add(emojiLabel, BorderLayout.WEST);

        // Value label - larger for balance
        JLabel valueLabel = new JLabel("Loading...", JLabel.LEFT);
        if (isLarge) {
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        } else {
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        }
        valueLabel.setForeground(Color.decode(colorHex));

        // Bottom section - title
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(108, 117, 125));

        bottomPanel.add(titleLabel, BorderLayout.WEST);

        card.add(topPanel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createActionsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setOpaque(false);

        transferButton = createActionButton("Transfer Money", "💸",
                "Send money to other accounts", Config.PRIMARY_COLOR);
        historyButton = createActionButton("Transaction History", "📋",
                "View your transaction history", Config.SECONDARY_COLOR);
        detailsButton = createActionButton("Account Details", "👤",
                "View complete account information", Config.SUCCESS_COLOR);
        logoutButton = createActionButton("Logout", "🚪",
                "Secure logout from system", Config.DANGER_COLOR);

        panel.add(transferButton);
        panel.add(historyButton);
        panel.add(detailsButton);
        panel.add(logoutButton);

        return panel;
    }

    private JButton createActionButton(String text, String emoji, String tooltip, String colorHex) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout(10, 0));

        // Create emoji label
        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        emojiLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Create text label
        JLabel textLabel = new JLabel("<html><center>" + text + "</center></html>");
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Panel to hold labels vertically
        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        contentPanel.setOpaque(false);
        contentPanel.add(emojiLabel);
        contentPanel.add(textLabel);

        button.add(contentPanel, BorderLayout.CENTER);

        Color bgColor = Color.decode(colorHex);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setToolTipText(tooltip);
        button.setBorder(new RoundedBorder(10, bgColor));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(0, 100));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
                button.setBorder(new RoundedBorder(10, bgColor.darker()));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
                button.setBorder(new RoundedBorder(10, bgColor));
            }
        });

        return button;
    }

    private void setupEventListeners() {
        transferButton.addActionListener(e -> openTransfer());
        historyButton.addActionListener(e -> openTransactionHistory());
        detailsButton.addActionListener(e -> openAccountDetails());
        logoutButton.addActionListener(e -> logout());
    }

    private void updateDashboard() {
        if (userAccount != null) {
            // Update balance with formatting
            balanceLabel.setText(String.format("$%,.2f", userAccount.getBalance()));
            balanceLabel.setForeground(Color.decode(Config.SUCCESS_COLOR));

            // Update account number
            accountNumberLabel.setText(userAccount.getAccountNumber());
            accountNumberLabel.setForeground(Color.decode(Config.PRIMARY_COLOR));

            // Update account type
            accountTypeLabel.setText(userAccount.getAccountType());
            accountTypeLabel.setForeground(Color.decode(Config.SECONDARY_COLOR));

            // Update bank name
            bankNameLabel.setText(userAccount.getBankName());
            bankNameLabel.setForeground(Color.decode(Config.ACCENT_COLOR));

            // Update status with color coding
            String status = userAccount.getStatus();
            statusLabel.setText(status);
            if ("ACTIVE".equals(status)) {
                statusLabel.setForeground(Color.decode(Config.SUCCESS_COLOR));
            } else if ("PENDING".equals(status)) {
                statusLabel.setForeground(Color.decode(Config.WARNING_COLOR));
            } else {
                statusLabel.setForeground(Color.decode(Config.DANGER_COLOR));
            }

            // Update initial deposit
            depositLabel.setText(String.format("$%,.2f", userAccount.getInitialDeposit()));
            depositLabel.setForeground(Color.decode(Config.WARNING_COLOR));

            // Update welcome message
            try {
                if (userAccount.getUser() != null && userAccount.getUser().getFirstName() != null) {
                    welcomeLabel.setText("Welcome back, " + userAccount.getUser().getFirstName() + "!");
                } else {
                    welcomeLabel.setText("Welcome back!");
                }
            } catch (Exception e) {
                welcomeLabel.setText("Welcome back!");
            }
        }
    }

    private void startAutoRefresh() {
        Timer timer = new Timer(1000, e -> updateTimeLabel());
        timer.start();
    }

    private void updateTimeLabel() {
        timeLabel.setText(java.time.LocalTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("hh:mm:ss a")));
    }

    private void openTransfer() {
        if (userAccount == null || !"ACTIVE".equals(userAccount.getStatus())) {
            JOptionPane.showMessageDialog(this,
                    "<html><div style='padding: 15px; text-align: center; font-family: Segoe UI;'>" +
                            "<h3 style='color: #F39C12; margin: 0 0 10px 0;'>⚠️ Account Inactive</h3>" +
                            "<p style='color: #7F8C8D;'>Your account is not active.</p>" +
                            "<p style='color: #7F8C8D;'>Please wait for administrator approval or contact support.</p>" +
                            "</div></html>",
                    "Account Inactive",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        new TransferFrame(userId).setVisible(true);
        dispose();
    }

    private void openTransactionHistory() {
        new TransactionHistoryFrame(userId).setVisible(true);
        dispose();
    }

    private void openAccountDetails() {
        new AccountDetailsFrame(userId).setVisible(true);
        dispose();
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><div style='padding: 15px; text-align: center; font-family: Segoe UI;'>" +
                        "<h3 style='color: #E74C3C;'>🚪 Confirm Logout</h3>" +
                        "<p style='color: #7F8C8D;'>Are you sure you want to logout?</p>" +
                        "</div></html>",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (userAccount != null) {
                LoggerUtil.logInfo("User logged out: " + userAccount.getAccountNumber());
            }
            new LoginFrame().setVisible(true);
            dispose();
        }
    }

    // Custom rounded border class
    static class RoundedBorder extends javax.swing.border.AbstractBorder {
        private int radius;
        private Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 1, this.radius + 1);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = insets.top = insets.bottom = this.radius + 1;
            return insets;
        }
    }
}