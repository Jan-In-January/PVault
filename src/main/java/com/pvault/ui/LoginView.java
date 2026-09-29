package com.pvault.ui;

import com.pvault.service.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

/**
 * MODULE 1 UI: Master Login Screen
 * --------------------------------
 * Connects the Master Login input to AuthService.
 * Displays lockout warnings and error messages according to requirements.
 */
public class LoginView extends VBox {

    private final AuthService authService;
    private final Runnable onLoginSuccess;
    private final Label statusLabel;
    private final PasswordField passwordField;
    private final Button loginButton;

    public LoginView(AuthService authService, Runnable onLoginSuccess) {
        this.authService = authService;
        this.onLoginSuccess = onLoginSuccess;

        setAlignment(Pos.CENTER);
        setPadding(new Insets(40));
        setSpacing(20);

        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        card.setMaxWidth(380);
        card.setAlignment(Pos.CENTER);

        Label title = new Label("PVault Login");
        title.getStyleClass().add("card-title");

        Label subtitle = new Label("Enter your Master Password to decrypt and unlock the vault.");
        subtitle.getStyleClass().add("card-desc");
        subtitle.setWrapText(true);

        passwordField = new PasswordField();
        passwordField.setPromptText("Master Password (mock: admin)");
        passwordField.setOnAction(e -> handleLogin());

        loginButton = new Button("Unlock Vault");
        loginButton.getStyleClass().add("btn-primary");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setOnAction(e -> handleLogin());

        statusLabel = new Label();
        statusLabel.setWrapText(true);

        card.getChildren().addAll(title, subtitle, passwordField, loginButton, statusLabel);
        getChildren().add(card);
    }

    private void handleLogin() {
        String enteredPassword = passwordField.getText();

        if (authService.isLockedOut()) {
            statusLabel.setText("Vault is locked due to repeated failed attempts.");
            statusLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
            loginButton.setDisable(true);
            passwordField.setDisable(true);
            return;
        }

        // Module 1 verification
        boolean success = authService.authenticate(enteredPassword);

        if (success) {
            statusLabel.setText("Access granted!");
            statusLabel.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
            onLoginSuccess.run();
        } else {
            int remaining = authService.getRemainingAttempts();
            if (authService.isLockedOut()) {
                statusLabel.setText("Account Locked Out! Too many failed attempts.");
                statusLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                loginButton.setDisable(true);
                passwordField.setDisable(true);
            } else {
                statusLabel.setText("Invalid Master Password! " + remaining + " attempts remaining.");
                statusLabel.setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
            }
        }
    }
}
