/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.dao;

import com.flashcourier.mavenproject.database.ConexionDB;
import com.flashcourier.mavenproject.modelo.Cliente;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

/**
 *
 * @author JoseLSR
 */
public class ClienteDAO {

    /** Registra un cliente (remitente o destinatario) usando sp_registrar_cliente. Devuelve el id generado. */
    public int registrarCliente(Connection con, Cliente cliente) throws SQLException {
        try (CallableStatement cs = con.prepareCall("{call sp_registrar_cliente(?,?,?,?,?)}")) {
            cs.setString(1, cliente.getNombres());
            cs.setString(2, cliente.getDni());
            cs.setString(3, cliente.getTelefono());
            cs.setString(4, cliente.getDireccion());
            cs.registerOutParameter(5, Types.INTEGER);
            cs.execute();
            return cs.getInt(5);
        }
    }

    /** Version standalone (abre y cierra su propia conexion) para usos fuera de una transaccion mayor. */
    public int registrarCliente(Cliente cliente) throws SQLException {
        try (Connection con = ConexionDB.getInstancia().getConexion()) {
            return registrarCliente(con, cliente);
        }
    }
}
