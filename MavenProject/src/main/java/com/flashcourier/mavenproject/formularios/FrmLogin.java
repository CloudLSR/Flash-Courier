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

    private JTextField txtCorreo;
    private JPasswordField txtContrasena;
    private JLabel lblMensaje;
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public FrmLogin() {
        setTitle("Flash Courier - Iniciar Sesion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 280);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel lblTitulo = new JLabel("FLASH COURIER", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(new JLabel("Correo:"), gbc);

        txtCorreo = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtCorreo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Contrasena:"), gbc);

        txtContrasena = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(txtContrasena, gbc);

        JButton btnIngresar = new JButton("Ingresar");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnIngresar, gbc);

        lblMensaje = new JLabel(" ", SwingConstants.CENTER);
        lblMensaje.setForeground(Color.RED);
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(lblMensaje, gbc);

        add(panel);

        btnIngresar.addActionListener(e -> ingresar());
        txtContrasena.addActionListener(e -> ingresar());
        
        txtCorreo.setText("admin@flashcourier.pe");
        txtContrasena.setText("admin");
    }

    private void ingresar() {
        String correo = txtCorreo.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());

        if (correo.isEmpty() || contrasena.isEmpty()) {
            lblMensaje.setText("Ingrese correo y contrasena");
            return;
        }

        try {
            Usuario usuario = usuarioDAO.validarCredenciales(correo, contrasena);
            if (usuario != null) {
                dispose();
                SwingUtilities.invokeLater(() -> new FrmDashboard(usuario).setVisible(true));
            } else {
                lblMensaje.setText("Credenciales incorrectas");
                txtContrasena.setText("");
                txtContrasena.requestFocus();
            }
        } catch (SQLException ex) {
            lblMensaje.setText("Error de conexion: " + ex.getMessage());
        }
    }
}
