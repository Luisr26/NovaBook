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
import com.codeup.novabook.Models.Entity.Rol;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RolDAO {

    public void agregar(Rol rol) throws SQLException {
        String sql = "INSERT INTO Rol (nombre) VALUES (?)";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, rol.getNombre());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) rol.setId(rs.getInt(1));
        }
    }

    public Rol obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM Rol WHERE id = ?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Rol(
                        rs.getInt("id"),
                        rs.getString("nombre")
                );
            }
        }
        return null;
    }

    public List<Rol> listar() throws SQLException {
        List<Rol> lista = new ArrayList<>();
        String sql = "SELECT * FROM Rol";
        try (Connection conn = ConnectionDB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Rol rol = new Rol(
                        rs.getInt("id"),
                        rs.getString("nombre")
                );
                lista.add(rol);
            }
        }
        return lista;
    }

    public void actualizar(Rol rol) throws SQLException {
        String sql = "UPDATE Rol SET nombre=? WHERE id=?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rol.getNombre());
            ps.setInt(2, rol.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM Rol WHERE id = ?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public Rol buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT * FROM Rol WHERE nombre = ?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Rol(
                        rs.getInt("id"),
                        rs.getString("nombre")
                );
            }
        }
        return null;
    }
}