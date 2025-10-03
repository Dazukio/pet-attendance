package pet.attendance.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class DatabaseConnection {
    private static final Properties envProperties = loadEnvFile();

    public static Connection getConnection() throws SQLException {
        String url = getEnv("DB_URL", "jdbc:postgresql://localhost:5432/attendance");
        String user = getEnv("DB_USER", "postgres");
        String password = getEnv("DB_PASSWORD", "password");

        System.out.println("Connecting to: " + url);
        System.out.println("User: " + user);

        return DriverManager.getConnection(url, user, password);
    }

    private static String getEnv(String key, String defaultValue) {
        // Check sys vars
        String value = System.getenv(key);
        if (value != null && !value.trim().isEmpty()) {
            return value;
        }

        // Check .env
        value = envProperties.getProperty(key);
        if (value != null && !value.trim().isEmpty()) {
            return value;
        }

        return defaultValue;
    }

    private static Properties loadEnvFile() {
        Properties props = new Properties();
        try {
            Path envPath = Paths.get(".env");
            if (Files.exists(envPath)) {
                props.load(Files.newBufferedReader(envPath));
                System.out.println(".env file loaded successfully");
            } else {
                System.out.println(".env file not found using defaults");
            }
        } catch (IOException e) {
            System.out.println("Could not load .env file: " + e.getMessage());
        }
        return props;
    }
}