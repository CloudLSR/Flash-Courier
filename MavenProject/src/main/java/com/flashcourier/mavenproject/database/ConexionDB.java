/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Patron Singleton: garantiza una unica instancia de conexion a la base de datos.
 * Puerto por defecto de MySQL: 3306.
 *
 * @author JoseLSR
 */
public class ConexionDB {

    private static ConexionDB instancia;

    private static final String URL = "jdbc:mysql://localhost:3306/flashcourier?useSSL=false&serverTimezone=America/Lima";
    private static final String USUARIO = "root";
    private static final String PASSWORD = ""; // Cambiar aqui si tu MySQL tiene contrasena de root

    private ConexionDB() {
    }

    public static ConexionDB getInstancia() {
        if (instancia == null) {
            instancia = new ConexionDB();
        }
        return instancia;
    }

    public Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}
