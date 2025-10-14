/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.codeup.novabook.Controllers;

/**
 *
 * @author Coder
 */
import com.codeup.novabook.Views.*;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainController {
    
    private Stage primaryStage;
    private String userRole;
    
    public MainController(Stage primaryStage, String userRole) {
        this.primaryStage = primaryStage;
        this.userRole = userRole;
    }
    
    public void navigateToBooksView() {
        BooksView booksView = new BooksView(primaryStage, userRole);
        primaryStage.setScene(booksView.getScene());
        primaryStage.setTitle("NovaBook - Books");
    }
    
    
    public void navigateToLoansView() {
        LoansView loansView = new LoansView(primaryStage, userRole);
        primaryStage.setScene(loansView.getScene());
        primaryStage.setTitle("NovaBook - Loans");
    }
    
    public void navigateToUsersView() {
        // Navigate directly to users management with role-based restrictions
        if ("Administrator".equals(userRole)) {
            // Full access to user management for administrators
            UsersView usersView = new UsersView(primaryStage, userRole);
            primaryStage.setScene(usersView.getScene());
            primaryStage.setTitle("NovaBook - User Management");
        } else {
            // Show access denied for non-administrators
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Access Denied");
            alert.setHeaderText("Insufficient Privileges");
            alert.setContentText("User management requires Administrator privileges.\nYour role: " + userRole);
            alert.showAndWait();
        }
    }
    
    public void navigateToReportsView() {
        ReportsView reportsView = new ReportsView(primaryStage, userRole);
        primaryStage.setScene(reportsView.getScene());
        primaryStage.setTitle("NovaBook - Import/Export");
    }
    
    public String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return now.format(formatter);
    }
    
    public String getSystemStatus() {
        return "Ready";
    }
    
    public String getCurrentUser() {
        return LoginController.getCurrentUserName();
    }
    
    public String getCurrentUserRole() {
        return LoginController.getCurrentUserRole();
    }
}