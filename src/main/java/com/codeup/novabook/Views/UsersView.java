/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.codeup.novabook.Views;

/**
 *
 * @author Coder
 */
import com.codeup.novabook.Models.DAO.UserDAO;
import com.codeup.novabook.Models.Entity.Users;
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

public class UsersView {
    
    private Scene scene;
    private Stage primaryStage;
    private UserDAO userDAO;
    private String userRole;
    
    private TableView<Users> tableUsers;
    private TextField txtSearch;
    private Label lblTotal;
    private Button btnAdd, btnEdit, btnDelete, btnRefresh, btnBack;
    
    private ObservableList<Users> usersList;
    
    public UsersView(Stage primaryStage, String userRole) {
        this.primaryStage = primaryStage;
        this.userRole = userRole;
        this.userDAO = new UserDAO();
        this.usersList = FXCollections.observableArrayList();
        
        initializeComponents();
        createLayout();
        setupEventHandlers();
        loadUsers();
    }
    
    private void initializeComponents() {
        tableUsers = new TableView<>();
        tableUsers.setItems(usersList);
        
        // Configurar columnas
        TableColumn<Users, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);
        
        TableColumn<Users, String> colName = new TableColumn<>("Nombre");
        colName.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colName.setPrefWidth(150);
        
        TableColumn<Users, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(200);
        
        TableColumn<Users, Boolean> colActive = new TableColumn<>("Activo");
        colActive.setCellValueFactory(new PropertyValueFactory<>("activo"));
        colActive.setPrefWidth(80);
        
        TableColumn<Users, String> colDateCreated = new TableColumn<>("Creation Date");
        colDateCreated.setCellValueFactory(new PropertyValueFactory<>("fechaCreacion"));
        colDateCreated.setPrefWidth(120);
        
        // Columna para roles (simplificada)
        TableColumn<Users, String> colRoles = new TableColumn<>("Roles");
        colRoles.setCellValueFactory(cellData -> {
            if (cellData.getValue().getRoles() != null && !cellData.getValue().getRoles().isEmpty()) {
                return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getRoles().get(0).getNombre()
                );
            }
            return new javafx.beans.property.SimpleStringProperty("No role");
        });
        colRoles.setPrefWidth(100);
        
        tableUsers.getColumns().addAll(colId, colName, colEmail, colActive, colDateCreated, colRoles);
        
        txtSearch = new TextField();
        txtSearch.setPromptText("Search by name or email...");
        txtSearch.setPrefWidth(200);
        
        lblTotal = new Label("Total users: 0");
        
        btnAdd = createButton("Add User", "#4CAF50");
        btnEdit = createButton("Edit", "#FF9800");
        btnDelete = createButton("Delete", "#f44336");
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
        
        Label title = new Label("User Management");
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
        root.setCenter(tableUsers);
        root.setBottom(bottomPanel);
        
        scene = new Scene(root, 900, 700);
    }
    
    private void setupEventHandlers() {
        btnBack.setOnAction(e -> goBack());
        btnAdd.setOnAction(e -> showAddDialog());
        btnEdit.setOnAction(e -> showEditDialog());
        btnDelete.setOnAction(e -> deleteUser());
        btnRefresh.setOnAction(e -> loadUsers());
        
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> searchUsers());
        
        btnEdit.setDisable(true);
        btnDelete.setDisable(true);
        
        tableUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            btnEdit.setDisable(newSelection == null);
            btnDelete.setDisable(newSelection == null);
        });
    }
    
    private void loadUsers() {
        try {
            List<Users> users = userDAO.listar();
            usersList.clear();
            usersList.addAll(users);
            lblTotal.setText("Total users: " + users.size());
        } catch (SQLException e) {
            showError("Error al cargar usuarios", "No se pudieron cargar los usuarios: " + e.getMessage());
        }
    }
    
    private void searchUsers() {
        String searchText = txtSearch.getText().toLowerCase();
        if (searchText.isEmpty()) {
            loadUsers();
            return;
        }
        
        try {
            List<Users> allUsers = userDAO.listar();
            List<Users> filteredUsers = allUsers.stream()
                    .filter(user -> user.getNombre().toLowerCase().contains(searchText) ||
                                   user.getEmail().toLowerCase().contains(searchText))
                    .toList();
            
            usersList.clear();
            usersList.addAll(filteredUsers);
            lblTotal.setText("Total users: " + filteredUsers.size());
        } catch (SQLException e) {
            showError("Error en búsqueda", "Error al buscar usuarios: " + e.getMessage());
        }
    }
    
    private void showAddDialog() {
        UserDialog dialog = new UserDialog(null);
        Optional<Users> result = dialog.showAndWait();
        
        if (result.isPresent()) {
            try {
                Users user = result.get();
                user.setFechaCreacion(LocalDateTime.now());
                user.setActivo(true);
                userDAO.agregar(user);
                loadUsers();
                showInfo("Éxito", "Usuario agregado correctamente");
            } catch (SQLException e) {
                showError("Error", "No se pudo agregar el usuario: " + e.getMessage());
            }
        }
    }
    
    private void showEditDialog() {
        Users selectedUser = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedUser == null) return;
        
        UserDialog dialog = new UserDialog(selectedUser);
        Optional<Users> result = dialog.showAndWait();
        
        if (result.isPresent()) {
            try {
                userDAO.actualizar(result.get());
                loadUsers();
                showInfo("Éxito", "Usuario actualizado correctamente");
            } catch (SQLException e) {
                showError("Error", "No se pudo actualizar el usuario: " + e.getMessage());
            }
        }
    }
    
    private void deleteUser() {
        Users selectedUser = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedUser == null) return;
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Delete usuario?");
        alert.setContentText("¿Está seguro de que desea eliminar el usuario \"" + selectedUser.getNombre() + "\"?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userDAO.eliminar(selectedUser.getId());
                loadUsers();
                showInfo("Éxito", "Usuario eliminado correctamente");
            } catch (SQLException e) {
                showError("Error", "No se pudo eliminar el usuario: " + e.getMessage());
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
    
    public Scene getScene() {
        return scene;
    }
    
    // Diálogo simple para agregar/editar usuario
    private class UserDialog extends Dialog<Users> {
        private TextField txtName, txtEmail, txtPassword;
        private CheckBox chkActive;
        
        public UserDialog(Users user) {
            setTitle(user == null ? "Add User" : "Edit User");
            setHeaderText(user == null ? "Ingrese los datos del nuevo usuario" : "Modifique los datos del usuario");
            
            txtName = new TextField();
            txtEmail = new TextField();
            txtPassword = new TextField();
            chkActive = new CheckBox("Activo");
            
            if (user != null) {
                txtName.setText(user.getNombre());
                txtEmail.setText(user.getEmail());
                txtPassword.setPromptText("Dejar vacío para mantener contraseña actual");
                chkActive.setSelected(user.isActivo());
            } else {
                chkActive.setSelected(true);
            }
            
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            
            grid.add(new Label("Nombre:"), 0, 0);
            grid.add(txtName, 1, 0);
            grid.add(new Label("Email:"), 0, 1);
            grid.add(txtEmail, 1, 1);
            grid.add(new Label("Password:"), 0, 2);
            grid.add(txtPassword, 1, 2);
            grid.add(chkActive, 1, 3);
            
            getDialogPane().setContent(grid);
            
            ButtonType buttonTypeOk = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
            getDialogPane().getButtonTypes().addAll(buttonTypeOk, ButtonType.CANCEL);
            
            setResultConverter(dialogButton -> {
                if (dialogButton == buttonTypeOk) {
                    Users result = user == null ? new Users() : user;
                    result.setNombre(txtName.getText());
                    result.setEmail(txtEmail.getText());
                    
                    // Solo actualizar contraseña si se especifica una nueva
                    if (user == null || !txtPassword.getText().trim().isEmpty()) {
                        result.setPassword(txtPassword.getText());
                    }
                    
                    result.setActivo(chkActive.isSelected());
                    return result;
                }
                return null;
            });
        }
    }
}