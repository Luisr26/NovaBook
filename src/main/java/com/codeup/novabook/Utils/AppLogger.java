package com.codeup.novabook.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.*;

/**
 * Application Logger for NovaBook Library Management System
 * 
 * This class configures and manages application-wide logging
 * with file output to app.log
 * 
 * @author Luis Alfredo - Clan Cienaga
 */
public class AppLogger {
    
    private static final Logger ROOT_LOGGER = Logger.getLogger("");
    private static boolean initialized = false;
    
    /**
     * Initialize the application logger
     * This should be called once at application startup
     */
    public static synchronized void initialize() {
        if (initialized) {
            return;
        }
        
        try {
            ConfigManager config = ConfigManager.getInstance();
            
            // Create logs directory if it doesn't exist
            String logPath = config.getLogFilePath();
            java.nio.file.Path logDir = Paths.get(logPath).getParent();
            if (logDir != null) {
                Files.createDirectories(logDir);
            }
            
            // Clear existing handlers
            Handler[] handlers = ROOT_LOGGER.getHandlers();
            for (Handler handler : handlers) {
                ROOT_LOGGER.removeHandler(handler);
            }
            
            // Set logging level from configuration
            Level logLevel = parseLevel(config.getLoggingLevel());
            ROOT_LOGGER.setLevel(logLevel);
            
            // Create file handler
            if (config.isLoggingEnabled()) {
                FileHandler fileHandler = createFileHandler(config);
                fileHandler.setFormatter(new CustomFormatter());
                fileHandler.setLevel(logLevel);
                ROOT_LOGGER.addHandler(fileHandler);
            }
            
            // Create console handler for development mode
            if (config.isDevelopmentMode()) {
                ConsoleHandler consoleHandler = new ConsoleHandler();
                consoleHandler.setFormatter(new SimpleFormatter());
                consoleHandler.setLevel(Level.INFO);
                ROOT_LOGGER.addHandler(consoleHandler);
            }
            
            initialized = true;
            
            // Log initialization success
            Logger logger = Logger.getLogger(AppLogger.class.getName());
            logger.info("=== NovaBook Application Started ===");
            logger.info("Application: " + config.getAppName());
            logger.info("Version: " + config.getAppVersion());
            logger.info("Developer: " + config.getAppDeveloper());
            logger.info("Mode: " + (config.isDevelopmentMode() ? "Development" : "Production"));
            logger.info("Logging Level: " + logLevel);
            logger.info("Log File: " + config.getLogFilePath());
            
        } catch (Exception e) {
            System.err.println("Failed to initialize application logger: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Create file handler with rotation
     */
    private static FileHandler createFileHandler(ConfigManager config) throws IOException {
        String logPath = config.getLogFilePath();
        int maxFiles = config.getLogMaxFiles();
        int maxSizeBytes = parseFileSize(config.getLogMaxFileSize());
        
        // Create rotating file handler
        FileHandler fileHandler = new FileHandler(
            logPath,      // File path
            maxSizeBytes, // Max file size in bytes
            maxFiles,     // Number of files to rotate
            true          // Append mode
        );
        
        return fileHandler;
    }
    
    /**
     * Parse log level string to Level enum
     */
    private static Level parseLevel(String levelString) {
        try {
            return Level.parse(levelString.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid log level: " + levelString + ". Using INFO.");
            return Level.INFO;
        }
    }
    
    /**
     * Parse file size string (e.g., "10MB") to bytes
     */
    private static int parseFileSize(String sizeString) {
        try {
            sizeString = sizeString.toUpperCase().trim();
            
            if (sizeString.endsWith("KB")) {
                return Integer.parseInt(sizeString.substring(0, sizeString.length() - 2)) * 1024;
            } else if (sizeString.endsWith("MB")) {
                return Integer.parseInt(sizeString.substring(0, sizeString.length() - 2)) * 1024 * 1024;
            } else if (sizeString.endsWith("GB")) {
                return Integer.parseInt(sizeString.substring(0, sizeString.length() - 2)) * 1024 * 1024 * 1024;
            } else {
                // Assume bytes
                return Integer.parseInt(sizeString);
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid file size format: " + sizeString + ". Using 10MB default.");
            return 10 * 1024 * 1024; // 10MB default
        }
    }
    
    /**
     * Get logger for specific class
     * @param clazz Class to get logger for
     * @return Logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(clazz.getName());
    }
    
    /**
     * Get logger by name
     * @param name Logger name
     * @return Logger instance
     */
    public static Logger getLogger(String name) {
        return Logger.getLogger(name);
    }
    
    /**
     * Shutdown logging system
     */
    public static void shutdown() {
        Handler[] handlers = ROOT_LOGGER.getHandlers();
        for (Handler handler : handlers) {
            handler.close();
            ROOT_LOGGER.removeHandler(handler);
        }
        
        Logger logger = Logger.getLogger(AppLogger.class.getName());
        logger.info("=== NovaBook Application Shutdown ===");
        
        initialized = false;
    }
    
    /**
     * Log application startup event
     */
    public static void logStartup() {
        Logger logger = getLogger(AppLogger.class);
        logger.info("Application starting...");
    }
    
    /**
     * Log user login event
     */
    public static void logUserLogin(String username, boolean success) {
        Logger logger = getLogger("SECURITY");
        if (success) {
            logger.info("User login successful: " + username);
        } else {
            logger.warning("User login failed: " + username);
        }
    }
    
    /**
     * Log database operation
     */
    public static void logDatabaseOperation(String operation, String table, boolean success, String details) {
        Logger logger = getLogger("DATABASE");
        String message = String.format("DB %s on %s: %s", operation, table, success ? "SUCCESS" : "FAILED");
        if (details != null && !details.isEmpty()) {
            message += " - " + details;
        }
        
        if (success) {
            logger.info(message);
        } else {
            logger.warning(message);
        }
    }
    
    /**
     * Log business operation (loans, returns, etc.)
     */
    public static void logBusinessOperation(String operation, String entity, String details) {
        Logger logger = getLogger("BUSINESS");
        String message = String.format("Business Operation - %s: %s", operation, entity);
        if (details != null && !details.isEmpty()) {
            message += " - " + details;
        }
        logger.info(message);
    }
    
    /**
     * Log error with exception
     */
    public static void logError(String component, String message, Throwable throwable) {
        Logger logger = getLogger("ERROR");
        logger.log(Level.SEVERE, component + " - " + message, throwable);
    }
    
    /**
     * Log system event
     */
    public static void logSystemEvent(String event, String details) {
        Logger logger = getLogger("SYSTEM");
        String message = "System Event - " + event;
        if (details != null && !details.isEmpty()) {
            message += ": " + details;
        }
        logger.info(message);
    }
    
    /**
     * Custom formatter for log files
     */
    private static class CustomFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return String.format("[%1$tF %1$tT] [%2$s] [%3$s] %4$s %n",
                new java.util.Date(record.getMillis()),
                record.getLevel(),
                record.getLoggerName(),
                record.getMessage()
            );
        }
    }
}