/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.controlador;

import com.flashcourier.mavenproject.dao.EnvioDAO;
import com.flashcourier.mavenproject.modelo.Cliente;
import com.flashcourier.mavenproject.modelo.Envio;
import com.flashcourier.mavenproject.modelo.HistorialEstado;
import com.flashcourier.mavenproject.modelo.Paquete;
import com.flashcourier.mavenproject.modelo.ResumenEstadisticas;
import com.flashcourier.mavenproject.modelo.state.EstadoEnvio;
import com.flashcourier.mavenproject.modelo.state.EstadoEnvioFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Logica de negocio: calculo de costos, generacion de codigo de tracking
 * y validacion de transiciones de estado (usa el patron State).
 *
 * @author JoseLSR
 */
public class EnvioControlador {

    private final EnvioDAO envioDAO = new EnvioDAO();
    private final com.flashcourier.mavenproject.dao.HistorialEstadoDAO historialDAO = new com.flashcourier.mavenproject.dao.HistorialEstadoDAO();

    private static final double COSTO_BASE = 10.0;
    private static final double COSTO_POR_KG = 2.5;

    public double calcularCosto(double peso) {
        return COSTO_BASE + (peso * COSTO_POR_KG);
    }

    public String generarCodigoTracking() {
        String letras = "ABCDEFGHJKLMNPQRSTUVWXYZ";
        Random r = new Random();
        StringBuilder sb = new StringBuilder("FC-");
        for (int i = 0; i < 3; i++) sb.append(letras.charAt(r.nextInt(letras.length())));
        sb.append("-").append(100000 + r.nextInt(900000));
        return sb.toString();
    }

    public Envio registrarEnvio(Cliente remitente, Cliente destinatario, Paquete paquete, String direccionDestino)
            throws SQLException {
        double costo = calcularCosto(paquete.getPeso());
        String tracking = generarCodigoTracking();
        return envioDAO.registrarEnvioCompleto(remitente, destinatario, paquete, direccionDestino, costo, tracking);
    }

    public Envio consultarTracking(String codigoTracking) throws SQLException {
        return envioDAO.consultarPorTracking(codigoTracking);
    }

    public List<HistorialEstado> obtenerHistorial(int idEnvio) throws SQLException {
        try (java.sql.Connection con = com.flashcourier.mavenproject.database.ConexionDB.getInstancia().getConexion()) {
            return historialDAO.listarPorEnvio(con, idEnvio);
        }
    }

    /** Calcula cual es el siguiente estado valido a partir del estado actual (patron State). */
    public String siguienteEstadoDe(String estadoActual) {
        EstadoEnvio actual = EstadoEnvioFactory.desdeNombre(estadoActual);
        return actual.siguienteEstado().getNombre();
    }

    public void actualizarEstado(int idEnvio, String nuevoEstado, String observacion) throws SQLException {
        envioDAO.actualizarEstado(idEnvio, nuevoEstado, observacion);
    }

    public void confirmarEntrega(int idEnvio, int idCourier, String observacion) throws SQLException {
        envioDAO.confirmarEntrega(idEnvio, idCourier, observacion);
    }

    public Envio buscarPorId(int idEnvio) throws SQLException {
        return envioDAO.buscarPorId(idEnvio);
    }

    /** Cancela un envio (estado final alternativo, disponible desde cualquier estado no terminal). */
    public void cancelarEnvio(int idEnvio, String observacion) throws SQLException {
        envioDAO.actualizarEstado(idEnvio, "Cancelado", observacion);
    }

    /** Cuenta los envios por estado, para el dashboard de estadisticas. */
    public Map<String, Integer> contarPorEstado() throws SQLException {
        return envioDAO.contarPorEstado();
    }

    /** Resumen general (total de pedidos, ingresos estimados, total de couriers) para Estadisticas. */
    public ResumenEstadisticas obtenerResumenEstadisticas() throws SQLException {
        return envioDAO.obtenerResumen();
    }

    /** Lista todos los envios con courier asignado, para la pantalla de detalle completo. */
    public List<Envio> listarEnvios() throws SQLException {
        return envioDAO.listarTodos();
    }
}
