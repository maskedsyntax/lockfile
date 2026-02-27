package com.maskedsyntax.lockfile.ui;

import com.maskedsyntax.lockfile.model.Attachment;
import com.maskedsyntax.lockfile.model.Entry;
import com.maskedsyntax.lockfile.utils.PasswordGenerator;
import com.maskedsyntax.lockfile.utils.PasswordStrengthChecker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class EntryDialog extends Dialog<Entry> {

    private final TextField titleField = new TextField();
    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final TextField urlField = new TextField();
    private final TextField totpField = new TextField();
    private final TextArea notesArea = new TextArea();
    private final List<Attachment> currentAttachments = new ArrayList<>();
    private final VBox attachmentListContainer = new VBox(5);

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

        // Custom Password Field with Generate Button
        VBox passBlock = new VBox(5);
        Label passLabel = new Label("PASSWORD");
        passLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 10px; -fx-font-weight: bold;");
        
        HBox passHBox = new HBox(5);
        passwordField.setPromptText("Secret passphrase");
        HBox.setHgrow(passwordField, Priority.ALWAYS);

        TextField plainPasswordField = new TextField();
        plainPasswordField.setPromptText("Secret passphrase");
        plainPasswordField.setManaged(false);
        plainPasswordField.setVisible(false);
        HBox.setHgrow(plainPasswordField, Priority.ALWAYS);

        // Bind text properties so they stay in sync
        plainPasswordField.textProperty().bindBidirectional(passwordField.textProperty());

        Button toggleBtn = new Button("", new FontIcon("fth-eye"));
        toggleBtn.setOnAction(e -> {
            boolean isVisible = plainPasswordField.isVisible();
            plainPasswordField.setVisible(!isVisible);
            plainPasswordField.setManaged(!isVisible);
            passwordField.setVisible(isVisible);
            passwordField.setManaged(isVisible);
            toggleBtn.setGraphic(new FontIcon(isVisible ? "fth-eye" : "fth-eye-off"));
        });
        
        Button generateBtn = new Button("Generate", new FontIcon("fth-refresh-cw"));
        generateBtn.setOnAction(e -> {
            String newPassword = PasswordGenerator.generatePassword(16);
            passwordField.setText(newPassword);
            if (!plainPasswordField.isVisible()) {
                toggleBtn.fire(); // Show password automatically when generated
            }
        });
        
        passHBox.getChildren().addAll(passwordField, plainPasswordField, toggleBtn, generateBtn);
        passBlock.getChildren().addAll(passLabel, passHBox);

        // Strength Meter
        HBox strengthHBox = new HBox(5);
        ProgressBar strengthBar = new ProgressBar(0);
        strengthBar.setPrefWidth(200);
        Label strengthLabel = new Label("Strength");
        strengthLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 10px;");
        strengthHBox.getChildren().addAll(strengthBar, strengthLabel);
        passBlock.getChildren().add(strengthHBox);

        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            PasswordStrengthChecker.Strength strength = PasswordStrengthChecker.calculateStrength(newVal);
            strengthBar.setProgress(strength.value);
            strengthBar.setStyle("-fx-accent: " + strength.color + ";");
            strengthLabel.setText(strength.label);
            strengthLabel.setStyle("-fx-text-fill: " + strength.color + "; -fx-font-size: 10px;");
        });

        content.getChildren().add(passBlock);

        content.getChildren().add(createFieldBlock("URL", urlField, "https://example.com"));
        content.getChildren().add(createFieldBlock("TOTP SECRET", totpField, "Base32 secret (optional)"));
        
        VBox notesBlock = new VBox(5);
        Label notesLabel = new Label("NOTES");
        notesLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 10px; -fx-font-weight: bold;");
        notesArea.setPrefRowCount(4);
        notesArea.setWrapText(true);
        notesBlock.getChildren().addAll(notesLabel, notesArea);
        content.getChildren().add(notesBlock);

        // Attachments Block
        VBox attachBlock = new VBox(5);
        Label attachLabel = new Label("ATTACHMENTS");
        attachLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 10px; -fx-font-weight: bold;");
        
        Button addAttachBtn = new Button("Add File", new FontIcon("fth-paperclip"));
        addAttachBtn.setOnAction(e -> handleAddAttachment());
        
        attachBlock.getChildren().addAll(attachLabel, attachmentListContainer, addAttachBtn);
        content.getChildren().add(attachBlock);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        dialogPane.setContent(scrollPane);

        if (existingEntry != null) {
            titleField.setText(existingEntry.getTitle());
            usernameField.setText(existingEntry.getUsername());
            passwordField.setText(existingEntry.getPassword());
            urlField.setText(existingEntry.getUrl());
            totpField.setText(existingEntry.getTotpSecret());
            notesArea.setText(existingEntry.getNotes());
            currentAttachments.addAll(existingEntry.getAttachments());
            refreshAttachmentList();
            
            // Trigger strength update for existing password
            PasswordStrengthChecker.Strength strength = PasswordStrengthChecker.calculateStrength(existingEntry.getPassword());
            strengthBar.setProgress(strength.value);
            strengthBar.setStyle("-fx-accent: " + strength.color + ";");
            strengthLabel.setText(strength.label);
            strengthLabel.setStyle("-fx-text-fill: " + strength.color + "; -fx-font-size: 10px;");
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
                entry.setAttachments(new ArrayList<>(currentAttachments));
                entry.setUpdatedAt(System.currentTimeMillis());
                return entry;
            }
            return null;
        });
    }

    private void handleAddAttachment() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Attach");
        File file = fileChooser.showOpenDialog(getOwner());
        if (file != null) {
            try {
                if (file.length() > 5 * 1024 * 1024) { // 5MB limit for sanity
                    Alert alert = new Alert(Alert.AlertType.ERROR, "File is too large (max 5MB).", ButtonType.OK);
                    alert.showAndWait();
                    return;
                }
                byte[] data = Files.readAllBytes(file.toPath());
                currentAttachments.add(new Attachment(file.getName(), data));
                refreshAttachmentList();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to read file.", ButtonType.OK);
                alert.showAndWait();
            }
        }
    }

    private void refreshAttachmentList() {
        attachmentListContainer.getChildren().clear();
        for (Attachment att : currentAttachments) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-padding: 5; -fx-background-color: #252525; -fx-background-radius: 4;");
            
            Label nameLabel = new Label(att.getFileName());
            nameLabel.setStyle("-fx-text-fill: white;");
            HBox.setHgrow(nameLabel, Priority.ALWAYS);
            
            Button removeBtn = new Button("", new FontIcon("fth-x"));
            removeBtn.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
            removeBtn.setOnAction(e -> {
                currentAttachments.remove(att);
                refreshAttachmentList();
            });
            
            row.getChildren().addAll(new FontIcon("fth-file"), nameLabel, removeBtn);
            attachmentListContainer.getChildren().add(row);
        }
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