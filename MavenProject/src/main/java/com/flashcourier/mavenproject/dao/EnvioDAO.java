/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.dao;

import com.flashcourier.mavenproject.database.ConexionDB;
import com.flashcourier.mavenproject.modelo.Envio;
import com.flashcourier.mavenproject.modelo.ResumenEstadisticas;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author JoseLSR
 */
public class EnvioDAO {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final PaqueteDAO paqueteDAO = new PaqueteDAO();
    private final HistorialEstadoDAO historialDAO = new HistorialEstadoDAO();

    /**
     * Registra el envio completo (remitente, destinatario, paquete, envio e historial inicial)
     * como UNA sola transaccion: si algo falla, se hace rollback de todo para no dejar
     * datos parciales o inconsistentes en la base de datos.
     */
    public Envio registrarEnvioCompleto(com.flashcourier.mavenproject.modelo.Cliente remitente, com.flashcourier.mavenproject.modelo.Cliente destinatario,
                                         com.flashcourier.mavenproject.modelo.Paquete paquete, String direccionDestino, double costo,
                                         String codigoTracking) throws SQLException {
        Connection con = null;
        try {
            con = ConexionDB.getInstancia().getConexion();
            con.setAutoCommit(false);

            int idRemitente = clienteDAO.registrarCliente(con, remitente);
            int idDestinatario = clienteDAO.registrarCliente(con, destinatario);
            int idPaquete = paqueteDAO.registrarPaquete(con, paquete);

            int idEnvio;
            try (CallableStatement cs = con.prepareCall("{call sp_registrar_envio(?,?,?,?,?,?,?)}")) {
                cs.setString(1, codigoTracking);
                cs.setString(2, direccionDestino);
                cs.setDouble(3, costo);
                cs.setInt(4, idRemitente);
                cs.setInt(5, idDestinatario);
                cs.setInt(6, idPaquete);
                cs.registerOutParameter(7, Types.INTEGER);
                cs.execute();
                idEnvio = cs.getInt(7);
            }

            historialDAO.registrarHistorial(con, idEnvio, "Registrado", "Envio registrado en agencia");

            con.commit();

            Envio envio = new Envio();
            envio.setIdEnvio(idEnvio);
            envio.setCodigoTracking(codigoTracking);
            envio.setEstado("Registrado");
            envio.setCosto(costo);
            envio.setDireccionDestino(direccionDestino);
            return envio;

        } catch (SQLException ex) {
            if (con != null) con.rollback();
            throw ex;
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    /** Consulta un envio por su codigo de tracking (para la pantalla de tracking del cliente). */
    public Envio consultarPorTracking(String codigoTracking) throws SQLException {
        try (Connection con = ConexionDB.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall("{call sp_consultar_envio_por_tracking(?)}")) {
            cs.setString(1, codigoTracking);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    Envio e = new Envio();
                    e.setIdEnvio(rs.getInt("id_envio"));
                    e.setCodigoTracking(rs.getString("codigo_tracking"));
                    e.setFechaRegistro(rs.getTimestamp("fecha_registro"));
                    e.setEstado(rs.getString("estado"));
                    e.setDireccionDestino(rs.getString("direccion_destino"));
                    e.setCosto(rs.getDouble("costo"));
                    e.setNombreRemitente(rs.getString("remitente"));
                    e.setNombreDestinatario(rs.getString("destinatario"));
                    e.setPeso(rs.getDouble("peso"));
                    e.setDimensiones(rs.getString("dimensiones"));
                    int idCourier = rs.getInt("id_courier");
                    e.setIdCourier(rs.wasNull() ? null : idCourier);
                    return e;
                }
            }
        }
        return null;
    }

    /** Actualiza el estado de un envio y registra el cambio en el historial (transaccional). */
    public void actualizarEstado(int idEnvio, String nuevoEstado, String observacion) throws SQLException {
        Connection con = null;
        try {
            con = ConexionDB.getInstancia().getConexion();
            con.setAutoCommit(false);

            try (CallableStatement cs = con.prepareCall("{call sp_actualizar_estado_envio(?,?)}")) {
                cs.setInt(1, idEnvio);
                cs.setString(2, nuevoEstado);
                cs.execute();
            }
            historialDAO.registrarHistorial(con, idEnvio, nuevoEstado, observacion);

            con.commit();
        } catch (SQLException ex) {
            if (con != null) con.rollback();
            throw ex;
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    /** Confirma la entrega: asigna courier, marca "Entregado" y deja constancia en el historial. */
    public void confirmarEntrega(int idEnvio, int idCourier, String observacion) throws SQLException {
        Connection con = null;
        try {
            con = ConexionDB.getInstancia().getConexion();
            con.setAutoCommit(false);

            try (CallableStatement cs = con.prepareCall("{call sp_confirmar_entrega(?,?)}")) {
                cs.setInt(1, idEnvio);
                cs.setInt(2, idCourier);
                cs.execute();
            }
            historialDAO.registrarHistorial(con, idEnvio, "Entregado", observacion);

            con.commit();
        } catch (SQLException ex) {
            if (con != null) con.rollback();
            throw ex;
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    /** Busca un envio por su ID interno (usado por las pantallas de actualizar estado / confirmar entrega). */
    public Envio buscarPorId(int idEnvio) throws SQLException {
        String sql = "SELECT e.*, r.nombres AS nombre_remitente, d.nombres AS nombre_destinatario, "
                   + "p.peso, p.dimensiones FROM envio e "
                   + "JOIN cliente r ON e.id_remitente = r.id_cliente "
                   + "JOIN cliente d ON e.id_destinatario = d.id_cliente "
                   + "JOIN paquete p ON e.id_paquete = p.id_paquete "
                   + "WHERE e.id_envio = ?";
        try (Connection con = ConexionDB.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEnvio);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Envio e = new Envio();
                    e.setIdEnvio(rs.getInt("id_envio"));
                    e.setCodigoTracking(rs.getString("codigo_tracking"));
                    e.setEstado(rs.getString("estado"));
                    e.setDireccionDestino(rs.getString("direccion_destino"));
                    e.setCosto(rs.getDouble("costo"));
                    e.setNombreRemitente(rs.getString("nombre_remitente"));
                    e.setNombreDestinatario(rs.getString("nombre_destinatario"));
                    e.setPeso(rs.getDouble("peso"));
                    e.setDimensiones(rs.getString("dimensiones"));
                    return e;
                }
            }
        }
        return null;
    }

    /** Cuenta los envios agrupados por estado. Usado por el dashboard de estadisticas del menu. */
    public Map<String, Integer> contarPorEstado() throws SQLException {
        Map<String, Integer> mapa = new LinkedHashMap<>();
        try (Connection con = ConexionDB.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall("{call sp_contar_envios_por_estado()}");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                mapa.put(rs.getString("estado"), rs.getInt("cantidad"));
            }
        }
        return mapa;
    }

    /**
     * Resumen general para la pantalla de Estadisticas: total de pedidos,
     * ingresos estimados (excluye los pedidos cancelados) y total de couriers.
     */
    public ResumenEstadisticas obtenerResumen() throws SQLException {
        try (Connection con = ConexionDB.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall("{call sp_estadisticas_generales()}");
             ResultSet rs = cs.executeQuery()) {
            if (rs.next()) {
                return new ResumenEstadisticas(
                        rs.getInt("total_envios"),
                        rs.getDouble("ingresos_estimados"),
                        rs.getInt("total_couriers"));
            }
        }
        return new ResumenEstadisticas(0, 0, 0);
    }

    /** Lista todos los envios con el nombre del courier asignado (si tiene), para la vista de lista completa. */
    public List<Envio> listarTodos() throws SQLException {
        List<Envio> lista = new ArrayList<>();
        try (Connection con = ConexionDB.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall("{call sp_listar_envios()}");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                Envio e = new Envio();
                e.setIdEnvio(rs.getInt("id_envio"));
                e.setCodigoTracking(rs.getString("codigo_tracking"));
                e.setFechaRegistro(rs.getTimestamp("fecha_registro"));
                e.setEstado(rs.getString("estado"));
                e.setDireccionDestino(rs.getString("direccion_destino"));
                e.setCosto(rs.getDouble("costo"));
                e.setNombreRemitente(rs.getString("nombre_remitente"));
                e.setNombreDestinatario(rs.getString("nombre_destinatario"));
                e.setPeso(rs.getDouble("peso"));
                e.setDimensiones(rs.getString("dimensiones"));
                e.setNombreCourier(rs.getString("nombre_courier"));
                lista.add(e);
            }
        }
        return lista;
    }
}
