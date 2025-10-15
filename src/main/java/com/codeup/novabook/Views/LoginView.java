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

public class LoginView {
    
    private Scene scene;
    private Stage primaryStage;
    private LoginController controller;
    
    // Components
    private TextField txtEmail;
    private PasswordField txtPassword;
    private Button btnLogin;
    private Label lblTitle, lblEmail, lblPassword, lblError;
    
    public LoginView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.controller = new LoginController(primaryStage);
        
        initializeComponents();
        createLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        // Title
        lblTitle = new Label("NovaBook");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        lblTitle.setStyle("-fx-text-fill: #2196F3;");
        
        // Labels
        lblEmail = new Label("Email:");
        lblEmail.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
        
        lblPassword = new Label("Password:");
        lblPassword.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
        
        lblError = new Label("");
        lblError.setStyle("-fx-text-fill: #f44336;");
        lblError.setWrapText(true);
        
        // Input fields
        txtEmail = new TextField();
        txtEmail.setPromptText("Enter your email");
        txtEmail.setPrefWidth(250);
        txtEmail.setStyle("-fx-font-size: 14px; -fx-padding: 8;");
        
        txtPassword = new PasswordField();
        txtPassword.setPromptText("Enter your password");
        txtPassword.setPrefWidth(250);
        txtPassword.setStyle("-fx-font-size: 14px; -fx-padding: 8;");
        
        // Login button
        btnLogin = new Button("Login");
        btnLogin.setPrefSize(100, 40);
        btnLogin.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        
        // Hover effect
        btnLogin.setOnMouseEntered(e -> 
            btnLogin.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;"));
        btnLogin.setOnMouseExited(e -> 
            btnLogin.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;"));
    }
    
    private void createLayout() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #FFFFFF;");
        
        // Title section
        VBox titleSection = new VBox(10);
        titleSection.setAlignment(Pos.CENTER);
        
        Label subtitle = new Label("Library Management System");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setStyle("-fx-text-fill: #666;");
        
        titleSection.getChildren().addAll(lblTitle, subtitle);
        
        // Login form
        VBox formSection = new VBox(15);
        formSection.setAlignment(Pos.CENTER);
        formSection.setPadding(new Insets(30));
        formSection.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-radius: 5;");
        
        formSection.getChildren().addAll(
            lblEmail, txtEmail,
            lblPassword, txtPassword,
            btnLogin,
            lblError
        );
        
        // Demo users info
        VBox infoSection = new VBox(8);
        infoSection.setAlignment(Pos.CENTER);
        infoSection.setStyle("-fx-padding: 20; -fx-background-color: #e3f2fd; -fx-border-radius: 5;");
        
        Label infoTitle = new Label("Demo Users (User: Password):");
        infoTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #1976d2; -fx-font-size: 14px;");
        
        // Administrator account
        Label adminTitle = new Label("👑 Administrator (Full Access):");
        adminTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #4CAF50; -fx-font-size: 12px;");
        
        Label adminInfo = new Label("• admin : admin");
        adminInfo.setStyle("-fx-text-fill: #333; -fx-font-size: 11px;");
        
        // Librarian account
        Label libTitle = new Label("📚 Librarian (Limited Access):");
        libTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #FF9800; -fx-font-size: 12px;");
        
        Label librarianInfo = new Label("• librarian : librarian");
        librarianInfo.setStyle("-fx-text-fill: #333; -fx-font-size: 11px;");
        
        // Partner account
        Label partnerTitle = new Label("🤝 Partner/Socio (Can Request Loans):");
        partnerTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #FF5722; -fx-font-size: 12px;");
        
        Label partnerInfo = new Label("• socio : socio123");
        partnerInfo.setStyle("-fx-text-fill: #333; -fx-font-size: 11px;");
        
        // User account
        Label userTitle = new Label("📖 User (Catalog View Only):");
        userTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #3F51B5; -fx-font-size: 12px;");
        
        Label userInfo = new Label("• usuario : usuario123");
        userInfo.setStyle("-fx-text-fill: #333; -fx-font-size: 11px;");
        
        infoSection.getChildren().addAll(infoTitle, adminTitle, adminInfo, libTitle, librarianInfo, partnerTitle, partnerInfo, userTitle, userInfo);
        
        root.getChildren().addAll(titleSection, formSection, infoSection);
        
        scene = new Scene(root, 450, 650);
    }
    
    private void setupEventHandlers() {
        btnLogin.setOnAction(e -> login());
        txtPassword.setOnAction(e -> login()); // Enter key in password field
        txtEmail.setOnAction(e -> txtPassword.requestFocus()); // Enter key in email field
    }
    
    private void login() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();
        
        lblError.setText("");
        
        if (email.isEmpty() || password.isEmpty()) {
            lblError.setText("Please fill in all fields");
            return;
        }
        
        String userRole = controller.login(email, password);
        if (userRole != null) {
            // Login successful, redirect based on user role
            switch (userRole) {
                case "Socio":
                    PartnerDashboardView partnerDashboard = new PartnerDashboardView(primaryStage, userRole);
                    primaryStage.setScene(partnerDashboard.getScene());
                    primaryStage.setTitle("NovaBook - Partner Dashboard");
                    break;
                    
                case "Usuario":
                    UserDashboardView userDashboard = new UserDashboardView(primaryStage, userRole);
                    primaryStage.setScene(userDashboard.getScene());
                    primaryStage.setTitle("NovaBook - Library Catalog");
                    break;
                    
                case "Administrator":
                    AdminDashboardView adminDashboard = new AdminDashboardView(primaryStage, userRole);
                    primaryStage.setScene(adminDashboard.getScene());
                    primaryStage.setTitle("NovaBook - Administrator Dashboard");
                    break;
                    
                case "Librarian":
                    LibrarianDashboardView librarianDashboard = new LibrarianDashboardView(primaryStage, userRole);
                    primaryStage.setScene(librarianDashboard.getScene());
                    primaryStage.setTitle("NovaBook - Librarian Dashboard");
                    break;
                    
                default:
                    // Fallback to main view for any other roles
                    MainView mainView = new MainView(primaryStage, userRole);
                    primaryStage.setScene(mainView.getScene());
                    primaryStage.setTitle("NovaBook - Library Management System");
                    break;
            }
        } else {
            lblError.setText("Invalid email or password");
            txtPassword.clear();
        }
    }
    
    public Scene getScene() {
        return scene;
    }
}