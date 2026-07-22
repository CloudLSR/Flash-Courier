/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.formularios;

import com.flashcourier.mavenproject.modelo.Usuario;

import javax.swing.*;
import java.awt.*;

/**
 * Menu principal tras el login: navegacion a cada caso de uso.
 * El dashboard de estadisticas vive en su propia pantalla (FrmEstadisticas),
 * accesible para todo el personal desde el boton "Estadisticas".
 *
 * @author rrffrl
 * @author JoseLSR
 */
public class FrmMenu extends JFrame {

    private final Usuario usuario;

    public FrmMenu(Usuario usuario) {
        this.usuario = usuario;
        setTitle("Flash Courier - Menu Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panelBienvenida = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 5, 2, 5);

        JLabel lblTitulo = new JLabel("FLASH COURIER", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panelBienvenida.add(lblTitulo, gbc);

        JLabel lblBienvenido = new JLabel("Bienvenido, " + usuario.getNombre(), SwingConstants.CENTER);
        lblBienvenido.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridy = 1;
        panelBienvenida.add(lblBienvenido, gbc);

        JLabel lblRol = new JLabel("Rol: " + usuario.getRol(), SwingConstants.CENTER);
        lblRol.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblRol.setForeground(Color.GRAY);
        gbc.gridy = 2;
        panelBienvenida.add(lblRol, gbc);

        main.add(panelBienvenida, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridBagLayout());
        GridBagConstraints bgc = new GridBagConstraints();
        bgc.fill = GridBagConstraints.HORIZONTAL;
        bgc.insets = new Insets(8, 20, 8, 20);
        bgc.gridx = 0;
        bgc.gridwidth = 1;
        int fila = 0;

        JButton btnRegistrar = crearBoton("Registrar Envio");
        btnRegistrar.addActionListener(e -> abrir(new FrmRegistrarEnvio()));
        bgc.gridy = fila++;
        panelBotones.add(btnRegistrar, bgc);

        JButton btnConsultar = crearBoton("Consultar Tracking");
        btnConsultar.addActionListener(e -> abrir(new FrmConsultarTracking()));
        bgc.gridy = fila++;
        panelBotones.add(btnConsultar, bgc);

        JButton btnActualizar = crearBoton("Actualizar Estado");
        btnActualizar.addActionListener(e -> abrir(new FrmActualizarEstado()));
        bgc.gridy = fila++;
        panelBotones.add(btnActualizar, bgc);

        // Estadisticas: disponible para todo el personal (solo lectura, no modifica datos).
        JButton btnEstadisticas = crearBoton("Estadisticas");
        btnEstadisticas.addActionListener(e -> abrir(new FrmEstadisticas()));
        bgc.gridy = fila++;
        panelBotones.add(btnEstadisticas, bgc);

        // Gestion de Personal: el boton siempre esta visible, pero solo el rol
        // Administracion puede usarlo. Para los demas roles aparece deshabilitado
        // ("apagado") en vez de ocultarse.
        boolean esAdmin = "Administracion".equalsIgnoreCase(usuario.getRol());
        JButton btnPersonal = crearBoton("Gestion de Personal");
        btnPersonal.setEnabled(esAdmin);
        if (!esAdmin) {
            btnPersonal.setToolTipText("Solo disponible para el rol Administracion");
        }
        btnPersonal.addActionListener(e -> abrir(new FrmGestionPersonal()));
        bgc.gridy = fila++;
        panelBotones.add(btnPersonal, bgc);

        main.add(panelBotones, BorderLayout.CENTER);

        JButton btnCerrarSesion = new JButton("Cerrar Sesion");
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelSur.add(btnCerrarSesion);
        main.add(panelSur, BorderLayout.SOUTH);

        add(main);
    }

    private JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setPreferredSize(new Dimension(250, 40));
        btn.setFocusPainted(false);
        return btn;
    }

    private void abrir(JFrame form) {
        form.setVisible(true);
    }

    private void cerrarSesion() {
        dispose();
        SwingUtilities.invokeLater(() -> new FrmLogin().setVisible(true));
    }
}
