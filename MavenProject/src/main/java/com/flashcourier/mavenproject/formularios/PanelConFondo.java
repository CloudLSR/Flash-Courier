/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.formularios;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Panel que dibuja una imagen de fondo escalada para cubrir todo su
 * espacio (estilo "cover": se agranda y recorta lo que sobra, sin
 * deformarse). Los componentes que se agreguen encima deben quedar
 * no-opacos (setOpaque(false)) para que la imagen se vea detras.
 *
 * Se usa unicamente en FrmMenu.
 *
 * @author JoseLSR
 */
public class PanelConFondo extends JPanel {

    private Image imagenFondo;

    public PanelConFondo(String rutaClasspath) {
        this(rutaClasspath, new BorderLayout());
    }

    public PanelConFondo(String rutaClasspath, LayoutManager layout) {
        super(layout);
        setOpaque(true);
        URL url = getClass().getResource(rutaClasspath);
        if (url != null) {
            imagenFondo = new ImageIcon(url).getImage();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagenFondo == null) {
            return;
        }
        int ancho = getWidth();
        int alto = getHeight();
        int anchoImg = imagenFondo.getWidth(this);
        int altoImg = imagenFondo.getHeight(this);
        if (anchoImg <= 0 || altoImg <= 0) {
            return;
        }
        double escala = Math.max((double) ancho / anchoImg, (double) alto / altoImg);
        int nuevoAncho = (int) Math.ceil(anchoImg * escala);
        int nuevoAlto = (int) Math.ceil(altoImg * escala);
        int x = (ancho - nuevoAncho) / 2;
        int y = (alto - nuevoAlto) / 2;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.drawImage(imagenFondo, x, y, nuevoAncho, nuevoAlto, this);
        g2.dispose();
    }
}
