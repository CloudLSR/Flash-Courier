package com.flashcourier.mavenproject.formularios;

import com.flashcourier.mavenproject.modelo.Usuario;

import javax.swing.*;
import java.awt.*;

public class FrmDashboard extends JFrame {

    private final Usuario usuario;

    public FrmDashboard(Usuario usuario) {
        this.usuario = usuario;
        setTitle("Flash Courier - Panel Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
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

        JButton btnRegistrar = crearBoton("Registrar Envio", "registrar_envio.png");
        btnRegistrar.addActionListener(e -> abrir(new FrmRegistrarEnvio()));
        bgc.gridy = 0;
        panelBotones.add(btnRegistrar, bgc);

        JButton btnConsultar = crearBoton("Consultar Tracking", "consultar_tracking.png");
        btnConsultar.addActionListener(e -> abrir(new FrmConsultarTracking()));
        bgc.gridy = 1;
        panelBotones.add(btnConsultar, bgc);

        JButton btnActualizar = crearBoton("Actualizar Estado", "actualizar_estado.png");
        btnActualizar.addActionListener(e -> abrir(new FrmActualizarEstado()));
        bgc.gridy = 2;
        panelBotones.add(btnActualizar, bgc);

        JButton btnConfirmar = crearBoton("Confirmar Entrega", "confirmar_entrega.png");
        btnConfirmar.addActionListener(e -> abrir(new FrmConfirmarEntrega()));
        bgc.gridy = 3;
        panelBotones.add(btnConfirmar, bgc);

        main.add(panelBotones, BorderLayout.CENTER);

        JButton btnCerrarSesion = new JButton("Cerrar Sesion");
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelSur.add(btnCerrarSesion);
        main.add(panelSur, BorderLayout.SOUTH);

        add(main);
    }

    private JButton crearBoton(String texto, String icono) {
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
