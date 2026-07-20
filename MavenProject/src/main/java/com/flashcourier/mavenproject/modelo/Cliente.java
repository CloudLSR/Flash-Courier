/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.modelo;

/**
 *
 * @author JoseLSR
 */
public class Cliente {
    private int idCliente;
    private String nombres;
    private String dni;
    private String telefono;
    private String direccion;

    public Cliente() {}

    public Cliente(String nombres, String dni, String telefono, String direccion) {
        this.nombres = nombres;
        this.dni = dni;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
