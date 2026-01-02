package com.smartbank.gui;

import com.smartbank.services.AuthService;
import com.smartbank.utils.Config;
import com.smartbank.utils.LoggerUtil;
import com.smartbank.utils.Validator;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton, adminButton;
    private JLabel titleLabel, subtitleLabel;

    public LoginFrame() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle(Config.APP_NAME + " - Secure Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with modern gradient
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                Color color1 = Color.decode("#2C3E50");
                Color color2 = Color.decode("#4A6491");
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        // Header section
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        titleLabel = new JLabel("Welcome to " + Config.APP_NAME);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        subtitleLabel = new JLabel("Secure Banking Portal");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(220, 220, 220));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createVerticalStrut(40));

        // Login form panel
        JPanel formPanel = createFormPanel();

        // Add all to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Event listeners
        setupEventListeners();
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        // Email field
        JPanel emailPanel = createInputPanel("Email Address:", emailField = new JTextField());

        // Password field
        JPanel passwordPanel = createInputPanel("Password:", passwordField = new JPasswordField());

        // Add components with proper spacing
        formPanel.add(emailPanel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(passwordPanel);
        formPanel.add(Box.createVerticalStrut(35));

        // Login button
        loginButton = createModernButton(" Login to Account", Config.ACCENT_COLOR);
        formPanel.add(loginButton);
        formPanel.add(Box.createVerticalStrut(25));

        // Separator
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(400, 1));
        separator.setForeground(new Color(255, 255, 255, 100));
        formPanel.add(separator);
        formPanel.add(Box.createVerticalStrut(25));

        // Options panel
        registerButton = createModernButton(" Create New Account", Config.SUCCESS_COLOR);
        adminButton = createModernButton(" Admin Portal", Config.WARNING_COLOR);

        formPanel.add(registerButton);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(adminButton);

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

        // Add subtle focus effect
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.decode(Config.ACCENT_COLOR), 2, true),
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

        // Modern hover effect with smooth transition
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
        loginButton.addActionListener(e -> handleUserLogin());
        registerButton.addActionListener(e -> openRegistration());
        adminButton.addActionListener(e -> openAdminLogin());

        // Enter key support
        emailField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> handleUserLogin());
    }

    private void handleUserLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (!Validator.isValidEmail(email)) {
            showError("Please enter a valid email address");
            return;
        }

        if (password.isEmpty()) {
            showError("Please enter your password");
            return;
        }

        // Show loading
        loginButton.setText("⏳ Authenticating...");
        loginButton.setEnabled(false);

        // Validate user credentials with password
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return AuthService.validateUserLogin(email, password);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        int userId = AuthService.getUserIdByEmail(email);
                        if (userId != -1) {
                            new DashboardFrame(userId).setVisible(true);
                            dispose();
                            LoggerUtil.logInfo("User logged in: " + email);
                        } else {
                            showError("Account not found. Please register first.");
                        }
                    } else {
                        showError("Invalid login credentials or account not approved");
                    }
                } catch (Exception e) {
                    showError("Login failed: " + e.getMessage());
                } finally {
                    loginButton.setText("🚀 Login to Account");
                    loginButton.setEnabled(true);
                }
            }
        }.execute();
    }

    private void openRegistration() {
        new RegisterFrame().setVisible(true);
        dispose();
    }

    private void openAdminLogin() {
        new AdminLoginFrame().setVisible(true);
        dispose();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
    }
}