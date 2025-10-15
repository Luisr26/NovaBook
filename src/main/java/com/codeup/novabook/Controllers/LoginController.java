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
        users.put("socio", new UserInfo("socio123", "Juan Pérez - Socio", "Socio"));
        users.put("usuario", new UserInfo("usuario123", "María García - Usuario", "Usuario"));
        
        // Additional test users
        users.put("test@admin.com", new UserInfo("test123", "Test Administrator", "Administrator"));
        users.put("test@lib.com", new UserInfo("test123", "Test Librarian", "Librarian"));
        users.put("socio@novabook.com", new UserInfo("socio123", "Juan Pérez - Socio", "Socio"));
        users.put("usuario@novabook.com", new UserInfo("usuario123", "María García - Usuario", "Usuario"));
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
        return "Administrator".equals(role) || "Librarian".equals(role) || "Socio".equals(role) || "Usuario".equals(role);
    }
    
    public boolean canAccessUserManagement(String role) {
        return "Administrator".equals(role);
    }
    
    public boolean canAccessBookManagement(String role) {
        return "Administrator".equals(role) || "Librarian".equals(role); // Only staff
    }
    
    public boolean canAccessPartnerManagement(String role) {
        return "Administrator".equals(role) || "Librarian".equals(role); // Only staff
    }
    
    public boolean canAccessLoanManagement(String role) {
        return "Administrator".equals(role) || "Librarian".equals(role); // Only staff
    }
    
    public boolean canAccessReports(String role) {
        return "Administrator".equals(role) || "Librarian".equals(role); // Only staff
    }
    
    public boolean canImportBooks(String role) {
        return "Administrator".equals(role);
    }
    
    // New permissions for Partner and User roles
    public boolean canViewBooks(String role) {
        return true; // All roles can view books
    }
    
    public boolean canCreateLoans(String role) {
        return "Administrator".equals(role) || "Librarian".equals(role) || "Socio".equals(role); // Staff and Partners
    }
    
    public boolean canViewOwnLoans(String role) {
        return "Socio".equals(role); // Partners can view their own loans
    }
    
    public boolean isPartnerRole(String role) {
        return "Socio".equals(role);
    }
    
    public boolean isUserRole(String role) {
        return "Usuario".equals(role);
    }
    
    public boolean isStaffRole(String role) {
        return "Administrator".equals(role) || "Librarian".equals(role);
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