package com.pvault.ui;

import com.pvault.service.AnalyzerService;
import com.pvault.service.GeneratorService;
import com.pvault.service.VaultService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

/**
 * MODULE 5: Main Menu / Navigation Screen
 * ---------------------------------------
 * Requirements from project-context.md:
 * 1. Loop/Event loop: Keeps application running and manages view state.
 * 2. Switch: Routes button/menu actions to the correct panel (Vault, Generator, Analyzer).
 */
public class MainDashboardView extends BorderPane {

    public enum TabType {
        VAULT,
        GENERATOR,
        ANALYZER
    }

    private final VaultView vaultView;
    private final GeneratorView generatorView;
    private final AnalyzerView analyzerView;
    private final Runnable onLockVault;

    private Button btnVault;
    private Button btnGenerator;
    private Button btnAnalyzer;
    private StackPane contentArea;

    public MainDashboardView(VaultService vaultService,
                             GeneratorService generatorService,
                             AnalyzerService analyzerService,
                             Runnable onLockVault) {
        this.onLockVault = onLockVault;

        this.vaultView = new VaultView(vaultService);
        this.generatorView = new GeneratorView(generatorService);
        this.analyzerView = new AnalyzerView(analyzerService);

        // Sidebar Navigation
        setLeft(buildSidebar());

        // Content Area
        contentArea = new StackPane();
        contentArea.setPadding(new Insets(10));
        setCenter(contentArea);

        // Default to Vault Tab
        switchTab(TabType.VAULT);
    }

    private Node buildSidebar() {
        VBox sidebar = new VBox(12);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(220);
        sidebar.setPadding(new Insets(24, 16, 24, 16));

        Label brandLabel = new Label("PVault");
        brandLabel.getStyleClass().add("brand-title");

        Label subtitle = new Label("Password Manager");
        subtitle.getStyleClass().add("brand-subtitle");

        VBox brandBox = new VBox(2, brandLabel, subtitle);
        brandBox.setPadding(new Insets(0, 0, 20, 0));

        btnVault = createNavButton("🔐 Vault Entries", () -> switchTab(TabType.VAULT));
        btnGenerator = createNavButton("⚡ Generator", () -> switchTab(TabType.GENERATOR));
        btnAnalyzer = createNavButton("🛡️ Strength Analyzer", () -> switchTab(TabType.ANALYZER));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnLock = new Button("🔒 Lock Vault");
        btnLock.getStyleClass().add("btn-secondary");
        btnLock.setMaxWidth(Double.MAX_VALUE);
        btnLock.setOnAction(e -> onLockVault.run());

        sidebar.getChildren().addAll(brandBox, btnVault, btnGenerator, btnAnalyzer, spacer, btnLock);
        return sidebar;
    }

    private Button createNavButton(String title, Runnable action) {
        Button btn = new Button(title);
        btn.getStyleClass().add("nav-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> action.run());
        return btn;
    }

    /**
     * Requirement: Switch statement routes button/menu actions to correct panel
     */
    public void switchTab(TabType tab) {
        btnVault.getStyleClass().remove("nav-button-active");
        btnGenerator.getStyleClass().remove("nav-button-active");
        btnAnalyzer.getStyleClass().remove("nav-button-active");

        contentArea.getChildren().clear();

        switch (tab) {
            case VAULT:
                btnVault.getStyleClass().add("nav-button-active");
                vaultView.refreshEntries();
                contentArea.getChildren().add(vaultView);
                break;
            case GENERATOR:
                btnGenerator.getStyleClass().add("nav-button-active");
                contentArea.getChildren().add(generatorView);
                break;
            case ANALYZER:
                btnAnalyzer.getStyleClass().add("nav-button-active");
                contentArea.getChildren().add(analyzerView);
                break;
        }
    }
}
