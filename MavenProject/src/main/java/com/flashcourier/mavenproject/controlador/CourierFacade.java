/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.controlador;

import com.flashcourier.mavenproject.dao.CourierDAO;
import com.flashcourier.mavenproject.modelo.Courier;

import java.sql.SQLException;
import java.util.List;

/**
 * Patron Facade: punto unico de acceso que consumen los formularios
 * (Registrar Envio, Consultar Tracking, Actualizar Estado, Confirmar Entrega).
 * Centraliza el acceso a los controladores y DAOs para simplificar la interfaz
 * que ve la capa de presentacion.
 *
 * @author JoseLSR
 */
public class CourierFacade {

    private final EnvioControlador envioControlador = new EnvioControlador();
    private final CourierDAO courierDAO = new CourierDAO();

    public EnvioControlador envios() {
        return envioControlador;
    }

    public List<Courier> listarCouriers() throws SQLException {
        return courierDAO.listarCouriers();
    }
}
