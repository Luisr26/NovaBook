/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.codeup.novabook.Controllers;

/**
 *
 * @author Coder
 */
import com.codeup.novabook.Reports.CSVReportGenerator;
import com.codeup.novabook.Reports.CSVBookImporter;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class ReportsController {
    
    private CSVReportGenerator reportGenerator;
    private CSVBookImporter bookImporter;
    private Stage primaryStage;
    
    public ReportsController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.reportGenerator = new CSVReportGenerator();
        this.bookImporter = new CSVBookImporter();
    }
    
    public boolean exportBooks() {
        File outputFile = selectOutputDirectory("books.csv");
        if (outputFile != null) {
            try {
                reportGenerator.generateBookCatalogReport(outputFile);
                showInfo("Export Complete", "Books exported to: " + outputFile.getAbsolutePath());
                return true;
            } catch (Exception e) {
                showError("Export Error", "Failed to export books: " + e.getMessage());
                return false;
            }
        }
        return false;
    }
    
    public boolean exportLoans() {
        File outputFile = selectOutputDirectory("loans.csv");
        if (outputFile != null) {
            try {
                reportGenerator.generateAllLoansReport(outputFile);
                showInfo("Export Complete", "Loans exported to: " + outputFile.getAbsolutePath());
                return true;
            } catch (Exception e) {
                showError("Export Error", "Failed to export loans: " + e.getMessage());
                return false;
            }
        }
        return false;
    }
    
    public boolean exportPartners() {
        File outputFile = selectOutputDirectory("partners.csv");
        if (outputFile != null) {
            try {
                reportGenerator.generatePartnersReport(outputFile);
                showInfo("Export Complete", "Partners exported to: " + outputFile.getAbsolutePath());
                return true;
            } catch (Exception e) {
                showError("Export Error", "Failed to export partners: " + e.getMessage());
                return false;
            }
        }
        return false;
    }
    
    public String importBooks() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV file - Format: Title,Author,ISBN,Year");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            try {
                CSVBookImporter.ImportResult result = bookImporter.importBooksFromCSV(selectedFile);
                
                StringBuilder message = new StringBuilder();
                message.append("=== IMPORT RESULT ===\\n");
                message.append(result.getSummary());
                
                if (result.hasErrors()) {
                    message.append("\\nErrors found:\\n");
                    for (String error : result.errors) {
                        message.append("• ").append(error).append("\\n");
                    }
                }
                message.append("==================\\n\\n");
                
                String dialogMessage = "Import completed!\\n\\n" + result.getSummary();
                if (result.hasErrors()) {
                    dialogMessage += "\\n\\nSome errors occurred. Check the log for details.";
                }
                showInfo("Import Complete", dialogMessage);
                
                return message.toString();
                
            } catch (Exception e) {
                String errorMsg = "ERROR: Failed to import - " + e.getMessage() + "\\n";
                showError("Import Failed", 
                    "Could not import the CSV file.\\n\\n" +
                    "Make sure your CSV has this format:\\n" +
                    "Title,Author,ISBN,Year\\n\\n" +
                    "Error: " + e.getMessage());
                return errorMsg;
            }
        }
        return "";
    }
    
    public boolean downloadTemplate() {
        try {
            // Save directly to user's home directory for simplicity
            File templateFile = new File(System.getProperty("user.home"), "books_template.csv");
            createTemplateFile(templateFile);
            showInfo("Template Ready", 
                "Template saved to: " + templateFile.getAbsolutePath() + 
                "\\n\\nEdit this file with your books and then use 'Import Books'.");
            return true;
        } catch (Exception e) {
            showError("Error", "Could not create template: " + e.getMessage());
            return false;
        }
    }
    
    private void createTemplateFile(File file) throws IOException {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(file))) {
            writer.println("Title,Author,ISBN,Publication Year");
            writer.println("\"Example Book Title\",\"Example Author\",\"978-0000000001\",2024");
            writer.println("\"Another Book\",\"Another Author\",\"978-0000000002\",2023");
            writer.println("\"Book with, comma in title\",\"Author Name\",\"978-0000000003\",2022");
        }
    }
    
    private File selectOutputDirectory(String defaultFilename) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Directory to Save Report");
        
        // Set initial directory to default reports directory
        File defaultDir = CSVReportGenerator.getDefaultReportsDirectory();
        directoryChooser.setInitialDirectory(defaultDir);
        
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
        
        if (selectedDirectory != null) {
            String timestampedFilename = CSVReportGenerator.getTimestampedFilename(defaultFilename);
            return new File(selectedDirectory, timestampedFilename);
        }
        
        return null;
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
}