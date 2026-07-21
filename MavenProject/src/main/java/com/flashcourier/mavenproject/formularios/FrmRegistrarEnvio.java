package com.flashcourier.mavenproject.formularios;

import com.flashcourier.mavenproject.controlador.CourierFacade;
import com.flashcourier.mavenproject.modelo.Cliente;
import com.flashcourier.mavenproject.modelo.Envio;
import com.flashcourier.mavenproject.modelo.Paquete;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class FrmRegistrarEnvio extends JFrame {

    private final CourierFacade facade = new CourierFacade();

    private final JTextField txtRemNombres = new JTextField(20);
    private final JTextField txtRemDni = new JTextField(15);
    private final JTextField txtRemTelefono = new JTextField(15);
    private final JTextField txtRemDireccion = new JTextField(20);

    private final JTextField txtDesNombres = new JTextField(20);
    private final JTextField txtDesDni = new JTextField(15);
    private final JTextField txtDesTelefono = new JTextField(15);
    private final JTextField txtDesDireccion = new JTextField(20);

    private final JTextField txtPeso = new JTextField(10);
    private final JTextField txtDimensiones = new JTextField(15);
    private final JTextField txtDescripcion = new JTextField(20);

    private final JTextField txtDirDestino = new JTextField(20);
    private final JTextField txtTracking = new JTextField(15);
    private final JTextField txtCosto = new JTextField(10);

    private final JLabel lblMensaje = new JLabel(" ");

    public FrmRegistrarEnvio() {
        setTitle("Flash Courier - Registrar Envio");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 5, 3, 5);

        int row = 0;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JLabel lblTitulo = new JLabel("REGISTRAR ENVIO", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        mainPanel.add(lblTitulo, gbc);

        row++;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel panelRem = crearPanelCliente("Remitente", txtRemNombres, txtRemDni, txtRemTelefono, txtRemDireccion);
        mainPanel.add(panelRem, gbc);

        row++;
        gbc.gridy = row;
        JPanel panelDes = crearPanelCliente("Destinatario", txtDesNombres, txtDesDni, txtDesTelefono, txtDesDireccion);
        mainPanel.add(panelDes, gbc);

        row++;
        gbc.gridy = row;
        JPanel panelPaq = crearPanelPaquete();
        mainPanel.add(panelPaq, gbc);

        row++;
        gbc.gridy = row;
        JPanel panelConf = crearPanelConfiguracion();
        mainPanel.add(panelConf, gbc);

        row++;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton btnRegistrar = new JButton("Registrar Envio");
        btnRegistrar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnRegistrar.addActionListener(e -> registrar());
        mainPanel.add(btnRegistrar, gbc);

        row++;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        lblMensaje.setForeground(Color.RED);
        lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(lblMensaje, gbc);

        add(mainPanel);

        txtPeso.addActionListener(e -> calcularCosto());
        generarTracking();
    }

    private JPanel crearPanelCliente(String titulo, JTextField txtNom, JTextField txtDni,
                                      JTextField txtTel, JTextField txtDir) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 5, 2, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nombres:"), gbc);
        gbc.gridx = 1;
        panel.add(txtNom, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("DNI:"), gbc);
        gbc.gridx = 1;
        panel.add(txtDni, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Telefono:"), gbc);
        gbc.gridx = 1;
        panel.add(txtTel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Direccion:"), gbc);
        gbc.gridx = 1;
        panel.add(txtDir, gbc);

        return panel;
    }

    private JPanel crearPanelPaquete() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Paquete"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 5, 2, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Peso (kg):"), gbc);
        gbc.gridx = 1;
        panel.add(txtPeso, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Dimensiones:"), gbc);
        gbc.gridx = 1;
        panel.add(txtDimensiones, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Descripcion:"), gbc);
        gbc.gridx = 1;
        panel.add(txtDescripcion, gbc);

        return panel;
    }

    private JPanel crearPanelConfiguracion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Configuracion del Envio"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 5, 2, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Direccion Destino:"), gbc);
        gbc.gridx = 1;
        panel.add(txtDirDestino, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Codigo Tracking:"), gbc);
        gbc.gridx = 1;
        txtTracking.setEditable(false);
        txtTracking.setBackground(Color.WHITE);
        panel.add(txtTracking, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Costo (S/):"), gbc);
        gbc.gridx = 1;
        txtCosto.setEditable(false);
        txtCosto.setBackground(Color.WHITE);
        panel.add(txtCosto, gbc);

        return panel;
    }

    private void generarTracking() {
        txtTracking.setText(facade.envios().generarCodigoTracking());
    }

    private void calcularCosto() {
        try {
            double peso = Double.parseDouble(txtPeso.getText().trim());
            double costo = facade.envios().calcularCosto(peso);
            txtCosto.setText(String.format("%.2f", costo));
        } catch (NumberFormatException e) {
            txtCosto.setText("");
        }
    }

    private void registrar() {
        try {
            String remNombres = txtRemNombres.getText().trim();
            String remDni = txtRemDni.getText().trim();
            String remTel = txtRemTelefono.getText().trim();
            String remDir = txtRemDireccion.getText().trim();

            String desNombres = txtDesNombres.getText().trim();
            String desDni = txtDesDni.getText().trim();
            String desTel = txtDesTelefono.getText().trim();
            String desDir = txtDesDireccion.getText().trim();

            String pesoStr = txtPeso.getText().trim();
            String dimensiones = txtDimensiones.getText().trim();
            String descripcion = txtDescripcion.getText().trim();

            String dirDestino = txtDirDestino.getText().trim();

            if (remNombres.isEmpty() || remDni.isEmpty() || desNombres.isEmpty() || desDni.isEmpty()
                    || pesoStr.isEmpty() || dirDestino.isEmpty()) {
                lblMensaje.setText("Complete los campos obligatorios (*)");
                return;
            }

            double peso = Double.parseDouble(pesoStr);

            Cliente remitente = new Cliente(remNombres, remDni, remTel, remDir);
            Cliente destinatario = new Cliente(desNombres, desDni, desTel, desDir);
            Paquete paquete = new Paquete(peso, dimensiones, descripcion);

            Envio envio = facade.envios().registrarEnvio(remitente, destinatario, paquete, dirDestino);

            JOptionPane.showMessageDialog(this,
                    "Envio registrado exitosamente!\n"
                    + "Tracking: " + envio.getCodigoTracking() + "\n"
                    + "Costo: S/ " + String.format("%.2f", envio.getCosto()),
                    "Registro Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);

            limpiar();
            generarTracking();

        } catch (NumberFormatException e) {
            lblMensaje.setText("Peso invalido, ingrese un numero");
        } catch (SQLException e) {
            lblMensaje.setText("Error al registrar: " + e.getMessage());
        }
    }

    private void limpiar() {
        for (JTextField tf : new JTextField[]{
                txtRemNombres, txtRemDni, txtRemTelefono, txtRemDireccion,
                txtDesNombres, txtDesDni, txtDesTelefono, txtDesDireccion,
                txtPeso, txtDimensiones, txtDescripcion, txtDirDestino
        }) {
            tf.setText("");
        }
        txtCosto.setText("");
        lblMensaje.setText(" ");
    }
}
