/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.modelo.state;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Colores asociados a cada estado del envio, compartidos entre el grafico de
 * Estadisticas y la tabla de historial de Consultar Tracking, para que un
 * mismo estado se vea siempre del mismo color en toda la aplicacion.
 *
 * @author JoseLSR
 */
public class EstadoColores {

    public static final Map<String, Color> COLORES_ESTADO = new LinkedHashMap<>();
    static {
        COLORES_ESTADO.put("Registrado", new Color(96, 165, 250));   // celeste
        COLORES_ESTADO.put("En Almacen", new Color(250, 204, 21));   // amarillo
        COLORES_ESTADO.put("En Ruta", new Color(251, 146, 60));      // naranja
        COLORES_ESTADO.put("En Reparto", new Color(167, 139, 250));  // lila
        COLORES_ESTADO.put("Entregado", new Color(74, 222, 128));    // verde
        COLORES_ESTADO.put("Cancelado", new Color(248, 113, 113));   // rojo
    }

    private EstadoColores() {}

    /** Color asociado al estado, o gris claro si el estado no se reconoce (o esta vacio). */
    public static Color de(String estado) {
        return COLORES_ESTADO.getOrDefault(estado, new Color(230, 230, 230));
    }
}
