package com.maskedsyntax.lockfile.crypto;

import org.junit.jupiter.api.Test;
import javax.crypto.SecretKey;
import static org.junit.jupiter.api.Assertions.*;

public class CryptoUtilsTest {

    @Test
    public void testEncryptionDecryption() throws Exception {
        String originalText = "SuperSecretPassword123!";
        String masterPassword = "MyMasterPassword";

        String encrypted = CryptoUtils.encrypt(originalText, masterPassword);
        assertNotNull(encrypted);
        assertNotEquals(originalText, encrypted);

        String decrypted = CryptoUtils.decrypt(encrypted, masterPassword);
        assertEquals(originalText, decrypted);
    }

    @Test
    public void testEncryptionDecryptionWithWrongPassword() throws Exception {
        String originalText = "SafeData";
        String masterPassword = "RightPassword";
        String wrongPassword = "WrongPassword";

        String encrypted = CryptoUtils.encrypt(originalText, masterPassword);
        
        assertThrows(Exception.class, () -> {
            CryptoUtils.decrypt(encrypted, wrongPassword);
        });
    }

    @Test
    public void testKeyDerivationUniqueness() throws Exception {
        String password = "testPassword";
        byte[] salt1 = CryptoUtils.generateRandomBytes(16);
        byte[] salt2 = CryptoUtils.generateRandomBytes(16);

        SecretKey key1 = CryptoUtils.deriveKey(password, salt1);
        SecretKey key2 = CryptoUtils.deriveKey(password, salt2);

        assertNotEquals(java.util.Base64.getEncoder().encodeToString(key1.getEncoded()),
                        java.util.Base64.getEncoder().encodeToString(key2.getEncoded()));
    }

    @Test
    public void testRandomBytesGeneration() {
        byte[] b1 = CryptoUtils.generateRandomBytes(16);
        byte[] b2 = CryptoUtils.generateRandomBytes(16);

        assertEquals(16, b1.length);
        assertEquals(16, b2.length);
        assertNotEquals(java.util.Arrays.toString(b1), java.util.Arrays.toString(b2));
    }
}
