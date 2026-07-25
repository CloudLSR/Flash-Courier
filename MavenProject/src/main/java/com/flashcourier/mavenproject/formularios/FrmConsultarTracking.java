/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.formularios;

import com.flashcourier.mavenproject.controlador.CourierFacade;
import com.flashcourier.mavenproject.modelo.Courier;
import com.flashcourier.mavenproject.modelo.Envio;
import com.flashcourier.mavenproject.modelo.HistorialEstado;
import com.flashcourier.mavenproject.modelo.state.EstadoColores;
import com.flashcourier.mavenproject.reportes.ComprobantePdfService;

import javax.swing.*;
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
 * Pantalla de Consultar Tracking. Muestra todos los datos capturados al
 * registrar el envio (remitente, destinatario y paquete) mas el historial
 * completo de movimientos, con hasta 5 filas fijas (una por cada estado del
 * flujo normal: Registrado, En Almacen, En Ruta, En Reparto, Entregado; si
 * el pedido se cancela antes, las filas restantes quedan vacias). Cada fila
 * se pinta con el color de su estado (mismos colores que Estadisticas).
 * Tambien permite descargar un comprobante en PDF con toda esta informacion.
 *
 * @author JoseLSR
 */
public class FrmConsultarTracking extends JFrame {

    private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static final int FILAS_HISTORIAL = 5;
    private static final Color COLOR_FILA_VACIA = new Color(240, 240, 240);

    private final CourierFacade facade = new CourierFacade();
    private final ComprobantePdfService pdfService = new ComprobantePdfService();

    private final JTextField txtCodigo = new JTextField(20);

    // Datos del Envio
    private final JLabel lblCodigo = new JLabel("-");
    private final JLabel lblFechaRegistro = new JLabel("-");
    private final JLabel lblEstado = new JLabel("-");
    private final JLabel lblCosto = new JLabel("-");
    private final JLabel lblCourierAsignado = new JLabel("-");

    // Datos del Remitente
    private final JLabel lblRemNombre = new JLabel("-");
    private final JLabel lblRemDni = new JLabel("-");
    private final JLabel lblRemTelefono = new JLabel("-");
    private final JLabel lblRemDireccion = new JLabel("-");

    // Datos del Destinatario (la direccion de entrega va aca, es la suya)
    private final JLabel lblDestNombre = new JLabel("-");
    private final JLabel lblDestDni = new JLabel("-");
    private final JLabel lblDestTelefono = new JLabel("-");
    private final JLabel lblDestDireccion = new JLabel("-");

    // Datos del Paquete
    private final JLabel lblPeso = new JLabel("-");
    private final JLabel lblDimensiones = new JLabel("-");
    private final JLabel lblDescripcion = new JLabel("-");

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"Estado", "Fecha y Hora", "Observación"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JButton btnDescargarPdf = new JButton("Descargar Comprobante (PDF)");

    private Envio envioActual;
    private List<HistorialEstado> historialActual = Collections.emptyList();

    public FrmConsultarTracking() {
        setTitle("Consultar Tracking - Flash Courier");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.add(new JLabel("Código de seguimiento:"));
        panelBusqueda.add(txtCodigo);
        JButton btnBuscar = new JButton("Consultar");
        panelBusqueda.add(btnBuscar);

        // Columna izquierda: datos del envio + del paquete.
        JPanel panelEnvio = new JPanel(new GridLayout(5, 2, 5, 4));
        panelEnvio.setBorder(BorderFactory.createTitledBorder("Datos del Envío"));
        panelEnvio.add(new JLabel("Código de tracking:")); panelEnvio.add(lblCodigo);
        panelEnvio.add(new JLabel("Fecha de registro:")); panelEnvio.add(lblFechaRegistro);
        panelEnvio.add(new JLabel("Estado actual:")); panelEnvio.add(lblEstado);
        panelEnvio.add(new JLabel("Costo:")); panelEnvio.add(lblCosto);
        panelEnvio.add(new JLabel("Courier asignado:")); panelEnvio.add(lblCourierAsignado);

        JPanel panelPaquete = new JPanel(new GridLayout(3, 2, 5, 4));
        panelPaquete.setBorder(BorderFactory.createTitledBorder("Datos del Paquete"));
        panelPaquete.add(new JLabel("Peso:")); panelPaquete.add(lblPeso);
        panelPaquete.add(new JLabel("Dimensiones:")); panelPaquete.add(lblDimensiones);
        panelPaquete.add(new JLabel("Descripción:")); panelPaquete.add(lblDescripcion);

        JPanel columnaIzquierda = new JPanel();
        columnaIzquierda.setLayout(new BoxLayout(columnaIzquierda, BoxLayout.Y_AXIS));
        columnaIzquierda.add(panelEnvio);
        columnaIzquierda.add(panelPaquete);

        // Columna derecha: datos del remitente + del destinatario.
        JPanel panelRemitente = new JPanel(new GridLayout(4, 2, 5, 4));
        panelRemitente.setBorder(BorderFactory.createTitledBorder("Datos del Remitente"));
        panelRemitente.add(new JLabel("Nombre:")); panelRemitente.add(lblRemNombre);
        panelRemitente.add(new JLabel("DNI:")); panelRemitente.add(lblRemDni);
        panelRemitente.add(new JLabel("Teléfono:")); panelRemitente.add(lblRemTelefono);
        panelRemitente.add(new JLabel("Dirección:")); panelRemitente.add(lblRemDireccion);

        JPanel panelDestinatario = new JPanel(new GridLayout(4, 2, 5, 4));
        panelDestinatario.setBorder(BorderFactory.createTitledBorder("Datos del Destinatario"));
        panelDestinatario.add(new JLabel("Nombre:")); panelDestinatario.add(lblDestNombre);
        panelDestinatario.add(new JLabel("DNI:")); panelDestinatario.add(lblDestDni);
        panelDestinatario.add(new JLabel("Teléfono:")); panelDestinatario.add(lblDestTelefono);
        panelDestinatario.add(new JLabel("Dirección:")); panelDestinatario.add(lblDestDireccion);

        JPanel columnaDerecha = new JPanel();
        columnaDerecha.setLayout(new BoxLayout(columnaDerecha, BoxLayout.Y_AXIS));
        columnaDerecha.add(panelRemitente);
        columnaDerecha.add(panelDestinatario);

        JPanel panelInfo = new JPanel(new GridLayout(1, 2, 10, 0));
        panelInfo.add(columnaIzquierda);
        panelInfo.add(columnaDerecha);

        JTable tabla = new JTable(modeloTabla);
        tabla.setRowHeight(24);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(400);
        tabla.setDefaultRenderer(Object.class, new RenderizadorFilaEstado());
        // Viewport fijo a exactamente 5 filas: nunca va a haber mas de 5 (o
        // menos, si se cancelo antes), asi que no debe quedar espacio de
        // sobra esperando filas que nunca llegan.
        tabla.setPreferredScrollableViewportSize(new Dimension(620, FILAS_HISTORIAL * tabla.getRowHeight()));
        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Historial de Movimientos (máximo 5, uno por estado)"));
        // Se fija el alto maximo al preferido para que, si se agranda la
        // ventana, el espacio extra no lo absorba la tabla sin necesidad.
        scrollTabla.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                scrollTabla.getPreferredSize().height));

        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnDescargarPdf.setEnabled(false);
        panelSur.add(btnDescargarPdf);

        // BoxLayout en vez de BorderLayout: cada bloque ocupa solo su alto
        // preferido (nada se estira para "rellenar" el resto de la ventana).
        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        panelInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollTabla.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelSur.setAlignmentX(Component.LEFT_ALIGNMENT);
        centro.add(panelInfo);
        centro.add(Box.createVerticalStrut(6));
        centro.add(scrollTabla);
        centro.add(panelSur);

        add(panelBusqueda, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);

        vaciarTabla();
        pack();
        setLocationRelativeTo(null);

        btnBuscar.addActionListener(e -> consultar());
        txtCodigo.addActionListener(e -> consultar());
        btnDescargarPdf.addActionListener(e -> descargarComprobante());
    }

    private void consultar() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa un código de seguimiento.", "Dato requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Envio envio = facade.envios().consultarTracking(codigo);
            if (envio == null) {
                JOptionPane.showMessageDialog(this, "No se encontró ningún envío con ese código.", "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
                limpiar();
                return;
            }
            envioActual = envio;

            lblCodigo.setText(envio.getCodigoTracking());
            lblFechaRegistro.setText(envio.getFechaRegistro() != null ? FORMATO_FECHA.format(envio.getFechaRegistro()) : "-");
            lblEstado.setText(envio.getEstado());
            lblCosto.setText("S/ " + String.format("%.2f", envio.getCosto()));

            // El courier solo se asigna al confirmar la entrega (ver
            // FrmActualizarEstado): mientras el paquete siga en nuestras
            // manos, no hay a quien hacer responsable todavia.
            String nombreCourier = resolverNombreCourier(envio.getIdCourier());
            envio.setNombreCourier(nombreCourier);
            lblCourierAsignado.setText(nombreCourier == null ? "Aún sin asignar (pedido en curso)" : nombreCourier);

            lblRemNombre.setText(valor(envio.getNombreRemitente()));
            lblRemDni.setText(valor(envio.getDniRemitente()));
            lblRemTelefono.setText(valor(envio.getTelefonoRemitente()));
            lblRemDireccion.setText(valor(envio.getDireccionRemitente()));

            lblDestNombre.setText(valor(envio.getNombreDestinatario()));
            lblDestDni.setText(valor(envio.getDniDestinatario()));
            lblDestTelefono.setText(valor(envio.getTelefonoDestinatario()));
            lblDestDireccion.setText(valor(envio.getDireccionDestino()));

            lblPeso.setText(String.format("%.2f kg", envio.getPeso()));
            lblDimensiones.setText(valor(envio.getDimensiones()));
            lblDescripcion.setText(valor(envio.getDescripcionPaquete()));

            historialActual = facade.envios().obtenerHistorial(envio.getIdEnvio());
            llenarTabla(historialActual);
            btnDescargarPdf.setEnabled(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al consultar el envío:\n" + ex.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String valor(String texto) {
        return (texto == null || texto.isEmpty()) ? "-" : texto;
    }

    /**
     * Busca el nombre del courier asignado. Devuelve null si el envio
     * todavia no tiene courier (aun no llega a la fase de entrega), que es
     * la senal para mostrar "Aun sin asignar" en vez de un dato falso.
     */
    private String resolverNombreCourier(Integer idCourier) {
        if (idCourier == null) return null;
        try {
            List<Courier> couriers = facade.listarCouriers();
            for (Courier c : couriers) {
                if (c.getIdCourier() == idCourier) return c.getNombre();
            }
        } catch (SQLException ignored) {
            // Si falla la consulta, se cae al valor por defecto de abajo.
        }
        return "Courier #" + idCourier;
    }

    /** Llena las 5 filas fijas con los movimientos reales; las que sobran quedan vacias. */
    private void llenarTabla(List<HistorialEstado> historial) {
        modeloTabla.setRowCount(0);
        for (int i = 0; i < FILAS_HISTORIAL; i++) {
            if (historial != null && i < historial.size()) {
                HistorialEstado h = historial.get(i);
                modeloTabla.addRow(new Object[]{h.getEstado(),
                        h.getFechaHora() != null ? FORMATO_FECHA.format(h.getFechaHora()) : "-",
                        h.getObservacion() == null || h.getObservacion().isEmpty() ? "-" : h.getObservacion()});
            } else {
                modeloTabla.addRow(new Object[]{"", "", ""});
            }
        }
    }

    private void vaciarTabla() {
        llenarTabla(Collections.emptyList());
    }

    private void descargarComprobante() {
        if (envioActual == null) return;

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar comprobante");
        chooser.setSelectedFile(new File("Comprobante-" + envioActual.getCodigoTracking() + ".pdf"));
        int resultado = chooser.showSaveDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION) return;

        File destino = chooser.getSelectedFile();
        if (!destino.getName().toLowerCase().endsWith(".pdf")) {
            destino = new File(destino.getParentFile(), destino.getName() + ".pdf");
        }

        try {
            pdfService.generar(envioActual, historialActual, destino);
            JOptionPane.showMessageDialog(this, "Comprobante guardado en:\n" + destino.getAbsolutePath(),
                    "Comprobante generado", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo generar el PDF:\n" + ex.getMessage(),
                    "Error al generar comprobante", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar() {
        envioActual = null;
        historialActual = Collections.emptyList();
        lblCodigo.setText("-");
        lblFechaRegistro.setText("-");
        lblEstado.setText("-");
        lblCosto.setText("-");
        lblCourierAsignado.setText("-");
        lblRemNombre.setText("-");
        lblRemDni.setText("-");
        lblRemTelefono.setText("-");
        lblRemDireccion.setText("-");
        lblDestNombre.setText("-");
        lblDestDni.setText("-");
        lblDestTelefono.setText("-");
        lblDestDireccion.setText("-");
        lblPeso.setText("-");
        lblDimensiones.setText("-");
        lblDescripcion.setText("-");
        vaciarTabla();
        btnDescargarPdf.setEnabled(false);
    }

    /**
     * Pinta cada fila con el color de su estado (los mismos colores que usa
     * Estadisticas, via EstadoColores). El texto va en una sola linea, como
     * antes (si es muy largo se recorta visualmente en pantalla); el PDF del
     * comprobante no lo recorta, ahi el texto siempre sale completo.
     * Las filas todavia no alcanzadas quedan en gris claro.
     */
    private class RenderizadorFilaEstado extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                          boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            Object estadoFila = table.getValueAt(row, 0);
            String estado = estadoFila == null ? "" : estadoFila.toString();
            boolean filaVacia = estado.isEmpty();

            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

            if (!isSelected) {
                label.setBackground(filaVacia ? COLOR_FILA_VACIA : EstadoColores.de(estado));
                label.setForeground(filaVacia ? Color.GRAY : Color.BLACK);
            }

            String texto = value == null ? "" : value.toString();
            if (texto.isEmpty()) {
                label.setText(filaVacia ? "-" : texto);
            }

            return label;
        }
    }
}
