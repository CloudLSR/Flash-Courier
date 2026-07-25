/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.modelo.state;

/**
 * Estado final alternativo: un envio puede cancelarse desde cualquier estado
 * que no sea ya terminal (Entregado o Cancelado). No forma parte de la cadena
 * lineal normal (Registrado -> ... -> Entregado), por eso no se llega a el
 * via siguienteEstado() sino mediante una accion explicita del usuario.
 *
 * @author JoseLSR
 */
public class EstadoCancelado implements EstadoEnvio {
    @Override
    public String getNombre() { return "Cancelado"; }

    @Override
    public EstadoEnvio siguienteEstado() { return this; } // Estado final
}
