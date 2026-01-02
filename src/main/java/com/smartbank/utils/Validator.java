package com.smartbank.utils;

import java.util.regex.Pattern;

public class Validator {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\d{10}$");
    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[a-zA-Z\\s]{2,50}$");
    private static final Pattern ACCOUNT_NUMBER_PATTERN =
            Pattern.compile("^\\d{12}$");

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }

    public static boolean isValidAmount(String amount) {
        try {
            double amt = Double.parseDouble(amount);
            return amt > 0 && amt <= 1000000; // Limit to 1 million
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidAccountNumber(String accountNumber) {
        return accountNumber != null && ACCOUNT_NUMBER_PATTERN.matcher(accountNumber).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }
}