package com.pvault.service;

import com.pvault.model.VaultEntry;
import java.util.List;

/**
 * MODULE 2: Password Vault (Store / Retrieve) Interface
 * ----------------------------------------------------
 * ASSIGNED TO: Member handling Vault Storage & AES Encryption
 *
 * Requirements from project-context.md:
 * 1. Loop: Iterates through saved entries to display them in the list, and to search/filter by site name.
 * 2. If-else: Checks if an entry with that site name already exists before adding (prevent duplicates); confirms before delete.
 * 3. AES encrypt on save, AES decrypt on view/copy.
 */
public interface VaultService {

    /**
     * Retrieves all saved entries. Entries may have their passwords masked or encrypted.
     */
    List<VaultEntry> getAllEntries();

    /**
     * Searches/filters entries matching the given query string (by site name or username).
     * Requirement: Uses loop to iterate and filter.
     */
    List<VaultEntry> searchEntries(String query);

    /**
     * Adds a new vault entry.
     * Requirement: If-else checks if site name already exists before adding (prevents duplicates).
     * Requirement: Encrypts the plainPassword using AES before saving.
     * 
     * @return true if added successfully, false if duplicate or error
     */
    boolean addEntry(String siteName, String username, String plainPassword, String category);

    /**
     * Decrypts an entry's password using AES to allow viewing or copying.
     */
    String decryptPassword(VaultEntry entry);

    /**
     * Updates an existing entry.
     */
    boolean updateEntry(VaultEntry entry, String newPlainPassword);

    /**
     * Deletes an entry from the vault.
     */
    boolean deleteEntry(VaultEntry entry);
}
