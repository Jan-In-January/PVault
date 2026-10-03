package com.pvault.service;

import com.pvault.db.DatabaseManager;
import com.pvault.model.VaultEntry;
import com.pvault.util.AESUtil;

import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * MODULE 2: real Vault implementation (SQLite + AES-256-GCM).
 * Call unlock(masterPassword) once after login before using the vault.
 */
public class SqliteVaultService implements VaultService {

    private SecretKey key; // null until unlock() is called

    // ---------- Unlocking ----------

    /** Derives the AES key from the master password. The salt is saved in vault.db. */
    public void unlock(char[] masterPassword) {
        try {
            byte[] salt = loadOrCreateSalt();
            this.key = AESUtil.deriveKey(masterPassword, salt);
        } catch (GeneralSecurityException | SQLException e) {
            throw new IllegalStateException("Could not unlock the vault", e);
        }
    }

    /** Forgets the AES key (call on logout). */
    public void lock() {
        this.key = null;
    }

    private SecretKey requireKey() {
        if (key == null) {
            throw new IllegalStateException("Vault is locked. Call unlock() first.");
        }
        return key;
    }

    private byte[] loadOrCreateSalt() throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (Statement st = conn.createStatement()) {
                st.execute("CREATE TABLE IF NOT EXISTS vault_meta ("
                        + "meta_key TEXT PRIMARY KEY, meta_value TEXT NOT NULL)");
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT meta_value FROM vault_meta WHERE meta_key = 'kdf_salt'");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Base64.getDecoder().decode(rs.getString(1));
                }
            }
            byte[] salt = AESUtil.generateSalt();
            try (PreparedStatement ins = conn.prepareStatement(
                    "INSERT INTO vault_meta (meta_key, meta_value) VALUES ('kdf_salt', ?)")) {
                ins.setString(1, Base64.getEncoder().encodeToString(salt));
                ins.executeUpdate();
            }
            return salt;
        }
    }

    // ---------- VaultService methods ----------

    @Override
    public List<VaultEntry> getAllEntries() {
        List<VaultEntry> entries = new ArrayList<>();
        String sql = "SELECT id, site_name, username, encrypted_password, category "
                + "FROM vault_entries ORDER BY site_name COLLATE NOCASE";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) { // loop through saved entries
                entries.add(new VaultEntry(
                        rs.getString("id"),
                        rs.getString("site_name"),
                        rs.getString("username"),
                        rs.getString("encrypted_password"),
                        rs.getString("category")));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load vault entries", e);
        }
        return entries;
    }

    @Override
    public List<VaultEntry> searchEntries(String query) {
        if (query == null || query.isBlank()) {
            return getAllEntries();
        }
        String lower = query.trim().toLowerCase();
        List<VaultEntry> results = new ArrayList<>();
        for (VaultEntry entry : getAllEntries()) { // loop + filter
            if (entry.getSiteName().toLowerCase().contains(lower)
                    || entry.getUsername().toLowerCase().contains(lower)
                    || entry.getCategory().toLowerCase().contains(lower)) {
                results.add(entry);
            }
        }
        return results;
    }

    @Override
    public boolean addEntry(String siteName, String username, String plainPassword, String category) {
        if (siteName == null || siteName.isBlank()
                || plainPassword == null || plainPassword.isEmpty()) {
            return false;
        }
        // if-else: prevent duplicate site names
        if (siteExists(siteName.trim(), null)) {
            return false;
        }
        try {
            String encrypted = AESUtil.encrypt(plainPassword, requireKey()); // AES on save
            VaultEntry entry = new VaultEntry(
                    siteName.trim(), username == null ? "" : username.trim(), encrypted, category);

            String sql = "INSERT INTO vault_entries "
                    + "(id, site_name, username, encrypted_password, category) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, entry.getId());
                ps.setString(2, entry.getSiteName());
                ps.setString(3, entry.getUsername());
                ps.setString(4, entry.getEncryptedPassword());
                ps.setString(5, entry.getCategory());
                ps.executeUpdate();
            }
            return true;
        } catch (GeneralSecurityException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String decryptPassword(VaultEntry entry) {
        try {
            return AESUtil.decrypt(entry.getEncryptedPassword(), requireKey()); // AES on view/copy
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "Could not decrypt password (wrong master password or corrupted data)", e);
        }
    }

    @Override
    public boolean updateEntry(VaultEntry entry, String newPlainPassword) {
        if (entry.getSiteName() == null || entry.getSiteName().isBlank()) {
            return false;
        }
        if (siteExists(entry.getSiteName().trim(), entry.getId())) {
            return false; // another entry already uses this site name
        }
        try {
            String encrypted = entry.getEncryptedPassword(); // keep old password by default
            if (newPlainPassword != null && !newPlainPassword.isEmpty()) {
                encrypted = AESUtil.encrypt(newPlainPassword, requireKey());
            }
            String sql = "UPDATE vault_entries SET site_name = ?, username = ?, "
                    + "encrypted_password = ?, category = ? WHERE id = ?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, entry.getSiteName().trim());
                ps.setString(2, entry.getUsername());
                ps.setString(3, encrypted);
                ps.setString(4, entry.getCategory());
                ps.setString(5, entry.getId());
                boolean updated = ps.executeUpdate() > 0;
                if (updated) {
                    entry.setEncryptedPassword(encrypted);
                }
                return updated;
            }
        } catch (GeneralSecurityException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteEntry(VaultEntry entry) {
        String sql = "DELETE FROM vault_entries WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entry.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------- Helper ----------

    /** True if another entry already uses this site name (case-insensitive). */
    private boolean siteExists(String siteName, String excludeId) {
        String sql = "SELECT COUNT(*) FROM vault_entries WHERE site_name = ? AND id <> ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, siteName);
            ps.setString(2, excludeId == null ? "" : excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to check for duplicate site", e);
        }
    }
}