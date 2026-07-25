package com.flashcourier.mavenproject.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Boton con forma de pildora (esquinas completamente redondeadas), sin
 * ningun color de marca: usa los mismos tonos grises/blancos del boton
 * de Swing por defecto, solo que con la silueta ovalada en vez de
 * rectangular. Pensado para los botones de navegacion del Menu
 * Principal (FrmMenu). Respeta el estado habilitado/deshabilitado y
 * muestra un borde de foco para navegacion con teclado.
 *
 * @author JoseLSR
 */
public class BotonPill extends JButton {

    private boolean hover = false;
    private boolean presionado = false;

    public BotonPill(String texto) {
        super(texto);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            @Override public void mousePressed(MouseEvent e) { presionado = true; repaint(); }
            @Override public void mouseReleased(MouseEvent e) { presionado = false; repaint(); }
        });
        addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { repaint(); }
            @Override public void focusLost(FocusEvent e) { repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arco = h; // esquinas totalmente redondeadas (pildora)

        Color relleno;
        Color borde;
        if (!isEnabled()) {
            relleno = new Color(0xE4, 0xE4, 0xE4);
            borde = new Color(0xBD, 0xBD, 0xBD);
        } else if (presionado) {
            relleno = new Color(0xD8, 0xD8, 0xD8);
            borde = new Color(0x8A, 0x8A, 0x8A);
        } else if (hover) {
            relleno = new Color(0xEF, 0xEF, 0xEF);
            borde = new Color(0x8A, 0x8A, 0x8A);
        } else {
            relleno = Color.WHITE;
            borde = new Color(0x9A, 0x9A, 0x9A);
        }

        g2.setColor(relleno);
        g2.fillRoundRect(0, 0, w - 1, h - 1, arco, arco);

        boolean enFoco = isFocusOwner();
        g2.setStroke(new BasicStroke(enFoco ? 2f : 1.2f));
        g2.setColor(enFoco ? new Color(0x2C, 0x8C, 0xE8) : borde);
        g2.drawRoundRect(0, 0, w - 1, h - 1, arco, arco);

        g2.dispose();

        setForeground(isEnabled() ? Color.BLACK : new Color(0x9A, 0x9A, 0x9A));
        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(Math.max(d.width, 160), Math.max(d.height, 40));
    }
}
