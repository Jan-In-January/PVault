package com.pvault.service;

import java.util.List;

/**
 * MODULE 4: Password Strength Analyzer Interface
 * ----------------------------------------------------
 * ASSIGNED TO: Member handling Password Strength Analyzer
 *
 * Requirements from project-context.md:
 * 1. Loop: Scans each character of input password to check uppercase, lowercase, digits, and symbols.
 * 2. Switch: Converts computed score into strength label (Weak / Medium / Strong / Very Strong).
 * 3. If-else: Flags specific weaknesses (too short, no numbers, common pattern, etc.) and gives feedback.
 */
public interface AnalyzerService {

    enum StrengthLevel {
        WEAK,
        MEDIUM,
        STRONG,
        VERY_STRONG
    }

    class AnalysisResult {
        private final StrengthLevel level;
        private final double scorePercentage; // 0.0 to 1.0 (for progress bar)
        private final List<String> suggestions;

        public AnalysisResult(StrengthLevel level, double scorePercentage, List<String> suggestions) {
            this.level = level;
            this.scorePercentage = scorePercentage;
            this.suggestions = suggestions;
        }

        public StrengthLevel getLevel() {
            return level;
        }

        public double getScorePercentage() {
            return scorePercentage;
        }

        public List<String> getSuggestions() {
            return suggestions;
        }
    }

    /**
     * Analyzes a candidate password and produces score, category, and actionable suggestions.
     */
    AnalysisResult analyze(String password);
}
