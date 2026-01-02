package com.smartbank.utils;

import java.security.SecureRandom;

public class RandomGenerator {
    private static final String NUMBERS = "0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static String generateAccountNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) { // 12-digit account number
            sb.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        }
        return sb.toString();
    }

    public static String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + random.nextInt(1000);
    }

    public static String generateCustomerId() {
        return "CUST" + System.currentTimeMillis() + random.nextInt(100);
    }
}