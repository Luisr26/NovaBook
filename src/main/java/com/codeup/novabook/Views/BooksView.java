/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.codeup.novabook.Views;

/**
 *
 * @author Coder
 */
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class BooksView {
    
    private Scene scene;
    private Stage primaryStage;
    private BookDAO bookDAO;
    private String userRole;
    
    // Componentes de la interfaz
    private TableView<Book> tableBooks;
    private TextField txtSearch;
    private Label lblTotal;
    private Label lblSelected;
    private Button btnAdd, btnEdit, btnDelete, btnRefresh, btnBack;
    
    private ObservableList<Book> booksList;
    
    public BooksView(Stage primaryStage, String userRole) {
        this.primaryStage = primaryStage;
        this.userRole = userRole;
        this.bookDAO = new BookDAO();
        this.booksList = FXCollections.observableArrayList();
        
        initializeComponents();
        createLayout();
        setupEventHandlers();
        loadBooks();
    }
    
    private void initializeComponents() {
        // Crear tabla
        tableBooks = new TableView<>();
        tableBooks.setItems(booksList);
        
        // Configurar columnas
        TableColumn<Book, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);
        
        TableColumn<Book, String> colTitle = new TableColumn<>("Title");
        colTitle.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colTitle.setPrefWidth(200);
        
        TableColumn<Book, String> colAuthor = new TableColumn<>("Author");
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colAuthor.setPrefWidth(150);
        
        TableColumn<Book, String> colIsbn = new TableColumn<>("ISBN");
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colIsbn.setPrefWidth(120);
        
        TableColumn<Book, Integer> colYear = new TableColumn<>("Year");
        colYear.setCellValueFactory(new PropertyValueFactory<>("anioPublicacion"));
        colYear.setPrefWidth(80);
        
        TableColumn<Book, Boolean> colAvailable = new TableColumn<>("Available");
        colAvailable.setCellValueFactory(new PropertyValueFactory<>("disponible"));
        colAvailable.setPrefWidth(100);
        
        TableColumn<Book, LocalDateTime> colDateAdded = new TableColumn<>("Date Added");
        colDateAdded.setCellValueFactory(new PropertyValueFactory<>("fechaAlta"));
        colDateAdded.setPrefWidth(120);
        
        tableBooks.getColumns().addAll(colId, colTitle, colAuthor, colIsbn, colYear, colAvailable, colDateAdded);
        
        // Otros componentes
        txtSearch = new TextField();
        txtSearch.setPromptText("Search by title or author...");
        txtSearch.setPrefWidth(200);
        
        lblTotal = new Label("Total books: 0");
        lblSelected = new Label("No book selected");
        
        // Buttons
        btnAdd = createButton("Add Book", "#4CAF50");
        btnEdit = createButton("Edit", "#FF9800");
        btnDelete = createButton("Delete", "#f44336");
        btnRefresh = createButton("Refresh", "#2196F3");
        btnBack = createButton("Back", "#9E9E9E");
    }
    
    private Button createButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 3;");
        button.setPrefWidth(120);
        return button;
    }
    
    private void createLayout() {
        BorderPane root = new BorderPane();
        
        // Panel superior con título y controles
        VBox topPanel = new VBox(10);
        topPanel.setPadding(new Insets(10));
        
        // Title
        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.setSpacing(10);
        
        Label title = new Label("Book Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        titleBox.getChildren().addAll(title, spacer, btnBack);
        
        // Controles
        HBox controlsBox = new HBox(10);
        controlsBox.setAlignment(Pos.CENTER_LEFT);
        
        Label lblSearch = new Label("Search:");
        controlsBox.getChildren().addAll(lblSearch, txtSearch, btnAdd, btnEdit, btnDelete, btnRefresh);
        
        topPanel.getChildren().addAll(titleBox, controlsBox);
        
        // Panel inferior con información
        HBox bottomPanel = new HBox(20);
        bottomPanel.setAlignment(Pos.CENTER_LEFT);
        bottomPanel.setPadding(new Insets(10));
        bottomPanel.setStyle("-fx-background-color: #f0f0f0;");
        
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        
        bottomPanel.getChildren().addAll(lblTotal, spacer2, lblSelected);
        
        // Ensamblar layout
        root.setTop(topPanel);
        root.setCenter(tableBooks);
        root.setBottom(bottomPanel);
        
        scene = new Scene(root, 900, 700);
    }
    
    private void setupEventHandlers() {
        btnBack.setOnAction(e -> goBack());
        btnAdd.setOnAction(e -> showAddDialog());
        btnEdit.setOnAction(e -> showEditDialog());
        btnDelete.setOnAction(e -> deleteBook());
        btnRefresh.setOnAction(e -> loadBooks());
        
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> searchBooks());
        
        tableBooks.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                lblSelected.setText("Selected: " + newSelection.getTitulo());
                btnEdit.setDisable(false);
                btnDelete.setDisable(false);
            } else {
                lblSelected.setText("No book selected");
                btnEdit.setDisable(true);
                btnDelete.setDisable(true);
            }
        });
        
        // Inicialmente deshabilitar botones de edición
        btnEdit.setDisable(true);
        btnDelete.setDisable(true);
    }
    
    private void loadBooks() {
        try {
            List<Book> books = bookDAO.listar();
            booksList.clear();
            booksList.addAll(books);
            lblTotal.setText("Total books: " + books.size());
        } catch (SQLException e) {
            showError("Error loading books", "No se pudieron cargar los libros: " + e.getMessage());
        }
    }
    
    private void searchBooks() {
        String searchText = txtSearch.getText().toLowerCase();
        if (searchText.isEmpty()) {
            loadBooks();
            return;
        }
        
        try {
            List<Book> allBooks = bookDAO.listar();
            List<Book> filteredBooks = allBooks.stream()
                    .filter(book -> book.getTitulo().toLowerCase().contains(searchText) ||
                                   book.getAutor().toLowerCase().contains(searchText))
                    .toList();
            
            booksList.clear();
            booksList.addAll(filteredBooks);
            lblTotal.setText("Total books: " + filteredBooks.size());
        } catch (SQLException e) {
            showError("Search error", "Error al buscar libros: " + e.getMessage());
        }
    }
    
    private void showAddDialog() {
        BookDialog dialog = new BookDialog(null);
        Optional<Book> result = dialog.showAndWait();
        
        if (result.isPresent()) {
            try {
                Book book = result.get();
                book.setFechaAlta(LocalDateTime.now());
                book.setDisponible(true);
                bookDAO.agregar(book);
                loadBooks();
                showInfo("Success", "Libro agregado correctamente");
            } catch (SQLException e) {
                showError("Error", "No se pudo agregar el libro: " + e.getMessage());
            }
        }
    }
    
    private void showEditDialog() {
        Book selectedBook = tableBooks.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showWarning("Warning", "Seleccione un libro para editar");
            return;
        }
        
        BookDialog dialog = new BookDialog(selectedBook);
        Optional<Book> result = dialog.showAndWait();
        
        if (result.isPresent()) {
            try {
                bookDAO.actualizar(result.get());
                loadBooks();
                showInfo("Success", "Book added successfully");
            } catch (SQLException e) {
                showError("Error", "No se pudo actualizar el libro: " + e.getMessage());
            }
        }
    }
    
    private void deleteBook() {
        Book selectedBook = tableBooks.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showWarning("Warning", "Seleccione un libro para eliminar");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Delete libro?");
        alert.setContentText("¿Está seguro de que desea eliminar el libro \"" + selectedBook.getTitulo() + "\"?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                bookDAO.eliminar(selectedBook.getId());
                loadBooks();
                showInfo("Success", "Libro eliminado correctamente");
            } catch (SQLException e) {
                showError("Error", "No se pudo eliminar el libro: " + e.getMessage());
            }
        }
    }
    
    private void goBack() {
        MainView mainView = new MainView(primaryStage, userRole);
        primaryStage.setScene(mainView.getScene());
        primaryStage.setTitle("NovaBook - Library Management System");
    }
    
    // Métodos de utilidad para mostrar mensajes
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
    
    // Clase interna para el diálogo de agregar/editar libro
    private class BookDialog extends Dialog<Book> {
        private TextField txtTitle, txtAuthor, txtIsbn, txtYear;
        private CheckBox chkAvailable;
        
        public BookDialog(Book book) {
            setTitle(book == null ? "Add Book" : "Edit Book");
            setHeaderText(book == null ? "Ingrese los datos del nuevo libro" : "Modifique los datos del libro");
            
            // Crear campos
            txtTitle = new TextField();
            txtAuthor = new TextField();
            txtIsbn = new TextField();
            txtYear = new TextField();
            chkAvailable = new CheckBox("Available");
            
            if (book != null) {
                txtTitle.setText(book.getTitulo());
                txtAuthor.setText(book.getAutor());
                txtIsbn.setText(book.getIsbn());
                txtYear.setText(String.valueOf(book.getAnioPublicacion()));
                chkAvailable.setSelected(book.isDisponible());
            } else {
                chkAvailable.setSelected(true);
            }
            
            // Layout del diálogo
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            
            grid.add(new Label("Title:"), 0, 0);
            grid.add(txtTitle, 1, 0);
            grid.add(new Label("Author:"), 0, 1);
            grid.add(txtAuthor, 1, 1);
            grid.add(new Label("ISBN:"), 0, 2);
            grid.add(txtIsbn, 1, 2);
            grid.add(new Label("Year:"), 0, 3);
            grid.add(txtYear, 1, 3);
            grid.add(chkAvailable, 1, 4);
            
            getDialogPane().setContent(grid);
            
            // Botones
            ButtonType buttonTypeOk = new ButtonType("Accept", ButtonBar.ButtonData.OK_DONE);
            getDialogPane().getButtonTypes().addAll(buttonTypeOk, ButtonType.CANCEL);
            
            // Convertir resultado
            setResultConverter(dialogButton -> {
                if (dialogButton == buttonTypeOk) {
                    try {
                        Book result = book == null ? new Book() : book;
                        result.setTitulo(txtTitle.getText());
                        result.setAutor(txtAuthor.getText());
                        result.setIsbn(txtIsbn.getText());
                        result.setAnioPublicacion(Integer.parseInt(txtYear.getText()));
                        result.setDisponible(chkAvailable.isSelected());
                        return result;
                    } catch (NumberFormatException e) {
                        showError("Error", "El año debe ser un número válido");
                        return null;
                    }
                }
                return null;
            });
            
            // Validaciones
            Button okButton = (Button) getDialogPane().lookupButton(buttonTypeOk);
            okButton.setDisable(true);
            
            txtTitle.textProperty().addListener((obs, oldVal, newVal) -> 
                okButton.setDisable(txtTitle.getText().trim().isEmpty() || 
                                   txtAuthor.getText().trim().isEmpty()));
            txtAuthor.textProperty().addListener((obs, oldVal, newVal) -> 
                okButton.setDisable(txtTitle.getText().trim().isEmpty() || 
                                   txtAuthor.getText().trim().isEmpty()));
        }
    }
}