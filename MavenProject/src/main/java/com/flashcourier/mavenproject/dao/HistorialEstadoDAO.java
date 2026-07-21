/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.dao;

import com.flashcourier.mavenproject.modelo.HistorialEstado;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JoseLSR
 */
public class HistorialEstadoDAO {

    public void registrarHistorial(Connection con, int idEnvio, String estado, String observacion) throws SQLException {
        try (CallableStatement cs = con.prepareCall("{call sp_registrar_historial(?,?,?)}")) {
            cs.setInt(1, idEnvio);
            cs.setString(2, estado);
            cs.setString(3, observacion);
            cs.execute();
        }
    }

    public List<HistorialEstado> listarPorEnvio(Connection con, int idEnvio) throws SQLException {
        List<HistorialEstado> lista = new ArrayList<>();
        try (CallableStatement cs = con.prepareCall("{call sp_consultar_historial_por_envio(?)}")) {
            cs.setInt(1, idEnvio);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    HistorialEstado h = new HistorialEstado(
                        rs.getString("estado"),
                        rs.getTimestamp("fecha_hora"),
                        rs.getString("observacion")
                    );
                    h.setIdEnvio(idEnvio);
                    lista.add(h);
                }
            }
        }
        return lista;
    }
}
