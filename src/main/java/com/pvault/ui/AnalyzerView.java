package com.pvault.ui;

import com.pvault.service.AnalyzerService;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

/**
 * MODULE 4 UI: Password Strength Analyzer Screen
 * -----------------------------------------------
 * Live password strength feedback, visual progress meter,
 * strength badges (Weak / Medium / Strong / Very Strong), and security suggestions.
 * Connects directly to AnalyzerService (Module 4 owner).
 */
public class AnalyzerView extends VBox {

    private final AnalyzerService analyzerService;
    private final TextField inputField;
    private final ProgressBar progressBar;
    private final Label badgeLabel;
    private final ListView<String> suggestionsList;

    public AnalyzerView(AnalyzerService analyzerService) {
        this.analyzerService = analyzerService;

        setPadding(new Insets(24));
        setSpacing(20);
        setMaxWidth(600);

        Label headerTitle = new Label("Password Strength Analyzer");
        headerTitle.getStyleClass().add("card-title");

        Label headerDesc = new Label("Test the resilience of a password against brute-force and common security flaws.");
        headerDesc.getStyleClass().add("card-desc");

        VBox card = new VBox(16);
        card.getStyleClass().add("card");

        Label inputTitle = new Label("Enter Password to Test:");
        inputTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #e2e8f0;");

        inputField = new TextField();
        inputField.setPromptText("Type or paste a password...");
        inputField.textProperty().addListener((obs, oldVal, newVal) -> updateAnalysis(newVal));

        progressBar = new ProgressBar(0);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(12);

        badgeLabel = new Label("AWAITING INPUT");
        badgeLabel.getStyleClass().addAll("badge", "badge-weak");

        Label feedbackTitle = new Label("Security Feedback & Recommendations:");
        feedbackTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #e2e8f0;");

        suggestionsList = new ListView<>();
        suggestionsList.setPrefHeight(120);

        card.getChildren().addAll(inputTitle, inputField, progressBar, badgeLabel, feedbackTitle, suggestionsList);

        getChildren().addAll(headerTitle, headerDesc, card);

        updateAnalysis("");
    }

    private void updateAnalysis(String password) {
        // Module 4 call
        AnalyzerService.AnalysisResult result = analyzerService.analyze(password);

        progressBar.setProgress(result.getScorePercentage());

        // Update badge
        badgeLabel.getStyleClass().removeAll("badge-weak", "badge-medium", "badge-strong", "badge-very-strong");

        switch (result.getLevel()) {
            case WEAK:
                badgeLabel.setText("WEAK");
                badgeLabel.getStyleClass().add("badge-weak");
                progressBar.setStyle("-fx-accent: #ef4444;");
                break;
            case MEDIUM:
                badgeLabel.setText("MEDIUM");
                badgeLabel.getStyleClass().add("badge-medium");
                progressBar.setStyle("-fx-accent: #f59e0b;");
                break;
            case STRONG:
                badgeLabel.setText("STRONG");
                badgeLabel.getStyleClass().add("badge-strong");
                progressBar.setStyle("-fx-accent: #10b981;");
                break;
            case VERY_STRONG:
                badgeLabel.setText("VERY STRONG");
                badgeLabel.getStyleClass().add("badge-very-strong");
                progressBar.setStyle("-fx-accent: #38bdf8;");
                break;
        }

        suggestionsList.getItems().setAll(result.getSuggestions());
    }
}
