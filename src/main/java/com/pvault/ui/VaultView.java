package com.pvault.ui;

import com.pvault.model.VaultEntry;
import com.pvault.service.VaultService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;

import java.util.Optional;

/**
 * MODULE 2 UI: Vault Manager Screen
 * ---------------------------------
 * Handles listing, live searching, adding, decrypting/viewing, and deleting entries.
 * Connects directly to VaultService (Module 2 owner).
 */
public class VaultView extends VBox {

    private final VaultService vaultService;
    private final TableView<VaultEntry> tableView;
    private final ObservableList<VaultEntry> tableData;
    private final TextField searchField;

    public VaultView(VaultService vaultService) {
        this.vaultService = vaultService;
        this.tableData = FXCollections.observableArrayList();

        setPadding(new Insets(24));
        setSpacing(16);

        // Header and Search bar
        Label headerTitle = new Label("Saved Passwords");
        headerTitle.getStyleClass().add("card-title");

        searchField = new TextField();
        searchField.setPromptText("Search sites, usernames, or categories...");
        searchField.setPrefWidth(300);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterEntries(newVal));

        Button btnAdd = new Button("+ Add New Entry");
        btnAdd.getStyleClass().add("btn-primary");
        btnAdd.setOnAction(e -> showAddEntryDialog());

        HBox topBar = new HBox(12, headerTitle, new Region(), searchField, btnAdd);
        HBox.setHgrow(topBar.getChildren().get(1), Priority.ALWAYS);
        topBar.setAlignment(Pos.CENTER_LEFT);

        // Table Setup
        tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<VaultEntry, String> colSite = new TableColumn<>("Site / Service");
        colSite.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSiteName()));

        TableColumn<VaultEntry, String> colCategory = new TableColumn<>("Category");
        colCategory.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory()));
        colCategory.setMaxWidth(140);

        TableColumn<VaultEntry, String> colUser = new TableColumn<>("Username / Email");
        colUser.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));

        TableColumn<VaultEntry, String> colPass = new TableColumn<>("Password");
        colPass.setCellValueFactory(data -> new SimpleStringProperty("••••••••••••"));

        tableView.getColumns().addAll(colSite, colCategory, colUser, colPass);
        tableView.setItems(tableData);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        // Action Toolbar
        Button btnView = new Button("View / Decrypt");
        btnView.getStyleClass().add("btn-secondary");
        btnView.setOnAction(e -> handleViewPassword());

        Button btnCopy = new Button("Copy Password");
        btnCopy.getStyleClass().add("btn-secondary");
        btnCopy.setOnAction(e -> handleCopyPassword());

        Button btnDelete = new Button("Delete");
        btnDelete.getStyleClass().add("btn-danger");
        btnDelete.setOnAction(e -> handleDeleteEntry());

        HBox bottomBar = new HBox(10, btnView, btnCopy, btnDelete);
        bottomBar.setAlignment(Pos.CENTER_LEFT);

        getChildren().addAll(topBar, tableView, bottomBar);

        refreshEntries();
    }

    public void refreshEntries() {
        tableData.setAll(vaultService.getAllEntries());
    }

    private void filterEntries(String query) {
        tableData.setAll(vaultService.searchEntries(query));
    }

    private void showAddEntryDialog() {
        Dialog<VaultEntry> dialog = new Dialog<>();
        dialog.setTitle("Add Vault Entry");
        dialog.setHeaderText("Create a new encrypted password entry");

        ButtonType saveBtnType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField siteInput = new TextField();
        siteInput.setPromptText("e.g. GitHub");
        TextField userInput = new TextField();
        userInput.setPromptText("Username / Email");
        PasswordField passInput = new PasswordField();
        passInput.setPromptText("Plaintext Password");
        TextField categoryInput = new TextField();
        categoryInput.setPromptText("e.g. Work, Personal");

        grid.add(new Label("Site Name:"), 0, 0);
        grid.add(siteInput, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(categoryInput, 1, 1);
        grid.add(new Label("Username:"), 0, 2);
        grid.add(userInput, 1, 2);
        grid.add(new Label("Password:"), 0, 3);
        grid.add(passInput, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveBtnType) {
                boolean success = vaultService.addEntry(
                    siteInput.getText(),
                    userInput.getText(),
                    passInput.getText(),
                    categoryInput.getText()
                );
                if (!success) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "An entry with this site name already exists!");
                    alert.showAndWait();
                    return null;
                }
                return new VaultEntry(siteInput.getText(), userInput.getText(), passInput.getText(), categoryInput.getText());
            }
            return null;
        });

        Optional<VaultEntry> result = dialog.showAndWait();
        if (result.isPresent()) {
            refreshEntries();
        }
    }

    private void handleViewPassword() {
        VaultEntry selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select an account from the table first.");
            return;
        }

        // Decrypts via Module 2 AES
        String decrypted = vaultService.decryptPassword(selected);

        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Decrypted Password");
        info.setHeaderText("Credentials for: " + selected.getSiteName());
        info.setContentText("Username: " + selected.getUsername() + "\nPassword: " + decrypted);
        info.showAndWait();
    }

    private void handleCopyPassword() {
        VaultEntry selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select an account from the table first.");
            return;
        }

        String decrypted = vaultService.decryptPassword(selected);
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(decrypted);
        clipboard.setContent(content);

        showAlert("Copied", "Password for " + selected.getSiteName() + " copied to clipboard!");
    }

    private void handleDeleteEntry() {
        VaultEntry selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select an account from the table first.");
            return;
        }

        // Requirement: Confirmation before delete
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
            "Are you sure you want to delete the credentials for " + selected.getSiteName() + "?", 
            ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(res -> {
            if (res == ButtonType.YES) {
                vaultService.deleteEntry(selected);
                refreshEntries();
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
