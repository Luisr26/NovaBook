/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.codeup.novabook.Reports;

/**
 *
 * @author Coder
 */
import com.codeup.novabook.Models.DAO.BookDAO;
import com.codeup.novabook.Models.Entity.Book;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CSVBookImporter {
    
    private final BookDAO bookDAO;
    
    public CSVBookImporter() {
        this.bookDAO = new BookDAO();
    }
    
    /**
     * Imports books from a CSV file
     * Expected CSV format: Title,Author,ISBN,Publication Year
     * @param csvFile The CSV file to import
     * @return ImportResult with statistics
     */
    public ImportResult importBooksFromCSV(File csvFile) throws IOException, SQLException {
        ImportResult result = new ImportResult();
        List<Book> booksToImport = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            int lineNumber = 0;
            
            // Skip header line
            if ((line = reader.readLine()) != null) {
                lineNumber++;
            }
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                try {
                    Book book = parseBookFromCSVLine(line, lineNumber);
                    if (book != null) {
                        booksToImport.add(book);
                        result.totalProcessed++;
                    }
                } catch (Exception e) {
                    result.errors.add("Line " + lineNumber + ": " + e.getMessage());
                }
            }
        }
        
        // Import books to database
        for (Book book : booksToImport) {
            try {
                // Check if book already exists by ISBN
                if (!bookDAO.existsByISBN(book.getIsbn())) {
                    bookDAO.insertar(book);
                    result.imported++;
                } else {
                    result.skipped++;
                    result.errors.add("Book with ISBN " + book.getIsbn() + " already exists - skipped");
                }
            } catch (SQLException e) {
                result.errors.add("Error importing book '" + book.getTitulo() + "': " + e.getMessage());
            }
        }
        
        return result;
    }
    
    private Book parseBookFromCSVLine(String line, int lineNumber) throws Exception {
        String[] fields = parseCSVLine(line);
        
        if (fields.length < 4) {
            throw new Exception("Invalid CSV format - expected 4 fields (Title,Author,ISBN,Year)");
        }
        
        String title = fields[0].trim();
        String author = fields[1].trim();
        String isbn = fields[2].trim();
        String yearStr = fields[3].trim();
        
        if (title.isEmpty()) {
            throw new Exception("Title cannot be empty");
        }
        
        if (author.isEmpty()) {
            throw new Exception("Author cannot be empty");
        }
        
        if (isbn.isEmpty()) {
            throw new Exception("ISBN cannot be empty");
        }
        
        int year;
        try {
            year = Integer.parseInt(yearStr);
            if (year < 1000 || year > 2030) {
                throw new Exception("Invalid publication year: " + year);
            }
        } catch (NumberFormatException e) {
            throw new Exception("Invalid publication year format: " + yearStr);
        }
        
        Book book = new Book();
        book.setTitulo(title);
        book.setAutor(author);
        book.setIsbn(isbn);
        book.setAnioPublicacion(year);
        book.setDisponible(true);
        book.setFechaAlta(LocalDateTime.now());
        
        return book;
    }
    
    private String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Double quote - add single quote to field
                    currentField.append('"');
                    i++; // Skip next quote
                } else {
                    // Toggle quote state
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // Field separator
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        
        // Add last field
        fields.add(currentField.toString());
        
        return fields.toArray(new String[0]);
    }
    
    public static class ImportResult {
        public int totalProcessed = 0;
        public int imported = 0;
        public int skipped = 0;
        public List<String> errors = new ArrayList<>();
        
        public boolean hasErrors() {
            return !errors.isEmpty();
        }
        
        public String getSummary() {
            StringBuilder sb = new StringBuilder();
            sb.append("Import completed:\n");
            sb.append("- Total processed: ").append(totalProcessed).append("\n");
            sb.append("- Successfully imported: ").append(imported).append("\n");
            sb.append("- Skipped (duplicates): ").append(skipped).append("\n");
            
            if (hasErrors()) {
                sb.append("- Errors: ").append(errors.size()).append("\n");
            }
            
            return sb.toString();
        }
    }
}