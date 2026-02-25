package com.maskedsyntax.lockfile.utils;

import javafx.application.Platform;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.util.Timer;
import java.util.TimerTask;

public class ClipboardUtils {
    private static Timer clearTimer;

    public static void copyWithAutoClear(String text, int seconds) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);

        if (clearTimer != null) {
            clearTimer.cancel();
        }

        clearTimer = new Timer(true);
        clearTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    Clipboard cb = Clipboard.getSystemClipboard();
                    if (cb.hasString() && text.equals(cb.getString())) {
                        cb.clear();
                    }
                });
            }
        }, seconds * 1000L);
    }
}