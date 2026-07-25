/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.formularios;

import com.flashcourier.mavenproject.controlador.CourierFacade;
import com.flashcourier.mavenproject.modelo.Envio;
import com.flashcourier.mavenproject.reportes.ListaEnviosPdfService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

/**
 * Tabla con el detalle de todos los envios registrados, coloreada por estado.
 * Se abre desde la pantalla de Estadisticas ("Ver detalle de todos los envios").
 * Rescatado y adaptado del aporte original de un companero de equipo.
 *
 * @author JoseLSR
 */
public class FrmListarEnvios extends JFrame {

    private final CourierFacade facade = new CourierFacade();
    private final ListaEnviosPdfService pdfService = new ListaEnviosPdfService();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Tracking", "Estado", "Remitente", "Destinatario",
                         "Peso (kg)", "Costo (S/)", "Courier", "Fecha"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JLabel lblMensaje = new JLabel(" ");
    private final JButton btnDescargarPdf = new JButton("Descargar PDF");
    private List<Envio> enviosActuales = Collections.emptyList();

    public FrmListarEnvios() {
        setTitle("Flash Courier - Lista de Envíos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(860, 500);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel header = new JPanel(new BorderLayout());
        JLabel titulo = new JLabel("LISTA DE ENVÍOS", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.add(titulo, BorderLayout.CENTER);

        JPanel accion = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarDatos());
        accion.add(btnRefrescar);
        btnDescargarPdf.addActionListener(e -> descargarPdf());
        accion.add(btnDescargarPdf);
        header.add(accion, BorderLayout.EAST);

        main.add(header, BorderLayout.NORTH);

        table.setFillsViewportHeight(true);
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                String estado = t.getValueAt(row, 1) != null ? t.getValueAt(row, 1).toString() : "";
                if (!isSelected) {
                    switch (estado) {
                        case "Registrado" -> c.setBackground(new Color(230, 240, 255));
                        case "En Almacén" -> c.setBackground(new Color(255, 255, 220));
                        case "En Ruta"    -> c.setBackground(new Color(255, 235, 200));
                        case "En Reparto" -> c.setBackground(new Color(255, 220, 220));
                        case "Entregado"  -> c.setBackground(new Color(220, 255, 220));
                        case "Cancelado"  -> c.setBackground(new Color(230, 230, 230));
                        default           -> c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        main.add(scroll, BorderLayout.CENTER);

        lblMensaje.setForeground(Color.RED);
        lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        main.add(lblMensaje, BorderLayout.SOUTH);

        add(main);
        cargarDatos();
    }

    private void cargarDatos() {
        try {
            List<Envio> envios = facade.envios().listarEnvios();
            enviosActuales = envios;
            model.setRowCount(0);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            for (Envio e : envios) {
                model.addRow(new Object[]{
                    e.getCodigoTracking(),
                    e.getEstado(),
                    e.getNombreRemitente(),
                    e.getNombreDestinatario(),
                    String.format("%.2f", e.getPeso()),
                    String.format("%.2f", e.getCosto()),
                    e.getNombreCourier() != null ? e.getNombreCourier() : "-",
                    e.getFechaRegistro() != null ? sdf.format(e.getFechaRegistro()) : "-",
                });
            }

            lblMensaje.setText("Total: " + envios.size() + " envío(s)");
            lblMensaje.setForeground(Color.DARK_GRAY);
        } catch (SQLException ex) {
            lblMensaje.setForeground(Color.RED);
            lblMensaje.setText("Error al cargar datos: " + ex.getMessage());
        }
    }

    private void descargarPdf() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar lista de envíos");
        chooser.setSelectedFile(new File("Lista-Envios.pdf"));
        int resultado = chooser.showSaveDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION) return;

        File destino = chooser.getSelectedFile();
        if (!destino.getName().toLowerCase().endsWith(".pdf")) {
            destino = new File(destino.getParentFile(), destino.getName() + ".pdf");
        }

        try {
            pdfService.generar(enviosActuales, destino);
            JOptionPane.showMessageDialog(this, "Lista guardada en:\n" + destino.getAbsolutePath(),
                    "PDF generado", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo generar el PDF:\n" + ex.getMessage(),
                    "Error al generar el PDF", JOptionPane.ERROR_MESSAGE);
        }
    }
}
