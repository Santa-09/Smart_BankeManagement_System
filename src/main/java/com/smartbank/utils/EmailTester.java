package com.smartbank.utils;

import com.smartbank.services.MailService;

public class EmailTester {
    public static void main(String[] args) {
        System.out.println("Testing Email Service...");

        // Test email sending
        boolean success = MailService.sendEmail(
                "santanubarik537@gmail.com",
                "Test Email from SmartBank",
                "This is a test email to verify the email service is working properly."
        );

        if (success) {
            System.out.println("✅ Email test completed successfully!");
        } else {
            System.out.println("❌ Email test failed!");
        }
    }
}