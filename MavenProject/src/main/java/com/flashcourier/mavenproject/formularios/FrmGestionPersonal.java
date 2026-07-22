/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.formularios;

import com.flashcourier.mavenproject.controlador.CourierFacade;
import com.flashcourier.mavenproject.modelo.Courier;
import com.flashcourier.mavenproject.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Gestion de Personal - pantalla exclusiva y funcional del rol Administracion
 * (el boton para abrirla aparece "apagado" para los demas roles en FrmMenu),
 * con dos pestanas:
 * 1) Personal de Entrega (couriers): agregar/eliminar, aparecen disponibles
 *    al confirmar una entrega desde Actualizar Estado.
 * 2) Usuarios del Sistema: cuentas de acceso a la app (Recepcionista/Supervisor).
 *    La cuenta con rol Administracion siempre aparece listada, pero nunca se
 *    puede eliminar (bloqueado tanto en la interfaz como en la base de datos).
 *
 * @author JoseLSR
 */
public class FrmGestionPersonal extends JFrame {

    private static final String ROL_ADMIN = "Administracion";

    private final CourierFacade facade = new CourierFacade();

    public FrmGestionPersonal() {
        setTitle("Gestion de Personal - Flash Courier");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 540);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Personal de Entrega", crearPanelCouriers());
        tabs.addTab("Usuarios del Sistema", crearPanelUsuarios());

        add(tabs);
    }

    // ------------------------------------------------------------------
    // Pestana 1: Personal de Entrega (couriers)
    // ------------------------------------------------------------------
    private JPanel crearPanelCouriers() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Nombre", "Telefono"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tabla = new JTable(model);
        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(24);

        JLabel lblMensaje = new JLabel(" ");
        lblMensaje.setForeground(Color.DARK_GRAY);

        Runnable cargar = () -> {
            try {
                List<Courier> couriers = facade.listarCouriers();
                model.setRowCount(0);
                for (Courier c : couriers) model.addRow(new Object[]{c.getIdCourier(), c.getNombre(), c.getTelefono()});
                lblMensaje.setForeground(Color.DARK_GRAY);
                lblMensaje.setText("Total: " + couriers.size() + " courier(s)");
            } catch (SQLException ex) {
                lblMensaje.setForeground(Color.RED);
                lblMensaje.setText("Error al cargar el personal: " + ex.getMessage());
            }
        };

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(new EmptyBorder(15, 15, 15, 15));
        main.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JTextField txtNombre = new JTextField(15);
        JTextField txtTelefono = new JTextField(12);

        JPanel panelForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelForm.add(new JLabel("Nombre:"));
        panelForm.add(txtNombre);
        panelForm.add(new JLabel("Telefono:"));
        panelForm.add(txtTelefono);
        JButton btnAgregar = new JButton("Agregar Courier");
        panelForm.add(btnAgregar);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnEliminar = new JButton("Eliminar seleccionado");
        panelAcciones.add(btnEliminar);

        JPanel panelSur = new JPanel();
        panelSur.setLayout(new BoxLayout(panelSur, BoxLayout.Y_AXIS));
        panelSur.add(panelForm);
        panelSur.add(panelAcciones);
        panelSur.add(lblMensaje);
        main.add(panelSur, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String telefono = txtTelefono.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingresa el nombre del courier.", "Dato requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                facade.registrarCourier(nombre, telefono);
                txtNombre.setText("");
                txtTelefono.setText("");
                cargar.run();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "No se pudo registrar el courier:\n" + ex.getMessage(),
                        "Error de base de datos", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Selecciona un courier de la tabla.", "Dato requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int idCourier = (int) model.getValueAt(fila, 0);
            String nombre = (String) model.getValueAt(fila, 1);
            int confirmacion = JOptionPane.showConfirmDialog(this, "¿Eliminar a " + nombre + "?",
                    "Confirmar eliminacion", JOptionPane.YES_NO_OPTION);
            if (confirmacion != JOptionPane.YES_OPTION) return;
            try {
                facade.eliminarCourier(idCourier);
                cargar.run();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "No se pudo eliminar. Es posible que este courier ya tenga entregas registradas.\n" + ex.getMessage(),
                        "Error de base de datos", JOptionPane.ERROR_MESSAGE);
            }
        });

        cargar.run();
        return main;
    }

    // ------------------------------------------------------------------
    // Pestana 2: Usuarios del Sistema (cuentas de acceso a la app)
    // ------------------------------------------------------------------
    private JPanel crearPanelUsuarios() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Nombre", "Correo", "Rol"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tabla = new JTable(model);
        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(24);

        JLabel lblMensaje = new JLabel(" ");
        lblMensaje.setForeground(Color.DARK_GRAY);

        Runnable cargar = () -> {
            try {
                List<Usuario> usuarios = facade.listarUsuarios();
                model.setRowCount(0);
                for (Usuario u : usuarios) model.addRow(new Object[]{u.getIdUsuario(), u.getNombre(), u.getCorreo(), u.getRol()});
                lblMensaje.setForeground(Color.DARK_GRAY);
                lblMensaje.setText("Total: " + usuarios.size() + " usuario(s). La cuenta Administracion no puede eliminarse.");
            } catch (SQLException ex) {
                lblMensaje.setForeground(Color.RED);
                lblMensaje.setText("Error al cargar los usuarios: " + ex.getMessage());
            }
        };

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(new EmptyBorder(15, 15, 15, 15));
        main.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JTextField txtNombre = new JTextField(12);
        JTextField txtCorreo = new JTextField(14);
        JPasswordField txtContrasena = new JPasswordField(10);
        JComboBox<String> cmbRol = new JComboBox<>(new String[]{"Recepcionista", "Supervisor"});

        JPanel panelForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelForm.add(new JLabel("Nombre:"));
        panelForm.add(txtNombre);
        panelForm.add(new JLabel("Correo:"));
        panelForm.add(txtCorreo);
        panelForm.add(new JLabel("Contrasena:"));
        panelForm.add(txtContrasena);
        panelForm.add(new JLabel("Rol:"));
        panelForm.add(cmbRol);
        JButton btnAgregar = new JButton("Agregar Usuario");

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAcciones.add(btnAgregar);
        JButton btnEliminar = new JButton("Eliminar seleccionado");
        panelAcciones.add(btnEliminar);

        JPanel panelSur = new JPanel();
        panelSur.setLayout(new BoxLayout(panelSur, BoxLayout.Y_AXIS));
        panelSur.add(panelForm);
        panelSur.add(panelAcciones);
        panelSur.add(lblMensaje);
        main.add(panelSur, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String correo = txtCorreo.getText().trim();
            String contrasena = new String(txtContrasena.getPassword());
            String rol = (String) cmbRol.getSelectedItem();
            if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Completa nombre, correo y contrasena.", "Dato requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                facade.registrarUsuario(nombre, correo, contrasena, rol);
                txtNombre.setText("");
                txtCorreo.setText("");
                txtContrasena.setText("");
                cargar.run();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "No se pudo registrar el usuario (¿correo repetido?):\n" + ex.getMessage(),
                        "Error de base de datos", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Selecciona un usuario de la tabla.", "Dato requerido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String rol = (String) model.getValueAt(fila, 3);
            if (ROL_ADMIN.equalsIgnoreCase(rol)) {
                JOptionPane.showMessageDialog(this, "La cuenta de Administracion no se puede eliminar.",
                        "Accion no permitida", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int idUsuario = (int) model.getValueAt(fila, 0);
            String nombre = (String) model.getValueAt(fila, 1);
            int confirmacion = JOptionPane.showConfirmDialog(this, "¿Eliminar a " + nombre + "? Ya no podra iniciar sesion.",
                    "Confirmar eliminacion", JOptionPane.YES_NO_OPTION);
            if (confirmacion != JOptionPane.YES_OPTION) return;
            try {
                facade.eliminarUsuario(idUsuario);
                cargar.run();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar el usuario:\n" + ex.getMessage(),
                        "Error de base de datos", JOptionPane.ERROR_MESSAGE);
            }
        });

        cargar.run();
        return main;
    }
}
