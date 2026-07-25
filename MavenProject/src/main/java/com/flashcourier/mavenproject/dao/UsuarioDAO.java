/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.dao;

import com.flashcourier.mavenproject.database.ConexionDB;
import com.flashcourier.mavenproject.modelo.Usuario;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * @author JoseLSR
 */
public class UsuarioDAO {

    /** No usa Stored Procedure porque es una consulta simple de autenticacion. */
    public Usuario validarCredenciales(String correo, String contrasena) throws SQLException {
        String sql = "SELECT id_usuario, nombre, correo, rol FROM usuario WHERE correo = ? AND contrasena = ?";
        try (Connection con = ConexionDB.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);
            ps.setString(2, contrasena);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(rs.getInt("id_usuario"), rs.getString("nombre"),
                            rs.getString("correo"), rs.getString("rol"));
                }
            }
        }
        return null;
    }

    /** Lista el personal que usa la app (Recepcionista, Supervisor, Administracion). Pantalla Gestion de Personal. */
    public List<Usuario> listarUsuarios() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        try (Connection con = ConexionDB.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall("{call sp_listar_usuarios()}");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                lista.add(new Usuario(rs.getInt("id_usuario"), rs.getString("nombre"),
                        rs.getString("correo"), rs.getString("rol")));
            }
        }
        return lista;
    }

    /** Registra un nuevo usuario de la app (Recepcionista o Supervisor). Pantalla Gestion de Personal (solo admin). */
    public Usuario registrarUsuario(String nombre, String correo, String contrasena, String rol) throws SQLException {
        try (Connection con = ConexionDB.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall("{call sp_registrar_usuario(?,?,?,?,?)}")) {
            cs.setString(1, nombre);
            cs.setString(2, correo);
            cs.setString(3, contrasena);
            cs.setString(4, rol);
            cs.registerOutParameter(5, Types.INTEGER);
            cs.execute();
            return new Usuario(cs.getInt(5), nombre, correo, rol);
        }
    }

    /**
     * Elimina un usuario de la app. La cuenta con rol Administracion nunca se borra
     * (el procedimiento almacenado lo bloquea a nivel de base de datos como respaldo).
     */
    public void eliminarUsuario(int idUsuario) throws SQLException {
        try (Connection con = ConexionDB.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall("{call sp_eliminar_usuario(?)}")) {
            cs.setInt(1, idUsuario);
            cs.execute();
        }
    }
}
