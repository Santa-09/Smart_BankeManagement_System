package com.smartbank.threads;

import com.smartbank.services.TransactionService;
import com.smartbank.services.MailService;
import com.smartbank.services.AccountService;
import com.smartbank.models.Account;
import java.math.BigDecimal;

public class TransferThread extends Thread {
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private String description;
    private TransferCallback callback;

    public interface TransferCallback {
        void onTransferComplete(boolean success, String message);
    }

    public TransferThread(String fromAccount, String toAccount, BigDecimal amount, String description, TransferCallback callback) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.description = description;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            // Perform transfer
            boolean success = TransactionService.transferMoney(fromAccount, toAccount, amount, description);

            if (success) {
                // Send email notifications
                Account sender = AccountService.getAccountByNumber(fromAccount);
                Account receiver = AccountService.getAccountByNumber(toAccount);

                if (sender != null && sender.getUser() != null) {
                    MailService.sendTransferNotification(
                            sender.getUser().getEmail(),
                            sender.getUser().getFullName(),
                            fromAccount,
                            toAccount,
                            amount.toString(),
                            description
                    );
                }

                if (receiver != null && receiver.getUser() != null) {
                    String receiverSubject = "💰 Fund Received Notification";
                    String receiverBody = String.format(
                            "Dear %s,\n\nYou have received $%.2f from account %s.\nDescription: %s\n\nThank you for banking with us!",
                            receiver.getUser().getFullName(), amount, fromAccount, description
                    );
                    MailService.sendEmail(receiver.getUser().getEmail(), receiverSubject, receiverBody);
                }

                callback.onTransferComplete(true, "Transfer completed successfully!");
            } else {
                callback.onTransferComplete(false, "Transfer failed. Please check account details and balance.");
            }
        } catch (Exception e) {
            callback.onTransferComplete(false, "An error occurred during transfer: " + e.getMessage());
        }
    }
}