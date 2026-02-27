package com.maskedsyntax.lockfile.ui;

import com.maskedsyntax.lockfile.model.Settings;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class SettingsDialog extends Dialog<Settings> {

    public SettingsDialog(Settings currentSettings) {
        setTitle("Application Settings");
        setHeaderText("Configure your preferences.");

        DialogPane dialogPane = getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/com/maskedsyntax/lockfile/style.css").toExternalForm());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(400);

        // Auto-lock
        VBox lockBlock = new VBox(5);
        Label lockLabel = new Label("AUTO-LOCK IDLE TIME (MINUTES)");
        lockLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 10px; -fx-font-weight: bold;");
        Spinner<Integer> lockSpinner = new ProgressBarSpinner(1, 60, currentSettings.getAutoLockMinutes());
        lockBlock.getChildren().addAll(lockLabel, lockSpinner);

        // Clipboard clear
        VBox clipBlock = new VBox(5);
        Label clipLabel = new Label("CLIPBOARD CLEAR DELAY (SECONDS)");
        clipLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 10px; -fx-font-weight: bold;");
        Spinner<Integer> clipSpinner = new ProgressBarSpinner(5, 300, currentSettings.getClipboardClearSeconds());
        clipBlock.getChildren().addAll(clipLabel, clipSpinner);

        content.getChildren().addAll(lockBlock, clipBlock);
        dialogPane.setContent(content);

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Settings newSettings = new Settings();
                newSettings.setAutoLockMinutes(lockSpinner.getValue());
                newSettings.setClipboardClearSeconds(clipSpinner.getValue());
                return newSettings;
            }
            return null;
        });
    }

    // Helper to make spinners look a bit better or just standard
    private static class ProgressBarSpinner extends Spinner<Integer> {
        public ProgressBarSpinner(int min, int max, int initial) {
            super(min, max, initial);
            setEditable(true);
            getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        }
    }
}
