package com.codeup.novabook.Models.DAO;

import com.codeup.novabook.Models.Connection.ConnectionDB;
import com.codeup.novabook.Models.Entity.Loan;
import com.codeup.novabook.Models.Entity.Book;
import com.codeup.novabook.Models.Entity.Partner;
import com.codeup.novabook.Utils.AppLogger;
import com.codeup.novabook.Utils.ConfigManager;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Data Access Object for Loan entity
 * Handles all database operations related to loans with logging and transaction support
 * 
 * @author Luis Alfredo - Clan Cienaga
 */

public class LoanDAO {
    
    private static final Logger LOGGER = AppLogger.getLogger(LoanDAO.class);
    private final BookDAO bookDAO;
    private final PartnerDAO partnerDAO;
    private final ConfigManager config;

    public LoanDAO() {
        this.bookDAO = new BookDAO();
        this.partnerDAO = new PartnerDAO();
        this.config = ConfigManager.getInstance();
    }

    /**
     * Create new loan with transaction support
     * This is a critical operation that requires atomicity
     */
    public void agregar(Loan prestamo) throws SQLException {
        String sql = "INSERT INTO Prestamo (libro_id, socio_id, fecha_prestamo, fecha_devolucion, devuelto) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        
        try {
            conn = ConnectionDB.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            LOGGER.info("Creating new loan for book ID: " + prestamo.getLibro().getId() + ", partner ID: " + prestamo.getSocio().getId());
            
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, prestamo.getLibro().getId());
                ps.setInt(2, prestamo.getSocio().getId());
                ps.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
                if (prestamo.getFechaDevolucion() != null) {
                    ps.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
                } else {
                    ps.setNull(4, Types.DATE);
                }
                ps.setBoolean(5, prestamo.isDevuelto());
                
                int rowsAffected = ps.executeUpdate();
                
                if (rowsAffected > 0) {
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        prestamo.setId(rs.getInt(1));
                    }
                    
                    // Mark book as unavailable - this should be done by database trigger
                    // But we keep it for redundancy
                    Book libro = prestamo.getLibro();
                    libro.setDisponible(false);
                    bookDAO.actualizar(libro);
                    
                    conn.commit(); // Commit transaction
                    
                    // Log successful operation
                    AppLogger.logBusinessOperation("LOAN_CREATED", 
                        "Loan ID: " + prestamo.getId(), 
                        "Book: " + libro.getTitulo() + ", Partner: " + prestamo.getSocio().getNombre());
                    
                    AppLogger.logDatabaseOperation("INSERT", "Prestamo", true, 
                        "New loan created with ID: " + prestamo.getId());
                    
                    LOGGER.info("Loan created successfully with ID: " + prestamo.getId());
                    
                } else {
                    conn.rollback();
                    throw new SQLException("Failed to create loan - no rows affected");
                }
            }
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    LOGGER.warning("Transaction rolled back due to error: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    LOGGER.severe("Error during rollback: " + rollbackEx.getMessage());
                    AppLogger.logError("LoanDAO", "Rollback failed", rollbackEx);
                }
            }
            
            AppLogger.logDatabaseOperation("INSERT", "Prestamo", false, e.getMessage());
            AppLogger.logError("LoanDAO", "Failed to create loan", e);
            throw e;
            
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    ConnectionDB.closeConnection(conn);
                } catch (SQLException e) {
                    LOGGER.warning("Error resetting connection: " + e.getMessage());
                }
            }
        }
    }

    public Loan obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM Prestamo WHERE id = ?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Book libro = bookDAO.obtenerPorId(rs.getInt("libro_id"));
                Partner socio = partnerDAO.obtenerPorId(rs.getInt("socio_id"));

                LocalDate fechaDevolucion = null;
                Date sqlDate = rs.getDate("fecha_devolucion");
                if (sqlDate != null) {
                    fechaDevolucion = sqlDate.toLocalDate();
                }

                return new Loan(
                        rs.getInt("id"),
                        libro,
                        socio,
                        rs.getDate("fecha_prestamo").toLocalDate(),
                        fechaDevolucion,
                        rs.getBoolean("devuelto")
                );
            }
        }
        return null;
    }

    public List<Loan> listar() throws SQLException {
        List<Loan> lista = new ArrayList<>();
        String sql = "SELECT * FROM Prestamo ORDER BY fecha_prestamo DESC";
        try (Connection conn = ConnectionDB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Book libro = bookDAO.obtenerPorId(rs.getInt("libro_id"));
                Partner socio = partnerDAO.obtenerPorId(rs.getInt("socio_id"));

                LocalDate fechaDevolucion = null;
                Date sqlDate = rs.getDate("fecha_devolucion");
                if (sqlDate != null) {
                    fechaDevolucion = sqlDate.toLocalDate();
                }

                Loan prestamo = new Loan(
                        rs.getInt("id"),
                        libro,
                        socio,
                        rs.getDate("fecha_prestamo").toLocalDate(),
                        fechaDevolucion,
                        rs.getBoolean("devuelto")
                );
                lista.add(prestamo);
            }
        }
        return lista;
    }

    public List<Loan> listarPrestamosActivos() throws SQLException {
        List<Loan> lista = new ArrayList<>();
        String sql = "SELECT * FROM Prestamo WHERE devuelto = false ORDER BY fecha_prestamo DESC";
        try (Connection conn = ConnectionDB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Book libro = bookDAO.obtenerPorId(rs.getInt("libro_id"));
                Partner socio = partnerDAO.obtenerPorId(rs.getInt("socio_id"));

                Loan prestamo = new Loan(
                        rs.getInt("id"),
                        libro,
                        socio,
                        rs.getDate("fecha_prestamo").toLocalDate(),
                        null, // No hay fecha de devolución aún
                        rs.getBoolean("devuelto")
                );
                lista.add(prestamo);
            }
        }
        return lista;
    }

    public List<Loan> listarPrestamosPorSocio(int socioId) throws SQLException {
        List<Loan> lista = new ArrayList<>();
        String sql = "SELECT * FROM Prestamo WHERE socio_id = ? ORDER BY fecha_prestamo DESC";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, socioId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Book libro = bookDAO.obtenerPorId(rs.getInt("libro_id"));
                Partner socio = partnerDAO.obtenerPorId(rs.getInt("socio_id"));

                LocalDate fechaDevolucion = null;
                Date sqlDate = rs.getDate("fecha_devolucion");
                if (sqlDate != null) {
                    fechaDevolucion = sqlDate.toLocalDate();
                }

                Loan prestamo = new Loan(
                        rs.getInt("id"),
                        libro,
                        socio,
                        rs.getDate("fecha_prestamo").toLocalDate(),
                        fechaDevolucion,
                        rs.getBoolean("devuelto")
                );
                lista.add(prestamo);
            }
        }
        return lista;
    }

    public List<Loan> listarPrestamosVencidos() throws SQLException {
        List<Loan> lista = new ArrayList<>();
        String sql = "SELECT * FROM Prestamo WHERE devuelto = false AND fecha_prestamo < DATE_SUB(CURDATE(), INTERVAL 15 DAY) ORDER BY fecha_prestamo ASC";
        try (Connection conn = ConnectionDB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Book libro = bookDAO.obtenerPorId(rs.getInt("libro_id"));
                Partner socio = partnerDAO.obtenerPorId(rs.getInt("socio_id"));

                Loan prestamo = new Loan(
                        rs.getInt("id"),
                        libro,
                        socio,
                        rs.getDate("fecha_prestamo").toLocalDate(),
                        null,
                        rs.getBoolean("devuelto")
                );
                lista.add(prestamo);
            }
        }
        return lista;
    }

    public void marcarComoDevuelto(int prestamoId) throws SQLException {
        String sql = "UPDATE Prestamo SET devuelto = true, fecha_devolucion = CURDATE() WHERE id = ?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, prestamoId);
            ps.executeUpdate();

            // Obtener el préstamo para marcar el libro como disponible
            Loan prestamo = obtenerPorId(prestamoId);
            if (prestamo != null) {
                Book libro = prestamo.getLibro();
                libro.setDisponible(true);
                bookDAO.actualizar(libro);
            }
        }
    }

    public void actualizar(Loan prestamo) throws SQLException {
        String sql = "UPDATE Prestamo SET libro_id=?, socio_id=?, fecha_prestamo=?, fecha_devolucion=?, devuelto=? WHERE id=?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, prestamo.getLibro().getId());
            ps.setInt(2, prestamo.getSocio().getId());
            ps.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            if (prestamo.getFechaDevolucion() != null) {
                ps.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
            } else {
                ps.setNull(4, Types.DATE);
            }
            ps.setBoolean(5, prestamo.isDevuelto());
            ps.setInt(6, prestamo.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        // Obtener el préstamo antes de eliminarlo para liberar el libro
        Loan prestamo = obtenerPorId(id);
        
        String sql = "DELETE FROM Prestamo WHERE id = ?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ps.executeUpdate();
            
            // Marcar el libro como disponible si no estaba devuelto
            if (prestamo != null && !prestamo.isDevuelto()) {
                Book libro = prestamo.getLibro();
                libro.setDisponible(true);
                bookDAO.actualizar(libro);
            }
        }
    }

    public boolean existePrestamoActivoParaLibro(int libroId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Prestamo WHERE libro_id = ? AND devuelto = false";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, libroId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}