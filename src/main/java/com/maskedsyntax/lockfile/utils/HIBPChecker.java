package com.maskedsyntax.lockfile.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.concurrent.CompletableFuture;

public class HIBPChecker {

    private static final String HIBP_API_URL = "https://api.pwnedpasswords.com/range/";

    public static CompletableFuture<Integer> getBreachCount(String password) {
        if (password == null || password.isEmpty()) {
            return CompletableFuture.completedFuture(0);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                String sha1 = sha1Hex(password).toUpperCase();
                String prefix = sha1.substring(0, 5);
                String suffix = sha1.substring(5);

                URL url = new URI(HIBP_API_URL + prefix).toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                if (connection.getResponseCode() != 200) {
                    return 0;
                }

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(":");
                        if (parts[0].equals(suffix)) {
                            return Integer.parseInt(parts[1]);
                        }
                    }
                }
                return 0;
            } catch (Exception e) {
                return 0;
            }
        });
    }

    private static String sha1Hex(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] result = md.digest(input.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(result);
    }
}
