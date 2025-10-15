/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.codeup.novabook.Views;

/**
 *
 * @author Coder
 */
import com.codeup.novabook.Controllers.MainController;
import com.codeup.novabook.Controllers.LoginController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MainView {
    
    private Scene scene;
    private Stage primaryStage;
    private MainController controller;
    private LoginController loginController;
    private String userRole;
    
    // Componentes de la interfaz
    private Label lblWelcome;
    private Label lblUser;
    private Button btnBooks, btnLoans, btnUsers, btnReports, btnLogout;
    
    public MainView(Stage primaryStage, String userRole) {
        this.primaryStage = primaryStage;
        this.userRole = userRole;
        this.controller = new MainController(primaryStage, userRole);
        this.loginController = new LoginController(primaryStage);
        
        // Redirect non-staff roles to their appropriate dashboards
        if ("Socio".equals(userRole)) {
            PartnerDashboardView partnerDashboard = new PartnerDashboardView(primaryStage, userRole);
            primaryStage.setScene(partnerDashboard.getScene());
            primaryStage.setTitle("NovaBook - Partner Dashboard");
            return;
        } else if ("Usuario".equals(userRole)) {
            UserDashboardView userDashboard = new UserDashboardView(primaryStage, userRole);
            primaryStage.setScene(userDashboard.getScene());
            primaryStage.setTitle("NovaBook - Library Catalog");
            return;
        }
        
        // For Administrator and Librarian roles, show the main menu
        initializeComponents();
        createLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        // Simple title with solid color
        lblWelcome = new Label("NovaBook");
        lblWelcome.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        lblWelcome.setStyle("-fx-text-fill: #2196F3;");
        
        // User info with role - more detailed
        String userName = controller.getCurrentUser();
        String currentRole = controller.getCurrentUserRole();
        lblUser = new Label("Logged in as: " + userName + " (" + currentRole + ")");
        
        // Solid color buttons - no icons, no shadows
        btnBooks = createSolidButton("Books", "#4CAF50");
        btnLoans = createSolidButton("Loans", "#FF9800");
        btnUsers = createSolidButton("Users", "#9C27B0");
        btnReports = createSolidButton("Import/Export", "#F44336");
        
        // Logout button
        btnLogout = new Button("Logout");
        btnLogout.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-font-size: 12px;");
        btnLogout.setPrefWidth(80);
        
        // Show current role in title and apply restrictions for user management
        System.out.println("Current user role: " + userRole); // Debug line
        
        // Visual indication for non-administrators that Users button has restrictions
        if (!loginController.canAccessUserManagement(userRole)) {
            // Keep button enabled but add tooltip to explain restriction
            Tooltip tooltip = new Tooltip("⚠️ User Management requires Administrator privileges");
            Tooltip.install(btnUsers, tooltip);
        }
    }
    
    private Button createSolidButton(String text, String color) {
        Button button = new Button(text);
        button.setPrefSize(200, 55);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;");
        
        // Simple hover effect with darker solid color
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
            default: return color;
        }
    }
    
    private void createLayout() {
        // Solid background color
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #FFFFFF;");
        
        // Simple title - no shadow
        lblWelcome.setAlignment(Pos.CENTER);
        lblWelcome.setStyle("-fx-text-fill: #2196F3;");
        
        // Subtitle with role indicator
        Label subtitle = new Label("Library Management System");
        subtitle.setFont(Font.font("Arial", 16));
        subtitle.setStyle("-fx-text-fill: #666;");
        
        // Role indicator
        Label roleIndicator = new Label("Current Role: " + userRole);
        roleIndicator.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        String roleColor = "Administrator".equals(userRole) ? "#4CAF50" : "#FF9800";
        roleIndicator.setStyle("-fx-text-fill: " + roleColor + "; -fx-background-color: #f5f5f5; -fx-padding: 5 10; -fx-background-radius: 15;");
        
        // Title section with logout
        HBox titleHeader = new HBox();
        titleHeader.setAlignment(Pos.CENTER);
        
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        
        titleHeader.getChildren().addAll(spacer1, btnLogout);
        
        VBox titleSection = new VBox(5);
        titleSection.setAlignment(Pos.CENTER);
        titleSection.getChildren().addAll(titleHeader, lblWelcome, subtitle, roleIndicator);
        
        // Button panel with spacing
        VBox buttonPanel = new VBox(12);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.getChildren().addAll(btnBooks, btnUsers, btnLoans, btnReports);
        
        // Simple footer
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.getChildren().add(lblUser);
        footer.setStyle("-fx-padding: 15;");
        lblUser.setStyle("-fx-text-fill: #666; -fx-font-size: 13px;");
        
        root.getChildren().addAll(titleSection, buttonPanel, footer);
        
        scene = new Scene(root, 600, 650);
    }
    
    
    private void setupEventHandlers() {
        btnBooks.setOnAction(e -> controller.navigateToBooksView());
        btnLoans.setOnAction(e -> controller.navigateToLoansView());
        btnUsers.setOnAction(e -> controller.navigateToUsersView());
        btnReports.setOnAction(e -> controller.navigateToReportsView());
        btnLogout.setOnAction(e -> logout());
    }
    
    private void logout() {
        LoginController.logout(); // Clear current user session
        LoginView loginView = new LoginView(primaryStage);
        primaryStage.setScene(loginView.getScene());
        primaryStage.setTitle("NovaBook - Login");
    }
    
    
    public Scene getScene() {
        return scene;
    }
}