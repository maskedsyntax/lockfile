package com.maskedsyntax.lockfile.ui;

import com.maskedsyntax.lockfile.model.Entry;
import com.maskedsyntax.lockfile.model.Folder;
import com.maskedsyntax.lockfile.model.Vault;
import com.maskedsyntax.lockfile.utils.ClipboardUtils;
import com.maskedsyntax.lockfile.utils.TOTPUtils;
import com.maskedsyntax.lockfile.utils.VaultManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.util.Optional;

public class VaultView extends BorderPane {

    private final Stage stage;
    private final Vault vault;
    private final File vaultFile;
    private final String masterPassword;

    private TreeView<Folder> folderTreeView;
    private TableView<Entry> entryTableView;
    private ObservableList<Entry> currentEntries;
    private Folder currentFolder = null;

    public VaultView(Stage stage, Vault vault, File vaultFile, String masterPassword) {
        this.stage = stage;
        this.vault = vault;
        this.vaultFile = vaultFile;
        this.masterPassword = masterPassword;

        initUI();
        loadVaultData();
    }

    private void initUI() {
        setTop(createToolBar());
        
        SplitPane splitPane = new SplitPane();
        VBox folderSection = createFolderView();
        VBox entrySection = createEntryView();
        
        splitPane.getItems().addAll(folderSection, entrySection);
        splitPane.setDividerPositions(0.25f);
        setCenter(splitPane);
    }

    private ToolBar createToolBar() {
        Button addFolderBtn = new Button("New Folder", new FontIcon("fth-folder-plus"));
        Button addEntryBtn = new Button("New Entry", new FontIcon("fth-plus-circle"));
        Button editEntryBtn = new Button("Edit", new FontIcon("fth-edit"));
        Button deleteEntryBtn = new Button("Delete", new FontIcon("fth-trash-2"));
        Button lockBtn = new Button("Lock", new FontIcon("fth-lock"));
        Button exportBtn = new Button("Export", new FontIcon("fth-download"));

        TextField searchField = new TextField();
        searchField.setPromptText("Search entries...");
        searchField.setPrefWidth(250);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> handleSearch(newVal));

        addFolderBtn.setOnAction(e -> handleAddFolder());
        addEntryBtn.setOnAction(e -> handleAddEntry());
        editEntryBtn.setOnAction(e -> handleEditEntry());
        deleteEntryBtn.setOnAction(e -> handleDeleteEntry());
        lockBtn.setOnAction(e -> handleLock());
        exportBtn.setOnAction(e -> handleExport());

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        return new ToolBar(addFolderBtn, addEntryBtn, new Separator(), editEntryBtn, deleteEntryBtn, spacer, searchField, new Separator(), lockBtn, exportBtn);
    }

    private void handleSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            updateEntryTable();
            return;
        }
        
        String lowerQuery = query.toLowerCase();
        ObservableList<Entry> filteredList = FXCollections.observableArrayList();
        
        java.util.List<Entry> sourceList = (currentFolder == null || currentFolder.getName().equals("Root")) ? vault.getRootEntries() : currentFolder.getEntries();
        
        for (Entry e : sourceList) {
            if ((e.getTitle() != null && e.getTitle().toLowerCase().contains(lowerQuery)) ||
                (e.getUsername() != null && e.getUsername().toLowerCase().contains(lowerQuery)) ||
                (e.getUrl() != null && e.getUrl().toLowerCase().contains(lowerQuery))) {
                filteredList.add(e);
            }
        }
        currentEntries.setAll(filteredList);
    }

    private VBox createFolderView() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setStyle("-fx-background-color: #181818;");

        Label sidebarTitle = new Label("COLLECTIONS");
        sidebarTitle.setStyle("-fx-text-fill: #666; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 5 0 5 10;");

        Button homeBtn = new Button("Home", new FontIcon("fth-home"));
        homeBtn.setMaxWidth(Double.MAX_VALUE);
        homeBtn.setAlignment(Pos.CENTER_LEFT);
        homeBtn.setOnAction(e -> {
            folderTreeView.getSelectionModel().clearSelection();
            currentFolder = null;
            updateEntryTable();
        });

        folderTreeView = new TreeView<>();
        folderTreeView.setShowRoot(false);

        folderTreeView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currentFolder = newVal.getValue();
            } else {
                currentFolder = null;
            }
            updateEntryTable();
        });

        vbox.getChildren().addAll(sidebarTitle, homeBtn, folderTreeView);
        VBox.setVgrow(folderTreeView, javafx.scene.layout.Priority.ALWAYS);
        return vbox;
    }

    private VBox createEntryView() {
        VBox vbox = new VBox(0);
        
        entryTableView = new TableView<>();
        entryTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        currentEntries = FXCollections.observableArrayList();
        entryTableView.setItems(currentEntries);

        TableColumn<Entry, String> titleCol = new TableColumn<>("TITLE");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        TableColumn<Entry, String> userCol = new TableColumn<>("USERNAME");
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        entryTableView.getColumns().addAll(titleCol, userCol);

        // Preview Area
        VBox previewPane = new VBox(15);
        previewPane.setPadding(new Insets(20));
        previewPane.setStyle("-fx-background-color: #1e1e1e; -fx-border-color: #2d2d2d; -fx-border-width: 1 0 0 0;");
        previewPane.setPrefHeight(250);

        Label previewTitle = new Label("Entry Details");
        previewTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        HBox quickActions = new HBox(10);
        Button copyUserBtn = new Button("User", new FontIcon("fth-user"));
        Button copyPassBtn = new Button("Password", new FontIcon("fth-key"));
        Button getTotpBtn = new Button("TOTP", new FontIcon("fth-clock"));
        quickActions.getChildren().addAll(copyUserBtn, copyPassBtn, getTotpBtn);

        Label notesLabel = new Label();
        notesLabel.setWrapText(true);
        notesLabel.setStyle("-fx-text-fill: #aaa; -fx-font-size: 13px;");

        previewPane.getChildren().addAll(previewTitle, quickActions, notesLabel);

        entryTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                previewTitle.setText(newVal.getTitle());
                notesLabel.setText(newVal.getNotes() != null && !newVal.getNotes().isEmpty() ? newVal.getNotes() : "No notes available.");
            } else {
                previewTitle.setText("Select an entry");
                notesLabel.setText("");
            }
        });

        copyUserBtn.setOnAction(e -> {
            Entry selected = entryTableView.getSelectionModel().getSelectedItem();
            if (selected != null && selected.getUsername() != null) {
                ClipboardUtils.copyWithAutoClear(selected.getUsername(), 60);
            }
        });

        copyPassBtn.setOnAction(e -> {
            Entry selected = entryTableView.getSelectionModel().getSelectedItem();
            if (selected != null && selected.getPassword() != null) {
                ClipboardUtils.copyWithAutoClear(selected.getPassword(), 10);
            }
        });

        getTotpBtn.setOnAction(e -> {
            Entry selected = entryTableView.getSelectionModel().getSelectedItem();
            if (selected != null && selected.getTotpSecret() != null && !selected.getTotpSecret().isEmpty()) {
                String totp = TOTPUtils.generateTOTP(selected.getTotpSecret());
                ClipboardUtils.copyWithAutoClear(totp, 10);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "TOTP: " + totp + "\n(Copied to clipboard)", ButtonType.OK);
                alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/maskedsyntax/lockfile/style.css").toExternalForm());
                alert.showAndWait();
            }
        });

        vbox.getChildren().addAll(entryTableView, previewPane);
        VBox.setVgrow(entryTableView, javafx.scene.layout.Priority.ALWAYS);
        return vbox;
    }

    private void loadVaultData() {
        Folder rootDummy = new Folder("Root");
        TreeItem<Folder> rootItem = new TreeItem<>(rootDummy);
        rootItem.setExpanded(true);

        for (Folder f : vault.getRootFolders()) {
            rootItem.getChildren().add(createTreeItem(f));
        }
        folderTreeView.setRoot(rootItem);
        
        currentFolder = null;
        updateEntryTable();
    }

    private TreeItem<Folder> createTreeItem(Folder folder) {
        TreeItem<Folder> item = new TreeItem<>(folder);
        for (Folder child : folder.getSubFolders()) {
            item.getChildren().add(createTreeItem(child));
        }
        return item;
    }

    private void updateEntryTable() {
        if (currentFolder == null || currentFolder.getName().equals("Root")) {
            currentEntries.setAll(vault.getRootEntries());
        } else {
            currentEntries.setAll(currentFolder.getEntries());
        }
    }

    private void saveVault() {
        try {
            VaultManager.saveVault(vault, masterPassword, vaultFile);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save vault: " + e.getMessage(), ButtonType.OK);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/maskedsyntax/lockfile/style.css").toExternalForm());
            alert.showAndWait();
        }
    }

    private void handleAddFolder() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Folder");
        dialog.setHeaderText("Create a new folder");
        dialog.setContentText("Folder Name:");
        dialog.setResizable(true);
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/com/maskedsyntax/lockfile/style.css").toExternalForm());

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            Folder newFolder = new Folder(name);
            TreeItem<Folder> selectedItem = folderTreeView.getSelectionModel().getSelectedItem();
            
            if (selectedItem == null || selectedItem.getValue().getName().equals("Root")) {
                vault.getRootFolders().add(newFolder);
                folderTreeView.getRoot().getChildren().add(new TreeItem<>(newFolder));
            } else {
                selectedItem.getValue().getSubFolders().add(newFolder);
                selectedItem.getChildren().add(new TreeItem<>(newFolder));
            }
            saveVault();
        });
    }

    private void handleAddEntry() {
        EntryDialog dialog = new EntryDialog(null);
        Optional<Entry> result = dialog.showAndWait();
        result.ifPresent(entry -> {
            if (currentFolder == null || currentFolder.getName().equals("Root")) {
                vault.getRootEntries().add(entry);
            } else {
                currentFolder.getEntries().add(entry);
            }
            saveVault();
            updateEntryTable();
        });
    }

    private void handleEditEntry() {
        Entry selected = entryTableView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        EntryDialog dialog = new EntryDialog(selected);
        Optional<Entry> result = dialog.showAndWait();
        result.ifPresent(entry -> {
            selected.setTitle(entry.getTitle());
            selected.setUsername(entry.getUsername());
            selected.setPassword(entry.getPassword());
            selected.setUrl(entry.getUrl());
            selected.setTotpSecret(entry.getTotpSecret());
            selected.setNotes(entry.getNotes());
            selected.setUpdatedAt(System.currentTimeMillis());
            
            saveVault();
            entryTableView.refresh();
        });
    }

    private void handleDeleteEntry() {
        Entry selected = entryTableView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete entry " + selected.getTitle() + "?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                if (currentFolder == null || currentFolder.getName().equals("Root")) {
                    vault.getRootEntries().remove(selected);
                } else {
                    currentFolder.getEntries().remove(selected);
                }
                saveVault();
                updateEntryTable();
            }
        });
    }

    private void handleLock() {
        stage.setScene(new LoginView(stage).getScene());
    }

    private void handleExport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Vault (Encrypted JSON)");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showSaveDialog(stage);
        
        if (file != null) {
            try {
                // To safely export, we could export raw JSON (unencrypted) or just copy the file.
                // For "encrypted JSON", we can just export a copy of the vault file.
                VaultManager.saveVault(vault, masterPassword, file);
                new Alert(Alert.AlertType.INFORMATION, "Export successful", ButtonType.OK).show();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Export failed: " + e.getMessage(), ButtonType.OK).show();
            }
        }
    }
}