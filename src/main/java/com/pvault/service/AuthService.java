package com.pvault.service;

/**
 * MODULE 1: Master Login / Authentication Interface
 * ----------------------------------------------------
 * ASSIGNED TO: Member handling Authentication
 *
 * Requirements from project-context.md:
 * 1. SHA-256 hash comparison: Compares SHA-256 hash of entered master password against stored hash.
 * 2. If-else: Grants or denies vault access.
 * 3. Lockout mechanism: Locks out after repeated failed attempts (e.g. 3-5 failed tries).
 */
public interface AuthService {

    /**
     * Verifies the entered master password against the stored master credential hash.
     * 
     * @param enteredMasterPassword The plaintext password entered by the user
     * @return true if hash matches and user is not locked out; false otherwise
     */
    boolean authenticate(String enteredMasterPassword);

    /**
     * Checks if the user is currently locked out due to repeated failed attempts.
     */
    boolean isLockedOut();

    /**
     * Returns the remaining attempts before lockout.
     */
    int getRemainingAttempts();

    /**
     * Checks whether a master password has already been created (first-time setup vs existing vault).
     */
    boolean hasMasterPasswordSet();

    /**
     * Sets or resets the master password (storing its SHA-256 hash).
     */
    boolean setupMasterPassword(String newMasterPassword);
}
