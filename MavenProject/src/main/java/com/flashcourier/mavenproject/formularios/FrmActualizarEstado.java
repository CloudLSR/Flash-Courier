package com.flashcourier.mavenproject.formularios;

import com.flashcourier.mavenproject.controlador.CourierFacade;
import com.flashcourier.mavenproject.modelo.Envio;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class FrmActualizarEstado extends JFrame {

    private final CourierFacade facade = new CourierFacade();
    private Envio envioActual;

    private final JTextField txtTracking = new JTextField(15);
    private final JLabel lblMensaje = new JLabel(" ");

    private final JLabel lblTracking = new JLabel("-");
    private final JLabel lblEstadoActual = new JLabel("-");
    private final JLabel lblSiguienteEstado = new JLabel("-");
    private final JLabel lblRemitente = new JLabel("-");
    private final JLabel lblDestinatario = new JLabel("-");
    private final JLabel lblFecha = new JLabel("-");

    private final JTextArea txtObservacion = new JTextArea(3, 30);
    private final JButton btnAvanzar = new JButton("Avanzar Estado");

    public FrmActualizarEstado() {
        setTitle("Flash Courier - Actualizar Estado");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(520, 480);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        main.add(crearPanelBusqueda(), BorderLayout.NORTH);
        main.add(crearPanelDetalle(), BorderLayout.CENTER);

        add(main);
        estadoInicial();
    }

    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 5, 3, 5);

        JLabel lblTitulo = new JLabel("ACTUALIZAR ESTADO", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        panel.add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Codigo Tracking:"), gbc);

        gbc.gridx = 1;
        panel.add(txtTracking, gbc);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscar());
        gbc.gridx = 2;
        panel.add(btnBuscar, gbc);

        txtTracking.addActionListener(e -> buscar());

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        lblMensaje.setForeground(Color.RED);
        lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblMensaje, gbc);

        return panel;
    }

    private JPanel crearPanelDetalle() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 5, 3, 5);

        int row = 0;

        JPanel infoEnvio = new JPanel(new GridBagLayout());
        infoEnvio.setBorder(BorderFactory.createTitledBorder("Envio"));
        GridBagConstraints igbc = new GridBagConstraints();
        igbc.fill = GridBagConstraints.HORIZONTAL;
        igbc.insets = new Insets(2, 5, 2, 5);

        igbc.gridx = 0;
        igbc.gridy = 0;
        infoEnvio.add(new JLabel("Tracking:"), igbc);
        igbc.gridx = 1;
        infoEnvio.add(lblTracking, igbc);

        igbc.gridx = 0;
        igbc.gridy = 1;
        infoEnvio.add(new JLabel("Estado Actual:"), igbc);
        igbc.gridx = 1;
        lblEstadoActual.setFont(new Font("SansSerif", Font.BOLD, 13));
        infoEnvio.add(lblEstadoActual, igbc);

        igbc.gridx = 0;
        igbc.gridy = 2;
        infoEnvio.add(new JLabel("Siguiente Estado:"), igbc);
        igbc.gridx = 1;
        lblSiguienteEstado.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblSiguienteEstado.setForeground(new Color(0, 100, 0));
        infoEnvio.add(lblSiguienteEstado, igbc);

        igbc.gridx = 0;
        igbc.gridy = 3;
        infoEnvio.add(new JLabel("Remitente:"), igbc);
        igbc.gridx = 1;
        infoEnvio.add(lblRemitente, igbc);

        igbc.gridx = 0;
        igbc.gridy = 4;
        infoEnvio.add(new JLabel("Destinatario:"), igbc);
        igbc.gridx = 1;
        infoEnvio.add(lblDestinatario, igbc);

        igbc.gridx = 0;
        igbc.gridy = 5;
        infoEnvio.add(new JLabel("Fecha Registro:"), igbc);
        igbc.gridx = 1;
        infoEnvio.add(lblFecha, igbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 1.0;
        panel.add(infoEnvio, gbc);

        row++;
        JPanel panelObs = new JPanel(new BorderLayout());
        panelObs.setBorder(BorderFactory.createTitledBorder("Observacion"));
        txtObservacion.setLineWrap(true);
        txtObservacion.setWrapStyleWord(true);
        panelObs.add(new JScrollPane(txtObservacion), BorderLayout.CENTER);

        gbc.gridy = row;
        panel.add(panelObs, gbc);

        row++;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        btnAvanzar.addActionListener(e -> avanzar());
        panel.add(btnAvanzar, gbc);

        return panel;
    }

    private void estadoInicial() {
        btnAvanzar.setEnabled(false);
        txtObservacion.setEnabled(false);
    }

    private void buscar() {
        String codigo = txtTracking.getText().trim();
        if (codigo.isEmpty()) {
            lblMensaje.setText("Ingrese un codigo de tracking");
            return;
        }

        try {
            Envio envio = facade.envios().consultarTracking(codigo);
            if (envio == null) {
                lblMensaje.setText("No se encontro el envio");
                limpiar();
                return;
            }

            envioActual = envio;
            lblMensaje.setText(" ");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            lblTracking.setText(envio.getCodigoTracking());
            lblEstadoActual.setText(envio.getEstado());
            lblRemitente.setText(envio.getNombreRemitente());
            lblDestinatario.setText(envio.getNombreDestinatario());
            lblFecha.setText(envio.getFechaRegistro() != null ? sdf.format(envio.getFechaRegistro()) : "-");

            String siguiente = facade.envios().siguienteEstadoDe(envio.getEstado());

            if (siguiente.equals(envio.getEstado())) {
                lblSiguienteEstado.setText("(Entregado - estado terminal)");
                btnAvanzar.setEnabled(false);
                txtObservacion.setEnabled(false);
            } else {
                lblSiguienteEstado.setText(siguiente);
                btnAvanzar.setEnabled(true);
                txtObservacion.setEnabled(true);
                txtObservacion.requestFocus();
            }

            txtObservacion.setText("");

        } catch (SQLException ex) {
            lblMensaje.setText("Error: " + ex.getMessage());
        }
    }

    private void avanzar() {
        if (envioActual == null) return;

        String siguiente;
        try {
            siguiente = facade.envios().siguienteEstadoDe(envioActual.getEstado());
        } catch (Exception ex) {
            lblMensaje.setText("Error al determinar siguiente estado");
            return;
        }

        if (siguiente.equals(envioActual.getEstado())) {
            lblMensaje.setText("El envio ya esta en estado terminal");
            return;
        }

        String observacion = txtObservacion.getText().trim();

        try {
            facade.envios().actualizarEstado(envioActual.getIdEnvio(), siguiente, observacion);

            JOptionPane.showMessageDialog(this,
                    "Estado actualizado exitosamente:\n"
                    + envioActual.getEstado() + "  →  " + siguiente,
                    "Actualizacion Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);

            envioActual.setEstado(siguiente);
            lblEstadoActual.setText(siguiente);
            lblSiguienteEstado.setText("");

            String prox = facade.envios().siguienteEstadoDe(siguiente);
            if (prox.equals(siguiente)) {
                lblSiguienteEstado.setText("(Entregado - estado terminal)");
                btnAvanzar.setEnabled(false);
                txtObservacion.setEnabled(false);
            } else {
                lblSiguienteEstado.setText(prox);
            }

            txtObservacion.setText("");

        } catch (SQLException ex) {
            lblMensaje.setText("Error al actualizar: " + ex.getMessage());
        }
    }

    private void limpiar() {
        envioActual = null;
        lblTracking.setText("-");
        lblEstadoActual.setText("-");
        lblSiguienteEstado.setText("-");
        lblRemitente.setText("-");
        lblDestinatario.setText("-");
        lblFecha.setText("-");
        txtObservacion.setText("");
        estadoInicial();
    }
}
