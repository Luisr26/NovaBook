/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.codeup.novabook.Controllers;

/**
 *
 * @author Coder
 */
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

public class LoginController {
    
    private Stage primaryStage;
    private Map<String, UserInfo> users;
    private static UserInfo currentUser = null;
    
    public LoginController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeUsers();
    }
    
    private void initializeUsers() {
        users = new HashMap<>();
        
        // Demo users with email format
        users.put("admin@nova.com", new UserInfo("admin123", "John Smith", "Administrator"));
        users.put("librarian@nova.com", new UserInfo("lib123", "Maria Garcia", "Librarian"));
        
        // Simple demo users (for quick testing)
        users.put("admin", new UserInfo("admin", "Administrator User", "Administrator"));
        users.put("librarian", new UserInfo("librarian", "Librarian User", "Librarian"));
        
        // Additional test users
        users.put("test@admin.com", new UserInfo("test123", "Test Administrator", "Administrator"));
        users.put("test@lib.com", new UserInfo("test123", "Test Librarian", "Librarian"));
    }
    
    public String login(String email, String password) {
        UserInfo user = users.get(email.toLowerCase());
        
        if (user != null && user.password.equals(password)) {
            currentUser = user; // Store current logged user
            return user.role;
        }
        
        return null; // Login failed
    }
    
    public boolean isValidRole(String role) {
        return "Administrator".equals(role) || "Librarian".equals(role);
    }
    
    public boolean canAccessUserManagement(String role) {
        return "Administrator".equals(role);
    }
    
    public boolean canAccessBookManagement(String role) {
        return true; // Both roles can access
    }
    
    public boolean canAccessPartnerManagement(String role) {
        return true; // Both roles can access
    }
    
    public boolean canAccessLoanManagement(String role) {
        return true; // Both roles can access
    }
    
    public boolean canAccessReports(String role) {
        return true; // Both roles can access
    }
    
    public boolean canImportBooks(String role) {
        return "Administrator".equals(role);
    }
    
    public static String getCurrentUserName() {
        return currentUser != null ? currentUser.name : "Unknown User";
    }
    
    public static String getCurrentUserRole() {
        return currentUser != null ? currentUser.role : "No Role";
    }
    
    public static void logout() {
        currentUser = null;
    }
    
    // Inner class to store user information
    private static class UserInfo {
        String password;
        String name;
        String role;
        
        UserInfo(String password, String name, String role) {
            this.password = password;
            this.name = name;
            this.role = role;
        }
    }
}