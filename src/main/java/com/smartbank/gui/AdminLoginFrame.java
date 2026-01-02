package com.smartbank.gui;

import com.smartbank.services.AuthService;
import com.smartbank.utils.Config;
import com.smartbank.utils.LoggerUtil;
import javax.swing.*;
import java.awt.*;

public class AdminLoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, backButton;
    private JLabel titleLabel;

    public AdminLoginFrame() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle(Config.APP_NAME + " - Admin Portal");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(550, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with professional gradient
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                Color color1 = Color.decode("#2C3E50");
                Color color2 = Color.decode("#34495E");
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        // Header section
        JPanel headerPanel = createHeaderPanel();

        // Login form panel
        JPanel formPanel = createFormPanel();

        // Add all to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Event listeners
        setupEventListeners();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));

        // Icon panel
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        iconPanel.setOpaque(false);
        JLabel iconLabel = new JLabel("");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        iconPanel.add(iconLabel);

        titleLabel = new JLabel("Admin Portal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Secure Administrative Access");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(220, 220, 220));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(iconPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(subtitleLabel);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        // Username field
        JPanel usernamePanel = createInputPanel(" Username:", usernameField = new JTextField());

        // Password field
        JPanel passwordPanel = createInputPanel(" Password:", passwordField = new JPasswordField());

        // Add components with proper spacing
        formPanel.add(usernamePanel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(passwordPanel);
        formPanel.add(Box.createVerticalStrut(35));

        // Login button
        loginButton = createModernButton(" Login to Admin Panel", Config.WARNING_COLOR);
        formPanel.add(loginButton);
        formPanel.add(Box.createVerticalStrut(15));

        // Back button
        backButton = createModernButton("← Back to Main Login", Config.SECONDARY_COLOR);
        formPanel.add(backButton);

        // Security notice
        formPanel.add(Box.createVerticalStrut(30));
        JPanel noticePanel = createSecurityNotice();
        formPanel.add(noticePanel);

        return formPanel;
    }

    private JPanel createInputPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(400, 80));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setMaximumSize(new Dimension(400, 45));
        field.setPreferredSize(new Dimension(400, 45));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Add focus effect
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.decode(Config.WARNING_COLOR), 2, true),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
        });

        panel.add(label);
        panel.add(Box.createVerticalStrut(8));
        panel.add(field);

        return panel;
    }

    private JPanel createSecurityNotice() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(true);
        panel.setBackground(new Color(52, 73, 94, 200));
        panel.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(241, 196, 15), 1, true),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel icon = new JLabel("⚠️", JLabel.CENTER);
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel noticeTitle = new JLabel("Security Notice");
        noticeTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        noticeTitle.setForeground(new Color(241, 196, 15));
        noticeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel noticeText = new JLabel("<html><div style='text-align: center; color: #ECF0F1;'>"
                + "Unauthorized access is strictly prohibited.<br>"
                + "All login attempts are logged and monitored."
                + "</div></html>");
        noticeText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        noticeText.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(icon);
        panel.add(Box.createVerticalStrut(8));
        panel.add(noticeTitle);
        panel.add(Box.createVerticalStrut(8));
        panel.add(noticeText);

        return panel;
    }

    private JButton createModernButton(String text, String color) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(400, 50));
        button.setPreferredSize(new Dimension(400, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(Color.decode(color));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Modern hover effect
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
        loginButton.addActionListener(e -> handleAdminLogin());
        backButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        // Enter key support
        usernameField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> handleAdminLogin());
    }

    private void handleAdminLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty()) {
            showError("Please enter admin username");
            return;
        }

        if (password.isEmpty()) {
            showError("Please enter admin password");
            return;
        }

        // Show loading
        loginButton.setText("⏳ Authenticating...");
        loginButton.setEnabled(false);

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return AuthService.validateAdminLogin(username, password);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        new AdminPanelFrame().setVisible(true);
                        dispose();
                        LoggerUtil.logInfo("Admin logged in: " + username);
                    } else {
                        showError("Invalid admin credentials");
                    }
                } catch (Exception e) {
                    showError("Login failed: " + e.getMessage());
                } finally {
                    loginButton.setText("🔐 Login to Admin Panel");
                    loginButton.setEnabled(true);
                }
            }
        }.execute();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                "<html><div style='text-align: center; padding: 20px; font-family: Segoe UI;'>" +
                        "<h2 style='color: #E74C3C; margin: 0 0 10px 0;'>❌ Admin Login Error</h2>" +
                        "<p style='color: #2C3E50;'>" + message + "</p>" +
                        "</div></html>",
                "Admin Login Error",
                JOptionPane.ERROR_MESSAGE);
    }
}