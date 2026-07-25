package com.flashcourier.mavenproject.ui;

import javax.swing.*;
import java.awt.*;

/**
 * JPanel que dibuja el banner de login como fondo, escalado para cubrir
 * todo el panel ("cover"), con un velo oscuro semitransparente encima
 * para que el texto y los controles sigan siendo legibles. Como el
 * banner es panoramico, sobra ancho respecto a la ventana; ese sobrante
 * se recorre lentamente de un lado a otro con un Timer, dando un efecto
 * de paneo (como un fondo parallax). Se usa solo en FrmLogin.
 *
 * @author JoseLSR
 */
public class PanelFondo extends JPanel {

    /** Cuanto avanza el paneo por frame (0..1 del recorrido disponible). Mientras mas chico, mas lento. */
    private static final float VELOCIDAD_PANEO = 0.0007f;
    private static final int INTERVALO_MS = -150;

    private final Image fondo;
    private final float opacidadVelo;

    private float desplazamiento = 0f; // 0 = extremo izquierdo, 1 = extremo derecho
    private float direccion = 1f;      // 1 = avanzando a la derecha, -1 = retrocediendo
    private Timer timerPaneo;

    public PanelFondo(LayoutManager layout) {
        this(layout, 0.18f);
    }

    /** @param opacidadVelo 0 = imagen nitida, 1 = fondo solido (sin imagen visible). */
    public PanelFondo(LayoutManager layout, float opacidadVelo) {
        super(layout);
        this.fondo = Tema.imagenFondo();
        this.opacidadVelo = opacidadVelo;
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        int w = getWidth();
        int h = getHeight();

        if (fondo != null) {
            int imgW = fondo.getWidth(this);
            int imgH = fondo.getHeight(this);
            if (imgW > 0 && imgH > 0) {
                double escala = Math.max(w / (double) imgW, h / (double) imgH);
                int destW = (int) Math.ceil(imgW * escala);
                int destH = (int) Math.ceil(imgH * escala);

                int margenX = destW - w; // cuanto sobra a los costados para poder panear
                int x;
                if (margenX > 0) {
                    x = -Math.round(margenX * desplazamiento);
                } else {
                    x = (w - destW) / 2;
                }
                int y = (h - destH) / 2;
                g2.drawImage(fondo, x, y, destW, destH, this);
            } else {
                g2.setColor(Tema.BG);
                g2.fillRect(0, 0, w, h);
            }
        } else {
            g2.setColor(Tema.BG);
            g2.fillRect(0, 0, w, h);
        }

        // Velo oscuro para contraste con los controles
        g2.setColor(new Color(0x0D, 0x02, 0x1C, Math.round(opacidadVelo * 255)));
        g2.fillRect(0, 0, w, h);

        g2.dispose();
    }

    /** Arranca el paneo cuando la ventana se muestra. */
    @Override
    public void addNotify() {
        super.addNotify();
        if (fondo != null && timerPaneo == null) {
            timerPaneo = new Timer(INTERVALO_MS, e -> {
                desplazamiento += direccion * VELOCIDAD_PANEO;
                if (desplazamiento >= 1f) {
                    desplazamiento = 1f;
                    direccion = -1f;
                } else if (desplazamiento <= 0f) {
                    desplazamiento = 0f;
                    direccion = 1f;
                }
                repaint();
            });
            timerPaneo.start();
        }
    }

    /** Detiene el paneo cuando se cierra la ventana, para no dejar el Timer corriendo en segundo plano. */
    @Override
    public void removeNotify() {
        if (timerPaneo != null) {
            timerPaneo.stop();
            timerPaneo = null;
        }
        super.removeNotify();
    }
}
