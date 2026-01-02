package com.smartbank.gui;

import com.smartbank.models.Account;
import com.smartbank.models.Transaction;
import com.smartbank.services.AccountService;
import com.smartbank.services.TransactionService;
import com.smartbank.utils.Config;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TransactionHistoryFrame extends JFrame {
    private int userId;
    private Account userAccount;
    private JTable transactionTable;
    private JButton backButton, refreshButton, exportButton;
    private JLabel summaryLabel;

    public TransactionHistoryFrame(int userId) {
        this.userId = userId;
        this.userAccount = AccountService.getAccountByUserId(userId);
        initializeUI();
        loadTransactionHistory();
    }

    private void initializeUI() {
        setTitle(Config.APP_NAME + " - Transaction History");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setResizable(true);

        // Main panel with modern gradient
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15)) {
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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Top panel combining header and account info
        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setOpaque(false);

        // Header
        JLabel headerLabel = new JLabel("Transaction History", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        headerLabel.setForeground(Color.decode(Config.PRIMARY_COLOR));

        // Account info panel
        JPanel infoPanel = createInfoPanel();

        topPanel.add(headerLabel, BorderLayout.NORTH);
        topPanel.add(infoPanel, BorderLayout.CENTER);

        // Table panel
        JPanel tablePanel = createTablePanel();

        // Bottom panel combining summary and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));
        bottomPanel.setOpaque(false);

        JPanel summaryPanel = createSummaryPanel();
        JPanel buttonPanel = createButtonPanel();

        bottomPanel.add(summaryPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        // Add all to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Event listeners
        setupEventListeners();
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // Create a styled info box
        JPanel infoBox = new JPanel();
        infoBox.setBackground(Color.WHITE);
        infoBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode(Config.PRIMARY_COLOR), 2),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        infoBox.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 0));

        JLabel accountInfo = new JLabel(
                "Account: " + userAccount.getAccountNumber() +
                        " | Bank: " + userAccount.getBankName() +
                        " | Balance: $" + String.format("%.2f", userAccount.getBalance())
        );
        accountInfo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        accountInfo.setForeground(Color.decode(Config.PRIMARY_COLOR));

        infoBox.add(accountInfo);
        panel.add(infoBox, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        String[] columns = {"Date & Time", "Type", "From/To Account", "Amount", "Description", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };

        transactionTable = new JTable(model);
        transactionTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        transactionTable.setRowHeight(35);
        transactionTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        transactionTable.getTableHeader().setBackground(Color.decode(Config.PRIMARY_COLOR));
        transactionTable.getTableHeader().setForeground(Color.WHITE);
        transactionTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.setSelectionBackground(new Color(220, 240, 255));
        transactionTable.setSelectionForeground(Color.BLACK);
        transactionTable.setGridColor(new Color(230, 230, 230));
        transactionTable.setShowGrid(true);
        transactionTable.setIntercellSpacing(new Dimension(1, 1));

        // Set column widths
        transactionTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Date
        transactionTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Type
        transactionTable.getColumnModel().getColumn(2).setPreferredWidth(180); // From/To
        transactionTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Amount
        transactionTable.getColumnModel().getColumn(4).setPreferredWidth(250); // Description
        transactionTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Status

        // Custom renderer for amount column
        transactionTable.getColumnModel().getColumn(3).setCellRenderer(new AmountRenderer());

        // Center align for Type and Status columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        transactionTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        transactionTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.decode(Config.PRIMARY_COLOR), 2),
                        "📋 Recent Transactions (Last 50)",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 15),
                        Color.decode(Config.PRIMARY_COLOR)
                ),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);

        // Create styled summary box
        JPanel summaryBox = new JPanel();
        summaryBox.setBackground(Color.WHITE);
        summaryBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode(Config.SECONDARY_COLOR), 2),
                BorderFactory.createEmptyBorder(12, 25, 12, 25)
        ));

        summaryLabel = new JLabel("Loading transaction summary...");
        summaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        summaryLabel.setForeground(Color.decode(Config.SECONDARY_COLOR));

        summaryBox.add(summaryLabel);
        panel.add(summaryBox);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        panel.setOpaque(false);

        refreshButton = createStyledButton("🔄 Refresh", Config.PRIMARY_COLOR);
        exportButton = createStyledButton("📊 Export", Config.SUCCESS_COLOR);
        backButton = createStyledButton("← Back to Dashboard", Config.DANGER_COLOR);

        panel.add(refreshButton);
        panel.add(exportButton);
        panel.add(backButton);

        return panel;
    }

    private JButton createStyledButton(String text, String color) {
        JButton button = new JButton(text);
        button.setBackground(Color.decode(color));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode(color).darker(), 2),
                BorderFactory.createEmptyBorder(12, 25, 12, 25)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.decode(color).darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.decode(color));
            }
        });

        return button;
    }

    private void setupEventListeners() {
        backButton.addActionListener(e -> {
            new DashboardFrame(userId).setVisible(true);
            dispose();
        });

        refreshButton.addActionListener(e -> loadTransactionHistory());

        exportButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "<html><div style='text-align: center; padding: 10px;'>" +
                            "<b>Export Feature</b><br><br>" +
                            "This feature would generate a PDF/Excel report<br>" +
                            "of your transaction history." +
                            "</div></html>",
                    "Export Transactions",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void loadTransactionHistory() {
        DefaultTableModel model = (DefaultTableModel) transactionTable.getModel();
        model.setRowCount(0); // Clear existing data

        List<Transaction> transactions = TransactionService.getTransactionsByAccount(
                userAccount.getAccountNumber());

        int totalTransactions = transactions.size();
        double totalIncoming = 0;
        double totalOutgoing = 0;

        for (Transaction transaction : transactions) {
            String type = transaction.getType();
            String fromTo;
            String amount;
            String status = "✅ Completed";

            if (transaction.getFromAccount().equals(userAccount.getAccountNumber())) {
                // Sent transaction
                fromTo = "To: " + transaction.getToAccount();
                amount = String.format("-$%.2f", transaction.getAmount());
                totalOutgoing += transaction.getAmount().doubleValue();
            } else {
                // Received transaction
                fromTo = "From: " + transaction.getFromAccount();
                amount = String.format("+$%.2f", transaction.getAmount());
                totalIncoming += transaction.getAmount().doubleValue();
            }

            model.addRow(new Object[]{
                    transaction.getTimestamp().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    type,
                    fromTo,
                    amount,
                    transaction.getDescription(),
                    status
            });
        }

        if (model.getRowCount() == 0) {
            model.addRow(new Object[]{
                    "No transactions found",
                    "-",
                    "-",
                    "-",
                    "Start making transactions to see them here",
                    "-"
            });
        }

        // Update summary
        updateSummary(totalTransactions, totalIncoming, totalOutgoing);
    }

    private void updateSummary(int totalTransactions, double totalIncoming, double totalOutgoing) {
        double netAmount = totalIncoming - totalOutgoing;
        String netSymbol = netAmount >= 0 ? "+" : "";

        String summary = String.format(
                "📊 Total: %d Transactions  |  💰 Incoming: $%.2f  |  💸 Outgoing: $%.2f  |  💵 Net: %s$%.2f",
                totalTransactions, totalIncoming, totalOutgoing, netSymbol, Math.abs(netAmount)
        );
        summaryLabel.setText(summary);
    }

    // Custom cell renderer for amount column
    private class AmountRenderer extends DefaultTableCellRenderer {
        public AmountRenderer() {
            setHorizontalAlignment(JLabel.RIGHT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null && !isSelected) {
                String amount = value.toString();
                if (amount.startsWith("+")) {
                    c.setForeground(new Color(0, 150, 0)); // Green for positive
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if (amount.startsWith("-")) {
                    c.setForeground(new Color(220, 20, 60)); // Red for negative
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else {
                    c.setForeground(Color.BLACK);
                    c.setFont(c.getFont().deriveFont(Font.PLAIN));
                }
            } else if (isSelected) {
                c.setForeground(table.getSelectionForeground());
            }

            return c;
        }
    }
}