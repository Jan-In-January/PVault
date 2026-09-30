package com.pvault.model;

/**
 * Represents a saved account/credential in the Password Vault.
 * Teammates implementing Module 2 (Vault Store/Retrieve & AES) can use or expand this model.
 */
public class VaultEntry {
    private String id;
    private String siteName;
    private String username;
    private String encryptedPassword; // Stores AES-encrypted password ciphertext or plain if decrypted
    private String category;

    public VaultEntry(String siteName, String username, String encryptedPassword, String category) {
        this.id = java.util.UUID.randomUUID().toString();
        this.siteName = siteName;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.category = (category == null || category.isBlank()) ? "General" : category;
    }

    // Used when loading an existing entry from the database (keeps its saved id)
    public VaultEntry(String id, String siteName, String username, String encryptedPassword, String category) {
        this.id = id;
        this.siteName = siteName;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.category = (category == null || category.isBlank()) ? "General" : category;
    }

    public String getId() {
        return id;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
