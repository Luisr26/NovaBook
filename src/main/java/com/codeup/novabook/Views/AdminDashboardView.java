/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.codeup.novabook.Views;

/**
 *
 * @author Coder
 */
import com.codeup.novabook.Controllers.LoginController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class AdminDashboardView {
    
    private Scene scene;
    private Stage primaryStage;
    private String userRole;
    
    // Components
    private Label lblTitle, lblWelcome, lblStats;
    private Button btnManageBooks, btnManageUsers, btnManageLoans, btnManagePartners, btnReports, btnSystemSettings, btnBack;
    
    public AdminDashboardView(Stage primaryStage, String userRole) {
        this.primaryStage = primaryStage;
        this.userRole = userRole;
        
        initializeComponents();
        createLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        // Title
        lblTitle = new Label("Administrator Dashboard");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblTitle.setStyle("-fx-text-fill: #4CAF50;");
        
        // Welcome message
        String userName = LoginController.getCurrentUserName();
        lblWelcome = new Label("Welcome, " + userName);
        lblWelcome.setFont(Font.font("Arial", 16));
        lblWelcome.setStyle("-fx-text-fill: #666;");
        
        // Stats info
        lblStats = new Label("Full system administration access");
        lblStats.setStyle("-fx-text-fill: #888; -fx-font-style: italic;");
        
        // Management buttons
        btnManageBooks = createSolidButton("📚 Manage Books", "#4CAF50");
        btnManageUsers = createSolidButton("👥 User Management", "#9C27B0");
        btnManageLoans = createSolidButton("📖 Loan Management", "#2196F3");
        btnManagePartners = createSolidButton("🤝 Partner Management", "#FF9800");
        btnReports = createSolidButton("📊 Reports & Analytics", "#F44336");
        btnSystemSettings = createSolidButton("⚙️ System Settings", "#607D8B");
        
        // Back button
        btnBack = new Button("← Back to Main");
        btnBack.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-font-size: 12px;");
        btnBack.setPrefWidth(120);
    }
    
    private Button createSolidButton(String text, String color) {
        Button button = new Button(text);
        button.setPrefSize(280, 45);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;");
        
        // Simple hover effect
        String hoverColor = getDarkerColor(color);
        button.setOnMouseEntered(e -> 
            button.setStyle("-fx-background-color: " + hoverColor + "; -fx-text-fill: white;"));
        button.setOnMouseExited(e -> 
            button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;"));
        
        return button;
    }
    
    private String getDarkerColor(String color) {
        switch (color) {
            case "#4CAF50": return "#45a049";
            case "#2196F3": return "#1976d2";
            case "#FF9800": return "#e68900";
            case "#9C27B0": return "#7b1fa2";
            case "#F44336": return "#d32f2f";
            case "#607D8B": return "#546e7a";
            default: return color;
        }
    }
    
    private void createLayout() {
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #FFFFFF;");
        
        // Header section
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        headerBox.getChildren().addAll(spacer, btnBack);
        
        VBox titleSection = new VBox(10);
        titleSection.setAlignment(Pos.CENTER);
        titleSection.getChildren().addAll(headerBox, lblTitle, lblWelcome, lblStats);
        
        // Management actions section
        VBox actionsSection = new VBox(12);
        actionsSection.setAlignment(Pos.CENTER);
        actionsSection.setPadding(new Insets(25));
        actionsSection.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-radius: 10;");
        
        Label actionsTitle = new Label("🛠️ System Management");
        actionsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        actionsTitle.setStyle("-fx-text-fill: #333;");
        
        // Create two columns for better organization
        HBox row1 = new HBox(15);
        row1.setAlignment(Pos.CENTER);
        row1.getChildren().addAll(btnManageBooks, btnManageUsers);
        
        HBox row2 = new HBox(15);
        row2.setAlignment(Pos.CENTER);
        row2.getChildren().addAll(btnManageLoans, btnManagePartners);
        
        HBox row3 = new HBox(15);
        row3.setAlignment(Pos.CENTER);
        row3.getChildren().addAll(btnReports, btnSystemSettings);
        
        actionsSection.getChildren().addAll(
            actionsTitle,
            row1,
            row2,
            row3
        );
        
        // Admin privileges info
        VBox infoSection = new VBox(10);
        infoSection.setAlignment(Pos.CENTER);
        infoSection.setPadding(new Insets(15));
        infoSection.setStyle("-fx-background-color: #e8f5e8; -fx-border-color: #4caf50; -fx-border-radius: 8;");
        
        Label infoTitle = new Label("👑 Administrator Privileges");
        infoTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #2e7d32;");
        
        Label infoText = new Label("Full access to all system functions including user management,\nsystem settings, and advanced reports.");
        infoText.setWrapText(true);
        infoText.setStyle("-fx-text-fill: #388e3c; -fx-text-alignment: center;");
        
        infoSection.getChildren().addAll(infoTitle, infoText);
        
        root.getChildren().addAll(titleSection, actionsSection, infoSection);
        
        scene = new Scene(root, 650, 700);
    }
    
    private void setupEventHandlers() {
        btnBack.setOnAction(e -> goBack());
        btnManageBooks.setOnAction(e -> openBooksView());
        btnManageUsers.setOnAction(e -> openUsersView());
        btnManageLoans.setOnAction(e -> openLoansView());
        btnManagePartners.setOnAction(e -> openPartnersView());
        btnReports.setOnAction(e -> openReportsView());
        btnSystemSettings.setOnAction(e -> showSystemSettings());
    }
    
    private void openBooksView() {
        BooksView booksView = new BooksView(primaryStage, userRole);
        primaryStage.setScene(booksView.getScene());
        primaryStage.setTitle("NovaBook - Books Management");
    }
    
    private void openUsersView() {
        UsersView usersView = new UsersView(primaryStage, userRole);
        primaryStage.setScene(usersView.getScene());
        primaryStage.setTitle("NovaBook - User Management");
    }
    
    private void openLoansView() {
        LoansView loansView = new LoansView(primaryStage, userRole);
        primaryStage.setScene(loansView.getScene());
        primaryStage.setTitle("NovaBook - Loan Management");
    }
    
    private void openPartnersView() {
        PartnersView partnersView = new PartnersView(primaryStage, userRole);
        primaryStage.setScene(partnersView.getScene());
        primaryStage.setTitle("NovaBook - Partner Management");
    }
    
    private void openReportsView() {
        ReportsView reportsView = new ReportsView(primaryStage, userRole);
        primaryStage.setScene(reportsView.getScene());
        primaryStage.setTitle("NovaBook - Reports & Analytics");
    }
    
    private void showSystemSettings() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("System Settings");
        alert.setHeaderText("System Configuration");
        alert.setContentText("System settings panel would be implemented here.\nThis would include database configuration, backup settings, etc.");
        alert.showAndWait();
    }
    
    private void goBack() {
        MainView mainView = new MainView(primaryStage, userRole);
        primaryStage.setScene(mainView.getScene());
        primaryStage.setTitle("NovaBook - Library Management System");
    }
    
    public Scene getScene() {
        return scene;
    }
}