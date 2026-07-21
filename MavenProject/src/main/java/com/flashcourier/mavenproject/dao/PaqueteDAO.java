/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.dao;

import com.flashcourier.mavenproject.database.ConexionDB;
import com.flashcourier.mavenproject.modelo.Paquete;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

/**
 *
 * @author JoseLSR
 */
public class PaqueteDAO {

    public int registrarPaquete(Connection con, Paquete paquete) throws SQLException {
        try (CallableStatement cs = con.prepareCall("{call sp_registrar_paquete(?,?,?,?)}")) {
            cs.setDouble(1, paquete.getPeso());
            cs.setString(2, paquete.getDimensiones());
            cs.setString(3, paquete.getDescripcion());
            cs.registerOutParameter(4, Types.INTEGER);
            cs.execute();
            return cs.getInt(4);
        }
    }

    public int registrarPaquete(Paquete paquete) throws SQLException {
        try (Connection con = ConexionDB.getInstancia().getConexion()) {
            return registrarPaquete(con, paquete);
        }
    }
}
