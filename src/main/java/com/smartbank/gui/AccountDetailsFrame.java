package com.smartbank.gui;

import com.smartbank.models.Account;
import com.smartbank.models.User;
import com.smartbank.services.AccountService;
import com.smartbank.utils.Config;
import javax.swing.*;
import java.awt.*;

public class AccountDetailsFrame extends JFrame {
    private int userId;
    private Account userAccount;

    public AccountDetailsFrame(int userId) {
        this.userId = userId;
        this.userAccount = AccountService.getAccountByUserId(userId);
        initializeUI();
    }

    private void initializeUI() {
        setTitle(Config.APP_NAME + " - Account Details");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 800);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with modern gradient
        JPanel mainPanel = new JPanel(new BorderLayout()) {
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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 35, 30, 35));

        // Header panel
        JPanel headerPanel = createHeaderPanel();

        // Details panel with scrolling
        JScrollPane scrollPane = new JScrollPane(createDetailsPanel());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Button panel
        JPanel buttonPanel = createButtonPanel();

        // Add all to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        JLabel headerLabel = new JLabel("Account Details");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        headerLabel.setForeground(Color.decode(Config.PRIMARY_COLOR));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Complete overview of your account information");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(headerLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(subtitleLabel);

        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        User user = userAccount.getUser();

        // Personal Information Section
        panel.add(createDetailSection("👤 Personal Information",
                new String[][]{
                        {"Full Name", user.getFullName()},
                        {"Email Address", user.getEmail()},
                        {"Phone Number", user.getPhone()},
                        {"Date of Birth", user.getDateOfBirth().toString()},
                        {"Residential Address", user.getAddress()}
                },
                "#3498DB"));

        panel.add(Box.createVerticalStrut(20));

        // Account Information Section
        panel.add(createDetailSection("Account Information",
                new String[][]{
                        {"Account Number", userAccount.getAccountNumber()},
                        {"Bank Name", userAccount.getBankName()},
                        {"Account Type", userAccount.getAccountType()},
                        {"Account Status", getStatusWithIcon(userAccount.getStatus())},
                        {"Current Balance", String.format("$%.2f", userAccount.getBalance())},
                        {"Initial Deposit", String.format("$%.2f", userAccount.getInitialDeposit())},
                        {"Member Since", userAccount.getCreatedAt().toLocalDate().toString()}
                },
                "#27AE60"));

        panel.add(Box.createVerticalStrut(20));

        // Login Information Section
        panel.add(createDetailSection("Login Information",
                new String[][]{
                        {"Username/Email", user.getEmail()},
                        {"Default Password", "Your date of birth (YYYY-MM-DD)"},
                        {"Security Note", "Change your password after first login"}
                },
                "#F39C12"));

        return panel;
    }

    private String getStatusWithIcon(String status) {
        switch (status) {
            case "ACTIVE": return "✅ ACTIVE";
            case "PENDING": return "⏳ PENDING";
            case "SUSPENDED": return "❌ SUSPENDED";
            default: return status;
        }
    }

    private JPanel createDetailSection(String title, String[][] details, String colorHex) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setOpaque(true);
        sectionPanel.setBackground(Color.WHITE);
        sectionPanel.setMaximumSize(new Dimension(600, Integer.MAX_VALUE));
        sectionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sectionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode(colorHex), 2, true),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        // Section title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.decode(colorHex));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        sectionPanel.add(titleLabel);

        // Add separator
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setForeground(Color.decode(colorHex));
        sectionPanel.add(separator);
        sectionPanel.add(Box.createVerticalStrut(15));

        // Add details
        for (int i = 0; i < details.length; i++) {
            JPanel detailPanel = createDetailRow(details[i][0], details[i][1]);
            sectionPanel.add(detailPanel);

            if (i < details.length - 1) {
                sectionPanel.add(Box.createVerticalStrut(12));
            }
        }

        return sectionPanel;
    }

    private JPanel createDetailRow(String key, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(550, Integer.MAX_VALUE));

        // Key label
        JLabel keyLabel = new JLabel(key + ":");
        keyLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        keyLabel.setForeground(new Color(80, 80, 80));
        keyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Value label
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueLabel.setForeground(new Color(40, 40, 40));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Highlight balance if it's the current balance row
        if (key.equals("Current Balance")) {
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            valueLabel.setForeground(Color.decode("#27AE60"));
        }

        // Highlight status
        if (key.equals("Account Status")) {
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        }

        panel.add(keyLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(valueLabel);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton backButton = createModernButton("← Back to Dashboard", Config.PRIMARY_COLOR);
        backButton.addActionListener(e -> {
            new DashboardFrame(userId).setVisible(true);
            dispose();
        });

        panel.add(backButton);
        return panel;
    }

    private JButton createModernButton(String text, String color) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(250, 50));
        button.setBackground(Color.decode(color));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
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
}