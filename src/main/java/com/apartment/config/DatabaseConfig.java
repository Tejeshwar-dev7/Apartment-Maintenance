package com.apartment.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConfig {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                PROPERTIES.load(input);
            }
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (IOException | ClassNotFoundException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private DatabaseConfig() {
    }

    public static Connection getConnection() throws SQLException {
        String url = System.getenv("DB_URL");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

        if (url == null || url.trim().isEmpty()) {
            url = PROPERTIES.getProperty("db.url");
        }
        if (username == null || username.trim().isEmpty()) {
            username = PROPERTIES.getProperty("db.username");
        }
        if (password == null || password.trim().isEmpty()) {
            password = PROPERTIES.getProperty("db.password");
        }

        if (url != null && url.startsWith("mysql://")) {
            url = "jdbc:" + url;
        }

        if (url == null || url.trim().isEmpty()
                || username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            throw new SQLException("Database connection is not configured. Set DB_URL, DB_USERNAME, and DB_PASSWORD, or provide db.properties.");
        }

        if (url.contains("DATABASE_HOST") || url.contains("DATABASE_NAME")) {
            throw new SQLException("Database URL still contains placeholder values. Replace DATABASE_HOST and DATABASE_NAME with your real online MySQL details.");
        }

        return DriverManager.getConnection(url, username, password);
    }
}
