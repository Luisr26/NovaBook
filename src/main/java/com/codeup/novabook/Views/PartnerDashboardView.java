package com.codeup.novabook.Views;

import com.codeup.novabook.Controllers.LoginController;
import com.codeup.novabook.Models.DAO.BookDAO;
import com.codeup.novabook.Models.DAO.LoanDAO;
import com.codeup.novabook.Models.Entity.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

/**
 * Vista de Dashboard para Socios/Partners
 * Permite ver libros disponibles y solicitar préstamos
 * @author Luis Alfredo - Clan Cienaga
 */
public class PartnerDashboardView {
    
    private Scene scene;
    private Stage primaryStage;
    private String userRole;
    private BookDAO bookDAO;
    private LoanDAO loanDAO;
    
    // Components
    private Label lblTitle, lblWelcome, lblStats;
    private Button btnViewAvailableBooks, btnMyLoans, btnRequestLoan, btnSearch, btnBack;
    private TableView<Book> tableBooks;
    private TextField txtSearch;
    private ObservableList<Book> booksList;
    
    public PartnerDashboardView(Stage primaryStage, String userRole) {
        this.primaryStage = primaryStage;
        this.userRole = userRole;
        this.bookDAO = new BookDAO();
        this.loanDAO = new LoanDAO();
        this.booksList = FXCollections.observableArrayList();
        
        initializeComponents();
        createLayout();
        setupEventHandlers();
        loadAvailableBooks();
    }
    
    private void initializeComponents() {
        // Title
        lblTitle = new Label("Partner Dashboard");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblTitle.setStyle("-fx-text-fill: #FF5722;");
        
        // Welcome message
        String userName = LoginController.getCurrentUserName();
        lblWelcome = new Label("Welcome, " + userName);
        lblWelcome.setFont(Font.font("Arial", 16));
        lblWelcome.setStyle("-fx-text-fill: #666;");
        
        // Stats info
        lblStats = new Label("Browse available books and manage your loans");
        lblStats.setStyle("-fx-text-fill: #888; -fx-font-style: italic;");
        
        // Action buttons
        btnViewAvailableBooks = createSolidButton("📚 View Available Books", "#4CAF50");
        btnMyLoans = createSolidButton("📋 My Loans", "#2196F3");
        btnRequestLoan = createSolidButton("📖 Request Loan", "#FF9800");
        btnSearch = createSolidButton("🔍 Search Books", "#9C27B0");
        
        // Back button
        btnBack = new Button("← Back to Login");
        btnBack.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-font-size: 12px;");
        btnBack.setPrefWidth(120);
        
        // Search components
        txtSearch = new TextField();
        txtSearch.setPromptText("Search by title, author, or ISBN...");
        txtSearch.setPrefWidth(300);
        
        // Books table
        setupBooksTable();
    }
    
    private void setupBooksTable() {
        tableBooks = new TableView<>();
        tableBooks.setItems(booksList);
        
        TableColumn<Book, String> colTitle = new TableColumn<>("Title");
        colTitle.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colTitle.setPrefWidth(200);
        
        TableColumn<Book, String> colAuthor = new TableColumn<>("Author");
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colAuthor.setPrefWidth(180);
        
        TableColumn<Book, String> colISBN = new TableColumn<>("ISBN");
        colISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colISBN.setPrefWidth(130);
        
        TableColumn<Book, Integer> colYear = new TableColumn<>("Year");
        colYear.setCellValueFactory(new PropertyValueFactory<>("anioPublicacion"));
        colYear.setPrefWidth(80);
        
        TableColumn<Book, String> colAvailable = new TableColumn<>("Available");
        colAvailable.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().isDisponible() ? "✅ Yes" : "❌ No"
            ));
        colAvailable.setPrefWidth(100);
        
        tableBooks.getColumns().addAll(colTitle, colAuthor, colISBN, colYear, colAvailable);
        tableBooks.setPrefHeight(300);
    }
    
    private Button createSolidButton(String text, String color) {
        Button button = new Button(text);
        button.setPrefSize(250, 45);
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
            case "#FF5722": return "#d84315";
            default: return color;
        }
    }
    
    private void createLayout() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
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
        VBox actionsSection = new VBox(12);
        actionsSection.setAlignment(Pos.CENTER);
        actionsSection.setPadding(new Insets(20));
        actionsSection.setStyle("-fx-background-color: #fff3e0; -fx-border-color: #ff9800; -fx-border-radius: 10;");
        
        Label actionsTitle = new Label("🎯 Quick Actions");
        actionsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        actionsTitle.setStyle("-fx-text-fill: #333;");
        
        HBox buttonsRow1 = new HBox(10);
        buttonsRow1.setAlignment(Pos.CENTER);
        buttonsRow1.getChildren().addAll(btnViewAvailableBooks, btnMyLoans);
        
        HBox buttonsRow2 = new HBox(10);
        buttonsRow2.setAlignment(Pos.CENTER);
        buttonsRow2.getChildren().addAll(btnRequestLoan, btnSearch);
        
        actionsSection.getChildren().addAll(actionsTitle, buttonsRow1, buttonsRow2);
        
        // Search section
        VBox searchSection = new VBox(10);
        searchSection.setAlignment(Pos.CENTER);
        searchSection.setPadding(new Insets(15));
        searchSection.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8;");
        
        Label searchTitle = new Label("📖 Browse Available Books");
        searchTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
        
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.getChildren().addAll(new Label("Search:"), txtSearch);
        
        searchSection.getChildren().addAll(searchTitle, searchBox, tableBooks);
        
        // Info section
        VBox infoSection = new VBox(10);
        infoSection.setAlignment(Pos.CENTER);
        infoSection.setPadding(new Insets(15));
        infoSection.setStyle("-fx-background-color: #e8f4fd; -fx-border-color: #2196f3; -fx-border-radius: 8;");
        
        Label infoTitle = new Label("ℹ️ Partner Privileges");
        infoTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #1976d2;");
        
        Label infoText = new Label("As a library partner, you can browse available books,\\nrequest loans, and manage your current loans.");
        infoText.setWrapText(true);
        infoText.setStyle("-fx-text-fill: #1565c0; -fx-text-alignment: center;");
        
        infoSection.getChildren().addAll(infoTitle, infoText);
        
        root.getChildren().addAll(titleSection, actionsSection, searchSection, infoSection);
        
        scene = new Scene(root, 750, 800);
    }
    
    private void setupEventHandlers() {
        btnBack.setOnAction(e -> goBack());
        btnViewAvailableBooks.setOnAction(e -> loadAvailableBooks());
        btnMyLoans.setOnAction(e -> viewMyLoans());
        btnRequestLoan.setOnAction(e -> requestLoan());
        btnSearch.setOnAction(e -> searchBooks());
        
        // Search on text change
        txtSearch.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.isEmpty()) {
                loadAvailableBooks();
            } else {
                searchBooks();
            }
        });
        
        // Double click on table to view book details
        tableBooks.setRowFactory(tv -> {
            TableRow<Book> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showBookDetails(row.getItem());
                }
            });
            return row;
        });
    }
    
    private void loadAvailableBooks() {
        try {
            List<Book> availableBooks = bookDAO.listar().stream()
                    .filter(Book::isDisponible)
                    .toList();
            booksList.clear();
            booksList.addAll(availableBooks);
            
            lblStats.setText("Showing " + availableBooks.size() + " available books");
        } catch (SQLException e) {
            showError("Error", "Could not load available books: " + e.getMessage());
        }
    }
    
    private void searchBooks() {
        String searchTerm = txtSearch.getText().toLowerCase().trim();
        if (searchTerm.isEmpty()) {
            loadAvailableBooks();
            return;
        }
        
        try {
            List<Book> searchResults = bookDAO.listar().stream()
                    .filter(book -> book.isDisponible() && (
                            book.getTitulo().toLowerCase().contains(searchTerm) ||
                            book.getAutor().toLowerCase().contains(searchTerm) ||
                            book.getIsbn().toLowerCase().contains(searchTerm)
                    ))
                    .toList();
            
            booksList.clear();
            booksList.addAll(searchResults);
            
            lblStats.setText("Found " + searchResults.size() + " books matching '" + searchTerm + "'");
        } catch (SQLException e) {
            showError("Search Error", "Error searching books: " + e.getMessage());
        }
    }
    
    private void viewMyLoans() {
        showInfo("My Loans", "This feature will show your personal loan history.\\nComing soon in next update!");
    }
    
    private void requestLoan() {
        Book selectedBook = tableBooks.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showWarning("No Book Selected", "Please select a book from the table to request a loan.");
            return;
        }
        
        if (!selectedBook.isDisponible()) {
            showWarning("Book Not Available", "The selected book is currently on loan to another partner.");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Request Loan");
        alert.setHeaderText("Confirm Loan Request");
        alert.setContentText("Do you want to request a loan for:\\n\\n" +
                "Title: " + selectedBook.getTitulo() + "\\n" +
                "Author: " + selectedBook.getAutor() + "\\n\\n" +
                "This request will be processed by library staff.");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            showInfo("Loan Requested", "Your loan request has been submitted.\\n\\n" +
                    "Book: " + selectedBook.getTitulo() + "\\n" +
                    "Status: Pending approval\\n\\n" +
                    "You will be notified once the loan is processed.");
        }
    }
    
    private void showBookDetails(Book book) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Book Details");
        alert.setHeaderText(book.getTitulo());
        alert.setContentText("Author: " + book.getAutor() + "\\n" +
                "ISBN: " + book.getIsbn() + "\\n" +
                "Year: " + book.getAnioPublicacion() + "\\n" +
                "Available: " + (book.isDisponible() ? "Yes" : "No"));
        alert.showAndWait();
    }
    
    private void goBack() {
        LoginView loginView = new LoginView(primaryStage);
        primaryStage.setScene(loginView.getScene());
        primaryStage.setTitle("NovaBook - Login");
        LoginController.logout();
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
    
    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public Scene getScene() {
        return scene;
    }
}