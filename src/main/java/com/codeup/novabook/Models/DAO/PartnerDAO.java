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
import com.codeup.novabook.Models.Entity.Partner;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartnerDAO {

    public void agregar(Partner partner) throws SQLException {
        String sql = "INSERT INTO Socio (nombre, direccion, telefono, email, activo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, partner.getNombre());
            ps.setString(2, partner.getDireccion());
            ps.setString(3, partner.getTelefono());
            ps.setString(4, partner.getEmail());
            ps.setBoolean(5, partner.isActivo());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) partner.setId(rs.getInt(1));
        }
    }

    public Partner obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM Socio WHERE id = ?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Partner(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("direccion"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getBoolean("activo"),
                        rs.getTimestamp("fecha_registro").toLocalDateTime()
                );
            }
        }
        return null;
    }

    public List<Partner> listar() throws SQLException {
        List<Partner> lista = new ArrayList<>();
        String sql = "SELECT * FROM Socio";
        try (Connection conn = ConnectionDB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Partner partner = new Partner(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("direccion"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getBoolean("activo"),
                        rs.getTimestamp("fecha_registro").toLocalDateTime()
                );
                lista.add(partner);
            }
        }
        return lista;
    }

    public List<Partner> listarActivos() throws SQLException {
        List<Partner> lista = new ArrayList<>();
        String sql = "SELECT * FROM Socio WHERE activo = true";
        try (Connection conn = ConnectionDB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Partner partner = new Partner(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("direccion"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getBoolean("activo"),
                        rs.getTimestamp("fecha_registro").toLocalDateTime()
                );
                lista.add(partner);
            }
        }
        return lista;
    }

    public void actualizar(Partner partner) throws SQLException {
        String sql = "UPDATE Socio SET nombre=?, direccion=?, telefono=?, email=?, activo=? WHERE id=?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, partner.getNombre());
            ps.setString(2, partner.getDireccion());
            ps.setString(3, partner.getTelefono());
            ps.setString(4, partner.getEmail());
            ps.setBoolean(5, partner.isActivo());
            ps.setInt(6, partner.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM Socio WHERE id = ?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public Partner buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM Socio WHERE email = ?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Partner(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("direccion"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getBoolean("activo"),
                        rs.getTimestamp("fecha_registro").toLocalDateTime()
                );
            }
        }
        return null;
    }
}