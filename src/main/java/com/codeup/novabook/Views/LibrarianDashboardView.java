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

public class LibrarianDashboardView {
    
    private Scene scene;
    private Stage primaryStage;
    private String userRole;
    
    // Components
    private Label lblTitle, lblWelcome, lblStats;
    private Button btnQuickAddBook, btnQuickLoan, btnSearchBooks, btnViewLoans, btnManagePartners, btnBack;
    
    public LibrarianDashboardView(Stage primaryStage, String userRole) {
        this.primaryStage = primaryStage;
        this.userRole = userRole;
        
        initializeComponents();
        createLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        // Title
        lblTitle = new Label("Librarian Dashboard");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblTitle.setStyle("-fx-text-fill: #FF9800;");
        
        // Welcome message
        String userName = LoginController.getCurrentUserName();
        lblWelcome = new Label("Welcome, " + userName);
        lblWelcome.setFont(Font.font("Arial", 16));
        lblWelcome.setStyle("-fx-text-fill: #666;");
        
        // Stats info
        lblStats = new Label("Quick access to your daily tasks");
        lblStats.setStyle("-fx-text-fill: #888; -fx-font-style: italic;");
        
        // Quick action buttons
        btnQuickAddBook = createSolidButton("📚 Quick Add Book", "#4CAF50");
        btnQuickLoan = createSolidButton("📜 New Loan", "#2196F3");
        btnSearchBooks = createSolidButton("🔍 Search Books", "#9C27B0");
        btnViewLoans = createSolidButton("📋 View Active Loans", "#FF9800");
        btnManagePartners = createSolidButton("🤝 Manage Partners", "#2196F3");
        
        // Back button
        btnBack = new Button("← Back to Main");
        btnBack.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-font-size: 12px;");
        btnBack.setPrefWidth(120);
    }
    
    private Button createSolidButton(String text, String color) {
        Button button = new Button(text);
        button.setPrefSize(250, 50);
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
        
        // Quick actions section
        VBox actionsSection = new VBox(15);
        actionsSection.setAlignment(Pos.CENTER);
        actionsSection.setPadding(new Insets(20));
        actionsSection.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-radius: 10;");
        
        Label actionsTitle = new Label("📋 Quick Actions");
        actionsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        actionsTitle.setStyle("-fx-text-fill: #333;");
        
        actionsSection.getChildren().addAll(
            actionsTitle,
            btnQuickAddBook,
            btnQuickLoan,
            btnSearchBooks,
            btnViewLoans,
            btnManagePartners
        );
        
        // Info section
        VBox infoSection = new VBox(10);
        infoSection.setAlignment(Pos.CENTER);
        infoSection.setPadding(new Insets(15));
        infoSection.setStyle("-fx-background-color: #fff3e0; -fx-border-color: #ffcc02; -fx-border-radius: 8;");
        
        Label infoTitle = new Label("ℹ️ Librarian Permissions");
        infoTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #e65100;");
        
        Label infoText = new Label("As a librarian, you can manage books, partners, and loans.\nUser management requires administrator privileges.");
        infoText.setWrapText(true);
        infoText.setStyle("-fx-text-fill: #bf360c; -fx-text-alignment: center;");
        
        infoSection.getChildren().addAll(infoTitle, infoText);
        
        root.getChildren().addAll(titleSection, actionsSection, infoSection);
        
        scene = new Scene(root, 600, 650);
    }
    
    private void setupEventHandlers() {
        btnBack.setOnAction(e -> goBack());
        btnQuickAddBook.setOnAction(e -> openBooksView());
        btnQuickLoan.setOnAction(e -> openLoansView());
        btnSearchBooks.setOnAction(e -> openBooksView());
        btnViewLoans.setOnAction(e -> openLoansView());
        btnManagePartners.setOnAction(e -> openPartnersView());
    }
    
    private void openBooksView() {
        BooksView booksView = new BooksView(primaryStage, userRole);
        primaryStage.setScene(booksView.getScene());
        primaryStage.setTitle("NovaBook - Books");
    }
    
    private void openLoansView() {
        LoansView loansView = new LoansView(primaryStage, userRole);
        primaryStage.setScene(loansView.getScene());
        primaryStage.setTitle("NovaBook - Loans");
    }
    
    private void openPartnersView() {
        PartnersView partnersView = new PartnersView(primaryStage, userRole);
        primaryStage.setScene(partnersView.getScene());
        primaryStage.setTitle("NovaBook - Partners");
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