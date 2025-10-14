/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.codeup.novabook.Models.DAO;

/**
 *
 * @author Coder
 */
import com.codeup.novabook.Models.Connection.ConnectionDB;
import com.codeup.novabook.Models.Entity.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public void agregar(Book libro) throws SQLException {
        String sql = "INSERT INTO Libro (titulo, autor, isbn, anio_publicacion, disponible) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setInt(4, libro.getAnioPublicacion());
            ps.setBoolean(5, libro.isDisponible());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) libro.setId(rs.getInt(1));
        }
    }

    public Book obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM Libro WHERE id = ?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Book(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("isbn"),
                        rs.getInt("anio_publicacion"),
                        rs.getBoolean("disponible"),
                        rs.getTimestamp("fecha_alta").toLocalDateTime()
                );
            }
        }
        return null;
    }

    public List<Book> listar() throws SQLException {
        List<Book> lista = new ArrayList<>();
        String sql = "SELECT * FROM Libro";
        try (Connection conn = ConnectionDB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Book libro = new Book(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("isbn"),
                        rs.getInt("anio_publicacion"),
                        rs.getBoolean("disponible"),
                        rs.getTimestamp("fecha_alta").toLocalDateTime()
                );
                lista.add(libro);
            }
        }
        return lista;
    }

    public void actualizar(Book libro) throws SQLException {
        String sql = "UPDATE Libro SET titulo=?, autor=?, isbn=?, anio_publicacion=?, disponible=? WHERE id=?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setInt(4, libro.getAnioPublicacion());
            ps.setBoolean(5, libro.isDisponible());
            ps.setInt(6, libro.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM Libro WHERE id = ?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    
    public void insertar(Book libro) throws SQLException {
        agregar(libro);
    }
    
    public boolean existsByISBN(String isbn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Libro WHERE isbn = ?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, isbn);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}

