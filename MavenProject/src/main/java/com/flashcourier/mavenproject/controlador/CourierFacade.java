/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.controlador;

import com.flashcourier.mavenproject.dao.CourierDAO;
import com.flashcourier.mavenproject.dao.UsuarioDAO;
import com.flashcourier.mavenproject.modelo.Courier;
import com.flashcourier.mavenproject.modelo.ResumenEstadisticas;
import com.flashcourier.mavenproject.modelo.Usuario;

import java.sql.SQLException;
import java.util.List;

/**
 * Patron Facade: punto unico de acceso que consumen los formularios
 * (Registrar Envio, Consultar Tracking, Actualizar Estado -que incluye
 * confirmar entrega-, y Gestion de Personal).
 * Centraliza el acceso a los controladores y DAOs para simplificar la interfaz
 * que ve la capa de presentacion.
 *
 * @author JoseLSR
 */
public class CourierFacade {

    private final EnvioControlador envioControlador = new EnvioControlador();
    private final CourierDAO courierDAO = new CourierDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public EnvioControlador envios() {
        return envioControlador;
    }

    /** Resumen general para la pantalla de Estadisticas (disponible para todo el personal). */
    public ResumenEstadisticas obtenerResumenEstadisticas() throws SQLException {
        return envioControlador.obtenerResumenEstadisticas();
    }

    public List<Courier> listarCouriers() throws SQLException {
        return courierDAO.listarCouriers();
    }

    /** Registra un nuevo courier (personal de entrega). Pantalla de Gestion de Personal (Administrador y Supervisor). */
    public Courier registrarCourier(String nombre, String telefono) throws SQLException {
        return courierDAO.registrarCourier(nombre, telefono);
    }

    /** Elimina un courier. Pantalla de Gestion de Personal (Administrador y Supervisor). */
    public void eliminarCourier(int idCourier) throws SQLException {
        courierDAO.eliminarCourier(idCourier);
    }

    /** Lista el personal que usa la app (Recepcionista, Supervisor, Administrador). Pantalla Gestion de Personal (solo admin). */
    public List<Usuario> listarUsuarios() throws SQLException {
        return usuarioDAO.listarUsuarios();
    }

    /** Registra un nuevo usuario de la app. Pantalla de Gestion de Personal (solo admin). */
    public Usuario registrarUsuario(String nombre, String correo, String contrasena, String rol) throws SQLException {
        return usuarioDAO.registrarUsuario(nombre, correo, contrasena, rol);
    }

    /** Elimina un usuario de la app (nunca la cuenta de Administrador). Pantalla Gestion de Personal (solo admin). */
    public void eliminarUsuario(int idUsuario) throws SQLException {
        usuarioDAO.eliminarUsuario(idUsuario);
    }
}
