package com.codeup.novabook.Models.Connection;

import com.codeup.novabook.Utils.ConfigManager;
import com.codeup.novabook.Utils.AppLogger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Database Connection Manager for NovaBook Library Management System
 * 
 * This class manages database connections using configuration from config.properties
 * and provides logging for connection events.
 * 
 * @author Luis Alfredo - Clan Cienaga
 */
public class ConnectionDB {
    
    private static final Logger LOGGER = AppLogger.getLogger(ConnectionDB.class);
    private static ConfigManager config = ConfigManager.getInstance();
    
    /**
     * Get database connection using configuration properties
     * @return Connection object or null if connection fails
     */
    public static Connection getConnection() {
        Connection conn = null;
        
        try {
            // Load database configuration from config.properties
            String url = config.getDatabaseUrl();
            String user = config.getDatabaseUsername();
            String password = config.getDatabasePassword();
            String driver = config.getDatabaseDriver();
            
            // Load database driver
            Class.forName(driver);
            
            // Establish connection
            conn = DriverManager.getConnection(url, user, password);
            
            // Log successful connection
            LOGGER.info("Database connection established successfully");
            AppLogger.logDatabaseOperation("CONNECTION", "DATABASE", true, "Connected to: " + url);
            
        } catch (SQLException e) {
            // Log database connection error
            String errorMsg = "Database connection failed: " + e.getMessage();
            LOGGER.severe(errorMsg);
            AppLogger.logDatabaseOperation("CONNECTION", "DATABASE", false, errorMsg);
            AppLogger.logError("ConnectionDB", "Failed to establish database connection", e);
            
        } catch (ClassNotFoundException e) {
            // Log driver loading error
            String errorMsg = "Database driver not found: " + e.getMessage();
            LOGGER.severe(errorMsg);
            AppLogger.logError("ConnectionDB", "Database driver not found", e);
        }
        
        return conn;
    }
    
    /**
     * Test database connection
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                LOGGER.info("Database connection test: SUCCESS");
                AppLogger.logSystemEvent("CONNECTION_TEST", "Database connection test successful");
                return true;
            }
        } catch (SQLException e) {
            LOGGER.warning("Database connection test failed: " + e.getMessage());
            AppLogger.logSystemEvent("CONNECTION_TEST", "Database connection test failed: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Close connection safely
     * @param conn Connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                LOGGER.fine("Database connection closed");
            } catch (SQLException e) {
                LOGGER.warning("Error closing database connection: " + e.getMessage());
                AppLogger.logError("ConnectionDB", "Error closing connection", e);
            }
        }
    }
    
    /**
     * Get database configuration info (for debugging)
     * @return String with connection info (without password)
     */
    public static String getConnectionInfo() {
        return String.format("Database URL: %s, User: %s, Driver: %s", 
            config.getDatabaseUrl(), 
            config.getDatabaseUsername(),
            config.getDatabaseDriver());
    }
}
