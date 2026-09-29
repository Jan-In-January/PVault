package com.pvault.service;

/**
 * MODULE 3: Password Generator Interface
 * ----------------------------------------------------
 * ASSIGNED TO: Member handling Password Generation
 *
 * Requirements from project-context.md:
 * 1. Loop: Builds the random password character-by-character until it reaches the requested length.
 * 2. Switch: Determines which character pool to pull from based on selected option:
 *    - Letters Only
 *    - Letters + Numbers
 *    - Letters + Numbers + Symbols
 * 3. If-else: Ensures generated password meets minimum requirements
 *    (e.g., must contain at least one number/symbol if selected) before accepting it.
 */
public interface GeneratorService {

    enum PoolOption {
        LETTERS_ONLY,
        LETTERS_AND_NUMBERS,
        LETTERS_NUMBERS_AND_SYMBOLS
    }

    /**
     * Generates a strong random password based on the desired length and character pool.
     *
     * @param length Desired character count (e.g., 8 to 64)
     * @param option The selected character pool
     * @return Generated password string
     */
    String generate(int length, PoolOption option);
}
