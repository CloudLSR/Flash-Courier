/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.formularios;

import com.flashcourier.mavenproject.dao.UsuarioDAO;
import com.flashcourier.mavenproject.modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 *
 * @author JoseLSR
 */
public class FrmLogin extends JFrame {

    private final JTextField txtCorreo = new JTextField(20);
    private final JPasswordField txtPassword = new JPasswordField(20);
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public FrmLogin() {
        setTitle("Flash Courier - Iniciar Sesion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 240);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("FLASH COURIER", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        panel.add(lblTitulo, c);

        c.gridwidth = 1;
        c.gridx = 0; c.gridy = 1;
        panel.add(new JLabel("Correo:"), c);
        c.gridx = 1;
        panel.add(txtCorreo, c);

        c.gridx = 0; c.gridy = 2;
        panel.add(new JLabel("Contrasena:"), c);
        c.gridx = 1;
        panel.add(txtPassword, c);

        JButton btnIngresar = new JButton("Ingresar");
        c.gridx = 0; c.gridy = 3; c.gridwidth = 2;
        panel.add(btnIngresar, c);

        JLabel lblAyuda = new JLabel("<html><small>Prueba: admin@flashcourier.pe / admin</small></html>", SwingConstants.CENTER);
        c.gridy = 4;
        panel.add(lblAyuda, c);

        add(panel);

        btnIngresar.addActionListener(e -> iniciarSesion());
        txtPassword.addActionListener(e -> iniciarSesion());
    }

    private void iniciarSesion() {
        String correo = txtCorreo.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (correo.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa correo y contrasena.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
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
                "No se pudo conectar a la base de datos.\nVerifica que MySQL este corriendo en el puerto 3306.\n\n" + ex.getMessage(),
                "Error de conexion", JOptionPane.ERROR_MESSAGE);
        }
    }
}
