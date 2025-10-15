/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.codeup.novabook.Views;

/**
 *
 * @author Coder
 */
import com.codeup.novabook.Models.DAO.PartnerDAO;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PartnersView {
    
    private Scene scene;
    private Stage primaryStage;
    private PartnerDAO partnerDAO;
    private String userRole;
    
    private TableView<Partner> tablePartners;
    private TextField txtSearch;
    private Label lblTotal;
    private Button btnAdd, btnEdit, btnDelete, btnRefresh, btnBack;
    
    private ObservableList<Partner> partnersList;
    
    public PartnersView(Stage primaryStage, String userRole) {
        this.primaryStage = primaryStage;
        this.userRole = userRole;
        this.partnerDAO = new PartnerDAO();
        this.partnersList = FXCollections.observableArrayList();
        
        initializeComponents();
        createLayout();
        setupEventHandlers();
        loadPartners();
    }
    
    private void initializeComponents() {
        tablePartners = new TableView<>();
        tablePartners.setItems(partnersList);
        
        // Configurar columnas
        TableColumn<Partner, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);
        
        TableColumn<Partner, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colName.setPrefWidth(150);
        
        TableColumn<Partner, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(180);
        
        TableColumn<Partner, String> colPhone = new TableColumn<>("Phone");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colPhone.setPrefWidth(120);
        
        TableColumn<Partner, Boolean> colActive = new TableColumn<>("Active");
        colActive.setCellValueFactory(new PropertyValueFactory<>("activo"));
        colActive.setPrefWidth(80);
        
        tablePartners.getColumns().addAll(colId, colName, colEmail, colPhone, colActive);
        
        txtSearch = new TextField();
        txtSearch.setPromptText("Search by name or email...");
        txtSearch.setPrefWidth(200);
        
        lblTotal = new Label("Total partners: 0");
        
        btnAdd = createButton("Add Partner", "#4CAF50");
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
        
        // Panel superior
        VBox topPanel = new VBox(10);
        topPanel.setPadding(new Insets(10));
        
        // Título
        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.setSpacing(10);
        
        Label title = new Label("Partner Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        titleBox.getChildren().addAll(title, spacer, btnBack);
        
        // Controles
        HBox controlsBox = new HBox(10);
        controlsBox.setAlignment(Pos.CENTER_LEFT);
        
        Label lblSearch = new Label("Buscar:");
        controlsBox.getChildren().addAll(lblSearch, txtSearch, btnAdd, btnEdit, btnDelete, btnRefresh);
        
        topPanel.getChildren().addAll(titleBox, controlsBox);
        
        // Panel inferior
        HBox bottomPanel = new HBox(20);
        bottomPanel.setAlignment(Pos.CENTER_LEFT);
        bottomPanel.setPadding(new Insets(10));
        bottomPanel.setStyle("-fx-background-color: #f0f0f0;");
        bottomPanel.getChildren().add(lblTotal);
        
        root.setTop(topPanel);
        root.setCenter(tablePartners);
        root.setBottom(bottomPanel);
        
        scene = new Scene(root, 900, 700);
    }
    
    private void setupEventHandlers() {
        btnBack.setOnAction(e -> goBack());
        btnAdd.setOnAction(e -> showAddDialog());
        btnEdit.setOnAction(e -> showEditDialog());
        btnDelete.setOnAction(e -> deletePartner());
        btnRefresh.setOnAction(e -> loadPartners());
        
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> searchPartners());
        
        btnEdit.setDisable(true);
        btnDelete.setDisable(true);
        
        tablePartners.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            btnEdit.setDisable(newSelection == null);
            btnDelete.setDisable(newSelection == null);
        });
    }
    
    private void loadPartners() {
        try {
            List<Partner> partners = partnerDAO.listar();
            partnersList.clear();
            partnersList.addAll(partners);
            lblTotal.setText("Total partners: " + partners.size());
        } catch (SQLException e) {
            showError("Error al cargar socios", "No se pudieron cargar los socios: " + e.getMessage());
        }
    }
    
    private void searchPartners() {
        String searchText = txtSearch.getText().toLowerCase();
        if (searchText.isEmpty()) {
            loadPartners();
            return;
        }
        
        try {
            List<Partner> allPartners = partnerDAO.listar();
            List<Partner> filteredPartners = allPartners.stream()
                    .filter(partner -> partner.getNombre().toLowerCase().contains(searchText) ||
                                      partner.getEmail().toLowerCase().contains(searchText))
                    .toList();
            
            partnersList.clear();
            partnersList.addAll(filteredPartners);
            lblTotal.setText("Total partners: " + filteredPartners.size());
        } catch (SQLException e) {
            showError("Error en búsqueda", "Error al buscar socios: " + e.getMessage());
        }
    }
    
    private void showAddDialog() {
        PartnerDialog dialog = new PartnerDialog(null);
        Optional<Partner> result = dialog.showAndWait();
        
        if (result.isPresent()) {
            try {
                Partner partner = result.get();
                partner.setFechaRegistro(LocalDateTime.now());
                partner.setActivo(true);
                partnerDAO.agregar(partner);
                loadPartners();
                showInfo("Éxito", "Socio agregado correctamente");
            } catch (SQLException e) {
                showError("Error", "No se pudo agregar el socio: " + e.getMessage());
            }
        }
    }
    
    private void showEditDialog() {
        Partner selectedPartner = tablePartners.getSelectionModel().getSelectedItem();
        if (selectedPartner == null) return;
        
        PartnerDialog dialog = new PartnerDialog(selectedPartner);
        Optional<Partner> result = dialog.showAndWait();
        
        if (result.isPresent()) {
            try {
                partnerDAO.actualizar(result.get());
                loadPartners();
                showInfo("Éxito", "Socio actualizado correctamente");
            } catch (SQLException e) {
                showError("Error", "No se pudo actualizar el socio: " + e.getMessage());
            }
        }
    }
    
    private void deletePartner() {
        Partner selectedPartner = tablePartners.getSelectionModel().getSelectedItem();
        if (selectedPartner == null) return;
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Delete socio?");
        alert.setContentText("¿Está seguro de que desea eliminar el socio \"" + selectedPartner.getNombre() + "\"?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                partnerDAO.eliminar(selectedPartner.getId());
                loadPartners();
                showInfo("Éxito", "Socio eliminado correctamente");
            } catch (SQLException e) {
                showError("Error", "No se pudo eliminar el socio: " + e.getMessage());
            }
        }
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
    
    // Diálogo simple para agregar/editar socio
    private class PartnerDialog extends Dialog<Partner> {
        private TextField txtName, txtEmail, txtPhone, txtAddress;
        private CheckBox chkActive;
        
        public PartnerDialog(Partner partner) {
            setTitle(partner == null ? "Add Partner" : "Edit Partner");
            setHeaderText(partner == null ? "Ingrese los datos del nuevo socio" : "Modifique los datos del socio");
            
            txtName = new TextField();
            txtEmail = new TextField();
            txtPhone = new TextField();
            txtAddress = new TextField();
            chkActive = new CheckBox("Active");
            
            if (partner != null) {
                txtName.setText(partner.getNombre());
                txtEmail.setText(partner.getEmail());
                txtPhone.setText(partner.getTelefono());
                txtAddress.setText(partner.getDireccion());
                chkActive.setSelected(partner.isActivo());
            } else {
                chkActive.setSelected(true);
            }
            
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            
            grid.add(new Label("Name:"), 0, 0);
            grid.add(txtName, 1, 0);
            grid.add(new Label("Email:"), 0, 1);
            grid.add(txtEmail, 1, 1);
            grid.add(new Label("Phone:"), 0, 2);
            grid.add(txtPhone, 1, 2);
            grid.add(new Label("Address:"), 0, 3);
            grid.add(txtAddress, 1, 3);
            grid.add(chkActive, 1, 4);
            
            getDialogPane().setContent(grid);
            
            ButtonType buttonTypeOk = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
            getDialogPane().getButtonTypes().addAll(buttonTypeOk, ButtonType.CANCEL);
            
            setResultConverter(dialogButton -> {
                if (dialogButton == buttonTypeOk) {
                    Partner result = partner == null ? new Partner() : partner;
                    result.setNombre(txtName.getText());
                    result.setEmail(txtEmail.getText());
                    result.setTelefono(txtPhone.getText());
                    result.setDireccion(txtAddress.getText());
                    result.setActivo(chkActive.isSelected());
                    return result;
                }
                return null;
            });
        }
    }
}