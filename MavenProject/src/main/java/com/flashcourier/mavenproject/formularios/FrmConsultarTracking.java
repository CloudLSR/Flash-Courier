package com.flashcourier.mavenproject.formularios;

import com.flashcourier.mavenproject.controlador.CourierFacade;
import com.flashcourier.mavenproject.modelo.Envio;
import com.flashcourier.mavenproject.modelo.HistorialEstado;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class FrmConsultarTracking extends JFrame {

    private final CourierFacade facade = new CourierFacade();
    private final JTextField txtTracking = new JTextField(15);
    private final JLabel lblMensaje = new JLabel(" ");

    private final JLabel lblEstado = new JLabel("-");
    private final JLabel lblFecha = new JLabel("-");
    private final JLabel lblCosto = new JLabel("-");
    private final JLabel lblRemitente = new JLabel("-");
    private final JLabel lblDestinatario = new JLabel("-");
    private final JLabel lblPeso = new JLabel("-");
    private final JLabel lblDimensiones = new JLabel("-");
    private final JLabel lblDirDestino = new JLabel("-");
    private final JLabel lblCourier = new JLabel("-");

    private final JTable tblHistorial = new JTable();
    private final DefaultTableModel modelHistorial = new DefaultTableModel(
            new String[]{"Fecha/Hora", "Estado", "Observacion"}, 0
    );

    public FrmConsultarTracking() {
        setTitle("Flash Courier - Consultar Tracking");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(650, 550);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        main.add(crearPanelBusqueda(), BorderLayout.NORTH);
        main.add(crearPanelResultados(), BorderLayout.CENTER);

        add(main);
    }

    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JLabel lblTitulo = new JLabel("CONSULTAR TRACKING", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        busqueda.add(new JLabel("Codigo Tracking:"));
        busqueda.add(txtTracking);

        JButton btnConsultar = new JButton("Consultar");
        btnConsultar.addActionListener(e -> consultar());
        busqueda.add(btnConsultar);

        txtTracking.addActionListener(e -> consultar());

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.add(lblTitulo, BorderLayout.NORTH);
        contenedor.add(busqueda, BorderLayout.CENTER);
        lblMensaje.setForeground(Color.RED);
        lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        contenedor.add(lblMensaje, BorderLayout.SOUTH);

        panel.add(contenedor);
        return panel;
    }

    private JPanel crearPanelResultados() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 5, 3, 5);

        int row = 0;

        JPanel infoEnvio = new JPanel(new GridBagLayout());
        infoEnvio.setBorder(BorderFactory.createTitledBorder("Datos del Envio"));
        GridBagConstraints igbc = new GridBagConstraints();
        igbc.fill = GridBagConstraints.HORIZONTAL;
        igbc.insets = new Insets(2, 5, 2, 5);

        igbc.gridx = 0;
        igbc.gridy = 0;
        infoEnvio.add(new JLabel("Estado:"), igbc);
        igbc.gridx = 1;
        lblEstado.setFont(new Font("SansSerif", Font.BOLD, 14));
        infoEnvio.add(lblEstado, igbc);

        igbc.gridx = 0;
        igbc.gridy = 1;
        infoEnvio.add(new JLabel("Fecha Registro:"), igbc);
        igbc.gridx = 1;
        infoEnvio.add(lblFecha, igbc);

        igbc.gridx = 0;
        igbc.gridy = 2;
        infoEnvio.add(new JLabel("Costo:"), igbc);
        igbc.gridx = 1;
        infoEnvio.add(lblCosto, igbc);

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
        infoEnvio.add(new JLabel("Peso:"), igbc);
        igbc.gridx = 1;
        infoEnvio.add(lblPeso, igbc);

        igbc.gridx = 0;
        igbc.gridy = 6;
        infoEnvio.add(new JLabel("Dimensiones:"), igbc);
        igbc.gridx = 1;
        infoEnvio.add(lblDimensiones, igbc);

        igbc.gridx = 0;
        igbc.gridy = 7;
        infoEnvio.add(new JLabel("Direccion Destino:"), igbc);
        igbc.gridx = 1;
        infoEnvio.add(lblDirDestino, igbc);

        igbc.gridx = 0;
        igbc.gridy = 8;
        infoEnvio.add(new JLabel("Courier:"), igbc);
        igbc.gridx = 1;
        infoEnvio.add(lblCourier, igbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        panel.add(infoEnvio, gbc);

        row++;
        JPanel panelHistorial = new JPanel(new BorderLayout());
        panelHistorial.setBorder(BorderFactory.createTitledBorder("Historial de Estados"));

        tblHistorial.setModel(modelHistorial);
        tblHistorial.setFillsViewportHeight(true);
        tblHistorial.getColumnModel().getColumn(0).setPreferredWidth(140);
        tblHistorial.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblHistorial.getColumnModel().getColumn(2).setPreferredWidth(250);
        JScrollPane scrollHistorial = new JScrollPane(tblHistorial);
        scrollHistorial.setPreferredSize(new Dimension(600, 200));
        panelHistorial.add(scrollHistorial, BorderLayout.CENTER);

        gbc.gridy = row;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(panelHistorial, gbc);

        return panel;
    }

    private void consultar() {
        String codigo = txtTracking.getText().trim();
        if (codigo.isEmpty()) {
            lblMensaje.setText("Ingrese un codigo de tracking");
            return;
        }

        try {
            Envio envio = facade.envios().consultarTracking(codigo);
            if (envio == null) {
                lblMensaje.setText("No se encontro el envio con tracking: " + codigo);
                limpiar();
                return;
            }

            lblMensaje.setText(" ");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            lblEstado.setText(envio.getEstado());
            lblFecha.setText(envio.getFechaRegistro() != null ? sdf.format(envio.getFechaRegistro()) : "-");
            lblCosto.setText("S/ " + String.format("%.2f", envio.getCosto()));
            lblRemitente.setText(envio.getNombreRemitente());
            lblDestinatario.setText(envio.getNombreDestinatario());
            lblPeso.setText(String.format("%.2f kg", envio.getPeso()));
            lblDimensiones.setText(envio.getDimensiones() != null ? envio.getDimensiones() : "-");
            lblDirDestino.setText(envio.getDireccionDestino());
            lblCourier.setText(envio.getIdCourier() != null ? "Asignado (ID: " + envio.getIdCourier() + ")" : "No asignado");

            List<HistorialEstado> historial = facade.envios().obtenerHistorial(envio.getIdEnvio());
            modelHistorial.setRowCount(0);
            for (HistorialEstado h : historial) {
                modelHistorial.addRow(new Object[]{
                        sdf.format(h.getFechaHora()),
                        h.getEstado(),
                        h.getObservacion() != null ? h.getObservacion() : ""
                });
            }

        } catch (SQLException ex) {
            lblMensaje.setText("Error: " + ex.getMessage());
        }
    }

    private void limpiar() {
        lblEstado.setText("-");
        lblFecha.setText("-");
        lblCosto.setText("-");
        lblRemitente.setText("-");
        lblDestinatario.setText("-");
        lblPeso.setText("-");
        lblDimensiones.setText("-");
        lblDirDestino.setText("-");
        lblCourier.setText("-");
        modelHistorial.setRowCount(0);
    }
}
