package com.maskedsyntax.lockfile.ui;

import com.maskedsyntax.lockfile.model.Entry;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class EntryDialog extends Dialog<Entry> {

    private final TextField titleField = new TextField();
    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final TextField urlField = new TextField();
    private final TextField totpField = new TextField();
    private final TextArea notesArea = new TextArea();

    public EntryDialog(Entry existingEntry) {
        setTitle(existingEntry == null ? "New Entry" : "Edit Entry");
        setHeaderText("Fill in the credential details below");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);

        grid.add(new Label("Username:"), 0, 1);
        grid.add(usernameField, 1, 1);

        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);

        grid.add(new Label("URL:"), 0, 3);
        grid.add(urlField, 1, 3);

        grid.add(new Label("TOTP Secret:"), 0, 4);
        grid.add(totpField, 1, 4);

        grid.add(new Label("Notes:"), 0, 5);
        notesArea.setPrefRowCount(3);
        grid.add(notesArea, 1, 5);

        getDialogPane().setContent(grid);

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
                Entry entry = new Entry();
                entry.setTitle(titleField.getText());
                entry.setUsername(usernameField.getText());
                entry.setPassword(passwordField.getText());
                entry.setUrl(urlField.getText());
                entry.setTotpSecret(totpField.getText());
                entry.setNotes(notesArea.getText());
                return entry;
            }
            return null;
        });
    }
}