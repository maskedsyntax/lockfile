package com.maskedsyntax.lockfile.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maskedsyntax.lockfile.crypto.CryptoUtils;
import com.maskedsyntax.lockfile.model.Vault;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class VaultManager {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String BACKUP_DIR = StorageUtils.getBackupDir();

    public static void saveVault(Vault vault, String password, File file) throws Exception {
        String json = mapper.writeValueAsString(vault);
        String encrypted = CryptoUtils.encrypt(json, password);
        
        Path path = file.toPath();
        Files.write(path, encrypted.getBytes(StandardCharsets.UTF_8));
        
        setSecurePermissions(path);
        createBackup(file);
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
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, vault);
        setSecurePermissions(file.toPath());
    }

    private static void createBackup(File file) {
        try {
            File backupDir = new File(BACKUP_DIR);
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String backupFileName = file.getName() + "." + timestamp + ".bak";
            Path backupPath = new File(backupDir, backupFileName).toPath();

            Files.copy(file.toPath(), backupPath, StandardCopyOption.REPLACE_EXISTING);
            setSecurePermissions(backupPath);
            
            cleanOldBackups(backupDir);
        } catch (Exception e) {
            // Backup failure should not block saving, but we could log it
            System.err.println("Failed to create backup: " + e.getMessage());
        }
    }

    private static void cleanOldBackups(File backupDir) {
        File[] files = backupDir.listFiles((dir, name) -> name.endsWith(".bak"));
        if (files != null && files.length > 10) {
            java.util.Arrays.sort(files, (f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));
            for (int i = 0; i < files.length - 10; i++) {
                files[i].delete();
            }
        }
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