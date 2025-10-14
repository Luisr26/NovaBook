/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.codeup.novabook.Views;

/**
 *
 * @author Coder
 */
import com.codeup.novabook.Models.DAO.LoanDAO;
import com.codeup.novabook.Models.DAO.BookDAO;
import com.codeup.novabook.Models.DAO.PartnerDAO;
import com.codeup.novabook.Models.Entity.Loan;
import com.codeup.novabook.Models.Entity.Book;
import com.codeup.novabook.Models.Entity.Partner;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class LoansView {
    
    private Scene scene;
    private Stage primaryStage;
    private LoanDAO loanDAO;
    private BookDAO bookDAO;
    private PartnerDAO partnerDAO;
    private String userRole;
    
    private TableView<Loan> tableLoans;
    private ComboBox<String> cmbFilter;
    private Label lblTotal, lblActive, lblOverdue;
    private Button btnAdd, btnReturn, btnRefresh, btnBack;
    
    private ObservableList<Loan> loansList;
    
    public LoansView(Stage primaryStage, String userRole) {
        this.primaryStage = primaryStage;
        this.userRole = userRole;
        this.loanDAO = new LoanDAO();
        this.bookDAO = new BookDAO();
        this.partnerDAO = new PartnerDAO();
        this.loansList = FXCollections.observableArrayList();
        
        initializeComponents();
        createLayout();
        setupEventHandlers();
        loadLoans();
    }
    
    private void initializeComponents() {
        tableLoans = new TableView<>();
        tableLoans.setItems(loansList);
        
        // Configurar columnas básicas
        TableColumn<Loan, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);
        
        // Para mostrar el título del libro en lugar del objeto completo
        TableColumn<Loan, String> colBook = new TableColumn<>("Book");
        colBook.setCellValueFactory(cellData -> {
            if (cellData.getValue().getLibro() != null) {
                return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLibro().getTitulo());
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        colBook.setPrefWidth(200);
        
        // Para mostrar el nombre del socio
        TableColumn<Loan, String> colPartner = new TableColumn<>("Partner");
        colPartner.setCellValueFactory(cellData -> {
            if (cellData.getValue().getSocio() != null) {
                return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSocio().getNombre());
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        colPartner.setPrefWidth(150);
        
        TableColumn<Loan, String> colLoanDate = new TableColumn<>("Loan Date");
        colLoanDate.setCellValueFactory(new PropertyValueFactory<>("fechaPrestamo"));
        colLoanDate.setPrefWidth(120);
        
        TableColumn<Loan, String> colReturnDate = new TableColumn<>("Return Date");
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("fechaDevolucion"));
        colReturnDate.setPrefWidth(120);
        
        TableColumn<Loan, Boolean> colReturned = new TableColumn<>("Returned");
        colReturned.setCellValueFactory(new PropertyValueFactory<>("devuelto"));
        colReturned.setPrefWidth(80);
        
        tableLoans.getColumns().addAll(colId, colBook, colPartner, colLoanDate, colReturnDate, colReturned);
        
        // Componentes de control
        cmbFilter = new ComboBox<>();
        cmbFilter.getItems().addAll("All", "Active", "Returned", "Overdue");
        cmbFilter.setValue("All");
        cmbFilter.setPrefWidth(150);
        
        lblTotal = new Label("Total loans: 0");
        lblActive = new Label("Active: 0");
        lblOverdue = new Label("Overdue: 0");
        
        // Botones
        btnAdd = createButton("New Loan", "#4CAF50");
        btnReturn = createButton("Return Book", "#FF9800");
        btnRefresh = createButton("Refresh", "#2196F3");
        btnBack = createButton("Back", "#9E9E9E");
    }
    
    private Button createButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 3;");
        button.setPrefWidth(130);
        return button;
    }
    
    private void createLayout() {
        BorderPane root = new BorderPane();
        
        // Panel superior
        VBox topPanel = new VBox(10);
        topPanel.setPadding(new Insets(10));
        
        // Título
        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.setSpacing(10);
        
        Label title = new Label("Loan Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        titleBox.getChildren().addAll(title, spacer, btnBack);
        
        // Controles
        HBox controlsBox = new HBox(10);
        controlsBox.setAlignment(Pos.CENTER_LEFT);
        
        Label lblFilter = new Label("Filter:");
        controlsBox.getChildren().addAll(lblFilter, cmbFilter, btnAdd, btnReturn, btnRefresh);
        
        topPanel.getChildren().addAll(titleBox, controlsBox);
        
        // Panel inferior
        HBox bottomPanel = new HBox(20);
        bottomPanel.setAlignment(Pos.CENTER_LEFT);
        bottomPanel.setPadding(new Insets(10));
        bottomPanel.setStyle("-fx-background-color: #f0f0f0;");
        
        Separator sep1 = new Separator();
        sep1.setOrientation(javafx.geometry.Orientation.VERTICAL);
        Separator sep2 = new Separator();
        sep2.setOrientation(javafx.geometry.Orientation.VERTICAL);
        
        bottomPanel.getChildren().addAll(lblTotal, sep1, lblActive, sep2, lblOverdue);
        
        root.setTop(topPanel);
        root.setCenter(tableLoans);
        root.setBottom(bottomPanel);
        
        scene = new Scene(root, 900, 700);
    }
    
    private void setupEventHandlers() {
        btnBack.setOnAction(e -> goBack());
        btnAdd.setOnAction(e -> showAddDialog());
        btnReturn.setOnAction(e -> returnBook());
        btnRefresh.setOnAction(e -> loadLoans());
        
        cmbFilter.setOnAction(e -> filterLoans());
        
        btnReturn.setDisable(true);
        
        tableLoans.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            btnReturn.setDisable(newSelection == null || newSelection.isDevuelto());
        });
    }
    
    private void loadLoans() {
        try {
            List<Loan> loans = loanDAO.listar();
            loansList.clear();
            loansList.addAll(loans);
            
            long activeCount = loans.stream().filter(loan -> !loan.isDevuelto()).count();
            long overdueCount = loanDAO.listarPrestamosVencidos().size();
            
            lblTotal.setText("Total loans: " + loans.size());
            lblActive.setText("Active: " + activeCount);
            lblOverdue.setText("Overdue: " + overdueCount);
        } catch (SQLException e) {
            showError("Error loading loans", "Could not load loans: " + e.getMessage());
        }
    }
    
    private void filterLoans() {
        String filter = cmbFilter.getValue();
        try {
            List<Loan> filteredLoans;
            switch (filter) {
                case "Active":
                    filteredLoans = loanDAO.listarPrestamosActivos();
                    break;
                case "Returned":
                    filteredLoans = loanDAO.listar().stream()
                            .filter(Loan::isDevuelto)
                            .toList();
                    break;
                case "Overdue":
                    filteredLoans = loanDAO.listarPrestamosVencidos();
                    break;
                default:
                    filteredLoans = loanDAO.listar();
                    break;
            }
            
            loansList.clear();
            loansList.addAll(filteredLoans);
        } catch (SQLException e) {
            showError("Filter error", "Error applying filter: " + e.getMessage());
        }
    }
    
    private void showAddDialog() {
        LoanDialog dialog = new LoanDialog();
        Optional<Loan> result = dialog.showAndWait();
        
        if (result.isPresent()) {
            try {
                Loan loan = result.get();
                loan.setFechaPrestamo(LocalDate.now());
                loan.setDevuelto(false);
                loanDAO.agregar(loan);
                loadLoans();
                showInfo("Success", "Loan created successfully");
            } catch (SQLException e) {
                showError("Error", "Could not create loan: " + e.getMessage());
            } catch (Exception e) {
                showError("Error", "An error occurred: " + e.getMessage());
            }
        }
    }
    
    private void returnBook() {
        Loan selectedLoan = tableLoans.getSelectionModel().getSelectedItem();
        if (selectedLoan == null || selectedLoan.isDevuelto()) {
            showWarning("Warning", "Please select an active loan to return");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Return");
        alert.setHeaderText("Return Book?");
        alert.setContentText("Do you confirm the return of the book \"" + 
                            selectedLoan.getLibro().getTitulo() + "\"?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                loanDAO.marcarComoDevuelto(selectedLoan.getId());
                loadLoans();
                showInfo("Success", "Book returned successfully");
            } catch (SQLException e) {
                showError("Error", "Could not process return: " + e.getMessage());
            }
        }
    }
    
    private void goBack() {
        MainView mainView = new MainView(primaryStage, userRole);
        primaryStage.setScene(mainView.getScene());
        primaryStage.setTitle("NovaBook - Library Management System");
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
    
    // Inner class for Loan Dialog
    private class LoanDialog extends Dialog<Loan> {
        private ComboBox<Book> cmbBooks;
        private ComboBox<Partner> cmbPartners;
        private DatePicker datePickerLoan;
        private Label lblInstructions;
        
        public LoanDialog() {
            setTitle("New Loan");
            setHeaderText("Create a new book loan");
            
            initializeComponents();
            createDialogLayout();
            setupValidation();
        }
        
        private void initializeComponents() {
            cmbBooks = new ComboBox<>();
            cmbPartners = new ComboBox<>();
            datePickerLoan = new DatePicker();
            lblInstructions = new Label("Select an available book and an active partner to create a loan.");
            
            // Set default date to today
            datePickerLoan.setValue(LocalDate.now());
            
            // Load available books and active partners
            loadAvailableBooks();
            loadActivePartners();
            
            // Set custom cell factories for better display
            cmbBooks.setCellFactory(lv -> new ListCell<Book>() {
                @Override
                protected void updateItem(Book book, boolean empty) {
                    super.updateItem(book, empty);
                    if (empty || book == null) {
                        setText(null);
                    } else {
                        setText(book.getTitulo() + " - " + book.getAutor());
                    }
                }
            });
            
            cmbBooks.setButtonCell(new ListCell<Book>() {
                @Override
                protected void updateItem(Book book, boolean empty) {
                    super.updateItem(book, empty);
                    if (empty || book == null) {
                        setText("Select a book...");
                    } else {
                        setText(book.getTitulo() + " - " + book.getAutor());
                    }
                }
            });
            
            cmbPartners.setCellFactory(lv -> new ListCell<Partner>() {
                @Override
                protected void updateItem(Partner partner, boolean empty) {
                    super.updateItem(partner, empty);
                    if (empty || partner == null) {
                        setText(null);
                    } else {
                        setText(partner.getNombre() + " (" + partner.getEmail() + ")");
                    }
                }
            });
            
            cmbPartners.setButtonCell(new ListCell<Partner>() {
                @Override
                protected void updateItem(Partner partner, boolean empty) {
                    super.updateItem(partner, empty);
                    if (empty || partner == null) {
                        setText("Select a partner...");
                    } else {
                        setText(partner.getNombre() + " (" + partner.getEmail() + ")");
                    }
                }
            });
        }
        
        private void loadAvailableBooks() {
            try {
                List<Book> availableBooks = bookDAO.listar().stream()
                        .filter(Book::isDisponible)
                        .toList();
                cmbBooks.getItems().addAll(availableBooks);
                
                if (availableBooks.isEmpty()) {
                    cmbBooks.setDisable(true);
                    lblInstructions.setText("No available books found. All books are currently on loan.");
                    lblInstructions.setStyle("-fx-text-fill: orange;");
                }
            } catch (SQLException e) {
                showError("Error", "Could not load available books: " + e.getMessage());
            }
        }
        
        private void loadActivePartners() {
            try {
                List<Partner> activePartners = partnerDAO.listarActivos();
                cmbPartners.getItems().addAll(activePartners);
                
                if (activePartners.isEmpty()) {
                    cmbPartners.setDisable(true);
                    lblInstructions.setText("No active partners found. Please register partners first.");
                    lblInstructions.setStyle("-fx-text-fill: orange;");
                }
            } catch (SQLException e) {
                showError("Error", "Could not load active partners: " + e.getMessage());
            }
        }
        
        private void createDialogLayout() {
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(15);
            grid.setPadding(new Insets(20, 150, 10, 10));
            
            // Instructions
            grid.add(lblInstructions, 0, 0, 2, 1);
            lblInstructions.setWrapText(true);
            lblInstructions.setMaxWidth(350);
            
            // Book selection
            grid.add(new Label("Book:"), 0, 1);
            grid.add(cmbBooks, 1, 1);
            cmbBooks.setPrefWidth(300);
            
            // Partner selection
            grid.add(new Label("Partner:"), 0, 2);
            grid.add(cmbPartners, 1, 2);
            cmbPartners.setPrefWidth(300);
            
            // Loan date
            grid.add(new Label("Loan Date:"), 0, 3);
            grid.add(datePickerLoan, 1, 3);
            datePickerLoan.setPrefWidth(300);
            
            // Information note
            Label noteLabel = new Label("Note: The loan period is 15 days. Books not returned within\nthis period will be marked as overdue.");
            noteLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 11px;");
            noteLabel.setWrapText(true);
            grid.add(noteLabel, 0, 4, 2, 1);
            
            getDialogPane().setContent(grid);
            
            // Buttons
            ButtonType buttonTypeOk = new ButtonType("Create Loan", ButtonBar.ButtonData.OK_DONE);
            getDialogPane().getButtonTypes().addAll(buttonTypeOk, ButtonType.CANCEL);
            
            // Result converter
            setResultConverter(dialogButton -> {
                if (dialogButton == buttonTypeOk) {
                    if (cmbBooks.getValue() == null || cmbPartners.getValue() == null) {
                        showWarning("Incomplete Information", "Please select both a book and a partner.");
                        return null;
                    }
                    
                    Loan loan = new Loan();
                    loan.setLibro(cmbBooks.getValue());
                    loan.setSocio(cmbPartners.getValue());
                    loan.setFechaPrestamo(datePickerLoan.getValue());
                    loan.setDevuelto(false);
                    return loan;
                }
                return null;
            });
        }
        
        private void setupValidation() {
            // Get OK button
            Button okButton = (Button) getDialogPane().lookupButton(
                    getDialogPane().getButtonTypes().stream()
                            .filter(bt -> bt.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                            .findFirst().orElse(null)
            );
            
            if (okButton != null) {
                // Initially disable if no items available
                boolean hasBooks = !cmbBooks.getItems().isEmpty();
                boolean hasPartners = !cmbPartners.getItems().isEmpty();
                okButton.setDisable(!hasBooks || !hasPartners);
                
                // Enable/disable based on selections
                cmbBooks.valueProperty().addListener((obs, oldVal, newVal) -> 
                        okButton.setDisable(newVal == null || cmbPartners.getValue() == null));
                
                cmbPartners.valueProperty().addListener((obs, oldVal, newVal) -> 
                        okButton.setDisable(newVal == null || cmbBooks.getValue() == null));
            }
        }
    }
}
