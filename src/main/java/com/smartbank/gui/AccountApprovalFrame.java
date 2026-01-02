package com.smartbank.gui;

import com.smartbank.models.Account;
import com.smartbank.services.AccountService;
import com.smartbank.services.MailService;
import com.smartbank.utils.Config;
import com.smartbank.utils.LoggerUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class AccountApprovalFrame extends JFrame {
    private JTable pendingAccountsTable;
    private JButton approveButton, rejectButton, backButton, refreshButton, viewDetailsButton;
    private JLabel statsLabel;
    private DefaultTableModel tableModel;

    public AccountApprovalFrame() {
        initializeUI();
        loadPendingAccounts();
    }

    private void initializeUI() {
        setTitle(Config.APP_NAME + " - Account Approval");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setResizable(true);

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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Header panel
        JPanel headerPanel = createHeaderPanel();

        // Stats panel
        JPanel statsPanel = createStatsPanel();

        // Table panel
        JPanel tablePanel = createTablePanel();

        // Buttons panel
        JPanel buttonPanel = createButtonPanel();

        // Combine header and stats
        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setOpaque(false);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(statsPanel, BorderLayout.CENTER);

        // Add all to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Event listeners
        setupEventListeners();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel headerLabel = new JLabel(" Pending Account Approvals");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        headerLabel.setForeground(Color.decode(Config.PRIMARY_COLOR));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Review and approve customer account applications");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(headerLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitleLabel);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        statsLabel = new JLabel(" Loading pending accounts...");
        statsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statsLabel.setForeground(Color.decode(Config.WARNING_COLOR));

        // Create a rounded panel for stats
        JPanel statsContainer = new JPanel(new FlowLayout(FlowLayout.CENTER)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        statsContainer.setOpaque(false);
        statsContainer.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        statsContainer.add(statsLabel);

        panel.add(statsContainer);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        String[] columns = {"Account No.", "Customer Name", "Email", "Phone", "Bank", "Type", "Initial Deposit", "Created Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        pendingAccountsTable = new JTable(tableModel);
        pendingAccountsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pendingAccountsTable.setRowHeight(40);
        pendingAccountsTable.setShowGrid(true);
        pendingAccountsTable.setGridColor(new Color(230, 230, 230));
        pendingAccountsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pendingAccountsTable.setSelectionBackground(Color.decode("#E3F2FD"));
        pendingAccountsTable.setSelectionForeground(Color.BLACK);

        // Style table header
        JTableHeader header = pendingAccountsTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(Color.decode(Config.PRIMARY_COLOR));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(BorderFactory.createEmptyBorder());

        // Center align columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < pendingAccountsTable.getColumnCount(); i++) {
            pendingAccountsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Set column widths
        pendingAccountsTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        pendingAccountsTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        pendingAccountsTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        pendingAccountsTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        pendingAccountsTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        pendingAccountsTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        pendingAccountsTable.getColumnModel().getColumn(6).setPreferredWidth(130);
        pendingAccountsTable.getColumnModel().getColumn(7).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(pendingAccountsTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode(Config.WARNING_COLOR), 2, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Add title to table
        JPanel tableTitlePanel = new JPanel(new BorderLayout());
        tableTitlePanel.setOpaque(false);
        tableTitlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel tableTitle = new JLabel(" Accounts Awaiting Approval");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(Color.decode(Config.WARNING_COLOR));

        tableTitlePanel.add(tableTitle, BorderLayout.WEST);

        panel.add(tableTitlePanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        viewDetailsButton = createStyledButton("👁 View Details", Config.INFO_COLOR);
        approveButton = createStyledButton(" Approve Account", Config.SUCCESS_COLOR);
        rejectButton = createStyledButton(" Reject Account", Config.DANGER_COLOR);
        refreshButton = createStyledButton(" Refresh", Config.PRIMARY_COLOR);
        backButton = createStyledButton("← Back to Admin Panel", Config.SECONDARY_COLOR);

        panel.add(viewDetailsButton);
        panel.add(approveButton);
        panel.add(rejectButton);
        panel.add(refreshButton);
        panel.add(backButton);

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

    private void setupEventListeners() {
        approveButton.addActionListener(e -> approveSelectedAccount());
        rejectButton.addActionListener(e -> rejectSelectedAccount());
        refreshButton.addActionListener(e -> loadPendingAccounts());
        backButton.addActionListener(e -> {
            new AdminPanelFrame().setVisible(true);
            dispose();
        });
        viewDetailsButton.addActionListener(e -> viewSelectedAccountDetails());

        // Double-click to view details
        pendingAccountsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    viewSelectedAccountDetails();
                }
            }
        });
    }

    private void loadPendingAccounts() {
        // Show loading state
        approveButton.setEnabled(false);
        rejectButton.setEnabled(false);
        viewDetailsButton.setEnabled(false);
        statsLabel.setText(" Loading pending accounts...");

        tableModel.setRowCount(0); // Clear existing data

        // Use SwingWorker for async loading
        new SwingWorker<List<Account>, Void>() {
            @Override
            protected List<Account> doInBackground() throws Exception {
                return AccountService.getPendingAccounts();
            }

            @Override
            protected void done() {
                try {
                    List<Account> pendingAccounts = get();

                    for (Account account : pendingAccounts) {
                        tableModel.addRow(new Object[]{
                                account.getAccountNumber(),
                                account.getUser().getFullName(),
                                account.getUser().getEmail(),
                                account.getUser().getPhone(),
                                account.getBankName(),
                                account.getAccountType(),
                                String.format("$%.2f", account.getInitialDeposit()),
                                account.getCreatedAt().toLocalDate().toString()
                        });
                    }

                    // Update stats
                    updateStats(pendingAccounts.size());

                    if (tableModel.getRowCount() == 0) {
                        tableModel.addRow(new Object[]{
                                "No pending accounts", "", "", "", "", "", "", ""
                        });
                    }

                    // Enable buttons
                    approveButton.setEnabled(tableModel.getRowCount() > 0);
                    rejectButton.setEnabled(tableModel.getRowCount() > 0);
                    viewDetailsButton.setEnabled(tableModel.getRowCount() > 0);

                } catch (Exception e) {
                    showError("Failed to load pending accounts: " + e.getMessage());
                    LoggerUtil.logError("Error loading pending accounts");
                }
            }
        }.execute();
    }

    private void updateStats(int count) {
        if (count == 0) {
            statsLabel.setText(" All accounts have been processed");
            statsLabel.setForeground(Color.decode(Config.SUCCESS_COLOR));
        } else {
            statsLabel.setText(" " + count + " Account" + (count == 1 ? "" : "s") + " Awaiting Approval");
            statsLabel.setForeground(Color.decode(Config.WARNING_COLOR));
        }
    }

    private void viewSelectedAccountDetails() {
        int selectedRow = pendingAccountsTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Please select an account to view details");
            return;
        }

        String accountNumber = (String) pendingAccountsTable.getValueAt(selectedRow, 0);
        if (accountNumber.equals("No pending accounts")) {
            return;
        }

        Account account = AccountService.getAccountByNumber(accountNumber);

        if (account != null) {
            String details = String.format(
                    "<html><div style='width: 450px; padding: 10px; font-family: Segoe UI;'>" +
                            "<h2 style='color: #2C3E50; margin: 0 0 15px 0; border-bottom: 2px solid #3498DB; padding-bottom: 10px;'>Account Details</h2>" +
                            "<div style='background: #F8F9FA; padding: 15px; border-radius: 8px; margin: 10px 0;'>" +
                            "<h3 style='color: #2980B9; margin: 0 0 10px 0;'>👤 Personal Information</h3>" +
                            "<table style='width: 100%%; border-collapse: collapse;'>" +
                            "<tr><td style='padding: 5px 0; color: #555;'><b>Name:</b></td><td style='text-align: right;'>%s</td></tr>" +
                            "<tr><td style='padding: 5px 0; color: #555;'><b>Email:</b></td><td style='text-align: right;'>%s</td></tr>" +
                            "<tr><td style='padding: 5px 0; color: #555;'><b>Phone:</b></td><td style='text-align: right;'>%s</td></tr>" +
                            "<tr><td style='padding: 5px 0; color: #555;'><b>Date of Birth:</b></td><td style='text-align: right;'>%s</td></tr>" +
                            "<tr><td style='padding: 5px 0; color: #555;'><b>Address:</b></td><td style='text-align: right;'>%s</td></tr>" +
                            "</table>" +
                            "</div>" +
                            "<div style='background: #E8F5E9; padding: 15px; border-radius: 8px; margin: 10px 0;'>" +
                            "<h3 style='color: #27AE60; margin: 0 0 10px 0;'>🏦 Account Information</h3>" +
                            "<table style='width: 100%%; border-collapse: collapse;'>" +
                            "<tr><td style='padding: 5px 0; color: #555;'><b>Account Number:</b></td><td style='text-align: right;'>%s</td></tr>" +
                            "<tr><td style='padding: 5px 0; color: #555;'><b>Bank:</b></td><td style='text-align: right;'>%s</td></tr>" +
                            "<tr><td style='padding: 5px 0; color: #555;'><b>Account Type:</b></td><td style='text-align: right;'>%s</td></tr>" +
                            "<tr><td style='padding: 5px 0; color: #555;'><b>Initial Deposit:</b></td><td style='text-align: right; color: #27AE60; font-weight: bold;'>$%.2f</td></tr>" +
                            "<tr><td style='padding: 5px 0; color: #555;'><b>Application Date:</b></td><td style='text-align: right;'>%s</td></tr>" +
                            "</table>" +
                            "</div>" +
                            "</div></html>",
                    account.getUser().getFullName(),
                    account.getUser().getEmail(),
                    account.getUser().getPhone(),
                    account.getUser().getDateOfBirth(),
                    account.getUser().getAddress(),
                    account.getAccountNumber(),
                    account.getBankName(),
                    account.getAccountType(),
                    account.getInitialDeposit(),
                    account.getCreatedAt().toLocalDate()
            );

            JOptionPane.showMessageDialog(this, details, "Account Details - " + accountNumber, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void approveSelectedAccount() {
        int selectedRow = pendingAccountsTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Please select an account to approve");
            return;
        }

        String accountNumber = (String) pendingAccountsTable.getValueAt(selectedRow, 0);
        if (accountNumber.equals("No pending accounts")) {
            return;
        }

        String customerName = (String) pendingAccountsTable.getValueAt(selectedRow, 1);
        String customerEmail = (String) pendingAccountsTable.getValueAt(selectedRow, 2);
        String bankName = (String) pendingAccountsTable.getValueAt(selectedRow, 4);
        String accountType = (String) pendingAccountsTable.getValueAt(selectedRow, 5);

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format(
                        "<html><div style='width: 350px; text-align: center; font-family: Segoe UI;'>" +
                                "<h2 style='color: #27AE60; margin: 0 0 15px 0;'>✅ Approve Account</h2>" +
                                "<p style='font-size: 14px;'>Approve account application for:</p>" +
                                "<p style='font-size: 16px; font-weight: bold; color: #2C3E50; margin: 10px 0;'>%s</p>" +
                                "<div style='background: #F8F9FA; padding: 15px; border-radius: 8px; margin: 15px 0; text-align: left;'>" +
                                "<p style='margin: 5px 0;'><b>Account:</b> %s</p>" +
                                "<p style='margin: 5px 0;'><b>Bank:</b> %s</p>" +
                                "<p style='margin: 5px 0;'><b>Type:</b> %s</p>" +
                                "</div>" +
                                "<p style='color: #7F8C8D; font-size: 12px;'>An approval email will be sent to the customer.</p>" +
                                "</div></html>",
                        customerName, accountNumber, bankName, accountType
                ),
                "Confirm Approval",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            approveButton.setEnabled(false);
            approveButton.setText(" Processing...");

            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    boolean success = AccountService.updateAccountStatus(accountNumber, "ACTIVE");
                    if (success) {
                        MailService.sendAccountApprovalEmail(customerEmail, customerName, accountNumber, accountType, bankName);
                    }
                    return success;
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            showSuccess("Account approved successfully!\n✉️ Confirmation email sent to customer.");
                            LoggerUtil.logInfo("Account approved: " + accountNumber);
                            loadPendingAccounts();
                        } else {
                            showError("Failed to approve account");
                        }
                    } catch (Exception e) {
                        showError("Error approving account: " + e.getMessage());
                    } finally {
                        approveButton.setText("✅ Approve Account");
                        approveButton.setEnabled(true);
                    }
                }
            }.execute();
        }
    }

    private void rejectSelectedAccount() {
        int selectedRow = pendingAccountsTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Please select an account to reject");
            return;
        }

        String accountNumber = (String) pendingAccountsTable.getValueAt(selectedRow, 0);
        if (accountNumber.equals("No pending accounts")) {
            return;
        }

        String customerName = (String) pendingAccountsTable.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format(
                        "<html><div style='width: 350px; text-align: center; font-family: Segoe UI;'>" +
                                "<h2 style='color: #E74C3C; margin: 0 0 15px 0;'>❌ Reject Account</h2>" +
                                "<p style='font-size: 14px;'>Reject account application for:</p>" +
                                "<p style='font-size: 16px; font-weight: bold; color: #2C3E50; margin: 10px 0;'>%s</p>" +
                                "<div style='background: #FFEBEE; padding: 15px; border-radius: 8px; margin: 15px 0;'>" +
                                "<p style='margin: 5px 0; color: #C0392B;'><b>Account:</b> %s</p>" +
                                "</div>" +
                                "<p style='color: #E74C3C; font-size: 13px; font-weight: bold;'>⚠️ This action cannot be undone.</p>" +
                                "</div></html>",
                        customerName, accountNumber
                ),
                "Confirm Rejection",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            rejectButton.setEnabled(false);
            rejectButton.setText("⏳ Processing...");

            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return AccountService.updateAccountStatus(accountNumber, "SUSPENDED");
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            showSuccess("Account rejected successfully!");
                            LoggerUtil.logInfo("Account rejected: " + accountNumber);
                            loadPendingAccounts();
                        } else {
                            showError("Failed to reject account");
                        }
                    } catch (Exception e) {
                        showError("Error rejecting account: " + e.getMessage());
                    } finally {
                        rejectButton.setText("❌ Reject Account");
                        rejectButton.setEnabled(true);
                    }
                }
            }.execute();
        }
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this,
                "<html><div style='text-align: center; padding: 20px; font-family: Segoe UI;'>" +
                        "<h2 style='color: #27AE60; margin: 0 0 10px 0;'>✅ Success</h2>" +
                        "<p style='color: #2C3E50;'>" + message + "</p>" +
                        "</div></html>",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
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

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this,
                "<html><div style='text-align: center; padding: 20px; font-family: Segoe UI;'>" +
                        "<h2 style='color: #F39C12; margin: 0 0 10px 0;'>⚠️ Warning</h2>" +
                        "<p style='color: #2C3E50;'>" + message + "</p>" +
                        "</div></html>",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
    }
}