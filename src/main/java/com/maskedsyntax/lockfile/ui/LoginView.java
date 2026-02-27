package com.maskedsyntax.lockfile.ui;

import com.maskedsyntax.lockfile.model.Vault;
import com.maskedsyntax.lockfile.utils.StorageUtils;
import com.maskedsyntax.lockfile.utils.VaultManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class LoginView extends VBox {

    private Stage stage;
    private File vaultFile;

    public LoginView(Stage stage) {
        this.stage = stage;
        this.vaultFile = StorageUtils.getDefaultVaultFile();

        setPadding(new Insets(40));
        setSpacing(20);
        setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Lockfile Vault");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label pathLabel = new Label("Vault Path: " + vaultFile.getAbsolutePath());
        pathLabel.setWrapText(true);
        Button changePathBtn = new Button("Change File");
        changePathBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Vault File");
            fileChooser.setInitialDirectory(vaultFile.getParentFile());
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Vault Files", "*.vault"));
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                vaultFile = file;
                pathLabel.setText("Vault Path: " + vaultFile.getAbsolutePath());
            }
        });

        HBox pathBox = new HBox(10, pathLabel, changePathBtn);
        pathBox.setAlignment(Pos.CENTER);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Master Password");

        Button unlockBtn = new Button("Unlock");
        unlockBtn.setDefaultButton(true);
        unlockBtn.setMaxWidth(Double.MAX_VALUE);
        
        Button createBtn = new Button("Create New Vault");
        createBtn.setMaxWidth(Double.MAX_VALUE);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setVisible(false);

        unlockBtn.setOnAction(e -> handleUnlock(passwordField.getText(), errorLabel));
        createBtn.setOnAction(e -> handleCreate(passwordField.getText(), errorLabel));

        getChildren().addAll(titleLabel, pathBox, passwordField, unlockBtn, createBtn, errorLabel);
    }

    private void handleUnlock(String password, Label errorLabel) {
        if (password.isEmpty()) {
            showError(errorLabel, "Password cannot be empty.");
            return;
        }
        if (!vaultFile.exists()) {
            showError(errorLabel, "Vault file not found. Create a new one.");
            return;
        }
        try {
            Vault vault = VaultManager.loadVault(vaultFile, password);
            openVaultView(vault, password);
        } catch (Exception e) {
            showError(errorLabel, "Failed to unlock vault. Incorrect password or corrupt file.");
        }
    }

    private void handleCreate(String password, Label errorLabel) {
        if (password.isEmpty()) {
            showError(errorLabel, "Password cannot be empty.");
            return;
        }
        if (vaultFile.exists()) {
            showError(errorLabel, "Vault file already exists. Choose a different path or unlock it.");
            return;
        }
        try {
            Vault vault = new Vault();
            VaultManager.saveVault(vault, password, vaultFile);
            openVaultView(vault, password);
        } catch (Exception e) {
            showError(errorLabel, "Failed to create vault.");
            e.printStackTrace();
        }
    }

    private void showError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void openVaultView(Vault vault, String password) {
        VaultView vaultView = new VaultView(stage, vault, vaultFile, password);
        Scene scene = new Scene(vaultView, 1000, 700);
        try {
            scene.getStylesheets().add(getClass().getResource("/com/maskedsyntax/lockfile/style.css").toExternalForm());
        } catch (Exception e) {
            // Ignore missing CSS
        }
        stage.setScene(scene);
        stage.setResizable(true);
        stage.centerOnScreen();
    }
}