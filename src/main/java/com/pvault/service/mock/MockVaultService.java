package com.pvault.service.mock;

import com.pvault.model.VaultEntry;
import com.pvault.service.VaultService;

import java.util.ArrayList;
import java.util.List;

/**
 * MOCK IMPLEMENTATION of VaultService
 * ------------------------------------
 * Preloaded with dummy sample data so the UI list, search, add, and delete can be tested right away.
 * The teammate in charge of Module 2 will replace or connect this with AES encryption/decryption
 * and real file/database storage.
 */
public class MockVaultService implements VaultService {

    private final List<VaultEntry> entries = new ArrayList<>();

    public MockVaultService() {
        // Sample seed data to showcase the UI
        entries.add(new VaultEntry("Google", "student@gmail.com", "P@ssw0rd123!", "Personal"));
        entries.add(new VaultEntry("GitHub", "dev-coder", "GitSecure#99", "Development"));
        entries.add(new VaultEntry("University Portal", "u20261001", "Campus!Key2026", "Education"));
        entries.add(new VaultEntry("Netflix", "family_stream", "MovieTime888$", "Entertainment"));
    }

    @Override
    public List<VaultEntry> getAllEntries() {
        return new ArrayList<>(entries);
    }

    @Override
    public List<VaultEntry> searchEntries(String query) {
        if (query == null || query.isBlank()) {
            return getAllEntries();
        }
        String lower = query.toLowerCase();
        List<VaultEntry> results = new ArrayList<>();
        // Loop implementation requirement
        for (VaultEntry entry : entries) {
            if (entry.getSiteName().toLowerCase().contains(lower) || 
                entry.getUsername().toLowerCase().contains(lower) ||
                entry.getCategory().toLowerCase().contains(lower)) {
                results.add(entry);
            }
        }
        return results;
    }

    @Override
    public boolean addEntry(String siteName, String username, String plainPassword, String category) {
        // If-else: prevent duplicate site name
        for (VaultEntry entry : entries) {
            if (entry.getSiteName().equalsIgnoreCase(siteName)) {
                return false; // Duplicate found
            }
        }
        // In real module, plainPassword would be AES encrypted here
        entries.add(new VaultEntry(siteName, username, plainPassword, category));
        return true;
    }

    @Override
    public String decryptPassword(VaultEntry entry) {
        // In real module, AES decryption will occur here
        return entry.getEncryptedPassword();
    }

    @Override
    public boolean updateEntry(VaultEntry entry, String newPlainPassword) {
        entry.setEncryptedPassword(newPlainPassword);
        return true;
    }

    @Override
    public boolean deleteEntry(VaultEntry entry) {
        return entries.removeIf(e -> e.getId().equals(entry.getId()));
    }
}
