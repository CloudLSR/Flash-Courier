/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.modelo;

/**
 *
 * @author JoseLSR
 */
public class Paquete {
    private int idPaquete;
    private double peso;
    private String dimensiones;
    private String descripcion;

    public Paquete() {}

    public Paquete(double peso, String dimensiones, String descripcion) {
        this.peso = peso;
        this.dimensiones = dimensiones;
        this.descripcion = descripcion;
    }

    public int getIdPaquete() { return idPaquete; }
    public void setIdPaquete(int idPaquete) { this.idPaquete = idPaquete; }
    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }
    public String getDimensiones() { return dimensiones; }
    public void setDimensiones(String dimensiones) { this.dimensiones = dimensiones; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
