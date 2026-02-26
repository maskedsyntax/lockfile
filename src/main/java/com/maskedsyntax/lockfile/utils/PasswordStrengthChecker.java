package com.maskedsyntax.lockfile.utils;

public class PasswordStrengthChecker {

    public enum Strength {
        WEAK(0.2, "#ff4d4d", "Weak"),
        FAIR(0.4, "#ffa64d", "Fair"),
        GOOD(0.6, "#ffff4d", "Good"),
        STRONG(0.8, "#a6ff4d", "Strong"),
        EXCELLENT(1.0, "#4dff4d", "Excellent");

        public final double value;
        public final String color;
        public final String label;

        Strength(double value, String color, String label) {
            this.value = value;
            this.color = color;
            this.label = label;
        }
    }

    public static Strength calculateStrength(String password) {
        if (password == null || password.isEmpty()) {
            return Strength.WEAK;
        }

        int score = 0;
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        // Checking for special characters (non-alphanumeric)
        if (password.matches(".*[^a-zA-Z0-9].*")) score++;

        if (score <= 2) return Strength.WEAK;
        if (score == 3) return Strength.FAIR;
        if (score == 4) return Strength.GOOD;
        if (score == 5) return Strength.STRONG;
        return Strength.EXCELLENT;
    }
}
