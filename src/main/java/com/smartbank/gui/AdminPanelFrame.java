package com.smartbank.gui;

import com.smartbank.services.ReportService;
import com.smartbank.utils.Config;
import com.smartbank.utils.LoggerUtil;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class AdminPanelFrame extends JFrame {
    private JLabel totalAccountsLabel, totalBalanceLabel, pendingAccountsLabel, todayTransactionsLabel;
    private JLabel totalUsersLabel, totalTransactionsLabel, timeLabel;
    private JButton accountApprovalButton, viewAccountsButton, viewTransactionsButton, systemStatsButton, logoutButton, bankStatsButton;
    private JPanel mainPanel;

    public AdminPanelFrame() {
        initializeUI();
        loadDashboardStats();
        startAutoRefresh();
    }

    private void initializeUI() {
        setTitle(Config.APP_NAME + " - Admin Panel");
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
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Left side - Title
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Admin Dashboard");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcomeLabel.setForeground(new Color(33, 37, 41));

        JLabel subtitleLabel = new JLabel("Monitor and manage your banking system");
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

        timeLabel = new JLabel("Last updated: " +
                java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm:ss a")),
                JLabel.RIGHT);
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

        // Create stat cards
        JPanel accountsCard = createStatCard("Total Accounts", "👥", Config.PRIMARY_COLOR);
        totalAccountsLabel = (JLabel) accountsCard.getComponent(1);

        JPanel balanceCard = createStatCard("Total Balance", "💰", Config.SUCCESS_COLOR);
        totalBalanceLabel = (JLabel) balanceCard.getComponent(1);

        JPanel pendingCard = createStatCard("Pending Accounts", "⏳", Config.WARNING_COLOR);
        pendingAccountsLabel = (JLabel) pendingCard.getComponent(1);

        JPanel transactionsCard = createStatCard("Today's Transactions", "📊", Config.SECONDARY_COLOR);
        todayTransactionsLabel = (JLabel) transactionsCard.getComponent(1);

        JPanel usersCard = createStatCard("Total Users", "👤", Config.INFO_COLOR);
        totalUsersLabel = (JLabel) usersCard.getComponent(1);

        JPanel allTransactionsCard = createStatCard("Total Transactions", "💳", Config.ACCENT_COLOR);
        totalTransactionsLabel = (JLabel) allTransactionsCard.getComponent(1);

        panel.add(accountsCard);
        panel.add(balanceCard);
        panel.add(pendingCard);
        panel.add(transactionsCard);
        panel.add(usersCard);
        panel.add(allTransactionsCard);

        return panel;
    }

    private JPanel createStatCard(String title, String emoji, String colorHex) {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(233, 236, 239), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        // Add shadow effect
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(12, new Color(233, 236, 239)),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        // Top section - emoji and title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(108, 117, 125));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        topPanel.add(emojiLabel, BorderLayout.WEST);

        // Value label
        JLabel valueLabel = new JLabel("Loading...", JLabel.LEFT);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(Color.decode(colorHex));

        // Bottom section - title
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(titleLabel, BorderLayout.WEST);

        card.add(topPanel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createActionsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 20, 20));
        panel.setOpaque(false);

        accountApprovalButton = createActionButton("Account Approval", "✅",
                "Review and approve pending accounts", Config.PRIMARY_COLOR);
        viewAccountsButton = createActionButton("All Accounts", "👥",
                "Browse all active bank accounts", Config.SECONDARY_COLOR);
        viewTransactionsButton = createActionButton("All Transactions", "📋",
                "View complete transaction history", Config.SUCCESS_COLOR);
        systemStatsButton = createActionButton("System Statistics", "📊",
                "View detailed system analytics", Config.INFO_COLOR);
        bankStatsButton = createActionButton("Bank-wise Stats", "🏦",
                "Statistics by bank", Config.ACCENT_COLOR);
        logoutButton = createActionButton("Logout", "🚪",
                "Secure logout from admin panel", Config.DANGER_COLOR);

        panel.add(accountApprovalButton);
        panel.add(viewAccountsButton);
        panel.add(viewTransactionsButton);
        panel.add(systemStatsButton);
        panel.add(bankStatsButton);
        panel.add(logoutButton);

        return panel;
    }

    private JButton createActionButton(String text, String emoji, String tooltip, String colorHex) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout(10, 0));

        // Create emoji label
        JLabel emojiLabel = new JLabel(emoji);
        emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        emojiLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Create text label
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Panel to hold labels
        JPanel contentPanel = new JPanel(new BorderLayout(10, 0));
        contentPanel.setOpaque(false);
        contentPanel.add(emojiLabel, BorderLayout.WEST);
        contentPanel.add(textLabel, BorderLayout.CENTER);

        button.add(contentPanel, BorderLayout.CENTER);

        Color bgColor = Color.decode(colorHex);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setToolTipText(tooltip);
        button.setBorder(new RoundedBorder(10, bgColor));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(0, 75));

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
        accountApprovalButton.addActionListener(e -> openAccountApproval());
        viewAccountsButton.addActionListener(e -> openAllAccounts());
        viewTransactionsButton.addActionListener(e -> openAllTransactions());
        systemStatsButton.addActionListener(e -> showSystemStats());
        bankStatsButton.addActionListener(e -> showBankStats());
        logoutButton.addActionListener(e -> logout());
    }

    private void loadDashboardStats() {
        Map<String, Object> stats = ReportService.getDashboardStats();

        totalAccountsLabel.setText(String.valueOf(stats.getOrDefault("totalAccounts", 0)));
        totalBalanceLabel.setText(String.format("$%,.2f", stats.getOrDefault("totalBalance", 0.0)));
        pendingAccountsLabel.setText(String.valueOf(stats.getOrDefault("pendingAccounts", 0)));
        todayTransactionsLabel.setText(String.valueOf(stats.getOrDefault("todayTransactions", 0)));
        totalUsersLabel.setText(String.valueOf(stats.getOrDefault("totalUsers", 0)));
        totalTransactionsLabel.setText(String.valueOf(stats.getOrDefault("totalTransactions", 0)));
    }

    private void startAutoRefresh() {
        Timer timer = new Timer(30000, e -> {
            loadDashboardStats();
            updateTimeLabel();
        });
        timer.start();
    }

    private void updateTimeLabel() {
        timeLabel.setText("Last updated: " +
                java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm:ss a")));
    }

    private void openAccountApproval() {
        new AccountApprovalFrame().setVisible(true);
        dispose();
    }

    private void openAllAccounts() {
        JOptionPane.showMessageDialog(this,
                "<html><div style='padding: 15px; font-family: Segoe UI;'>" +
                        "<h2 style='color: #2C3E50; margin: 0 0 15px 0;'>📋 View All Accounts</h2>" +
                        "<p style='color: #7F8C8D; margin: 0 0 15px 0;'>Comprehensive account management system</p>" +
                        "<ul style='color: #34495E; line-height: 1.8;'>" +
                        "<li>Advanced search and filter capabilities</li>" +
                        "<li>Real-time account status monitoring</li>" +
                        "<li>Bulk export to CSV/PDF formats</li>" +
                        "<li>Detailed account analytics and insights</li>" +
                        "<li>Quick actions for account management</li>" +
                        "</ul>" +
                        "</div></html>",
                "All Accounts",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void openAllTransactions() {
        JOptionPane.showMessageDialog(this,
                "<html><div style='padding: 15px; font-family: Segoe UI;'>" +
                        "<h2 style='color: #2C3E50; margin: 0 0 15px 0;'>💳 All Transactions</h2>" +
                        "<p style='color: #7F8C8D; margin: 0 0 15px 0;'>Complete transaction monitoring system</p>" +
                        "<ul style='color: #34495E; line-height: 1.8;'>" +
                        "<li>Advanced date range and type filtering</li>" +
                        "<li>Export to multiple formats (PDF/Excel/CSV)</li>" +
                        "<li>Transaction trend analytics and graphs</li>" +
                        "<li>Automated fraud detection alerts</li>" +
                        "<li>Real-time transaction monitoring</li>" +
                        "</ul>" +
                        "</div></html>",
                "All Transactions",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showSystemStats() {
        Map<String, Object> stats = ReportService.getDashboardStats();

        StringBuilder statsMessage = new StringBuilder();
        statsMessage.append("<html><div style='padding: 20px; font-family: Segoe UI;'>");
        statsMessage.append("<h2 style='color: #2C3E50; margin: 0 0 20px 0;'>📊 System Statistics</h2>");
        statsMessage.append("<div style='background: #ECF0F1; padding: 20px; border-radius: 8px; border-left: 4px solid #3498DB;'>");

        statsMessage.append("<h3 style='color: #34495E; margin: 0 0 15px 0;'>Overall Performance</h3>");
        statsMessage.append("<table style='width: 100%; color: #2C3E50;'>");
        statsMessage.append("<tr><td style='padding: 8px;'><b>👥 Total Users:</b></td><td style='text-align: right;'>")
                .append(stats.get("totalUsers")).append("</td></tr>");
        statsMessage.append("<tr><td style='padding: 8px;'><b>🏦 Total Accounts:</b></td><td style='text-align: right;'>")
                .append(stats.get("totalAccounts")).append("</td></tr>");
        statsMessage.append("<tr><td style='padding: 8px;'><b>💰 Total Balance:</b></td><td style='text-align: right;'>$")
                .append(String.format("%,.2f", stats.get("totalBalance"))).append("</td></tr>");
        statsMessage.append("<tr><td style='padding: 8px;'><b>⏳ Pending Accounts:</b></td><td style='text-align: right;'>")
                .append(stats.get("pendingAccounts")).append("</td></tr>");
        statsMessage.append("<tr><td style='padding: 8px;'><b>📊 Today's Transactions:</b></td><td style='text-align: right;'>")
                .append(stats.get("todayTransactions")).append("</td></tr>");
        statsMessage.append("<tr><td style='padding: 8px;'><b>💳 Total Transactions:</b></td><td style='text-align: right;'>")
                .append(stats.get("totalTransactions")).append("</td></tr>");
        statsMessage.append("</table>");

        statsMessage.append("</div>");
        statsMessage.append("<p style='color: #95A5A6; font-size: 12px; margin: 15px 0 0 0; text-align: center;'>Last updated: ")
                .append(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm:ss a")))
                .append("</p>");
        statsMessage.append("</div></html>");

        JOptionPane.showMessageDialog(this, statsMessage.toString(), "System Statistics", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showBankStats() {
        Map<String, Object> bankStats = ReportService.getBankWiseStats();

        StringBuilder statsMessage = new StringBuilder();
        statsMessage.append("<html><div style='padding: 20px; font-family: Segoe UI;'>");
        statsMessage.append("<h2 style='color: #2C3E50; margin: 0 0 20px 0;'>🏦 Bank-wise Statistics</h2>");
        statsMessage.append("<div style='background: #ECF0F1; padding: 20px; border-radius: 8px; border-left: 4px solid #27AE60;'>");

        if (bankStats.isEmpty()) {
            statsMessage.append("<p style='color: #7F8C8D;'>No bank statistics available yet.</p>");
        } else {
            statsMessage.append("<h3 style='color: #34495E; margin: 0 0 15px 0;'>Account Distribution</h3>");
            statsMessage.append("<table style='width: 100%; color: #2C3E50;'>");

            for (String key : bankStats.keySet()) {
                if (key.endsWith("_count")) {
                    String bankName = key.replace("_count", "");
                    int count = (Integer) bankStats.get(key);
                    double balance = (Double) bankStats.getOrDefault(bankName + "_balance", 0.0);

                    statsMessage.append("<tr style='border-bottom: 1px solid #BDC3C7;'>");
                    statsMessage.append("<td style='padding: 12px;'><b>").append(bankName.toUpperCase()).append("</b></td>");
                    statsMessage.append("<td style='text-align: right; padding: 12px;'>").append(count).append(" accounts</td>");
                    statsMessage.append("<td style='text-align: right; padding: 12px;'><b>$")
                            .append(String.format("%,.2f", balance)).append("</b></td>");
                    statsMessage.append("</tr>");
                }
            }
            statsMessage.append("</table>");
        }

        statsMessage.append("</div>");
        statsMessage.append("</div></html>");

        JOptionPane.showMessageDialog(this, statsMessage.toString(), "Bank Statistics", JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><div style='padding: 15px; text-align: center; font-family: Segoe UI;'>" +
                        "<h3 style='color: #E74C3C;'>🚪 Confirm Logout</h3>" +
                        "<p style='color: #7F8C8D;'>Are you sure you want to logout from the admin panel?</p>" +
                        "</div></html>",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            LoggerUtil.logInfo("Admin logged out");
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