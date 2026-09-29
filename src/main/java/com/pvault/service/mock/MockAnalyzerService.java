package com.pvault.service.mock;

import com.pvault.service.AnalyzerService;
import java.util.ArrayList;
import java.util.List;

/**
 * MOCK / INITIAL IMPLEMENTATION of AnalyzerService
 * -------------------------------------------------
 * Fulfills the loop, switch, and if-else requirements from project-context.md.
 * Teammate for Module 4 can customize scoring logic or expand feedback rules.
 */
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

        // Requirement: Loop scans each character
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSymbol = true;
        }

        int score = 0;
        List<String> feedback = new ArrayList<>();

        // Requirement: If-else flags specific weaknesses
        if (password.length() >= 8) {
            score++;
        } else {
            feedback.add("Too short: minimum 8 characters recommended.");
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
            feedback.add("Add at least one numeric digit (0-9).");
        }

        if (hasSymbol) {
            score++;
        } else {
            feedback.add("Add special symbols (e.g., !, @, #, $, %).");
        }

        StrengthLevel level;
        double percentage;

        // Requirement: Switch converts score into strength level
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
            feedback.add("Excellent! This password is very strong and resilient.");
        }

        return new AnalysisResult(level, percentage, feedback);
    }
}
