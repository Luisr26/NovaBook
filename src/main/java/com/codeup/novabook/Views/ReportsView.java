/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.codeup.novabook.Views;

/**
 *
 * @author Coder
 */
import com.codeup.novabook.Controllers.ReportsController;
import com.codeup.novabook.Views.MainView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Optional;

public class ReportsView {
    
    private Scene scene;
    private Stage primaryStage;
    private ReportsController controller;
    private String userRole;
    
    // Components
    private Button btnExportBooks, btnExportLoans, btnExportPartners, btnImportBooks, btnDownloadTemplate, btnBack;
    private Label lblInstructions;
    
    public ReportsView(Stage primaryStage, String userRole) {
        this.primaryStage = primaryStage;
        this.userRole = userRole;
        this.controller = new ReportsController(primaryStage);
        
        initializeComponents();
        createLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        // Create solid color buttons - no shadows or effects
        btnExportBooks = createSolidButton("Export Books", "#4CAF50");
        btnExportLoans = createSolidButton("Export Loans", "#FF9800");
        btnExportPartners = createSolidButton("Export Partners", "#2196F3");
        btnImportBooks = createSolidButton("Import Books", "#9C27B0");
        btnDownloadTemplate = createSolidButton("Download Template", "#607D8B");
        
        btnBack = new Button("Back");
        btnBack.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-font-size: 14px;");
        btnBack.setPrefWidth(100);
        
        // Labels
        lblInstructions = new Label("CSV format for import: Title,Author,ISBN,Year (download template first)");
        lblInstructions.setWrapText(true);
        lblInstructions.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
    }
    
    private Button createSolidButton(String text, String color) {
        Button button = new Button(text);
        button.setPrefSize(150, 40);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14px;");
        
        // Simple solid color hover effect
        String hoverColor = getDarkerColor(color);
        button.setOnMouseEntered(e -> 
            button.setStyle("-fx-background-color: " + hoverColor + "; -fx-text-fill: white; -fx-font-size: 14px;"));
        button.setOnMouseExited(e -> 
            button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14px;"));
        
        return button;
    }
    
    private String getDarkerColor(String color) {
        switch (color) {
            case "#4CAF50": return "#45a049";
            case "#FF9800": return "#e68900";
            case "#2196F3": return "#1976d2";
            case "#9C27B0": return "#7b1fa2";
            case "#607D8B": return "#546e7a";
            default: return color;
        }
    }
    
    private void createLayout() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        
        // Title and back button
        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("CSV Import/Export");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        titleBox.getChildren().addAll(title, spacer, btnBack);
        
        // Instructions
        lblInstructions.setPadding(new Insets(0, 0, 10, 0));
        
        // Export buttons
        HBox exportBox = new HBox(10);
        exportBox.setAlignment(Pos.CENTER);
        exportBox.getChildren().addAll(btnExportBooks, btnExportLoans, btnExportPartners);
        
        // Import buttons
        HBox importBox = new HBox(10);
        importBox.setAlignment(Pos.CENTER);
        importBox.getChildren().addAll(btnDownloadTemplate, btnImportBooks);
        
        // Simple labels
        Label exportLabel = new Label("Export Data:");
        exportLabel.setStyle("-fx-font-weight: bold;");
        
        Label importLabel = new Label("Import Books:");
        importLabel.setStyle("-fx-font-weight: bold;");
        
        root.getChildren().addAll(titleBox, lblInstructions, 
            exportLabel, exportBox, 
            importLabel, importBox);
        
        // Solid background color
        root.setStyle("-fx-background-color: #FFFFFF;");
        
        scene = new Scene(root, 700, 400);
    }
    
    private void setupEventHandlers() {
        btnBack.setOnAction(e -> goBack());
        btnExportBooks.setOnAction(e -> exportBooks());
        btnExportLoans.setOnAction(e -> exportLoans());
        btnExportPartners.setOnAction(e -> exportPartners());
        btnDownloadTemplate.setOnAction(e -> downloadTemplate());
        btnImportBooks.setOnAction(e -> importBooks());
    }
    
    private void exportBooks() {
        controller.exportBooks();
    }
    
    private void exportLoans() {
        controller.exportLoans();
    }
    
    private void exportPartners() {
        controller.exportPartners();
    }
    
    private void importBooks() {
        controller.importBooks();
    }
    
    private void downloadTemplate() {
        controller.downloadTemplate();
    }
    
    
    private void goBack() {
        // Redirect to appropriate dashboard based on user role
        if ("Administrator".equals(userRole)) {
            AdminDashboardView adminDashboard = new AdminDashboardView(primaryStage, userRole);
            primaryStage.setScene(adminDashboard.getScene());
            primaryStage.setTitle("NovaBook - Administrator Dashboard");
        } else if ("Librarian".equals(userRole)) {
            LibrarianDashboardView librarianDashboard = new LibrarianDashboardView(primaryStage, userRole);
            primaryStage.setScene(librarianDashboard.getScene());
            primaryStage.setTitle("NovaBook - Librarian Dashboard");
        } else {
            // Fallback to MainView
            MainView mainView = new MainView(primaryStage, userRole);
            primaryStage.setScene(mainView.getScene());
            primaryStage.setTitle("NovaBook - Library Management System");
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public Scene getScene() {
        return scene;
    }
}
