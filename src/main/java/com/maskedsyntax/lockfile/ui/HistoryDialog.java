package com.maskedsyntax.lockfile.ui;

import com.maskedsyntax.lockfile.model.Entry;
import com.maskedsyntax.lockfile.model.PasswordRecord;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HistoryDialog extends Dialog<String> {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public HistoryDialog(Entry entry) {
        setTitle("Password History - " + entry.getTitle());
        setHeaderText("Previous passwords for this account.");

        DialogPane dialogPane = getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/com/maskedsyntax/lockfile/style.css").toExternalForm());
        
        ButtonType revertButtonType = new ButtonType("Revert", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(revertButtonType, ButtonType.CANCEL);

        TableView<PasswordRecord> tableView = new TableView<>();
        List<PasswordRecord> history = entry.getPasswordHistory();
        tableView.setItems(FXCollections.observableArrayList(history));

        TableColumn<PasswordRecord, String> dateCol = new TableColumn<>("DATE");
        dateCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(dateFormat.format(new Date(cellData.getValue().timestamp())))
        );
        dateCol.setPrefWidth(150);

        TableColumn<PasswordRecord, String> passCol = new TableColumn<>("PASSWORD");
        passCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().password()));
        passCol.setPrefWidth(200);

        tableView.getColumns().addAll(dateCol, passCol);

        VBox vbox = new VBox(10, tableView);
        vbox.setPadding(new Insets(10));
        dialogPane.setContent(vbox);

        setResultConverter(dialogButton -> {
            if (dialogButton == revertButtonType) {
                PasswordRecord selected = tableView.getSelectionModel().getSelectedItem();
                return selected != null ? selected.password() : null;
            }
            return null;
        });
    }
}
