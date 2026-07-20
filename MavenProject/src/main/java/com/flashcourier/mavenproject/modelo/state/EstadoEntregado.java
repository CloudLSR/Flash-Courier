/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.modelo.state;

/**
 *
 * @author JoseLSR
 */
public class EstadoEntregado implements EstadoEnvio {
    @Override
    public String getNombre() { return "Entregado"; }

    @Override
    public EstadoEnvio siguienteEstado() { return this; } // Estado final
}
