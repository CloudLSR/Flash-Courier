package com.flashcourier.mavenproject.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Boton con esquinas redondeadas y relleno degradado rosa-morado, al
 * estilo synthwave. En hover se aclara y en press se oscurece, sin
 * depender de ninguna libreria externa de Look&amp;Feel. Se usa solo en
 * FrmLogin.
 *
 * @author JoseLSR
 */
public class BotonRetro extends JButton {

    private boolean hover = false;
    private boolean presionado = false;

    public BotonRetro(String texto) {
        super(texto);
        setFont(Tema.fuenteTitulo(13));
        setForeground(Color.WHITE);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            @Override public void mousePressed(MouseEvent e) { presionado = true; repaint(); }
            @Override public void mouseReleased(MouseEvent e) { presionado = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arco = h; // esquinas totalmente redondeadas (pill)

        Color desde = presionado ? Tema.ROSA_OSCURO.darker() : (hover ? brillar(Tema.ROSA) : Tema.ROSA);
        Color hasta = presionado ? Tema.MORADO_OSCURO.darker() : (hover ? brillar(Tema.MORADO) : Tema.MORADO);

        GradientPaint gradiente = new GradientPaint(0, 0, desde, w, h, hasta);
        g2.setPaint(gradiente);
        g2.fillRoundRect(0, 0, w - 1, h - 1, arco, arco);

        if (hover) {
            g2.setStroke(new BasicStroke(1.5f));
            g2.setColor(new Color(255, 255, 255, 90));
            g2.drawRoundRect(0, 0, w - 1, h - 1, arco, arco);
        }

        g2.dispose();

        setForeground(Color.WHITE);
        super.paintComponent(g);
    }

    private static Color brillar(Color c) {
        return new Color(
                Math.min(255, c.getRed() + 30),
                Math.min(255, c.getGreen() + 30),
                Math.min(255, c.getBlue() + 30));
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(Math.max(d.width, 120), Math.max(d.height, 38));
    }
}
