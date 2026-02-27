package com.maskedsyntax.lockfile.utils;

import java.io.File;

public class StorageUtils {

    public static String getAppDataDir() {
        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");

        if (os.contains("win")) {
            String appData = System.getenv("LOCALAPPDATA");
            if (appData != null) {
                return appData + File.separator + "Lockfile";
            }
            return userHome + File.separator + "AppData" + File.separator + "Local" + File.separator + "Lockfile";
        } else if (os.contains("mac")) {
            return userHome + File.separator + "Library" + File.separator + "Application Support" + File.separator + "Lockfile";
        } else {
            // Linux/Unix follow XDG spec or default to hidden folder
            String xdgData = System.getenv("XDG_DATA_HOME");
            if (xdgData != null) {
                return xdgData + File.separator + "lockfile";
            }
            return userHome + File.separator + ".local" + File.separator + "share" + File.separator + "lockfile";
        }
    }

    public static File getDefaultVaultFile() {
        String dir = getAppDataDir();
        new File(dir).mkdirs();
        return new File(dir, "vault.lockfile");
    }

    public static String getBackupDir() {
        String dir = getAppDataDir() + File.separator + "backups";
        new File(dir).mkdirs();
        return dir;
    }

    public static String getIconCacheDir() {
        String dir = getAppDataDir() + File.separator + "cache" + File.separator + "icons";
        new File(dir).mkdirs();
        return dir;
    }
}
