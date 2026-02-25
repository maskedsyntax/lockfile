package com.maskedsyntax.lockfile.ui;

import com.maskedsyntax.lockfile.model.Entry;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class EntryDialog extends Dialog<Entry> {

    private final TextField titleField = new TextField();
    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final TextField urlField = new TextField();
    private final TextField totpField = new TextField();
    private final TextArea notesArea = new TextArea();

    public EntryDialog(Entry existingEntry) {
        setTitle(existingEntry == null ? "New Entry" : "Edit Entry");
        setHeaderText(null);

        // Apply style to dialog pane
        DialogPane dialogPane = getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/com/maskedsyntax/lockfile/style.css").toExternalForm());
        dialogPane.getStyleClass().add("entry-dialog");
        setResizable(true);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(450);

        Label header = new Label(existingEntry == null ? "Create New Credential" : "Edit Credential");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        content.getChildren().add(header);
        content.getChildren().add(createFieldBlock("TITLE", titleField, "e.g. GitHub, Banking, etc."));
        content.getChildren().add(createFieldBlock("USERNAME", usernameField, "Email or username"));
        content.getChildren().add(createFieldBlock("PASSWORD", passwordField, "Secret passphrase"));
        content.getChildren().add(createFieldBlock("URL", urlField, "https://example.com"));
        content.getChildren().add(createFieldBlock("TOTP SECRET", totpField, "Base32 secret (optional)"));
        
        VBox notesBlock = new VBox(5);
        Label notesLabel = new Label("NOTES");
        notesLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 10px; -fx-font-weight: bold;");
        notesArea.setPrefRowCount(4);
        notesArea.setWrapText(true);
        notesBlock.getChildren().addAll(notesLabel, notesArea);
        content.getChildren().add(notesBlock);

        dialogPane.setContent(content);

        if (existingEntry != null) {
            titleField.setText(existingEntry.getTitle());
            usernameField.setText(existingEntry.getUsername());
            passwordField.setText(existingEntry.getPassword());
            urlField.setText(existingEntry.getUrl());
            totpField.setText(existingEntry.getTotpSecret());
            notesArea.setText(existingEntry.getNotes());
        }

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Entry entry = (existingEntry != null) ? existingEntry : new Entry();
                entry.setTitle(titleField.getText());
                entry.setUsername(usernameField.getText());
                entry.setPassword(passwordField.getText());
                entry.setUrl(urlField.getText());
                entry.setTotpSecret(totpField.getText());
                entry.setNotes(notesArea.getText());
                entry.setUpdatedAt(System.currentTimeMillis());
                return entry;
            }
            return null;
        });
    }

    private VBox createFieldBlock(String labelText, Control field, String prompt) {
        VBox block = new VBox(5);
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: #888; -fx-font-size: 10px; -fx-font-weight: bold;");
        
        if (field instanceof TextField tf) tf.setPromptText(prompt);
        
        block.getChildren().addAll(label, field);
        return block;
    }
}