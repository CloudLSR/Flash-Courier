/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.modelo.state;

/**
 * Patron State: representa un estado posible de un Envio y sabe cual es
 * el siguiente estado valido en el flujo logistico (evita transiciones invalidas).
 *
 * @author JoseLSR
 */
public interface EstadoEnvio {
    String getNombre();
    EstadoEnvio siguienteEstado();
}
