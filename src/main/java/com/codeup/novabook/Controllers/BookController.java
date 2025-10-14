/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.codeup.novabook.Controllers;

/**
 *
 * @author Coder
 */
import com.codeup.novabook.Models.DAO.BookDAO;
import com.codeup.novabook.Models.Entity.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class BookController {
    
    private BookDAO bookDAO;
    
    public BookController() {
        this.bookDAO = new BookDAO();
    }
    
    public ObservableList<Book> getAllBooks() {
        try {
            List<Book> books = bookDAO.listar();
            return FXCollections.observableArrayList(books);
        } catch (SQLException e) {
            showError("Database Error", "Could not load books: " + e.getMessage());
            return FXCollections.observableArrayList();
        }
    }
    
    public boolean addBook(String title, String author, String isbn, int year) {
        try {
            Book book = new Book();
            book.setTitulo(title);
            book.setAutor(author);
            book.setIsbn(isbn);
            book.setAnioPublicacion(year);
            book.setDisponible(true);
            book.setFechaAlta(LocalDateTime.now());
            
            bookDAO.agregar(book);
            showInfo("Success", "Book added successfully");
            return true;
        } catch (SQLException e) {
            showError("Database Error", "Could not add book: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateBook(Book book) {
        try {
            bookDAO.actualizar(book);
            showInfo("Success", "Book updated successfully");
            return true;
        } catch (SQLException e) {
            showError("Database Error", "Could not update book: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteBook(int bookId) {
        try {
            bookDAO.eliminar(bookId);
            showInfo("Success", "Book deleted successfully");
            return true;
        } catch (SQLException e) {
            showError("Database Error", "Could not delete book: " + e.getMessage());
            return false;
        }
    }
    
    public Book getBookById(int id) {
        try {
            return bookDAO.obtenerPorId(id);
        } catch (SQLException e) {
            showError("Database Error", "Could not find book: " + e.getMessage());
            return null;
        }
    }
    
    public boolean isValidBook(String title, String author, String isbn, String yearStr) {
        if (title == null || title.trim().isEmpty()) {
            showError("Validation Error", "Title is required");
            return false;
        }
        if (author == null || author.trim().isEmpty()) {
            showError("Validation Error", "Author is required");
            return false;
        }
        if (isbn == null || isbn.trim().isEmpty()) {
            showError("Validation Error", "ISBN is required");
            return false;
        }
        
        try {
            int year = Integer.parseInt(yearStr);
            if (year < 1000 || year > 2030) {
                showError("Validation Error", "Year must be between 1000 and 2030");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Validation Error", "Year must be a valid number");
            return false;
        }
        
        return true;
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