/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.modelo;

/**
 *
 * @author JoseLSR
 */
public class Courier {
    private int idCourier;
    private String nombre;
    private String telefono;

    public Courier() {}

    public Courier(int idCourier, String nombre, String telefono) {
        this.idCourier = idCourier;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public int getIdCourier() { return idCourier; }
    public void setIdCourier(int idCourier) { this.idCourier = idCourier; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    @Override
    public String toString() {
        return idCourier + " - " + nombre;
    }
}
