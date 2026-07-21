/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.formularios;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author JoseLSR
 */
public class FrmLogin extends JFrame {

    public FrmLogin() {
        setTitle("Flash Courier - Iniciar Sesion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 240);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel("FLASH COURIER", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblEstado = new JLabel("(pantalla de login pendiente)", SwingConstants.CENTER);
        lblEstado.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblEstado.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        panel.add(lblTitulo);
        panel.add(lblEstado);
        add(panel);
    }
}
