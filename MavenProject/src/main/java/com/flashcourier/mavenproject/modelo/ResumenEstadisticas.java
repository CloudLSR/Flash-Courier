/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.modelo;

/**
 * Resumen general para la pantalla de Estadisticas: total de pedidos,
 * ingresos estimados (solo de pedidos no cancelados) y total de personal
 * de entrega (couriers) registrado.
 *
 * @author JoseLSR
 */
public class ResumenEstadisticas {
    private int totalEnvios;
    private double ingresosEstimados;
    private int totalCouriers;

    public ResumenEstadisticas() {}

    public ResumenEstadisticas(int totalEnvios, double ingresosEstimados, int totalCouriers) {
        this.totalEnvios = totalEnvios;
        this.ingresosEstimados = ingresosEstimados;
        this.totalCouriers = totalCouriers;
    }

    public int getTotalEnvios() { return totalEnvios; }
    public void setTotalEnvios(int totalEnvios) { this.totalEnvios = totalEnvios; }
    public double getIngresosEstimados() { return ingresosEstimados; }
    public void setIngresosEstimados(double ingresosEstimados) { this.ingresosEstimados = ingresosEstimados; }
    public int getTotalCouriers() { return totalCouriers; }
    public void setTotalCouriers(int totalCouriers) { this.totalCouriers = totalCouriers; }
}
