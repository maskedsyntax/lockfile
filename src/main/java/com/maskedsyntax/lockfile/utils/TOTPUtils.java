package com.maskedsyntax.lockfile.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class TOTPUtils {

    private static final int[] DIGITS_POWER = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};

    public static String generateTOTP(String base32Secret) {
        try {
            byte[] key = decodeBase32(base32Secret);
            long timeStep = System.currentTimeMillis() / 30000L;
            return generateTOTP(key, timeStep, 6);
        } catch (Exception e) {
            return "Error";
        }
    }

    private static String generateTOTP(byte[] key, long time, int returnDigits) 
            throws NoSuchAlgorithmException, InvalidKeyException {
        
        byte[] data = ByteBuffer.allocate(8).putLong(time).array();
        
        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        
        byte[] hash = mac.doFinal(data);
        
        int offset = hash[hash.length - 1] & 0xf;
        int binary = ((hash[offset] & 0x7f) << 24) |
                ((hash[offset + 1] & 0xff) << 16) |
                ((hash[offset + 2] & 0xff) << 8) |
                (hash[offset + 3] & 0xff);
                
        int otp = binary % DIGITS_POWER[returnDigits];
        
        String result = Integer.toString(otp);
        while (result.length() < returnDigits) {
            result = "0" + result;
        }
        return result;
    }

    private static byte[] decodeBase32(String base32) {
        String base32chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
        base32 = base32.toUpperCase().replaceAll("=", "");
        byte[] bytes = new byte[base32.length() * 5 / 8];
        int buffer = 0;
        int bitsLeft = 0;
        int count = 0;
        for (char c : base32.toCharArray()) {
            buffer <<= 5;
            buffer |= base32chars.indexOf(c) & 31;
            bitsLeft += 5;
            if (bitsLeft >= 8) {
                bytes[count++] = (byte) (buffer >> (bitsLeft - 8));
                bitsLeft -= 8;
            }
        }
        return bytes;
    }
}