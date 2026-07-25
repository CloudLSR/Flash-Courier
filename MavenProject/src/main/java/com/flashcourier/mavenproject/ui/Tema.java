package com.flashcourier.mavenproject.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Paleta y utilidades visuales "retrowave" para la pantalla de Login de
 * Flash Courier. Los colores estan tomados del fondo retro-future
 * (moradas y fucsias del atardecer synthwave) mas un cian de acento
 * clasico del genero para dar contraste. Todo se aplica sobre Swing
 * puro, sin dependencias externas.
 *
 * Nota: a proposito NO se toca UIManager (nada de estilo global), para
 * que el resto de pantallas de la app no se vean afectadas por esta
 * paleta; solo se usa manualmente dentro de FrmLogin.
 *
 * @author JoseLSR
 */
public final class Tema {

    private Tema() {
    }

    // --- Paleta base -------------------------------------------------
    public static final Color BG = new Color(0x0D, 0x02, 0x1C);          // fondo general, casi negro-morado
    public static final Color BG_PANEL = new Color(0x18, 0x0A, 0x36, 235); // panel translucido sobre el fondo
    public static final Color BG_CAMPO = new Color(0x22, 0x0F, 0x45);     // fondo de inputs

    public static final Color ROSA = new Color(0xFF, 0x2E, 0x97);        // acento primario (botones)
    public static final Color ROSA_OSCURO = new Color(0xC3, 0x26, 0x8A); // tomado del atardecer
    public static final Color MORADO = new Color(0x8A, 0x3F, 0xFC);      // acento secundario
    public static final Color MORADO_OSCURO = new Color(0x49, 0x02, 0x75);
    public static final Color CIAN = new Color(0x2C, 0xE8, 0xF5);        // acento frio / info

    public static final Color TEXTO = new Color(0xF3, 0xEA, 0xFF);
    public static final Color TEXTO_TENUE = new Color(0xB6, 0xA3, 0xD9);
    public static final Color BORDE = new Color(0x55, 0x2E, 0x8C);

    // --- Tipografia ----------------------------------------------------
    public static final String FUENTE = "Segoe UI";

    public static Font fuenteTitulo(int tamano) {
        return new Font(FUENTE, Font.BOLD, tamano);
    }

    public static Font fuenteTexto(int tamano) {
        return new Font(FUENTE, Font.PLAIN, tamano);
    }

    public static Font fuenteTextoItalica(int tamano) {
        return new Font(FUENTE, Font.ITALIC, tamano);
    }

    /** Aplica el estilo oscuro estandar a un JTextField o JPasswordField. */
    public static void estilizarCampo(JTextField campo) {
        campo.setBackground(BG_CAMPO);
        campo.setForeground(TEXTO);
        campo.setCaretColor(ROSA);
        campo.setFont(fuenteTexto(13));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1), new EmptyBorder(6, 8, 6, 8)));
    }

    /** Icono del logo de Flash Courier escalado al alto indicado (mantiene proporcion). */
    public static ImageIcon logo(int altoDeseado) {
        try {
            ImageIcon original = new ImageIcon(Tema.class.getResource("/img/logo.png"));
            int ancho = (int) (original.getIconWidth() * (altoDeseado / (double) original.getIconHeight()));
            Image escalada = original.getImage().getScaledInstance(ancho, altoDeseado, Image.SCALE_SMOOTH);
            return new ImageIcon(escalada);
        } catch (Exception ex) {
            return null;
        }
    }

    /** Imagen de fondo del login (mismo banner 1 que ya usa el resto del login). */
    public static Image imagenFondo() {
        try {
            return new ImageIcon(Tema.class.getResource("/img/banner-login.png")).getImage();
        } catch (Exception ex) {
            return null;
        }
    }
}
