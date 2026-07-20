package com.flashcourier.mavenproject.formularios;

import com.flashcourier.mavenproject.controlador.CourierFacade;
import com.flashcourier.mavenproject.modelo.Courier;
import com.flashcourier.mavenproject.modelo.Envio;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class FrmConfirmarEntrega extends JFrame {

    private final CourierFacade facade = new CourierFacade();
    private Envio envioActual;

    private final JTextField txtTracking = new JTextField(15);
    private final JLabel lblMensaje = new JLabel(" ");

    private final JLabel lblTracking = new JLabel("-");
    private final JLabel lblEstado = new JLabel("-");
    private final JLabel lblRemitente = new JLabel("-");
    private final JLabel lblDestinatario = new JLabel("-");
    private final JLabel lblFecha = new JLabel("-");
    private final JLabel lblDirDestino = new JLabel("-");

    private final JComboBox<Courier> cmbCourier = new JComboBox<>();
    private final JTextArea txtObservacion = new JTextArea(3, 30);
    private final JButton btnConfirmar = new JButton("Confirmar Entrega");

    public FrmConfirmarEntrega() {
        setTitle("Flash Courier - Confirmar Entrega");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(520, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        main.add(crearPanelBusqueda(), BorderLayout.NORTH);
        main.add(crearPanelDetalle(), BorderLayout.CENTER);

        add(main);
        estadoInicial();
        cargarCouriers();
    }

    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 5, 3, 5);

        JLabel lblTitulo = new JLabel("CONFIRMAR ENTREGA", SwingConstants.CENTER);
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
        infoEnvio.add(new JLabel("Estado:"), igbc);
        igbc.gridx = 1;
        lblEstado.setFont(new Font("SansSerif", Font.BOLD, 13));
        infoEnvio.add(lblEstado, igbc);

        igbc.gridx = 0;
        igbc.gridy = 2;
        infoEnvio.add(new JLabel("Remitente:"), igbc);
        igbc.gridx = 1;
        infoEnvio.add(lblRemitente, igbc);

        igbc.gridx = 0;
        igbc.gridy = 3;
        infoEnvio.add(new JLabel("Destinatario:"), igbc);
        igbc.gridx = 1;
        infoEnvio.add(lblDestinatario, igbc);

        igbc.gridx = 0;
        igbc.gridy = 4;
        infoEnvio.add(new JLabel("Direccion Destino:"), igbc);
        igbc.gridx = 1;
        infoEnvio.add(lblDirDestino, igbc);

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
        JPanel panelCourier = new JPanel(new BorderLayout());
        panelCourier.setBorder(BorderFactory.createTitledBorder("Courier"));
        panelCourier.add(cmbCourier, BorderLayout.CENTER);
        gbc.gridy = row;
        panel.add(panelCourier, gbc);

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
        btnConfirmar.addActionListener(e -> confirmar());
        panel.add(btnConfirmar, gbc);

        return panel;
    }

    private void estadoInicial() {
        btnConfirmar.setEnabled(false);
        cmbCourier.setEnabled(false);
        txtObservacion.setEnabled(false);
    }

    private void cargarCouriers() {
        try {
            List<Courier> couriers = facade.listarCouriers();
            cmbCourier.removeAllItems();
            for (Courier c : couriers) {
                cmbCourier.addItem(c);
            }
        } catch (SQLException ex) {
            lblMensaje.setText("Error al cargar couriers: " + ex.getMessage());
        }
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

            if ("Entregado".equals(envio.getEstado())) {
                lblMensaje.setText("El envio ya fue entregado");
                limpiar();
                lblTracking.setText(envio.getCodigoTracking());
                lblEstado.setText(envio.getEstado());
                return;
            }

            envioActual = envio;
            lblMensaje.setText(" ");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            lblTracking.setText(envio.getCodigoTracking());
            lblEstado.setText(envio.getEstado());
            lblRemitente.setText(envio.getNombreRemitente());
            lblDestinatario.setText(envio.getNombreDestinatario());
            lblDirDestino.setText(envio.getDireccionDestino());
            lblFecha.setText(envio.getFechaRegistro() != null ? sdf.format(envio.getFechaRegistro()) : "-");

            btnConfirmar.setEnabled(true);
            cmbCourier.setEnabled(true);
            txtObservacion.setEnabled(true);

        } catch (SQLException ex) {
            lblMensaje.setText("Error: " + ex.getMessage());
        }
    }

    private void confirmar() {
        if (envioActual == null) return;

        Courier seleccionado = (Courier) cmbCourier.getSelectedItem();
        if (seleccionado == null) {
            lblMensaje.setText("Seleccione un courier");
            return;
        }

        String observacion = txtObservacion.getText().trim();

        try {
            facade.envios().confirmarEntrega(envioActual.getIdEnvio(), seleccionado.getIdCourier(), observacion);

            JOptionPane.showMessageDialog(this,
                    "Entrega confirmada exitosamente!\n"
                    + "Tracking: " + envioActual.getCodigoTracking() + "\n"
                    + "Courier: " + seleccionado.getNombre(),
                    "Entrega Confirmada",
                    JOptionPane.INFORMATION_MESSAGE);

            limpiar();

        } catch (SQLException ex) {
            lblMensaje.setText("Error al confirmar entrega: " + ex.getMessage());
        }
    }

    private void limpiar() {
        envioActual = null;
        lblTracking.setText("-");
        lblEstado.setText("-");
        lblRemitente.setText("-");
        lblDestinatario.setText("-");
        lblDirDestino.setText("-");
        lblFecha.setText("-");
        txtObservacion.setText("");
        estadoInicial();
    }
}
