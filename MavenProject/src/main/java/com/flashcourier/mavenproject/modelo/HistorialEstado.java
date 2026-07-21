/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.modelo;

import java.sql.Timestamp;

/**
 *
 * @author JoseLSR
 */
public class HistorialEstado {
    private int idHistorial;
    private int idEnvio;
    private String estado;
    private Timestamp fechaHora;
    private String observacion;

    public HistorialEstado() {}

    public HistorialEstado(String estado, Timestamp fechaHora, String observacion) {
        this.estado = estado;
        this.fechaHora = fechaHora;
        this.observacion = observacion;
    }

    public int getIdHistorial() { return idHistorial; }
    public void setIdHistorial(int idHistorial) { this.idHistorial = idHistorial; }
    public int getIdEnvio() { return idEnvio; }
    public void setIdEnvio(int idEnvio) { this.idEnvio = idEnvio; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Timestamp getFechaHora() { return fechaHora; }
    public void setFechaHora(Timestamp fechaHora) { this.fechaHora = fechaHora; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}
