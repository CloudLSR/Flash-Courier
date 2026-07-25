/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.formularios;

import com.flashcourier.mavenproject.controlador.CourierFacade;
import com.flashcourier.mavenproject.modelo.ResumenEstadisticas;
import com.flashcourier.mavenproject.modelo.state.EstadoColores;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Dashboard de estadisticas: disponible para todo el personal (solo lectura).
 * Muestra el total de pedidos, los ingresos estimados (excluyendo los
 * cancelados), el total de couriers y un grafico de barras con el desglose
 * de envios por estado.
 *
 * @author JoseLSR
 */
public class FrmEstadisticas extends JFrame {

    private static final Map<String, Color> COLORES_ESTADO = EstadoColores.COLORES_ESTADO;

    private final CourierFacade facade = new CourierFacade();
    private final JPanel panelTarjetas = new JPanel(new GridLayout(1, 4, 10, 0));
    private final PanelGrafico panelGrafico = new PanelGrafico();
    private final JLabel lblMensaje = new JLabel(" ");

    public FrmEstadisticas() {
        setTitle("Estadísticas - Flash Courier");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(620, 560);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(12, 12));
        main.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel titulo = new JLabel("Panel de Estadísticas", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        main.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(12, 12));
        centro.add(panelTarjetas, BorderLayout.NORTH);

        JPanel panelChart = new JPanel(new BorderLayout(6, 6));
        panelChart.setBorder(BorderFactory.createTitledBorder("Pedidos por estado"));
        panelChart.add(panelGrafico, BorderLayout.CENTER);
        centro.add(panelChart, BorderLayout.CENTER);

        main.add(centro, BorderLayout.CENTER);

        lblMensaje.setForeground(Color.RED);
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.add(lblMensaje, BorderLayout.WEST);

        JButton btnVerTodos = new JButton("Ver detalle de todos los envíos");
        btnVerTodos.addActionListener(e -> new FrmListarEnvios().setVisible(true));
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> cargarDatos());
        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBtn.add(btnVerTodos);
        panelBtn.add(btnActualizar);
        panelSur.add(panelBtn, BorderLayout.EAST);
        main.add(panelSur, BorderLayout.SOUTH);

        add(main);

        cargarDatos();
    }

    private void cargarDatos() {
        panelTarjetas.removeAll();
        lblMensaje.setText(" ");
        try {
            ResumenEstadisticas resumen = facade.obtenerResumenEstadisticas();
            Map<String, Integer> conteo = facade.envios().contarPorEstado();
            int cancelados = conteo.getOrDefault("Cancelado", 0);

            NumberFormat moneda = NumberFormat.getCurrencyInstance(new Locale("es", "PE"));

            panelTarjetas.add(tarjeta(String.valueOf(resumen.getTotalEnvios()), "Pedidos totales"));
            panelTarjetas.add(tarjeta(moneda.format(resumen.getIngresosEstimados()), "Ingresos estimados"));
            panelTarjetas.add(tarjeta(String.valueOf(cancelados), "Cancelados"));
            panelTarjetas.add(tarjeta(String.valueOf(resumen.getTotalCouriers()), "Personal de entrega"));

            panelGrafico.setDatos(conteo);
        } catch (SQLException ex) {
            lblMensaje.setText("No se pudieron cargar las estadísticas: " + ex.getMessage());
        }
        panelTarjetas.revalidate();
        panelTarjetas.repaint();
    }

    private JPanel tarjeta(String numero, String etiqueta) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(12, 6, 12, 6)));

        JLabel lblNumero = new JLabel(numero, SwingConstants.CENTER);
        lblNumero.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblNumero.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblEtiqueta = new JLabel(etiqueta, SwingConstants.CENTER);
        lblEtiqueta.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblEtiqueta.setForeground(Color.GRAY);
        lblEtiqueta.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblNumero);
        card.add(lblEtiqueta);
        return card;
    }

    /** Grafico de barras simple (sin dependencias externas) para el desglose de envios por estado. */
    private static class PanelGrafico extends JPanel {
        private Map<String, Integer> datos = new LinkedHashMap<>();

        PanelGrafico() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(500, 260));
        }

        void setDatos(Map<String, Integer> datos) {
            this.datos = datos;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (datos == null || datos.isEmpty()) {
                g.drawString("Sin datos todavía", 15, 20);
                return;
            }
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int max = datos.values().stream().mapToInt(Integer::intValue).max().orElse(1);
            if (max == 0) max = 1;

            int margenInferior = 34;
            int alturaMax = getHeight() - margenInferior - 20;
            int n = COLORES_ESTADO.size();
            int anchoBarra = Math.max(30, (getWidth() - 20) / (n * 2));
            int x = 15;

            for (Map.Entry<String, Color> entry : COLORES_ESTADO.entrySet()) {
                String estado = entry.getKey();
                int cantidad = datos.getOrDefault(estado, 0);
                int alto = (int) (((double) cantidad / max) * alturaMax);
                int y = getHeight() - margenInferior - alto;

                g2.setColor(entry.getValue());
                g2.fillRoundRect(x, y, anchoBarra, Math.max(alto, 1), 6, 6);

                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2.drawString(String.valueOf(cantidad), x + anchoBarra / 2 - 5, y - 6);

                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                String etiqueta = estado.length() > 10 ? estado.substring(0, 9) + "." : estado;
                g2.drawString(etiqueta, x - 2, getHeight() - margenInferior + 14);

                x += anchoBarra * 2;
            }
        }
    }
}
