/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.modelo.state;

/**
 *
 * @author JoseLSR
 */
public class EstadoEnAlmacen implements EstadoEnvio {
    @Override
    public String getNombre() { return "En Almacén"; }

    @Override
    public EstadoEnvio siguienteEstado() { return new EstadoEnRuta(); }
}
