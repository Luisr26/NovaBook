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
import com.codeup.novabook.Models.DAO.LoanDAO;
import com.codeup.novabook.Models.Entity.Book;
import com.codeup.novabook.Models.Entity.Loan;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CSVReportGenerator {
    
    private static final String CSV_SEPARATOR = ",";
    private static final String CSV_QUOTE = "\"";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private final BookDAO bookDAO;
    private final LoanDAO loanDAO;
    
    public CSVReportGenerator() {
        this.bookDAO = new BookDAO();
        this.loanDAO = new LoanDAO();
    }
    
    /**
     * Generates a CSV report of the complete book catalog
     * @param outputFile The file where the CSV will be saved
     * @throws SQLException If there's an error accessing the database
     * @throws IOException If there's an error writing the file
     */
    public void generateBookCatalogReport(File outputFile) throws SQLException, IOException {
        List<Book> books = bookDAO.listar();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            // Write CSV header
            writeCSVHeader(writer, new String[]{
                "ID", "Title", "Author", "ISBN", "Publication Year", 
                "Available", "Date Added", "Status"
            });
            
            // Write book data
            for (Book book : books) {
                String[] rowData = {
                    String.valueOf(book.getId()),
                    escapeCSVValue(book.getTitulo()),
                    escapeCSVValue(book.getAutor()),
                    escapeCSVValue(book.getIsbn()),
                    String.valueOf(book.getAnioPublicacion()),
                    book.isDisponible() ? "Yes" : "No",
                    book.getFechaAlta() != null ? book.getFechaAlta().format(DATETIME_FORMATTER) : "",
                    book.isDisponible() ? "Available" : "On Loan"
                };
                writeCSVRow(writer, rowData);
            }
        }
    }
    
    /**
     * Generates a CSV report of overdue loans
     * @param outputFile The file where the CSV will be saved
     * @throws SQLException If there's an error accessing the database
     * @throws IOException If there's an error writing the file
     */
    public void generateOverdueLoansReport(File outputFile) throws SQLException, IOException {
        List<Loan> overdueLoans = loanDAO.listarPrestamosVencidos();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            // Write CSV header
            writeCSVHeader(writer, new String[]{
                "Loan ID", "Book Title", "Book Author", "Book ISBN",
                "Partner Name", "Partner Email", "Partner Phone",
                "Loan Date", "Days Overdue", "Status"
            });
            
            // Write overdue loan data
            for (Loan loan : overdueLoans) {
                long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(
                    loan.getFechaPrestamo().plusDays(15), // 15-day loan period
                    java.time.LocalDate.now()
                );
                
                String[] rowData = {
                    String.valueOf(loan.getId()),
                    escapeCSVValue(loan.getLibro().getTitulo()),
                    escapeCSVValue(loan.getLibro().getAutor()),
                    escapeCSVValue(loan.getLibro().getIsbn()),
                    escapeCSVValue(loan.getSocio().getNombre()),
                    escapeCSVValue(loan.getSocio().getEmail()),
                    escapeCSVValue(loan.getSocio().getTelefono()),
                    loan.getFechaPrestamo().format(DATE_FORMATTER),
                    String.valueOf(daysOverdue),
                    "OVERDUE"
                };
                writeCSVRow(writer, rowData);
            }
        }
    }
    
    /**
     * Generates a comprehensive loans report (all loans with status)
     * @param outputFile The file where the CSV will be saved
     * @throws SQLException If there's an error accessing the database
     * @throws IOException If there's an error writing the file
     */
    public void generateAllLoansReport(File outputFile) throws SQLException, IOException {
        List<Loan> allLoans = loanDAO.listar();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            // Write CSV header
            writeCSVHeader(writer, new String[]{
                "Loan ID", "Book Title", "Book Author", "Partner Name", 
                "Partner Email", "Loan Date", "Return Date", "Status", 
                "Days Since Loan", "Is Overdue"
            });
            
            // Write loan data
            for (Loan loan : allLoans) {
                long daysSinceLoan = java.time.temporal.ChronoUnit.DAYS.between(
                    loan.getFechaPrestamo(), java.time.LocalDate.now()
                );
                
                String status;
                String isOverdue = "No";
                
                if (loan.isDevuelto()) {
                    status = "Returned";
                } else if (daysSinceLoan > 15) {
                    status = "Overdue";
                    isOverdue = "Yes";
                } else {
                    status = "Active";
                }
                
                String[] rowData = {
                    String.valueOf(loan.getId()),
                    escapeCSVValue(loan.getLibro().getTitulo()),
                    escapeCSVValue(loan.getLibro().getAutor()),
                    escapeCSVValue(loan.getSocio().getNombre()),
                    escapeCSVValue(loan.getSocio().getEmail()),
                    loan.getFechaPrestamo().format(DATE_FORMATTER),
                    loan.getFechaDevolucion() != null ? loan.getFechaDevolucion().format(DATE_FORMATTER) : "",
                    status,
                    String.valueOf(daysSinceLoan),
                    isOverdue
                };
                writeCSVRow(writer, rowData);
            }
        }
    }
    
    /**
     * Generates a partners report
     * @param outputFile The file where the CSV will be saved
     * @throws SQLException If there's an error accessing the database
     * @throws IOException If there's an error writing the file
     */
    public void generatePartnersReport(File outputFile) throws SQLException, IOException {
        List<com.codeup.novabook.Models.Entity.Partner> partners = 
            new com.codeup.novabook.Models.DAO.PartnerDAO().listar();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            // Write CSV header
            writeCSVHeader(writer, new String[]{
                "Partner ID", "Name", "Email", "Phone", "Address",
                "Active", "Registration Date", "Status"
            });
            
            // Write partner data
            for (com.codeup.novabook.Models.Entity.Partner partner : partners) {
                String[] rowData = {
                    String.valueOf(partner.getId()),
                    escapeCSVValue(partner.getNombre()),
                    escapeCSVValue(partner.getEmail()),
                    escapeCSVValue(partner.getTelefono()),
                    escapeCSVValue(partner.getDireccion()),
                    partner.isActivo() ? "Yes" : "No",
                    partner.getFechaRegistro() != null ? partner.getFechaRegistro().format(DATETIME_FORMATTER) : "",
                    partner.isActivo() ? "Active" : "Inactive"
                };
                writeCSVRow(writer, rowData);
            }
        }
    }
    
    /**
     * Writes the CSV header row
     */
    private void writeCSVHeader(BufferedWriter writer, String[] headers) throws IOException {
        writeCSVRow(writer, headers);
    }
    
    /**
     * Writes a CSV row
     */
    private void writeCSVRow(BufferedWriter writer, String[] values) throws IOException {
        StringBuilder line = new StringBuilder();
        
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                line.append(CSV_SEPARATOR);
            }
            line.append(values[i]);
        }
        
        writer.write(line.toString());
        writer.newLine();
    }
    
    /**
     * Escapes CSV values to handle commas, quotes, and newlines
     */
    private String escapeCSVValue(String value) {
        if (value == null) {
            return "";
        }
        
        // If the value contains comma, quote, or newline, wrap it in quotes
        if (value.contains(CSV_SEPARATOR) || value.contains(CSV_QUOTE) || value.contains("\n") || value.contains("\r")) {
            // Escape existing quotes by doubling them
            String escaped = value.replace(CSV_QUOTE, CSV_QUOTE + CSV_QUOTE);
            return CSV_QUOTE + escaped + CSV_QUOTE;
        }
        
        return value;
    }
    
    /**
     * Gets a timestamped filename for reports
     */
    public static String getTimestampedFilename(String baseFilename) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return baseFilename.replace(".csv", "_" + timestamp + ".csv");
    }
    
    /**
     * Gets the default reports directory (creates if doesn't exist)
     */
    public static File getDefaultReportsDirectory() {
        String userHome = System.getProperty("user.home");
        File reportsDir = new File(userHome, "NovaBook_Reports");
        
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }
        
        return reportsDir;
    }
}