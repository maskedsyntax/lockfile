package com.maskedsyntax.lockfile.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Settings {
    private int autoLockMinutes = 5;
    private int clipboardClearSeconds = 30;
    private boolean minimizeToTray = false;

    public Settings() {}

    public int getAutoLockMinutes() { return autoLockMinutes; }
    public void setAutoLockMinutes(int autoLockMinutes) { this.autoLockMinutes = autoLockMinutes; }

    public int getClipboardClearSeconds() { return clipboardClearSeconds; }
    public void setClipboardClearSeconds(int clipboardClearSeconds) { this.clipboardClearSeconds = clipboardClearSeconds; }

    public boolean isMinimizeToTray() { return minimizeToTray; }
    public void setMinimizeToTray(boolean minimizeToTray) { this.minimizeToTray = minimizeToTray; }
}
