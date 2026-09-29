package com.pvault.ui;

import com.pvault.service.GeneratorService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;

/**
 * MODULE 3 UI: Password Generator Screen
 * --------------------------------------
 * Allows configuring length, selecting character pool (Letters / Numbers / Symbols),
 * generating passwords, and copying to clipboard.
 * Connects directly to GeneratorService (Module 3 owner).
 */
public class GeneratorView extends VBox {

    private final GeneratorService generatorService;
    private final TextField resultField;
    private final Slider lengthSlider;
    private final Label lengthLabel;
    private final RadioButton rbLetters;
    private final RadioButton rbLettersNumbers;
    private final RadioButton rbAll;

    public GeneratorView(GeneratorService generatorService) {
        this.generatorService = generatorService;

        setPadding(new Insets(24));
        setSpacing(20);
        setMaxWidth(600);

        Label headerTitle = new Label("Password Generator");
        headerTitle.getStyleClass().add("card-title");

        Label headerDesc = new Label("Create high-entropy, randomized passwords tailored to your security needs.");
        headerDesc.getStyleClass().add("card-desc");

        // Generator Result Card
        VBox resultCard = new VBox(12);
        resultCard.getStyleClass().add("card");

        resultField = new TextField();
        resultField.setEditable(false);
        resultField.setStyle("-fx-font-family: monospace; -fx-font-size: 16px; -fx-font-weight: bold; -fx-alignment: center;");

        Button btnCopy = new Button("Copy to Clipboard");
        btnCopy.getStyleClass().add("btn-secondary");
        btnCopy.setOnAction(e -> copyGeneratedPassword());

        Button btnGenerate = new Button("Generate New Password");
        btnGenerate.getStyleClass().add("btn-primary");
        btnGenerate.setOnAction(e -> generatePassword());

        HBox btnRow = new HBox(12, btnGenerate, btnCopy);
        btnRow.setAlignment(Pos.CENTER);

        resultCard.getChildren().addAll(resultField, btnRow);

        // Options Card
        VBox optionsCard = new VBox(16);
        optionsCard.getStyleClass().add("card");

        Label lengthTitle = new Label("Password Length:");
        lengthTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #e2e8f0;");

        lengthSlider = new Slider(15, 64, 16);
        lengthSlider.setShowTickLabels(true);
        lengthSlider.setShowTickMarks(true);
        lengthSlider.setMajorTickUnit(7);
        lengthSlider.setBlockIncrement(1);

        lengthLabel = new Label("16 characters");
        lengthLabel.setStyle("-fx-text-fill: #38bdf8; -fx-font-weight: bold;");

        // UI Event Listener: dynamically regenerates when slider length changes
        lengthSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int oldInt = oldVal.intValue();
            int newInt = newVal.intValue();
            lengthLabel.setText(newInt + " characters");
            if (oldInt != newInt) {
                generatePassword();
            }
        });

        HBox lengthHeader = new HBox(10, lengthTitle, lengthLabel);

        Label poolTitle = new Label("Character Pool:");
        poolTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #e2e8f0;");

        ToggleGroup poolGroup = new ToggleGroup();

        rbLetters = new RadioButton("Letters Only (a-z, A-Z)");
        rbLetters.setToggleGroup(poolGroup);
        rbLetters.setStyle("-fx-text-fill: #cbd5e1;");

        rbLettersNumbers = new RadioButton("Letters + Numbers (a-z, A-Z, 0-9)");
        rbLettersNumbers.setToggleGroup(poolGroup);
        rbLettersNumbers.setStyle("-fx-text-fill: #cbd5e1;");

        rbAll = new RadioButton("Letters + Numbers + Symbols (!@#$...)");
        rbAll.setToggleGroup(poolGroup);
        rbAll.setSelected(true);
        rbAll.setStyle("-fx-text-fill: #cbd5e1;");

        // UI Event Listener: dynamically regenerates when character pool option changes
        poolGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                generatePassword();
            }
        });

        VBox radioBox = new VBox(8, rbLetters, rbLettersNumbers, rbAll);

        optionsCard.getChildren().addAll(lengthHeader, lengthSlider, poolTitle, radioBox);

        getChildren().addAll(headerTitle, headerDesc, resultCard, optionsCard);

        // Generate initial sample password
        generatePassword();
    }

    private void generatePassword() {
        int length = (int) lengthSlider.getValue();
        GeneratorService.PoolOption option;

        if (rbLetters.isSelected()) {
            option = GeneratorService.PoolOption.LETTERS_ONLY;
        } else if (rbLettersNumbers.isSelected()) {
            option = GeneratorService.PoolOption.LETTERS_NUMBERS;
        } else {
            option = GeneratorService.PoolOption.LETTERS_NUMBERS_SYMBOLS;
        }

        // Call Module 3
        String pass = generatorService.generatePassword(length, option);
        resultField.setText(pass);
    }

    private void copyGeneratedPassword() {
        String pass = resultField.getText();
        if (pass == null || pass.isEmpty()) return;

        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(pass);
        clipboard.setContent(content);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Generated password copied to clipboard!");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
