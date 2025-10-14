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
import com.codeup.novabook.Models.Entity.Users;
import com.codeup.novabook.Models.Entity.Rol;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserDAO {

    private final RolDAO rolDAO;

    public UserDAO() {
        this.rolDAO = new RolDAO();
    }

    public void agregar(Users user) throws SQLException {
        String sql = "INSERT INTO Usuario (nombre, email, password, activo) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getNombre());
            ps.setString(2, user.getEmail());
            ps.setString(3, encryptPassword(user.getPassword()));
            ps.setBoolean(4, user.isActivo());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getInt(1));
            }
        }
    }

    public Users obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM Usuario WHERE id = ?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Users user = new Users(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("activo"),
                        rs.getTimestamp("fecha_creacion").toLocalDateTime()
                );
                
                
                user.setRoles(obtenerRolesDelUsuario(id));
                return user;
            }
        }
        return null;
    }

    public List<Users> listar() throws SQLException {
        List<Users> lista = new ArrayList<>();
        String sql = "SELECT * FROM Usuario";
        try (Connection conn = ConnectionDB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Users user = new Users(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("activo"),
                        rs.getTimestamp("fecha_creacion").toLocalDateTime()
                );
                
                // Cargar roles del usuario
                user.setRoles(obtenerRolesDelUsuario(user.getId()));
                lista.add(user);
            }
        }
        return lista;
    }

    public void actualizar(Users user) throws SQLException {
        String sql = "UPDATE Usuario SET nombre=?, email=?, activo=? WHERE id=?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getNombre());
            ps.setString(2, user.getEmail());
            ps.setBoolean(3, user.isActivo());
            ps.setInt(4, user.getId());
            ps.executeUpdate();
        }
    }

    public void actualizarPassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE Usuario SET password=? WHERE id=?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, encryptPassword(newPassword));
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        // Primero eliminar relaciones de roles
        eliminarRolesDelUsuario(id);
        
        // Luego eliminar usuario
        String sql = "DELETE FROM Usuario WHERE id = ?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public Users autenticar(String email, String password) throws SQLException {
        String sql = "SELECT * FROM Usuario WHERE email = ? AND password = ? AND activo = true";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, encryptPassword(password));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Users user = new Users(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("activo"),
                        rs.getTimestamp("fecha_creacion").toLocalDateTime()
                );
                
                // Cargar roles del usuario
                user.setRoles(obtenerRolesDelUsuario(user.getId()));
                return user;
            }
        }
        return null;
    }

    public Users buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM Usuario WHERE email = ?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Users user = new Users(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("activo"),
                        rs.getTimestamp("fecha_creacion").toLocalDateTime()
                );
                
                // Cargar roles del usuario
                user.setRoles(obtenerRolesDelUsuario(user.getId()));
                return user;
            }
        }
        return null;
    }

    // Métodos para gestionar roles de usuarios
    public void asignarRolAUsuario(int userId, int rolId) throws SQLException {
        String sql = "INSERT INTO Usuario_Rol (usuario_id, rol_id) VALUES (?, ?)";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, rolId);
            ps.executeUpdate();
        }
    }

    public void eliminarRolDeUsuario(int userId, int rolId) throws SQLException {
        String sql = "DELETE FROM Usuario_Rol WHERE usuario_id = ? AND rol_id = ?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, rolId);
            ps.executeUpdate();
        }
    }

    public void eliminarRolesDelUsuario(int userId) throws SQLException {
        String sql = "DELETE FROM Usuario_Rol WHERE usuario_id = ?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    public List<Rol> obtenerRolesDelUsuario(int userId) throws SQLException {
        List<Rol> roles = new ArrayList<>();
        String sql = "SELECT r.* FROM Rol r INNER JOIN Usuario_Rol ur ON r.id = ur.rol_id WHERE ur.usuario_id = ?";
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Rol rol = new Rol(
                        rs.getInt("id"),
                        rs.getString("nombre")
                );
                roles.add(rol);
            }
        }
        return roles;
    }

    // Método para encriptar contraseñas
    private String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar la contraseña", e);
        }
    }
}