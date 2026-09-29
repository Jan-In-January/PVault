package com.pvault.service.mock;

import com.pvault.service.GeneratorService;
import java.security.SecureRandom;

/**
 * MOCK / INITIAL IMPLEMENTATION of GeneratorService
 * --------------------------------------------------
 * Provides functional generation out of the box while satisfying
 * the loop, switch, and if-else requirements from project-context.md.
 * Teammate for Module 3 can refine or extend this class.
 */
public class MockGeneratorService implements GeneratorService {

    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+[]{}<>/?~";
    private final SecureRandom random = new SecureRandom();

    @Override
    public String generate(int length, PoolOption option) {
        if (length < 4) {
            length = 4;
        }

        String pool;
        // Requirement: Switch determines pool
        switch (option) {
            case LETTERS_ONLY:
                pool = LETTERS;
                break;
            case LETTERS_AND_NUMBERS:
                pool = LETTERS + NUMBERS;
                break;
            case LETTERS_NUMBERS_AND_SYMBOLS:
            default:
                pool = LETTERS + NUMBERS + SYMBOLS;
                break;
        }

        StringBuilder sb = new StringBuilder();
        // Requirement: Loop builds random password character-by-character
        for (int i = 0; i < length; i++) {
            int idx = random.nextInt(pool.length());
            sb.append(pool.charAt(idx));
        }

        return sb.toString();
    }
}
