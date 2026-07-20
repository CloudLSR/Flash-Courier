/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.modelo.state;

/**
 *
 * @author JoseLSR
 */
public class EstadoEnRuta implements EstadoEnvio {
    @Override
    public String getNombre() { return "En Ruta"; }

    @Override
    public EstadoEnvio siguienteEstado() { return new EstadoEnReparto(); }
}
