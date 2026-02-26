package com.maskedsyntax.lockfile.utils;

import java.security.SecureRandom;

public class PasswordGenerator {
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_=+[]{}|;:,.<>?/";
    private static final String ALL_CHARS = UPPER + LOWER + DIGITS + SPECIAL;
    
    private static final SecureRandom random = new SecureRandom();

    public static String generatePassword(int length) {
        if (length < 4) {
            length = 16;
        }
        
        StringBuilder sb = new StringBuilder(length);
        
        // Ensure at least one of each type
        sb.append(UPPER.charAt(random.nextInt(UPPER.length())));
        sb.append(LOWER.charAt(random.nextInt(LOWER.length())));
        sb.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        sb.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));
        
        // Fill the rest randomly
        for (int i = 4; i < length; i++) {
            sb.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }
        
        // Shuffle the characters
        char[] password = sb.toString().toCharArray();
        for (int i = password.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = password[i];
            password[i] = password[j];
            password[j] = temp;
        }
        
        return new String(password);
    }
}