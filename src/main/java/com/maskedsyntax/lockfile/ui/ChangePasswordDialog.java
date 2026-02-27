package com.maskedsyntax.lockfile.ui;

import com.maskedsyntax.lockfile.crypto.CryptoUtils;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class ChangePasswordDialog extends Dialog<char[]> {

    public ChangePasswordDialog() {
        setTitle("Change Master Password");
        setHeaderText("Enter your new master password. This will re-encrypt your entire vault.");

        DialogPane dialogPane = getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/com/maskedsyntax/lockfile/style.css").toExternalForm());

        ButtonType changeButtonType = new ButtonType("Change Password", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Master Password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm New Password");

        content.getChildren().addAll(new Label("New Password:"), newPasswordField, new Label("Confirm Password:"), confirmPasswordField);
        dialogPane.setContent(content);

        // Validation
        Button okButton = (Button) dialogPane.lookupButton(changeButtonType);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (newPasswordField.getText().isEmpty() || !newPasswordField.getText().equals(confirmPasswordField.getText())) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Passwords do not match or are empty.", ButtonType.OK);
                alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/maskedsyntax/lockfile/style.css").toExternalForm());
                alert.showAndWait();
                event.consume();
            }
        });

        setResultConverter(dialogButton -> {
            if (dialogButton == changeButtonType) {
                return newPasswordField.getText().toCharArray();
            }
            return null;
        });
    }
}
