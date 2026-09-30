package com.pvault.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseManager {

    // Creates/opens vault.db in the working directory (project root when using mvnw)
    private static final String DB_URL = "jdbc:sqlite:vault.db";

    private DatabaseManager() { }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /** Call once at startup. Safe to call repeatedly. */
    public static void initialize() {
        String sql = """
            CREATE TABLE IF NOT EXISTS vault_entries (
                id                 TEXT PRIMARY KEY,
                site_name          TEXT NOT NULL UNIQUE COLLATE NOCASE,
                username           TEXT NOT NULL,
                encrypted_password TEXT NOT NULL,
                category           TEXT NOT NULL DEFAULT 'General'
            )
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to initialize vault.db", e);
        }
    }
}