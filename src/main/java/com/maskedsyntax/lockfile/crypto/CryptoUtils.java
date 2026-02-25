package com.maskedsyntax.lockfile.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class CryptoUtils {

    private static final int AES_KEY_SIZE = 256;
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128; // bits
    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 600000;

    public static SecretKey deriveKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, AES_KEY_SIZE);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    public static byte[] generateRandomBytes(int length) {
        byte[] bytes = new byte[length];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }

    public static String encrypt(String plainText, String password) throws Exception {
        byte[] salt = generateRandomBytes(SALT_LENGTH);
        SecretKey key = deriveKey(password, salt);

        byte[] iv = generateRandomBytes(GCM_IV_LENGTH);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

        byte[] cipherText = cipher.doFinal(plainText.getBytes("UTF-8"));

        byte[] encryptedMessage = new byte[SALT_LENGTH + GCM_IV_LENGTH + cipherText.length];
        System.arraycopy(salt, 0, encryptedMessage, 0, SALT_LENGTH);
        System.arraycopy(iv, 0, encryptedMessage, SALT_LENGTH, GCM_IV_LENGTH);
        System.arraycopy(cipherText, 0, encryptedMessage, SALT_LENGTH + GCM_IV_LENGTH, cipherText.length);

        return Base64.getEncoder().encodeToString(encryptedMessage);
    }

    public static String decrypt(String encryptedMessageBase64, String password) throws Exception {
        byte[] encryptedMessage = Base64.getDecoder().decode(encryptedMessageBase64);

        if (encryptedMessage.length < SALT_LENGTH + GCM_IV_LENGTH) {
            throw new IllegalArgumentException("Invalid encrypted payload");
        }

        byte[] salt = new byte[SALT_LENGTH];
        System.arraycopy(encryptedMessage, 0, salt, 0, SALT_LENGTH);

        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(encryptedMessage, SALT_LENGTH, iv, 0, GCM_IV_LENGTH);

        byte[] cipherText = new byte[encryptedMessage.length - SALT_LENGTH - GCM_IV_LENGTH];
        System.arraycopy(encryptedMessage, SALT_LENGTH + GCM_IV_LENGTH, cipherText, 0, cipherText.length);

        SecretKey key = deriveKey(password, salt);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

        byte[] plainTextBytes = cipher.doFinal(cipherText);
        return new String(plainTextBytes, "UTF-8");
    }
}