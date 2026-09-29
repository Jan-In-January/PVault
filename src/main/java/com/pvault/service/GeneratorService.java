package com.pvault.service;

import java.security.SecureRandom;
import java.util.regex.Pattern;

/**
 * MODULE 3: Password Generator Service
 * ------------------------------------
 * - Cryptographic Security: Uses java.security.SecureRandom to comply with ISO 27002.
 * - NIST SP 800-63B Compliance: Enforces a strict 15-character minimum length.
 * - Architectural Constraints:
 *     1. Switch: selects the active character pool.
 *     2. Loop: generates password characters one-by-one.
 *     3. If-else: validates minimum requirements (digits/symbols) before returning.
 */
public class GeneratorService {

    public enum PoolOption {
        LETTERS_ONLY,
        LETTERS_NUMBERS,
        LETTERS_NUMBERS_SYMBOLS
    }

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+[]{}|;:,.<>?/";

    // Precomputed pools to avoid repeated string concatenation overhead
    private static final String POOL_LETTERS = LOWERCASE + UPPERCASE;
    private static final String POOL_LETTERS_NUMBERS = LOWERCASE + UPPERCASE + NUMBERS;
    private static final String POOL_ALL = LOWERCASE + UPPERCASE + NUMBERS + SYMBOLS;

    // Precompiled regex patterns for minimum requirement validation
    private static final Pattern NUMBER_PATTERN = Pattern.compile(".*\\d.*");
    private static final Pattern SYMBOL_PATTERN = Pattern.compile(".*[!@#$%^&*()\\-_=+\\[\\]{}|;:,.<>?/].*");

    private final SecureRandom random = new SecureRandom();

    /**
     * Generates a cryptographically secure random password complying with NIST SP 800-63B.
     *
     * @param length Desired character count (strictly enforced to >= 15 characters)
     * @param option The selected character pool
     * @return Validated random password
     */
    public String generatePassword(int length, PoolOption option) {
        // Enforce the 15-character NIST SP 800-63B minimum standard
        if (length < 15) {
            length = 15;
        }

        if (option == null) {
            option = PoolOption.LETTERS_NUMBERS_SYMBOLS;
        }

        String pool;
        // Requirement: switch statement determines the character pool
        switch (option) {
            case LETTERS_ONLY:
                pool = POOL_LETTERS;
                break;
            case LETTERS_NUMBERS:
                pool = POOL_LETTERS_NUMBERS;
                break;
            case LETTERS_NUMBERS_SYMBOLS:
            default:
                pool = POOL_ALL;
                break;
        }

        String password;
        boolean isValid;

        do {
            StringBuilder passwordBuilder = new StringBuilder(length);
            
            // Requirement: loop generates character-by-character
            for (int i = 0; i < length; i++) {
                int randomIndex = random.nextInt(pool.length());
                passwordBuilder.append(pool.charAt(randomIndex));
            }
            
            password = passwordBuilder.toString();
            // Requirement: if-else validates minimum character requirements
            isValid = checkMinimumRequirements(password, option);
            
        } while (!isValid);

        return password;
    }

    /**
     * Requirement: if-else block ensures the password meets pool constraints.
     */
    private boolean checkMinimumRequirements(String password, PoolOption option) {
        if (option == PoolOption.LETTERS_NUMBERS) {
            return NUMBER_PATTERN.matcher(password).matches();
        } else if (option == PoolOption.LETTERS_NUMBERS_SYMBOLS) {
            boolean hasNumber = NUMBER_PATTERN.matcher(password).matches();
            boolean hasSymbol = SYMBOL_PATTERN.matcher(password).matches();
            return hasNumber && hasSymbol;
        }

        return true; 
    }
}