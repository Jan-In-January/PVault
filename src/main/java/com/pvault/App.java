package com.pvault;
import com.pvault.db.DatabaseManager;
import com.pvault.service.AnalyzerService;
import com.pvault.service.AuthService;
import com.pvault.service.GeneratorService;
import com.pvault.service.SqliteVaultService;
import com.pvault.service.VaultService;
import com.pvault.service.mock.MockAnalyzerService;
import com.pvault.service.mock.MockAuthService;
import com.pvault.service.mock.MockVaultService;
import com.pvault.ui.LoginView;
import com.pvault.ui.MainDashboardView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Main JavaFX Application Entry Point.
 * ------------------------------------
 * Connects the services with the UI layers and handles screen switching.
 */
public class App extends Application {

    // Services (Real GeneratorService integrated for Module 3)
    private final AuthService authService = new MockAuthService();
    private final VaultService vaultService = createVaultService();
    private final GeneratorService generatorService = new GeneratorService();
    private final AnalyzerService analyzerService = new MockAnalyzerService();

    private StackPane rootContainer;
    private LoginView loginView;
    private MainDashboardView dashboardView;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("PVault - Personal Password Vault");

        rootContainer = new StackPane();

        loginView = new LoginView(authService, this::showDashboard);
        dashboardView = new MainDashboardView(vaultService, generatorService, analyzerService, this::showLogin);

        // Start with the Login Screen
        showLogin();

        Scene scene = new Scene(rootContainer, 960, 620);

        // Load custom stylesheet
        URL cssResource = getClass().getResource("/styles/styles.css");
        if (cssResource != null) {
            scene.getStylesheets().add(cssResource.toExternalForm());
        }

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(520);
        primaryStage.show();
    }

    private void showLogin() {
        rootContainer.getChildren().setAll(loginView);
    }

    private void showDashboard() {
        rootContainer.getChildren().setAll(dashboardView);
    }

    // TEMPORARY: unlocks with the mock master password until Login is wired to the vault (Phase 3)
    private static VaultService createVaultService() {
        SqliteVaultService service = new SqliteVaultService();
        service.unlock("admin".toCharArray());
        return service;
    }

    public static void main(String[] args) {
        DatabaseManager.initialize();
        launch(args);
    }
}
