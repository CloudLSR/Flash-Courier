/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.modelo;

import java.sql.Timestamp;

/**
 * Entidad central del negocio. Reune informacion de cliente, paquete y estado.
 *
 * @author JoseLSR
 */
public class Envio {
    private int idEnvio;
    private String codigoTracking;
    private Timestamp fechaRegistro;
    private String estado;
    private String direccionDestino;
    private double costo;
    private int idRemitente;
    private int idDestinatario;
    private int idPaquete;
    private Integer idCourier;

    // Campos de conveniencia para mostrar en tablas / tracking (JOIN)
    private String nombreRemitente;
    private String dniRemitente;
    private String telefonoRemitente;
    private String direccionRemitente;
    private String nombreDestinatario;
    private String dniDestinatario;
    private String telefonoDestinatario;
    private double peso;
    private String dimensiones;
    private String descripcionPaquete;
    private String nombreCourier;

    public int getIdEnvio() { return idEnvio; }
    public void setIdEnvio(int idEnvio) { this.idEnvio = idEnvio; }
    public String getCodigoTracking() { return codigoTracking; }
    public void setCodigoTracking(String codigoTracking) { this.codigoTracking = codigoTracking; }
    public Timestamp getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Timestamp fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getDireccionDestino() { return direccionDestino; }
    public void setDireccionDestino(String direccionDestino) { this.direccionDestino = direccionDestino; }
    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }
    public int getIdRemitente() { return idRemitente; }
    public void setIdRemitente(int idRemitente) { this.idRemitente = idRemitente; }
    public int getIdDestinatario() { return idDestinatario; }
    public void setIdDestinatario(int idDestinatario) { this.idDestinatario = idDestinatario; }
    public int getIdPaquete() { return idPaquete; }
    public void setIdPaquete(int idPaquete) { this.idPaquete = idPaquete; }
    public Integer getIdCourier() { return idCourier; }
    public void setIdCourier(Integer idCourier) { this.idCourier = idCourier; }
    public String getNombreRemitente() { return nombreRemitente; }
    public void setNombreRemitente(String nombreRemitente) { this.nombreRemitente = nombreRemitente; }
    public String getDniRemitente() { return dniRemitente; }
    public void setDniRemitente(String dniRemitente) { this.dniRemitente = dniRemitente; }
    public String getTelefonoRemitente() { return telefonoRemitente; }
    public void setTelefonoRemitente(String telefonoRemitente) { this.telefonoRemitente = telefonoRemitente; }
    public String getDireccionRemitente() { return direccionRemitente; }
    public void setDireccionRemitente(String direccionRemitente) { this.direccionRemitente = direccionRemitente; }
    public String getNombreDestinatario() { return nombreDestinatario; }
    public void setNombreDestinatario(String nombreDestinatario) { this.nombreDestinatario = nombreDestinatario; }
    public String getDniDestinatario() { return dniDestinatario; }
    public void setDniDestinatario(String dniDestinatario) { this.dniDestinatario = dniDestinatario; }
    public String getTelefonoDestinatario() { return telefonoDestinatario; }
    public void setTelefonoDestinatario(String telefonoDestinatario) { this.telefonoDestinatario = telefonoDestinatario; }
    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }
    public String getDimensiones() { return dimensiones; }
    public void setDimensiones(String dimensiones) { this.dimensiones = dimensiones; }
    public String getDescripcionPaquete() { return descripcionPaquete; }
    public void setDescripcionPaquete(String descripcionPaquete) { this.descripcionPaquete = descripcionPaquete; }
    public String getNombreCourier() { return nombreCourier; }
    public void setNombreCourier(String nombreCourier) { this.nombreCourier = nombreCourier; }
}
