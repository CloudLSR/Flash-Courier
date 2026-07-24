/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.reportes;

import com.flashcourier.mavenproject.modelo.Envio;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Genera el reporte en PDF de la tabla completa de "Lista de Envios" (la
 * pantalla que se abre desde Estadisticas -> "Ver detalle de todos los
 * envios"), para que el Admin o Supervisor lo puedan imprimir o archivar.
 * En horizontal (A4 apaisado) porque son 8 columnas; pagina automaticamente
 * si la lista no entra en una sola hoja.
 *
 * @author JoseLSR
 */
public class ListaEnviosPdfService {

    private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static final float MARGEN_X = 35;
    private static final float MARGEN_SUPERIOR = 45;
    private static final float MARGEN_INFERIOR = 40;
    private static final float ALTO_FILA = 18;

    // Ancho de cada columna (suman el ancho util de una A4 apaisada).
    private static final String[] ENCABEZADOS = {"Tracking", "Estado", "Remitente", "Destinatario",
            "Peso (kg)", "Costo (S/)", "Courier", "Fecha"};
    private static final float[] ANCHOS = {95, 70, 130, 130, 65, 65, 110, 100};

    public void generar(List<Envio> envios, File destino) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = crearPaginaApaisada(doc);
            float anchoUtil = page.getMediaBox().getWidth() - MARGEN_X * 2;
            float y = page.getMediaBox().getHeight() - MARGEN_SUPERIOR;

            PDPageContentStream cs = new PDPageContentStream(doc, page);
            y = escribirEncabezado(cs, page, y, anchoUtil);

            if (envios == null || envios.isEmpty()) {
                cs.setFont(PDType1Font.HELVETICA, 10);
                texto(cs, MARGEN_X, y, "No hay envios registrados.");
            } else {
                cs.setFont(PDType1Font.HELVETICA, 9);
                for (Envio e : envios) {
                    if (y < MARGEN_INFERIOR) {
                        cs.close();
                        page = crearPaginaApaisada(doc);
                        y = page.getMediaBox().getHeight() - MARGEN_SUPERIOR;
                        cs = new PDPageContentStream(doc, page);
                        y = escribirEncabezado(cs, page, y, anchoUtil);
                        cs.setFont(PDType1Font.HELVETICA, 9);
                    }
                    y = escribirFila(cs, y, e);
                }
            }
            cs.close();
            doc.save(destino);
        }
    }

    private PDPage crearPaginaApaisada(PDDocument doc) {
        PDPage page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
        doc.addPage(page);
        return page;
    }

    private float escribirEncabezado(PDPageContentStream cs, PDPage page, float y, float anchoUtil) throws IOException {
        cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
        texto(cs, MARGEN_X, y, "FLASH COURIER - Lista de Envios");
        y -= 22;

        cs.setLineWidth(0.6f);
        cs.moveTo(MARGEN_X, y);
        cs.lineTo(MARGEN_X + anchoUtil, y);
        cs.stroke();
        y -= 16;

        cs.setFont(PDType1Font.HELVETICA_BOLD, 9);
        float x = MARGEN_X;
        for (int i = 0; i < ENCABEZADOS.length; i++) {
            texto(cs, x, y, ENCABEZADOS[i]);
            x += ANCHOS[i];
        }
        y -= 4;
        cs.moveTo(MARGEN_X, y);
        cs.lineTo(MARGEN_X + anchoUtil, y);
        cs.stroke();
        y -= 13;
        return y;
    }

    private float escribirFila(PDPageContentStream cs, float y, Envio e) throws IOException {
        String[] valores = {
                valor(e.getCodigoTracking()),
                valor(e.getEstado()),
                valor(e.getNombreRemitente()),
                valor(e.getNombreDestinatario()),
                String.format("%.2f", e.getPeso()),
                String.format("%.2f", e.getCosto()),
                e.getNombreCourier() != null ? e.getNombreCourier() : "-",
                e.getFechaRegistro() != null ? FORMATO_FECHA.format(e.getFechaRegistro()) : "-"
        };

        float x = MARGEN_X;
        for (int i = 0; i < valores.length; i++) {
            String val = recortar(valores[i], PDType1Font.HELVETICA, 9, ANCHOS[i] - 4);
            texto(cs, x, y, val);
            x += ANCHOS[i];
        }
        return y - ALTO_FILA;
    }

    private String valor(String s) {
        return (s == null || s.isEmpty()) ? "-" : s;
    }

    /** Recorta un texto (con "...") solo si de verdad no entra en el ancho de columna disponible. */
    private String recortar(String texto, PDFont fuente, float tamano, float anchoMaximo) throws IOException {
        if (fuente.getStringWidth(texto) / 1000 * tamano <= anchoMaximo) return texto;
        String recortado = texto;
        while (recortado.length() > 1 && fuente.getStringWidth(recortado + "...") / 1000 * tamano > anchoMaximo) {
            recortado = recortado.substring(0, recortado.length() - 1);
        }
        return recortado + "...";
    }

    private void texto(PDPageContentStream cs, float x, float y, String contenido) throws IOException {
        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.showText(contenido != null ? contenido : "-");
        cs.endText();
    }
}
