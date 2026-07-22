/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.formularios;

import com.flashcourier.mavenproject.controlador.CourierFacade;
import com.flashcourier.mavenproject.modelo.Envio;
import com.flashcourier.mavenproject.modelo.HistorialEstado;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author JoseLSR
 */
public class FrmConsultarTracking extends JFrame {

    private final CourierFacade facade = new CourierFacade();
    private final JTextField txtCodigo = new JTextField(20);
    private final JLabel lblEstado = new JLabel("-");
    private final JLabel lblRemitente = new JLabel("-");
    private final JLabel lblDestinatario = new JLabel("-");
    private final JLabel lblDestino = new JLabel("-");
    private final JLabel lblCosto = new JLabel("-");
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"Estado", "Fecha y Hora", "Observacion"}, 0);

    public FrmConsultarTracking() {
        setTitle("Consultar Tracking - Flash Courier");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(560, 480);
        setLocationRelativeTo(null);

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.add(new JLabel("Codigo de seguimiento:"));
        panelBusqueda.add(txtCodigo);
        JButton btnBuscar = new JButton("Consultar");
        panelBusqueda.add(btnBuscar);

        JPanel panelInfo = new JPanel(new GridLayout(5, 2, 5, 5));
        panelInfo.setBorder(BorderFactory.createTitledBorder("Informacion del Envio"));
        panelInfo.add(new JLabel("Estado actual:")); panelInfo.add(lblEstado);
        panelInfo.add(new JLabel("Remitente:")); panelInfo.add(lblRemitente);
        panelInfo.add(new JLabel("Destinatario:")); panelInfo.add(lblDestinatario);
        panelInfo.add(new JLabel("Direccion destino:")); panelInfo.add(lblDestino);
        panelInfo.add(new JLabel("Costo:")); panelInfo.add(lblCosto);

        JTable tabla = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Historial de Movimientos"));

        JPanel centro = new JPanel(new BorderLayout(5, 5));
        centro.add(panelInfo, BorderLayout.NORTH);
        centro.add(scrollTabla, BorderLayout.CENTER);

        add(panelBusqueda, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);

        btnBuscar.addActionListener(e -> consultar());
        txtCodigo.addActionListener(e -> consultar());
    }

    private void consultar() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa un codigo de seguimiento.", "Dato requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Envio envio = facade.envios().consultarTracking(codigo);
            if (envio == null) {
                JOptionPane.showMessageDialog(this, "No se encontro ningun envio con ese codigo.", "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
                limpiar();
                return;
            }
            lblEstado.setText(envio.getEstado());
            lblRemitente.setText(envio.getNombreRemitente());
            lblDestinatario.setText(envio.getNombreDestinatario());
            lblDestino.setText(envio.getDireccionDestino());
            lblCosto.setText("S/ " + String.format("%.2f", envio.getCosto()));

            List<HistorialEstado> historial = facade.envios().obtenerHistorial(envio.getIdEnvio());
            modeloTabla.setRowCount(0);
            for (HistorialEstado h : historial) {
                modeloTabla.addRow(new Object[]{h.getEstado(), h.getFechaHora(), h.getObservacion()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al consultar el envio:\n" + ex.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar() {
        lblEstado.setText("-");
        lblRemitente.setText("-");
        lblDestinatario.setText("-");
        lblDestino.setText("-");
        lblCosto.setText("-");
        modeloTabla.setRowCount(0);
    }
}
