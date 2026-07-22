/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.formularios;

import com.flashcourier.mavenproject.controlador.CourierFacade;
import com.flashcourier.mavenproject.modelo.Courier;
import com.flashcourier.mavenproject.modelo.Envio;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Fusiona Actualizar Estado y Confirmar Entrega: el flujo normal avanza el
 * envio al siguiente estado (patron State); cuando el siguiente estado es
 * "Entregado" se pide ademas seleccionar el courier que hizo la entrega.
 * Tambien permite cancelar el pedido desde cualquier estado no terminal.
 *
 * @author JoseLSR
 */
public class FrmActualizarEstado extends JFrame {

    private static final String[] ESTADOS_TERMINALES = {"Entregado", "Cancelado"};

    private final CourierFacade facade = new CourierFacade();
    private final JTextField txtCodigo = new JTextField(18);
    private final JLabel lblEstadoActual = new JLabel("-");
    private final JLabel lblSiguienteEstado = new JLabel("-");
    private final JLabel lblCourier = new JLabel("Courier que entrega:");
    private final JComboBox<Courier> cmbCourier = new JComboBox<>();
    private final JCheckBox chkCancelar = new JCheckBox("Cancelar este pedido");
    private final JTextField txtObservacion = new JTextField(25);
    private final JButton btnActualizar = new JButton("Actualizar Estado");

    private Envio envioActual;

    public FrmActualizarEstado() {
        setTitle("Actualizar Estado de Envio - Flash Courier");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        cargarCouriers();

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;

        JButton btnBuscar = new JButton("Buscar");

        c.gridx = 0; c.gridy = 0; panel.add(new JLabel("Codigo de tracking:"), c);
        c.gridx = 1; panel.add(txtCodigo, c);
        c.gridx = 2; panel.add(btnBuscar, c);

        c.gridx = 0; c.gridy = 1; panel.add(new JLabel("Estado actual:"), c);
        c.gridx = 1; c.gridwidth = 2; panel.add(lblEstadoActual, c); c.gridwidth = 1;

        c.gridx = 0; c.gridy = 2; panel.add(new JLabel("Siguiente estado:"), c);
        c.gridx = 1; c.gridwidth = 2; panel.add(lblSiguienteEstado, c); c.gridwidth = 1;

        c.gridx = 0; c.gridy = 3; panel.add(lblCourier, c);
        c.gridx = 1; c.gridwidth = 2; panel.add(cmbCourier, c); c.gridwidth = 1;
        lblCourier.setVisible(false);
        cmbCourier.setVisible(false);

        c.gridx = 1; c.gridy = 4; c.gridwidth = 2;
        panel.add(chkCancelar, c); c.gridwidth = 1;

        c.gridx = 0; c.gridy = 5; panel.add(new JLabel("Observacion:"), c);
        c.gridx = 1; c.gridwidth = 2; panel.add(txtObservacion, c); c.gridwidth = 1;

        c.gridx = 1; c.gridy = 6; c.gridwidth = 2;
        btnActualizar.setEnabled(false);
        panel.add(btnActualizar, c);

        add(panel);

        btnBuscar.addActionListener(e -> buscar());
        btnActualizar.addActionListener(e -> actualizar());
        chkCancelar.addActionListener(e -> refrescarVistaSegunEstado());
    }

    private void cargarCouriers() {
        try {
            List<Courier> couriers = facade.listarCouriers();
            for (Courier c : couriers) cmbCourier.addItem(c);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar la lista de couriers:\n" + ex.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean esEstadoTerminal(String estado) {
        for (String terminal : ESTADOS_TERMINALES) {
            if (terminal.equals(estado)) return true;
        }
        return false;
    }

    private void buscar() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa un codigo de tracking.", "Dato requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            envioActual = facade.envios().consultarTracking(codigo);
            if (envioActual == null) {
                JOptionPane.showMessageDialog(this, "No se encontro un envio con ese codigo.", "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
                envioActual = null;
                refrescarVistaSegunEstado();
                return;
            }
            lblEstadoActual.setText(envioActual.getEstado());
            chkCancelar.setSelected(false);
            refrescarVistaSegunEstado();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar el envio:\n" + ex.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Actualiza lo que se muestra (siguiente estado, combo de courier, checkbox de cancelar, boton) segun el envio y estado actuales. */
    private void refrescarVistaSegunEstado() {
        if (envioActual == null) {
            lblSiguienteEstado.setText("-");
            lblCourier.setVisible(false);
            cmbCourier.setVisible(false);
            chkCancelar.setEnabled(false);
            btnActualizar.setEnabled(false);
            return;
        }

        if (esEstadoTerminal(envioActual.getEstado())) {
            lblSiguienteEstado.setText("(este envio ya no puede actualizarse: " + envioActual.getEstado() + ")");
            lblCourier.setVisible(false);
            cmbCourier.setVisible(false);
            chkCancelar.setEnabled(false);
            chkCancelar.setSelected(false);
            btnActualizar.setEnabled(false);
            return;
        }

        chkCancelar.setEnabled(true);

        if (chkCancelar.isSelected()) {
            lblSiguienteEstado.setText("Cancelado");
            lblCourier.setVisible(false);
            cmbCourier.setVisible(false);
            btnActualizar.setText("Cancelar Pedido");
            btnActualizar.setEnabled(true);
            return;
        }

        String siguiente = facade.envios().siguienteEstadoDe(envioActual.getEstado());
        lblSiguienteEstado.setText(siguiente);

        boolean esEntrega = "Entregado".equals(siguiente);
        lblCourier.setVisible(esEntrega);
        cmbCourier.setVisible(esEntrega);
        btnActualizar.setText(esEntrega ? "Confirmar Entrega" : "Actualizar Estado");
        btnActualizar.setEnabled(true);
    }

    private void actualizar() {
        if (envioActual == null) return;
        String observacion = txtObservacion.getText().trim();

        try {
            if (chkCancelar.isSelected()) {
                if (observacion.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Ingresa un motivo de cancelacion en Observacion.",
                            "Dato requerido", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                facade.envios().cancelarEnvio(envioActual.getIdEnvio(), observacion);
                JOptionPane.showMessageDialog(this, "El pedido fue cancelado.", "Cancelacion registrada", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                return;
            }

            String siguiente = facade.envios().siguienteEstadoDe(envioActual.getEstado());

            if ("Entregado".equals(siguiente)) {
                Courier courier = (Courier) cmbCourier.getSelectedItem();
                if (courier == null) {
                    JOptionPane.showMessageDialog(this, "No hay couriers registrados. Agrega uno en Gestion de Personal.",
                            "Dato requerido", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                facade.envios().confirmarEntrega(envioActual.getIdEnvio(), courier.getIdCourier(), observacion);
                JOptionPane.showMessageDialog(this, "Entrega confirmada. Estado: Entregado.", "Actualizacion exitosa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                facade.envios().actualizarEstado(envioActual.getIdEnvio(), siguiente, observacion);
                JOptionPane.showMessageDialog(this, "Estado actualizado a: " + siguiente, "Actualizacion exitosa", JOptionPane.INFORMATION_MESSAGE);
            }
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar el estado:\n" + ex.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }
}
