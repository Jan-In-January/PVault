// Made by ARWIN JOHN A. ALAB-AB pogi

package com.pvault.service.mock;

import com.pvault.service.AnalyzerService;
import java.util.ArrayList;
import java.util.List;

public class MockAnalyzerService implements AnalyzerService {

    @Override
    public AnalysisResult analyze(String password) {
        if (password == null || password.isEmpty()) {
            return new AnalysisResult(StrengthLevel.WEAK, 0.0, List.of("Enter a password to analyze."));
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSymbol = false;

        for (int i = 0; i < password.length(); i++) {
            char ch = password.charAt(i);

            if (Character.isUpperCase(ch)) {
                hasUpper = true;
            } else if (Character.isLowerCase(ch)) {
                hasLower = true;
            } else if (Character.isDigit(ch)) {
                hasDigit = true;
            } else {
                hasSymbol = true; 
            }
        }

        int score = 0;
        List<String> feedback = new ArrayList<>();

        if (password.length() >= 8) {
            score++;
        } else {
            feedback.add("Too short! minimum 8 characters recommended.");
        }

        if (password.length() >= 14) {
            score++;
        }

        if (hasUpper && hasLower) {
            score++;
        } else {
            feedback.add("Add a mixture of uppercase and lowercase letters.");
        }

        if (hasDigit) {
            score++;
        } else {
            feedback.add("Add at least one digit (0-9).");
        }

        if (hasSymbol) {
            score++;
        } else {
            feedback.add("Add special symbols (e.g., !, @, #, $, %).");
        }

        StrengthLevel level;
        double percentage;

        switch (score) {
            case 5:
                level = StrengthLevel.VERY_STRONG;
                percentage = 1.0;
                break;
            case 4:
                level = StrengthLevel.STRONG;
                percentage = 0.8;
                break;
            case 3:
                level = StrengthLevel.MEDIUM;
                percentage = 0.55;
                break;
            default:
                level = StrengthLevel.WEAK;
                percentage = 0.25;
                break;
        }

        if (feedback.isEmpty()) {
            feedback.add("Excellent! This password is very strong.");
        }

        return new AnalysisResult(level, percentage, feedback);
    }
}
