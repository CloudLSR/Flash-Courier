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
 * Fusiona Actualizar Estado y asignacion de courier: el flujo normal avanza
 * el envio al siguiente estado (patron State). El courier se elige una sola
 * vez, justo al pasar a "En Reparto" (momento en que el paquete deja
 * nuestras manos y pasa a las del courier); de ahi en adelante, incluido el
 * paso a "Entregado", se muestra fijo y bloqueado, solo de referencia.
 * La etiqueta "Courier:" nunca cambia de texto (para que el combo no se
 * mueva de lugar); mientras esta bloqueado y no hay courier asignado, el
 * combo simplemente queda vacio (sin ningun texto de aviso adentro).
 * Tambien permite cancelar el pedido desde cualquier estado no terminal,
 * sin que eso dependa de si ya se asigno courier o no.
 *
 * @author JoseLSR
 */
public class FrmActualizarEstado extends JFrame {

    private static final String[] ESTADOS_TERMINALES = {"Entregado", "Cancelado"};

    // Estado en el que se elige el courier (una sola vez). Antes de llegar
    // aca, el combo aparece bloqueado y vacio; despues, aparece fijo con el
    // que ya se eligio.
    private static final String ESTADO_ASIGNACION_COURIER = "En Reparto";

    // Observacion predefinida segun el estado al que se va a pasar (coherente
    // con el siguiente estado calculado por el patron State). Se rellena sola
    // cada vez que cambia el estado destino, pero la casilla sigue siendo
    // editable por si se quiere precisar algo puntual.
    private static final java.util.Map<String, String> TEXTOS_PREDEFINIDOS = java.util.Map.of(
            "En Almacén", "Paquete recibido y almacenado en el centro de distribución.",
            "En Ruta", "Paquete en ruta hacia la dirección de destino.",
            "En Reparto", "Paquete asignado a un courier y en reparto.",
            "Entregado", "Paquete entregado satisfactoriamente al destinatario.",
            "Cancelado", "Pedido cancelado."
    );

    private final CourierFacade facade = new CourierFacade();
    private final JTextField txtCodigo = new JTextField(18);
    private final JLabel lblEstadoActual = new JLabel("-");
    private final JLabel lblSiguienteEstado = new JLabel("-");
    private final JLabel lblCourier = new JLabel("Courier:");
    private final JComboBox<Courier> cmbCourier = new JComboBox<>();
    private final JCheckBox chkCancelar = new JCheckBox("Cancelar este pedido");
    private final JTextField txtObservacion = new JTextField(25);
    private final JButton btnActualizar = new JButton("Actualizar Estado");

    private Envio envioActual;

    public FrmActualizarEstado() {
        setTitle("Actualizar Estado de Envío - Flash Courier");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(560, 400);
        setLocationRelativeTo(null);

        cargarCouriers();

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;

        JButton btnBuscar = new JButton("Buscar");

        c.gridx = 0; c.gridy = 0; panel.add(new JLabel("Código de tracking:"), c);
        c.gridx = 1; panel.add(txtCodigo, c);
        c.gridx = 2; panel.add(btnBuscar, c);

        c.gridx = 0; c.gridy = 1; panel.add(new JLabel("Estado actual:"), c);
        c.gridx = 1; c.gridwidth = 2; panel.add(lblEstadoActual, c); c.gridwidth = 1;

        c.gridx = 0; c.gridy = 2; panel.add(new JLabel("Siguiente estado:"), c);
        c.gridx = 1; c.gridwidth = 2; panel.add(lblSiguienteEstado, c); c.gridwidth = 1;

        // El combo de courier queda siempre visible (nunca se oculta) y la
        // etiqueta nunca cambia de texto, para que nada se mueva de lugar;
        // lo que varia es si esta habilitado y que item tiene seleccionado.
        c.gridx = 0; c.gridy = 3; panel.add(lblCourier, c);
        c.gridx = 1; c.gridwidth = 2; panel.add(cmbCourier, c); c.gridwidth = 1;

        c.gridx = 1; c.gridy = 4; c.gridwidth = 2;
        panel.add(chkCancelar, c); c.gridwidth = 1;

        c.gridx = 0; c.gridy = 5; panel.add(new JLabel("Observación:"), c);
        c.gridx = 1; c.gridwidth = 2; panel.add(txtObservacion, c); c.gridwidth = 1;

        c.gridx = 1; c.gridy = 6; c.gridwidth = 2;
        btnActualizar.setEnabled(false);
        panel.add(btnActualizar, c);

        add(panel);

        btnBuscar.addActionListener(e -> buscar());
        btnActualizar.addActionListener(e -> actualizar());
        chkCancelar.addActionListener(e -> refrescarVistaSegunEstado());

        // Al abrir, todavia no hay ningun envio cargado: todo lo editable
        // (incluido el combo de courier) arranca bloqueado hasta que se
        // busque uno.
        refrescarVistaSegunEstado();
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

    /** Selecciona en el combo el courier con ese ID (o lo deja sin seleccion si no se encuentra). */
    private void preseleccionarCourier(int idCourier) {
        for (int i = 0; i < cmbCourier.getItemCount(); i++) {
            Courier c = cmbCourier.getItemAt(i);
            if (c.getIdCourier() == idCourier) {
                cmbCourier.setSelectedItem(c);
                return;
            }
        }
        cmbCourier.setSelectedItem(null);
    }

    /**
     * Deja el combo bloqueado (no editable): si el envio ya tiene courier
     * asignado lo muestra fijo con su nombre; si no, lo deja simplemente
     * vacio (sin ningun texto de aviso adentro).
     */
    private void bloquearCourier(Integer idCourier) {
        cmbCourier.setEnabled(false);
        if (idCourier != null) {
            preseleccionarCourier(idCourier);
        } else {
            cmbCourier.setSelectedItem(null);
        }
    }

    private void buscar() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa un código de tracking.", "Dato requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            envioActual = facade.envios().consultarTracking(codigo);
            if (envioActual == null) {
                JOptionPane.showMessageDialog(this, "No se encontró un envío con ese código.", "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
                envioActual = null;
                refrescarVistaSegunEstado();
                return;
            }
            lblEstadoActual.setText(envioActual.getEstado());
            chkCancelar.setSelected(false);
            refrescarVistaSegunEstado();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar el envío:\n" + ex.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Actualiza lo que se muestra (siguiente estado, combo de courier, checkbox
     * de cancelar, observacion predefinida, boton) segun el envio y estado
     * actuales. Se llama al abrir el formulario, tras buscar un envio, y cada
     * vez que se marca/desmarca "Cancelar este pedido".
     */
    private void refrescarVistaSegunEstado() {
        if (envioActual == null) {
            // Aun no se busco ningun envio: no hay nada que editar todavia.
            lblSiguienteEstado.setText("-");
            bloquearCourier(null);
            chkCancelar.setSelected(false);
            chkCancelar.setEnabled(false);
            txtObservacion.setText("");
            txtObservacion.setEnabled(false);
            btnActualizar.setEnabled(false);
            return;
        }

        if (esEstadoTerminal(envioActual.getEstado())) {
            // Entregado o Cancelado: el envio queda visible para consulta/historial,
            // pero ya no admite mas cambios. El courier se deja fijo, de referencia
            // (si llego a asignarse antes de cancelar, por ejemplo).
            lblSiguienteEstado.setText("(este envío ya no puede actualizarse: " + envioActual.getEstado() + ")");
            bloquearCourier(envioActual.getIdCourier());
            chkCancelar.setSelected(false);
            chkCancelar.setEnabled(false);
            txtObservacion.setText("");
            txtObservacion.setEnabled(false);
            btnActualizar.setEnabled(false);
            return;
        }

        chkCancelar.setEnabled(true);
        txtObservacion.setEnabled(true);

        if (chkCancelar.isSelected()) {
            // Cancelar no depende del courier para nada: se deja tal cual este
            // (fijo, sin poder tocarlo) y no afecta si ya estaba asignado o no.
            lblSiguienteEstado.setText("Cancelado");
            bloquearCourier(envioActual.getIdCourier());
            txtObservacion.setText(TEXTOS_PREDEFINIDOS.get("Cancelado"));
            btnActualizar.setText("Cancelar Pedido");
            btnActualizar.setEnabled(true);
            return;
        }

        String siguiente = facade.envios().siguienteEstadoDe(envioActual.getEstado());
        lblSiguienteEstado.setText(siguiente);
        txtObservacion.setText(TEXTOS_PREDEFINIDOS.getOrDefault(siguiente, ""));

        if (ESTADO_ASIGNACION_COURIER.equals(siguiente)) {
            // Recien aca se habilita para elegir el courier que hara el reparto.
            cmbCourier.setEnabled(true);
            cmbCourier.setSelectedItem(null);
        } else {
            // Todavia no llega al paso de asignacion (Registrado/En Almacen/En
            // Ruta), o ya paso por el (En Reparto -> Entregado): en ambos casos
            // queda bloqueado, mostrando el courier ya asignado o vacio si aun
            // no corresponde.
            bloquearCourier(envioActual.getIdCourier());
        }

        boolean esAsignacion = ESTADO_ASIGNACION_COURIER.equals(siguiente);
        btnActualizar.setText(esAsignacion ? "Asignar Courier y Enviar a Reparto" : "Actualizar Estado");
        btnActualizar.setEnabled(true);
    }

    private void actualizar() {
        if (envioActual == null) return;
        String observacion = txtObservacion.getText().trim();

        try {
            if (chkCancelar.isSelected()) {
                if (observacion.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Ingresa un motivo de cancelación en Observación.",
                            "Dato requerido", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                facade.envios().cancelarEnvio(envioActual.getIdEnvio(), observacion);
                JOptionPane.showMessageDialog(this, "El pedido fue cancelado.", "Cancelación registrada", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                return;
            }

            String siguiente = facade.envios().siguienteEstadoDe(envioActual.getEstado());

            if (ESTADO_ASIGNACION_COURIER.equals(siguiente)) {
                Courier courier = (Courier) cmbCourier.getSelectedItem();
                if (courier == null) {
                    JOptionPane.showMessageDialog(this, "Selecciona el courier que hará el reparto.",
                            "Dato requerido", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                facade.envios().asignarCourierYAvanzar(envioActual.getIdEnvio(), courier.getIdCourier(), siguiente, observacion);
                JOptionPane.showMessageDialog(this, "Estado actualizado a: " + siguiente + ". Courier asignado: " + courier.getNombre(),
                        "Actualización exitosa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                facade.envios().actualizarEstado(envioActual.getIdEnvio(), siguiente, observacion);
                JOptionPane.showMessageDialog(this, "Estado actualizado a: " + siguiente, "Actualización exitosa", JOptionPane.INFORMATION_MESSAGE);
            }
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar el estado:\n" + ex.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }
}
