package com.codeup.novabook.App;

import com.codeup.novabook.Models.Connection.ConnectionDB;
import com.codeup.novabook.Utils.AppLogger;
import com.codeup.novabook.Utils.ConfigManager;
import com.codeup.novabook.Views.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * NovaBook Library Management System - Main Application
 * 
 * This is the main entry point for the NovaBook application.
 * It initializes logging, configuration, and database connection
 * before launching the JavaFX interface.
 * 
 * @author Luis Alfredo - Clan Cienaga
 */
public class NovaBook extends Application {
    
    private static final Logger LOGGER = AppLogger.getLogger(NovaBook.class);
    private static ConfigManager config;

    public static void main(String[] args) {
        try {
            // Initialize application logging
            AppLogger.initialize();
            LOGGER.info("NovaBook application starting...");
            
            // Load configuration
            config = ConfigManager.getInstance();
            LOGGER.info("Configuration loaded successfully");
            
            // Log application info
            AppLogger.logSystemEvent("APPLICATION_START", 
                "Starting " + config.getAppName() + " v" + config.getAppVersion());
            
            // Test database connection
            LOGGER.info("Testing database connection...");
            if (!ConnectionDB.testConnection()) {
                LOGGER.severe("Failed to establish database connection. Exiting application.");
                AppLogger.logSystemEvent("APPLICATION_EXIT", "Database connection failed");
                AppLogger.shutdown();
                System.exit(1);
            }
            
            LOGGER.info("Database connection verified successfully");
            LOGGER.info(ConnectionDB.getConnectionInfo());
            
            // Start JavaFX application
            LOGGER.info("Launching JavaFX interface...");
            launch(args);
            
        } catch (Exception e) {
            System.err.println("Critical error during application startup: " + e.getMessage());
            e.printStackTrace();
            
            // Try to log error if logger is available
            try {
                AppLogger.logError("NovaBook", "Critical startup error", e);
                AppLogger.shutdown();
            } catch (Exception logError) {
                System.err.println("Additional error during error logging: " + logError.getMessage());
            }
            
            System.exit(1);
        }
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Configure main window
            primaryStage.setTitle(config.getAppName() + " - Login");
            primaryStage.setResizable(false);
            
            LOGGER.info("Initializing login interface...");
            AppLogger.logSystemEvent("UI_START", "JavaFX interface initialized");
            
            // Create and show login view
            LoginView loginView = new LoginView(primaryStage);
            primaryStage.setScene(loginView.getScene());
            primaryStage.show();
            
            LOGGER.info("Login interface displayed successfully");
            
            // Handle application closing
            primaryStage.setOnCloseRequest(event -> {
                LOGGER.info("Application shutdown requested by user");
                AppLogger.logSystemEvent("APPLICATION_SHUTDOWN", "User requested application closure");
                
                try {
                    // Clean up database connections
                    com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.checkedShutdown();
                    LOGGER.info("Database connections cleaned up");
                } catch (Exception e) {
                    LOGGER.warning("Error during database cleanup: " + e.getMessage());
                    AppLogger.logError("NovaBook", "Database cleanup error", e);
                }
                
                // Shutdown logging system
                AppLogger.shutdown();
                
                System.out.println("NovaBook application closed successfully");
            });
            
        } catch (Exception e) {
            LOGGER.severe("Error initializing JavaFX interface: " + e.getMessage());
            AppLogger.logError("NovaBook", "JavaFX initialization error", e);
            throw e;
        }
    }
}
