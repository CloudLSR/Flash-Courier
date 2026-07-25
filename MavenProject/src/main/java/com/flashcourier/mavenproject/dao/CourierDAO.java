/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.dao;

import com.flashcourier.mavenproject.database.ConexionDB;
import com.flashcourier.mavenproject.modelo.Courier;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JoseLSR
 */
public class CourierDAO {

    public List<Courier> listarCouriers() throws SQLException {
        List<Courier> lista = new ArrayList<>();
        try (Connection con = ConexionDB.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall("{call sp_listar_couriers()}")) {
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Courier(rs.getInt("id_courier"), rs.getString("nombre"), rs.getString("telefono")));
                }
            }
        }
        return lista;
    }

    /** Registra un nuevo courier (personal de entrega). Usado por Gestion de Personal (Administrador y Supervisor). */
    public Courier registrarCourier(String nombre, String telefono) throws SQLException {
        try (Connection con = ConexionDB.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall("{call sp_registrar_courier(?,?,?)}")) {
            cs.setString(1, nombre);
            cs.setString(2, telefono);
            cs.registerOutParameter(3, Types.INTEGER);
            cs.execute();
            return new Courier(cs.getInt(3), nombre, telefono);
        }
    }

    /** Elimina un courier. Falla con SQLException si tiene envios asociados (restriccion de llave foranea). */
    public void eliminarCourier(int idCourier) throws SQLException {
        try (Connection con = ConexionDB.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall("{call sp_eliminar_courier(?)}")) {
            cs.setInt(1, idCourier);
            cs.execute();
        }
    }
}
