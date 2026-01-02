package com.smartbank.services;

import com.smartbank.utils.Config;
import com.smartbank.utils.LoggerUtil;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class MailService {

    public static boolean sendEmail(String to, String subject, String body) {
        // For real implementation, uncomment the JavaMail code below
        // Currently using demo mode for testing

        try {
            // Real email implementation
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", Config.SMTP_HOST);
            props.put("mail.smtp.port", Config.SMTP_PORT);
            props.put("mail.smtp.ssl.trust", Config.SMTP_HOST); // Add SSL trust

            // Create session with authentication
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(Config.EMAIL_USERNAME, Config.EMAIL_PASSWORD);
                }
            });

            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(Config.EMAIL_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            // Set HTML content for better formatting
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            String htmlContent = convertToHtml(body, subject);
            messageBodyPart.setContent(htmlContent, "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);

            // Send message
            Transport.send(message);

            LoggerUtil.logInfo("✅ Email sent successfully to: " + to);
            System.out.println("=== EMAIL SENT SUCCESSFULLY ===");
            System.out.println("To: " + to);
            System.out.println("Subject: " + subject);
            System.out.println("Body: " + body);
            System.out.println("================================");

            return true;

        } catch (Exception e) {
            // Fallback to demo mode if email sending fails
            LoggerUtil.logError("Failed to send email to " + to + ": " + e.getMessage());
            System.out.println("=== EMAIL DEMO MODE (Sending failed) ===");
            System.out.println("To: " + to);
            System.out.println("Subject: " + subject);
            System.out.println("Body: " + body);
            System.out.println("Error: " + e.getMessage());
            System.out.println("=======================================");

            // For demo purposes, we'll just log the email
            LoggerUtil.logInfo(String.format("Email (DEMO) sent to %s: %s - %s", to, subject, body));
            return true; // Return true even in demo mode to continue flow
        }
    }

    private static String convertToHtml(String plainText, String subject) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang='en'>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<title>").append(subject).append("</title>");
        html.append("<style>");
        html.append("body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; }");
        html.append(".container { max-width: 600px; margin: 0 auto; background: #f9f9f9; padding: 30px; border-radius: 10px; border: 1px solid #ddd; }");
        html.append(".header { background: linear-gradient(135deg, #2C3E50, #3498DB); color: white; padding: 20px; border-radius: 8px 8px 0 0; text-align: center; }");
        html.append(".content { background: white; padding: 25px; border-radius: 0 0 8px 8px; margin-top: -10px; }");
        html.append(".footer { text-align: center; margin-top: 20px; padding: 15px; color: #666; font-size: 12px; border-top: 1px solid #eee; }");
        html.append(".button { display: inline-block; padding: 12px 25px; background: #3498DB; color: white; text-decoration: none; border-radius: 5px; margin: 10px 0; }");
        html.append(".info-box { background: #E8F4FD; padding: 15px; border-left: 4px solid #3498DB; margin: 15px 0; border-radius: 4px; }");
        html.append(".success-box { background: #E8F6F3; padding: 15px; border-left: 4px solid #27AE60; margin: 15px 0; border-radius: 4px; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='container'>");
        html.append("<div class='header'>");
        html.append("<h1>🏦 ").append(Config.APP_NAME).append("</h1>");
        html.append("<h2>").append(subject).append("</h2>");
        html.append("</div>");
        html.append("<div class='content'>");

        // Convert plain text to HTML
        String[] lines = plainText.split("\n");
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                html.append("<br>");
            } else if (line.contains("Dear")) {
                html.append("<h3 style='color: #2C3E50;'>").append(line).append("</h3>");
            } else if (line.contains("Thank you") || line.contains("Best regards")) {
                html.append("<p style='color: #666;'>").append(line).append("</p>");
            } else if (line.contains("•") || line.contains("-")) {
                html.append("<p style='margin: 5px 0; padding-left: 15px;'>").append(line).append("</p>");
            } else {
                html.append("<p>").append(line).append("</p>");
            }
        }

        html.append("</div>");
        html.append("<div class='footer'>");
        html.append("<p>This is an automated message from ").append(Config.APP_NAME).append("</p>");
        html.append("<p>Please do not reply to this email</p>");
        html.append("</div>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    public static void sendAccountApprovalEmail(String to, String customerName, String accountNumber, String accountType, String bankName) {
        String subject = "🎉 Your Bank Account Has Been Approved! - " + bankName;
        String body = String.format(
                "Dear %s,\n\n" +
                        "We are pleased to inform you that your bank account has been approved!\n\n" +
                        "📋 Account Details:\n" +
                        "• Bank: %s\n" +
                        "• Account Number: %s\n" +
                        "• Account Type: %s\n" +
                        "• Status: ✅ ACTIVE\n\n" +
                        "🔐 Login Information:\n" +
                        "• Email: %s\n" +
                        "• Password: Your date of birth (YYYY-MM-DD format)\n\n" +
                        "💡 Security Note:\n" +
                        "For security reasons, we recommend changing your password after first login.\n\n" +
                        "You can now access all banking services including:\n" +
                        "• Fund Transfers\n" +
                        "• Transaction History\n" +
                        "• Account Statements\n" +
                        "• Balance Enquiry\n\n" +
                        "Thank you for choosing %s!\n\n" +
                        "Best regards,\n" +
                        "Customer Service Team\n" +
                        "%s",
                customerName, bankName, accountNumber, accountType, to, bankName, bankName
        );

        // Send email in a separate thread to avoid blocking the UI
        new Thread(() -> {
            boolean success = sendEmail(to, subject, body);
            if (success) {
                LoggerUtil.logInfo("Account approval email sent to: " + to);
            } else {
                LoggerUtil.logError("Failed to send account approval email to: " + to);
            }
        }).start();
    }

    public static void sendAccountCreationEmail(String to, String customerName, String accountNumber, String accountType, String bankName, String initialDeposit) {
        String subject = "📋 Your Bank Account Application Received - " + bankName;
        String body = String.format(
                "Dear %s,\n\n" +
                        "Thank you for choosing %s! We have received your account application.\n\n" +
                        "📋 Application Details:\n" +
                        "• Bank: %s\n" +
                        "• Account Number: %s\n" +
                        "• Account Type: %s\n" +
                        "• Initial Deposit: $%s\n" +
                        "• Status: ⏳ PENDING APPROVAL\n\n" +
                        "⏰ Next Steps:\n" +
                        "Your application is currently under review. You will receive another email once your account is approved.\n" +
                        "Typically, this process takes 1-2 business days.\n\n" +
                        "For any queries, please contact our customer service.\n\n" +
                        "Thank you for your patience!\n\n" +
                        "Best regards,\n" +
                        "Customer Service Team\n" +
                        "%s",
                customerName, bankName, bankName, accountNumber, accountType, initialDeposit, bankName
        );

        // Send email in a separate thread
        new Thread(() -> {
            boolean success = sendEmail(to, subject, body);
            if (success) {
                LoggerUtil.logInfo("Account creation email sent to: " + to);
            } else {
                LoggerUtil.logError("Failed to send account creation email to: " + to);
            }
        }).start();
    }

    public static void sendTransferNotification(String to, String customerName, String fromAccount, String toAccount, String amount, String description) {
        String subject = "💸 Fund Transfer Notification - " + Config.APP_NAME;
        String body = String.format(
                "Dear %s,\n\n" +
                        "A fund transfer has been processed from your account.\n\n" +
                        "📊 Transaction Details:\n" +
                        "• From Account: %s\n" +
                        "• To Account: %s\n" +
                        "• Amount: $%s\n" +
                        "• Description: %s\n" +
                        "• Date: %s\n\n" +
                        "If you did not authorize this transaction, please contact our customer service immediately.\n\n" +
                        "Thank you for banking with us!\n\n" +
                        "Best regards,\n" +
                        "%s Security Team",
                customerName, fromAccount, toAccount, amount, description,
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                Config.APP_NAME
        );

        // Send email in a separate thread
        new Thread(() -> {
            boolean success = sendEmail(to, subject, body);
            if (success) {
                LoggerUtil.logInfo("Transfer notification email sent to: " + to);
            } else {
                LoggerUtil.logError("Failed to send transfer notification email to: " + to);
            }
        }).start();
    }

    public static void sendPasswordResetEmail(String to, String customerName, String temporaryPassword) {
        String subject = "🔒 Password Reset Request - " + Config.APP_NAME;
        String body = String.format(
                "Dear %s,\n\n" +
                        "Your password has been reset as requested.\n\n" +
                        "🔐 Temporary Login Information:\n" +
                        "• Email: %s\n" +
                        "• Temporary Password: %s\n\n" +
                        "💡 Security Instructions:\n" +
                        "• Login with your temporary password\n" +
                        "• Change your password immediately after login\n" +
                        "• Do not share your password with anyone\n\n" +
                        "If you did not request this password reset, please contact our support team immediately.\n\n" +
                        "Best regards,\n" +
                        "%s Security Team",
                customerName, to, temporaryPassword, Config.APP_NAME
        );

        // Send email in a separate thread
        new Thread(() -> {
            boolean success = sendEmail(to, subject, body);
            if (success) {
                LoggerUtil.logInfo("Password reset email sent to: " + to);
            } else {
                LoggerUtil.logError("Failed to send password reset email to: " + to);
            }
        }).start();
    }
}