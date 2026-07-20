/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.modelo.state;

/** Traduce un nombre de estado (String, tal como se guarda en BD) a su objeto State.
 *
 * @author JoseLSR
 */
public class EstadoEnvioFactory {
    public static EstadoEnvio desdeNombre(String nombre) {
        switch (nombre) {
            case "Registrado": return new EstadoRegistrado();
            case "En Almacen": return new EstadoEnAlmacen();
            case "En Ruta": return new EstadoEnRuta();
            case "En Reparto": return new EstadoEnReparto();
            case "Entregado": return new EstadoEntregado();
            default: return new EstadoRegistrado();
        }
    }

    public static final String[] TODOS_LOS_ESTADOS = {
        "Registrado", "En Almacen", "En Ruta", "En Reparto", "Entregado"
    };
}
