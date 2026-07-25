/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.formularios;

import com.flashcourier.mavenproject.controlador.CourierFacade;
import com.flashcourier.mavenproject.modelo.Cliente;
import com.flashcourier.mavenproject.modelo.Envio;
import com.flashcourier.mavenproject.modelo.Paquete;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.sql.SQLException;

/**
 *
 * @author JoseLSR
 */
public class FrmRegistrarEnvio extends JFrame {

    private final CourierFacade facade = new CourierFacade();

    // Remitente
    private final JTextField txtRemNombres = new JTextField(18);
    private final JTextField txtRemDni = new JTextField(18);
    private final JTextField txtRemTelefono = new JTextField(18);
    private final JTextField txtRemDireccion = new JTextField(18);

    // Destinatario
    private final JTextField txtDestNombres = new JTextField(18);
    private final JTextField txtDestDni = new JTextField(18);
    private final JTextField txtDestTelefono = new JTextField(18);
    private final JTextField txtDestDireccion = new JTextField(18);

    // Paquete
    private final JTextField txtPeso = new JTextField(8);
    private final JTextField txtDimensiones = new JTextField(12);
    private final JTextField txtDescripcion = new JTextField(18);

    public FrmRegistrarEnvio() {
        setTitle("Registrar Envío - Flash Courier");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(520, 560);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        panel.add(seccion("Datos del Remitente",
                new String[]{"Nombres:", "DNI:", "Teléfono:", "Dirección:"},
                new JTextField[]{txtRemNombres, txtRemDni, txtRemTelefono, txtRemDireccion}));

        panel.add(Box.createVerticalStrut(10));

        panel.add(seccion("Datos del Destinatario",
                new String[]{"Nombres:", "DNI:", "Teléfono:", "Dirección de entrega:"},
                new JTextField[]{txtDestNombres, txtDestDni, txtDestTelefono, txtDestDireccion}));

        panel.add(Box.createVerticalStrut(10));

        panel.add(seccion("Datos del Paquete",
                new String[]{"Peso (kg):", "Dimensiones:", "Descripción:"},
                new JTextField[]{txtPeso, txtDimensiones, txtDescripcion}));

        JButton btnRegistrar = new JButton("Registrar Envío");
        btnRegistrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(15));
        panel.add(btnRegistrar);

        JScrollPane scroll = new JScrollPane(panel);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll);

        btnRegistrar.addActionListener(e -> registrar());
    }

    private JPanel seccion(String titulo, String[] etiquetas, JTextField[] campos) {
        JPanel seccionPanel = new JPanel(new GridLayout(etiquetas.length, 2, 5, 5));
        seccionPanel.setBorder(BorderFactory.createTitledBorder(titulo));
        for (int i = 0; i < etiquetas.length; i++) {
            seccionPanel.add(new JLabel(etiquetas[i]));
            seccionPanel.add(campos[i]);
        }
        return seccionPanel;
    }

    private void registrar() {
        try {
            if (txtRemNombres.getText().isBlank() || txtDestNombres.getText().isBlank() || txtPeso.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Completa al menos nombres del remitente, destinatario y el peso.",
                        "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double peso;
            try {
                peso = Double.parseDouble(txtPeso.getText().trim());
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "El peso debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Cliente remitente = new Cliente(txtRemNombres.getText().trim(), txtRemDni.getText().trim(),
                    txtRemTelefono.getText().trim(), txtRemDireccion.getText().trim());
            Cliente destinatario = new Cliente(txtDestNombres.getText().trim(), txtDestDni.getText().trim(),
                    txtDestTelefono.getText().trim(), txtDestDireccion.getText().trim());
            Paquete paquete = new Paquete(peso, txtDimensiones.getText().trim(), txtDescripcion.getText().trim());

            Envio envio = facade.envios().registrarEnvio(remitente, destinatario, paquete, txtDestDireccion.getText().trim());

            mostrarResultado(envio);
            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar el envío:\n" + ex.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Dialogo de exito con el codigo de tracking en un campo de solo lectura y un boton para copiarlo. */
    private void mostrarResultado(Envio envio) {
        JDialog dialogo = new JDialog(this, "Registro exitoso", true);
        dialogo.setSize(380, 220);
        dialogo.setLocationRelativeTo(this);
        dialogo.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblOk = new JLabel("Envío registrado correctamente.");
        lblOk.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblOk);
        panel.add(Box.createVerticalStrut(15));

        JLabel lblEtiquetaCodigo = new JLabel("Código de seguimiento:");
        lblEtiquetaCodigo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblEtiquetaCodigo);
        panel.add(Box.createVerticalStrut(5));

        JPanel panelCodigo = new JPanel(new BorderLayout(5, 0));
        JTextField txtCodigo = new JTextField(envio.getCodigoTracking());
        txtCodigo.setEditable(false);
        txtCodigo.setHorizontalAlignment(JTextField.CENTER);
        txtCodigo.setFont(new Font("Monospaced", Font.BOLD, 14));
        JButton btnCopiar = new JButton("Copiar");
        btnCopiar.addActionListener(e -> {
            StringSelection seleccion = new StringSelection(envio.getCodigoTracking());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(seleccion, null);
            btnCopiar.setText("¡Copiado!");
        });
        panelCodigo.add(txtCodigo, BorderLayout.CENTER);
        panelCodigo.add(btnCopiar, BorderLayout.EAST);
        panelCodigo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(panelCodigo);
        panel.add(Box.createVerticalStrut(15));

        JLabel lblCosto = new JLabel("Costo calculado: S/ " + String.format("%.2f", envio.getCosto()));
        lblCosto.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblCosto);
        panel.add(Box.createVerticalStrut(15));

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCerrar.addActionListener(e -> dialogo.dispose());
        panel.add(btnCerrar);

        dialogo.add(panel);
        dialogo.setVisible(true);
    }
}
