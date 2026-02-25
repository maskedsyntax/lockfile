package com.maskedsyntax.lockfile.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maskedsyntax.lockfile.crypto.CryptoUtils;
import com.maskedsyntax.lockfile.model.Vault;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

public class VaultManager {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void saveVault(Vault vault, String password, File file) throws Exception {
        String json = mapper.writeValueAsString(vault);
        String encrypted = CryptoUtils.encrypt(json, password);
        
        Path path = file.toPath();
        Files.write(path, encrypted.getBytes(StandardCharsets.UTF_8));
        
        setSecurePermissions(path);
    }

    public static Vault loadVault(File file, String password) throws Exception {
        if (!file.exists()) {
            return new Vault();
        }
        
        byte[] encryptedBytes = Files.readAllBytes(file.toPath());
        String encrypted = new String(encryptedBytes, StandardCharsets.UTF_8);
        String json = CryptoUtils.decrypt(encrypted, password);
        
        return mapper.readValue(json, Vault.class);
    }

    public static void exportVault(Vault vault, File file) throws Exception {
        // Unencrypted export (optional feature) or just JSON
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, vault);
        setSecurePermissions(file.toPath());
    }

    private static void setSecurePermissions(Path path) {
        try {
            if (path.getFileSystem().supportedFileAttributeViews().contains("posix")) {
                Set<PosixFilePermission> perms = new HashSet<>();
                perms.add(PosixFilePermission.OWNER_READ);
                perms.add(PosixFilePermission.OWNER_WRITE);
                Files.setPosixFilePermissions(path, perms);
            }
        } catch (IOException e) {
            // Ignore if OS does not support POSIX permissions
        }
    }
}