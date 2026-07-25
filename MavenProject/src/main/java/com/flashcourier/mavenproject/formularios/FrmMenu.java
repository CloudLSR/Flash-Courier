/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.formularios;

import com.flashcourier.mavenproject.modelo.Usuario;
import com.flashcourier.mavenproject.ui.BotonPill;

import javax.swing.*;
import java.awt.*;

/**
 * Menu principal tras el login: navegacion a cada caso de uso.
 * El dashboard de estadisticas vive en su propia pantalla (FrmEstadisticas),
 * accesible para todo el personal desde el boton "Estadisticas".
 *
 * @author JoseLSR
 */
public class FrmMenu extends JFrame {

    private final Usuario usuario;

    public FrmMenu(Usuario usuario) {
        this.usuario = usuario;
        setTitle("Flash Courier - Menú Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        main.setOpaque(false);

        JPanel panelBienvenida = new JPanel(new GridBagLayout());
        panelBienvenida.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 5, 2, 5);

        JLabel lblTitulo = new JLabel("FLASH COURIER", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panelBienvenida.add(lblTitulo, gbc);

        JLabel lblBienvenido = new JLabel("Bienvenido(a), " + usuario.getNombre(), SwingConstants.CENTER);
        lblBienvenido.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblBienvenido.setForeground(new Color(0xFF, 0xE1, 0x66));
        gbc.gridy = 1;
        panelBienvenida.add(lblBienvenido, gbc);

        JLabel lblRol = new JLabel("Rol: " + usuario.getRol(), SwingConstants.CENTER);
        lblRol.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 12));
        lblRol.setForeground(new Color(0x9A, 0xE6, 0x6A));
        gbc.gridy = 2;
        panelBienvenida.add(lblRol, gbc);

        main.add(panelBienvenida, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridBagLayout());
        panelBotones.setOpaque(false);
        GridBagConstraints bgc = new GridBagConstraints();
        bgc.fill = GridBagConstraints.HORIZONTAL;
        bgc.insets = new Insets(8, 20, 8, 20);
        bgc.gridx = 0;
        bgc.gridwidth = 1;
        int fila = 0;

        JButton btnRegistrar = crearBoton("Registrar Envío");
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

        // Jerarquia de roles:
        // - Recepcionista: operativa (Registrar/Consultar/Actualizar). Sin Estadisticas
        //   ni Gestion de Personal.
        // - Supervisor: todo lo del Recepcionista + Estadisticas + Gestion de Personal
        //   (pero solo la pestana de Personal de Entrega, ver FrmGestionPersonal).
        // - Administrador: acceso total, incluyendo Usuarios del Sistema.
        boolean esAdmin = "Administrador".equalsIgnoreCase(usuario.getRol());
        boolean esSupervisor = "Supervisor".equalsIgnoreCase(usuario.getRol());
        boolean puedeVerEstadisticas = esAdmin || esSupervisor;
        boolean puedeGestionarPersonal = esAdmin || esSupervisor;

        // Estadisticas: bloqueada para Recepcionista (aparece "apagada" en vez de
        // ocultarse, igual que Gestion de Personal).
        JButton btnEstadisticas = crearBoton("Estadísticas");
        btnEstadisticas.setEnabled(puedeVerEstadisticas);
        if (!puedeVerEstadisticas) {
            btnEstadisticas.setToolTipText("Solo disponible para Supervisor y Administrador");
        }
        btnEstadisticas.addActionListener(e -> abrir(new FrmEstadisticas()));
        bgc.gridy = fila++;
        panelBotones.add(btnEstadisticas, bgc);

        // Gestion de Personal: el boton siempre esta visible, pero solo se habilita
        // para Supervisor y Administrador. Para Recepcionista aparece deshabilitado
        // ("apagado") en vez de ocultarse. Dentro de la pantalla, FrmGestionPersonal
        // decide segun el rol si muestra 1 o 2 pestanas.
        JButton btnPersonal = crearBoton("Gestión de Personal");
        btnPersonal.setEnabled(puedeGestionarPersonal);
        if (!puedeGestionarPersonal) {
            btnPersonal.setToolTipText("Solo disponible para Supervisor y Administrador");
        }
        btnPersonal.addActionListener(e -> abrir(new FrmGestionPersonal(usuario)));
        bgc.gridy = fila++;
        panelBotones.add(btnPersonal, bgc);

        main.add(panelBotones, BorderLayout.CENTER);

        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        final Color rojoNormal = new Color(0xD9, 0x3A, 0x3A);
        final Color rojoHover = new Color(0xE8, 0x5A, 0x5A);
        final Color rojoPresionado = new Color(0xB8, 0x2A, 0x2A);
        btnCerrarSesion.setBackground(rojoNormal);
        btnCerrarSesion.setForeground(Color.WHITE);
        btnCerrarSesion.setOpaque(true);
        btnCerrarSesion.setBorderPainted(false);
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnCerrarSesion.setBackground(rojoHover);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnCerrarSesion.setBackground(rojoNormal);
            }
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                btnCerrarSesion.setBackground(rojoPresionado);
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                btnCerrarSesion.setBackground(btnCerrarSesion.contains(e.getPoint()) ? rojoHover : rojoNormal);
            }
        });
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelSur.setOpaque(false);
        panelSur.add(btnCerrarSesion);
        main.add(panelSur, BorderLayout.SOUTH);

        PanelConFondo fondo = new PanelConFondo("/img/banner-menu.png");
        fondo.add(main, BorderLayout.CENTER);
        setContentPane(fondo);
    }

    private JButton crearBoton(String texto) {
        BotonPill btn = new BotonPill(texto);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setPreferredSize(new Dimension(250, 40));
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
