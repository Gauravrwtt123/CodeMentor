package com.codementor.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Central JDBC connection factory. Reads environment variables first, then db.properties. */
public final class DBConnection {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                PROPERTIES.load(in);
            }
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Unable to load db.properties: " + e.getMessage());
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("MySQL JDBC driver is missing. Check mysql-connector-j in pom.xml.");
        }
    }

    private DBConnection() { }

    private static String config(String envName, String propertyName, String defaultValue) {
        String env = System.getenv(envName);
        if (env != null && !env.isBlank()) return env;
        String value = PROPERTIES.getProperty(propertyName);
        return (value == null || value.isBlank()) ? defaultValue : value.trim();
    }

    public static Connection getConnection() throws SQLException {
        String url = config("DB_URL", "db.url",
                "jdbc:mysql://localhost:3306/codementor?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata");
        String username = config("DB_USERNAME", "db.username", "root");
        String password = config("DB_PASSWORD", "db.password", "");
        return DriverManager.getConnection(url, username, password);
    }

    /** Simple connectivity check used by the database diagnostic endpoint. */
    public static boolean testConnection() throws SQLException {
        try (Connection connection = getConnection()) {
            return connection.isValid(3);
        }
    }
}
