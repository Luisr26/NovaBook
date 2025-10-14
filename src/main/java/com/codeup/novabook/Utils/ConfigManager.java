package com.codeup.novabook.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuration Manager for NovaBook Library Management System
 * 
 * This class handles loading and accessing configuration properties
 * from the config.properties file.
 * 
 * @author Luis Alfredo - Clan Cienaga
 */
public class ConfigManager {
    
    private static final Logger LOGGER = Logger.getLogger(ConfigManager.class.getName());
    private static ConfigManager instance;
    private Properties properties;
    private static final String CONFIG_FILE = "config.properties";
    
    /**
     * Private constructor for Singleton pattern
     */
    private ConfigManager() {
        loadProperties();
    }
    
    /**
     * Get singleton instance of ConfigManager
     * @return ConfigManager instance
     */
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    /**
     * Load properties from config.properties file
     */
    private void loadProperties() {
        properties = new Properties();
        
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (inputStream == null) {
                throw new RuntimeException("Configuration file '" + CONFIG_FILE + "' not found in classpath");
            }
            
            properties.load(inputStream);
            LOGGER.info("Configuration loaded successfully from " + CONFIG_FILE);
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading configuration file: " + CONFIG_FILE, e);
            throw new RuntimeException("Failed to load configuration", e);
        }
    }
    
    // =======================================================================
    // DATABASE CONFIGURATION METHODS
    // =======================================================================
    
    public String getDatabaseUrl() {
        return getProperty("database.url", "jdbc:mysql://localhost:3306/Biblioteca");
    }
    
    public String getDatabaseUsername() {
        return getProperty("database.username", "root");
    }
    
    public String getDatabasePassword() {
        return getProperty("database.password", "password");
    }
    
    public String getDatabaseDriver() {
        return getProperty("database.driver", "com.mysql.cj.jdbc.Driver");
    }
    
    public int getMaxConnections() {
        return getIntProperty("database.pool.maxConnections", 10);
    }
    
    public int getMinConnections() {
        return getIntProperty("database.pool.minConnections", 2);
    }
    
    public int getConnectionTimeout() {
        return getIntProperty("database.pool.connectionTimeout", 30000);
    }
    
    // =======================================================================
    // BUSINESS LOGIC CONFIGURATION METHODS
    // =======================================================================
    
    public int getLoanPeriodDays() {
        return getIntProperty("loan.period.days", 14);
    }
    
    public double getFinePerDay() {
        return getDoubleProperty("loan.fine.per.day", 1.0);
    }
    
    public int getMaxBooksPerUser() {
        return getIntProperty("loan.max.books.per.user", 3);
    }
    
    // =======================================================================
    // VALIDATION CONFIGURATION METHODS
    // =======================================================================
    
    public boolean isIsbnValidationEnabled() {
        return getBooleanProperty("isbn.validation.enabled", true);
    }
    
    public boolean isEmailValidationEnabled() {
        return getBooleanProperty("email.validation.enabled", true);
    }
    
    public boolean isPhoneValidationEnabled() {
        return getBooleanProperty("phone.validation.enabled", false);
    }
    
    // =======================================================================
    // LOGGING CONFIGURATION METHODS
    // =======================================================================
    
    public boolean isLoggingEnabled() {
        return getBooleanProperty("logging.enabled", true);
    }
    
    public String getLoggingLevel() {
        return getProperty("logging.level", "INFO");
    }
    
    public String getLogFilePath() {
        return getProperty("logging.file.path", "logs/app.log");
    }
    
    public String getLogMaxFileSize() {
        return getProperty("logging.max.file.size", "10MB");
    }
    
    public int getLogMaxFiles() {
        return getIntProperty("logging.max.files", 5);
    }
    
    // =======================================================================
    // APPLICATION CONFIGURATION METHODS
    // =======================================================================
    
    public String getAppName() {
        return getProperty("app.name", "NovaBook Library Management System");
    }
    
    public String getAppVersion() {
        return getProperty("app.version", "1.0.0");
    }
    
    public String getAppDeveloper() {
        return getProperty("app.developer", "Luis Alfredo - Clan Cienaga");
    }
    
    public boolean isDevelopmentMode() {
        return "development".equalsIgnoreCase(getProperty("app.mode", "development"));
    }
    
    public boolean isDebugEnabled() {
        return getBooleanProperty("debug.enabled", true);
    }
    
    // =======================================================================
    // REPORTS CONFIGURATION METHODS
    // =======================================================================
    
    public String getReportsExportPath() {
        return getProperty("reports.export.path", "exports/");
    }
    
    public String getReportsDateFormat() {
        return getProperty("reports.date.format", "yyyy-MM-dd");
    }
    
    public boolean isReportsIncludeHeaders() {
        return getBooleanProperty("reports.include.headers", true);
    }
    
    // =======================================================================
    // UI CONFIGURATION METHODS
    // =======================================================================
    
    public String getUiDateFormat() {
        return getProperty("ui.date.format", "dd/MM/yyyy");
    }
    
    public String getCurrencySymbol() {
        return getProperty("ui.currency.symbol", "$");
    }
    
    public String getLanguage() {
        return getProperty("ui.language", "es");
    }
    
    // =======================================================================
    // SECURITY CONFIGURATION METHODS
    // =======================================================================
    
    public int getPasswordMinLength() {
        return getIntProperty("security.password.min.length", 6);
    }
    
    public int getSessionTimeout() {
        return getIntProperty("security.session.timeout", 30);
    }
    
    public int getMaxLoginAttempts() {
        return getIntProperty("security.max.login.attempts", 3);
    }
    
    // =======================================================================
    // GENERIC PROPERTY ACCESS METHODS
    // =======================================================================
    
    /**
     * Get string property with default value
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value or default
     */
    public String getProperty(String key, String defaultValue) {
        String value = properties.getProperty(key, defaultValue);
        LOGGER.fine("Configuration: " + key + " = " + value);
        return value;
    }
    
    /**
     * Get string property
     * @param key Property key
     * @return Property value
     */
    public String getProperty(String key) {
        return getProperty(key, null);
    }
    
    /**
     * Get integer property with default value
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value as integer or default
     */
    public int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid integer value for property " + key + ": " + value + ". Using default: " + defaultValue);
            return defaultValue;
        }
    }
    
    /**
     * Get double property with default value
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value as double or default
     */
    public double getDoubleProperty(String key, double defaultValue) {
        String value = getProperty(key, String.valueOf(defaultValue));
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid double value for property " + key + ": " + value + ". Using default: " + defaultValue);
            return defaultValue;
        }
    }
    
    /**
     * Get boolean property with default value
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value as boolean or default
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key, String.valueOf(defaultValue));
        if (value != null) {
            return "true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "1".equals(value);
        }
        return defaultValue;
    }
    
    /**
     * Reload properties from file
     * Useful for runtime configuration changes
     */
    public void reloadProperties() {
        loadProperties();
    }
    
    /**
     * Get all properties as Properties object
     * @return Properties object
     */
    public Properties getAllProperties() {
        return (Properties) properties.clone();
    }
    
    /**
     * Check if a property exists
     * @param key Property key
     * @return true if property exists
     */
    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }
}