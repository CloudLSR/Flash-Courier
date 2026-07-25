/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.formularios;

import com.flashcourier.mavenproject.dao.UsuarioDAO;
import com.flashcourier.mavenproject.modelo.Usuario;
import com.flashcourier.mavenproject.ui.BotonRetro;
import com.flashcourier.mavenproject.ui.PanelFondo;
import com.flashcourier.mavenproject.ui.Tema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

/**
 *
 * @author JoseLSR
 */
public class FrmLogin extends JFrame {

    private final JTextField txtCorreo = new JTextField(18);
    private final JPasswordField txtPassword = new JPasswordField(18);
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public FrmLogin() {
        setTitle("Flash Courier - Iniciar Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        PanelFondo fondo = new PanelFondo(new GridBagLayout(), 0.18f);
        setContentPane(fondo);

        JPanel tarjeta = new JPanel(new GridBagLayout());
        tarjeta.setBackground(Tema.BG_PANEL);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.MORADO, 1),
                new EmptyBorder(28, 32, 28, 32)));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridwidth = 2;

        ImageIcon icono = Tema.logo(72);
        if (icono != null) {
            JLabel lblLogo = new JLabel(icono, SwingConstants.CENTER);
            c.gridy = 0;
            c.insets = new Insets(0, 6, 4, 6);
            tarjeta.add(lblLogo, c);
        }

        JLabel lblTitulo = new JLabel("FLASH COURIER", SwingConstants.CENTER);
        lblTitulo.setFont(Tema.fuenteTitulo(24));
        lblTitulo.setForeground(Tema.ROSA);
        c.gridy = 1;
        c.insets = new Insets(4, 6, 2, 6);
        tarjeta.add(lblTitulo, c);

        JLabel lblSubtitulo = new JLabel("Panel de gestión de envíos", SwingConstants.CENTER);
        lblSubtitulo.setFont(Tema.fuenteTextoItalica(12));
        lblSubtitulo.setForeground(Tema.CIAN);
        c.gridy = 2;
        c.insets = new Insets(0, 6, 18, 6);
        tarjeta.add(lblSubtitulo, c);

        c.gridwidth = 1;
        c.insets = new Insets(8, 6, 4, 6);
        JLabel lblCorreo = new JLabel("Correo");
        lblCorreo.setForeground(Tema.TEXTO_TENUE);
        lblCorreo.setFont(Tema.fuenteTexto(12));
        c.gridx = 0; c.gridy = 3; c.gridwidth = 2;
        tarjeta.add(lblCorreo, c);

        Tema.estilizarCampo(txtCorreo);
        c.gridy = 4;
        tarjeta.add(txtCorreo, c);

        JLabel lblPass = new JLabel("Contraseña");
        lblPass.setForeground(Tema.TEXTO_TENUE);
        lblPass.setFont(Tema.fuenteTexto(12));
        c.gridy = 5;
        c.insets = new Insets(12, 6, 4, 6);
        tarjeta.add(lblPass, c);

        Tema.estilizarCampo(txtPassword);
        c.gridy = 6;
        c.insets = new Insets(0, 6, 4, 6);
        tarjeta.add(txtPassword, c);

        BotonRetro btnIngresar = new BotonRetro("INGRESAR");
        c.gridy = 7;
        c.insets = new Insets(20, 6, 6, 6);
        tarjeta.add(btnIngresar, c);

        JLabel lblAyuda = new JLabel("<html><center><small>Prueba: admin@flashcourier.pe / admin</small></center></html>", SwingConstants.CENTER);
        lblAyuda.setForeground(Tema.TEXTO_TENUE);
        c.gridy = 8;
        c.insets = new Insets(4, 6, 0, 6);
        tarjeta.add(lblAyuda, c);

        fondo.add(tarjeta);

        btnIngresar.addActionListener(e -> iniciarSesion());
        txtPassword.addActionListener(e -> iniciarSesion());
    }

    private void iniciarSesion() {
        String correo = txtCorreo.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (correo.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa correo y contraseña.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Usuario usuario = usuarioDAO.validarCredenciales(correo, pass);
            if (usuario == null) {
                JOptionPane.showMessageDialog(this, "Credenciales incorrectas.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            new FrmMenu(usuario).setVisible(true);
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "No se pudo conectar a la base de datos.\nVerifica que MySQL esté corriendo en el puerto 3306.\n\n" + ex.getMessage(),
                "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    }
}
