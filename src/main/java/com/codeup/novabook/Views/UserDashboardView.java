package com.codeup.novabook.Views;

import com.codeup.novabook.Controllers.LoginController;
import com.codeup.novabook.Models.DAO.BookDAO;
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
 * Vista de Dashboard para Usuarios regulares
 * Solo permite ver el catálogo de libros (sin préstamos)
 * @author Luis Alfredo - Clan Cienaga
 */
public class UserDashboardView {
    
    private Scene scene;
    private Stage primaryStage;
    private String userRole;
    private BookDAO bookDAO;
    
    // Components
    private Label lblTitle, lblWelcome, lblStats;
    private Button btnViewAllBooks, btnSearchBooks, btnBack;
    private TableView<Book> tableBooks;
    private TextField txtSearch;
    private ComboBox<String> cmbFilter;
    private ObservableList<Book> booksList;
    
    public UserDashboardView(Stage primaryStage, String userRole) {
        this.primaryStage = primaryStage;
        this.userRole = userRole;
        this.bookDAO = new BookDAO();
        this.booksList = FXCollections.observableArrayList();
        
        initializeComponents();
        createLayout();
        setupEventHandlers();
        loadAllBooks();
    }
    
    private void initializeComponents() {
        // Title
        lblTitle = new Label("Library Catalog");
        lblTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblTitle.setStyle("-fx-text-fill: #3F51B5;");
        
        // Welcome message
        String userName = LoginController.getCurrentUserName();
        lblWelcome = new Label("Welcome, " + userName);
        lblWelcome.setFont(Font.font("Arial", 16));
        lblWelcome.setStyle("-fx-text-fill: #666;");
        
        // Stats info
        lblStats = new Label("Browse our complete book catalog");
        lblStats.setStyle("-fx-text-fill: #888; -fx-font-style: italic;");
        
        // Action buttons
        btnViewAllBooks = createSolidButton("📚 View All Books", "#4CAF50");
        btnSearchBooks = createSolidButton("🔍 Search Books", "#2196F3");
        
        // Back button
        btnBack = new Button("← Back to Login");
        btnBack.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-font-size: 12px;");
        btnBack.setPrefWidth(120);
        
        // Search components
        txtSearch = new TextField();
        txtSearch.setPromptText("Search by title, author, or ISBN...");
        txtSearch.setPrefWidth(300);
        
        // Filter combo
        cmbFilter = new ComboBox<>();
        cmbFilter.getItems().addAll("All Books", "Available Only", "On Loan");
        cmbFilter.setValue("All Books");
        cmbFilter.setPrefWidth(150);
        
        // Books table
        setupBooksTable();
    }
    
    private void setupBooksTable() {
        tableBooks = new TableView<>();
        tableBooks.setItems(booksList);
        
        TableColumn<Book, String> colTitle = new TableColumn<>("Title");
        colTitle.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colTitle.setPrefWidth(250);
        
        TableColumn<Book, String> colAuthor = new TableColumn<>("Author");
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colAuthor.setPrefWidth(200);
        
        TableColumn<Book, String> colISBN = new TableColumn<>("ISBN");
        colISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colISBN.setPrefWidth(150);
        
        TableColumn<Book, Integer> colYear = new TableColumn<>("Year");
        colYear.setCellValueFactory(new PropertyValueFactory<>("anioPublicacion"));
        colYear.setPrefWidth(80);
        
        TableColumn<Book, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().isDisponible() ? "✅ Available" : "📖 On Loan"
            ));
        colStatus.setPrefWidth(100);
        
        tableBooks.getColumns().addAll(colTitle, colAuthor, colISBN, colYear, colStatus);
        tableBooks.setPrefHeight(400);
    }
    
    private Button createSolidButton(String text, String color) {
        Button button = new Button(text);
        button.setPrefSize(200, 40);
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
            case "#3F51B5": return "#303f9f";
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
        actionsSection.setStyle("-fx-background-color: #e8eaf6; -fx-border-color: #3f51b5; -fx-border-radius: 10;");
        
        Label actionsTitle = new Label("📖 Browse Catalog");
        actionsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        actionsTitle.setStyle("-fx-text-fill: #333;");
        
        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.getChildren().addAll(btnViewAllBooks, btnSearchBooks);
        
        actionsSection.getChildren().addAll(actionsTitle, buttonsBox);
        
        // Search and filter section
        VBox searchSection = new VBox(10);
        searchSection.setAlignment(Pos.CENTER);
        searchSection.setPadding(new Insets(15));
        searchSection.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 8;");
        
        Label searchTitle = new Label("🔍 Search & Filter");
        searchTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
        
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.getChildren().addAll(
            new Label("Search:"), txtSearch,
            new Label("Filter:"), cmbFilter
        );
        
        searchSection.getChildren().addAll(searchTitle, searchBox);
        
        // Books catalog section
        VBox catalogSection = new VBox(10);
        catalogSection.setAlignment(Pos.CENTER);
        catalogSection.setPadding(new Insets(15));
        catalogSection.setStyle("-fx-background-color: #fafafa; -fx-border-color: #ccc; -fx-border-radius: 8;");
        
        Label catalogTitle = new Label("📚 Book Catalog");
        catalogTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #333; -fx-font-size: 14px;");
        
        catalogSection.getChildren().addAll(catalogTitle, tableBooks);
        
        // Info section
        VBox infoSection = new VBox(10);
        infoSection.setAlignment(Pos.CENTER);
        infoSection.setPadding(new Insets(15));
        infoSection.setStyle("-fx-background-color: #fff3e0; -fx-border-color: #ff9800; -fx-border-radius: 8;");
        
        Label infoTitle = new Label("ℹ️ User Access");
        infoTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #e65100;");
        
        Label infoText = new Label("As a registered user, you can browse our complete book catalog.\\n" +
                "To request loans, please contact library staff or consider\\n" +
                "upgrading to a Partner membership.");
        infoText.setWrapText(true);
        infoText.setStyle("-fx-text-fill: #bf360c; -fx-text-alignment: center;");
        
        infoSection.getChildren().addAll(infoTitle, infoText);
        
        root.getChildren().addAll(titleSection, actionsSection, searchSection, catalogSection, infoSection);
        
        scene = new Scene(root, 800, 900);
    }
    
    private void setupEventHandlers() {
        btnBack.setOnAction(e -> goBack());
        btnViewAllBooks.setOnAction(e -> loadAllBooks());
        btnSearchBooks.setOnAction(e -> searchBooks());
        
        // Search on text change
        txtSearch.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.isEmpty()) {
                applyFilter();
            } else {
                searchBooks();
            }
        });
        
        // Filter change
        cmbFilter.setOnAction(e -> applyFilter());
        
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
    
    private void loadAllBooks() {
        try {
            List<Book> allBooks = bookDAO.listar();
            booksList.clear();
            booksList.addAll(allBooks);
            
            lblStats.setText("Showing " + allBooks.size() + " books in our catalog");
            txtSearch.clear();
            cmbFilter.setValue("All Books");
        } catch (SQLException e) {
            showError("Error", "Could not load books: " + e.getMessage());
        }
    }
    
    private void applyFilter() {
        String filter = cmbFilter.getValue();
        try {
            List<Book> filteredBooks;
            switch (filter) {
                case "Available Only":
                    filteredBooks = bookDAO.listar().stream()
                            .filter(Book::isDisponible)
                            .toList();
                    break;
                case "On Loan":
                    filteredBooks = bookDAO.listar().stream()
                            .filter(book -> !book.isDisponible())
                            .toList();
                    break;
                default:
                    filteredBooks = bookDAO.listar();
                    break;
            }
            
            booksList.clear();
            booksList.addAll(filteredBooks);
            
            lblStats.setText("Showing " + filteredBooks.size() + " books (" + filter + ")");
        } catch (SQLException e) {
            showError("Filter Error", "Error applying filter: " + e.getMessage());
        }
    }
    
    private void searchBooks() {
        String searchTerm = txtSearch.getText().toLowerCase().trim();
        if (searchTerm.isEmpty()) {
            applyFilter();
            return;
        }
        
        try {
            List<Book> searchResults = bookDAO.listar().stream()
                    .filter(book -> 
                            book.getTitulo().toLowerCase().contains(searchTerm) ||
                            book.getAutor().toLowerCase().contains(searchTerm) ||
                            book.getIsbn().toLowerCase().contains(searchTerm)
                    )
                    .toList();
            
            booksList.clear();
            booksList.addAll(searchResults);
            
            lblStats.setText("Found " + searchResults.size() + " books matching '" + searchTerm + "'");
        } catch (SQLException e) {
            showError("Search Error", "Error searching books: " + e.getMessage());
        }
    }
    
    private void showBookDetails(Book book) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Book Details");
        alert.setHeaderText(book.getTitulo());
        
        String content = "Author: " + book.getAutor() + "\\n" +
                "ISBN: " + book.getIsbn() + "\\n" +
                "Year: " + book.getAnioPublicacion() + "\\n" +
                "Status: " + (book.isDisponible() ? "Available" : "On Loan") + "\\n\\n" +
                "Note: To request a loan for this book, please visit the library\\n" +
                "or contact our staff directly.";
        
        alert.setContentText(content);
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
    
    public Scene getScene() {
        return scene;
    }
}