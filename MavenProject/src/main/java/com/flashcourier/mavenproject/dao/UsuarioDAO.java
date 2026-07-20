/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.dao;

import com.flashcourier.mavenproject.database.ConexionDB;
import com.flashcourier.mavenproject.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** No usa Stored Procedure porque es una consulta simple de autenticacion.
 *
 * @author JoseLSR
 */
public class UsuarioDAO {

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
}
