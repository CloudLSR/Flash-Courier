/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject.reportes;

import com.flashcourier.mavenproject.modelo.Envio;
import com.flashcourier.mavenproject.modelo.HistorialEstado;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Genera el comprobante en PDF de un envio: todos los datos registrados
 * (envio, remitente, destinatario, paquete) mas el historial completo de
 * estados. Sirve para entregarlo/imprimirlo aunque el envio ya este en un
 * estado final (Entregado o Cancelado): el codigo de tracking siempre queda
 * en el sistema, asi que el comprobante puede regenerarse en cualquier
 * momento. El texto de observacion se ajusta en varias lineas (no se corta
 * con "...") para que siempre se lea completo.
 *
 * @author JoseLSR
 */
public class ComprobantePdfService {

    private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static final float MARGEN_X = 50;

    public void generar(Envio envio, List<HistorialEstado> historial, File destino) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            float y = page.getMediaBox().getHeight() - 60;
            float anchoUtil = page.getMediaBox().getWidth() - MARGEN_X * 2;

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.setFont(PDType1Font.HELVETICA_BOLD, 18);
                cs.beginText();
                cs.newLineAtOffset(MARGEN_X, y);
                cs.showText("FLASH COURIER");
                cs.endText();
                y -= 20;

                cs.setFont(PDType1Font.HELVETICA, 11);
                cs.beginText();
                cs.newLineAtOffset(MARGEN_X, y);
                cs.showText("Comprobante de seguimiento de envio");
                cs.endText();
                y -= 22;

                y = linea(cs, y, anchoUtil);
                y -= 15;

                y = campo(cs, y, "Codigo de tracking:", envio.getCodigoTracking());
                y = campo(cs, y, "Fecha de registro:", envio.getFechaRegistro() != null ? FORMATO_FECHA.format(envio.getFechaRegistro()) : "-");
                y = campo(cs, y, "Estado actual:", envio.getEstado());
                y = campo(cs, y, "Costo del envio:", "S/ " + String.format("%.2f", envio.getCosto()));
                y = campo(cs, y, "Courier asignado:", envio.getNombreCourier() != null ? envio.getNombreCourier() : "Aun sin asignar (pedido en curso)");

                y -= 6;
                cs.setFont(PDType1Font.HELVETICA_BOLD, 11);
                texto(cs, MARGEN_X, y, "Remitente");
                y -= 15;
                y = campo(cs, y, "Nombre:", envio.getNombreRemitente());
                y = campo(cs, y, "DNI:", envio.getDniRemitente());
                y = campo(cs, y, "Telefono:", envio.getTelefonoRemitente());
                y = campo(cs, y, "Direccion:", envio.getDireccionRemitente());

                y -= 6;
                cs.setFont(PDType1Font.HELVETICA_BOLD, 11);
                texto(cs, MARGEN_X, y, "Destinatario");
                y -= 15;
                y = campo(cs, y, "Nombre:", envio.getNombreDestinatario());
                y = campo(cs, y, "DNI:", envio.getDniDestinatario());
                y = campo(cs, y, "Telefono:", envio.getTelefonoDestinatario());
                y = campo(cs, y, "Direccion:", envio.getDireccionDestino());

                y -= 6;
                cs.setFont(PDType1Font.HELVETICA_BOLD, 11);
                texto(cs, MARGEN_X, y, "Paquete");
                y -= 15;
                y = campo(cs, y, "Peso:", String.format("%.2f kg", envio.getPeso()));
                y = campo(cs, y, "Dimensiones:", envio.getDimensiones());
                y = campo(cs, y, "Descripcion:", envio.getDescripcionPaquete());

                y -= 8;
                y = linea(cs, y, anchoUtil);
                y -= 22;

                cs.setFont(PDType1Font.HELVETICA_BOLD, 13);
                texto(cs, MARGEN_X, y, "Historial de movimientos");
                y -= 20;

                float colEstado = MARGEN_X;
                float colFecha = MARGEN_X + 100;
                float colObs = MARGEN_X + 220;
                float anchoObs = anchoUtil - (colObs - MARGEN_X);

                cs.setFont(PDType1Font.HELVETICA_BOLD, 10);
                texto(cs, colEstado, y, "Estado");
                texto(cs, colFecha, y, "Fecha y hora");
                texto(cs, colObs, y, "Observacion");
                y -= 6;
                y = linea(cs, y, anchoUtil);
                y -= 14;

                cs.setFont(PDType1Font.HELVETICA, 10);
                if (historial == null || historial.isEmpty()) {
                    texto(cs, colEstado, y, "Sin movimientos registrados.");
                    y -= 16;
                } else {
                    for (HistorialEstado h : historial) {
                        if (y < 60) break; // proteccion basica ante historiales muy largos
                        String obs = h.getObservacion() == null || h.getObservacion().isEmpty() ? "-" : h.getObservacion();
                        List<String> lineasObs = ajustarTexto(obs, PDType1Font.HELVETICA, 10, anchoObs);

                        float yFila = y;
                        texto(cs, colEstado, yFila, h.getEstado());
                        texto(cs, colFecha, yFila, h.getFechaHora() != null ? FORMATO_FECHA.format(h.getFechaHora()) : "-");
                        for (String linea : lineasObs) {
                            texto(cs, colObs, yFila, linea);
                            yFila -= 13;
                        }
                        y = Math.min(yFila, y - 16);
                    }
                }

                y -= 20;
                cs.setFont(PDType1Font.HELVETICA_OBLIQUE, 8);
                texto(cs, MARGEN_X, Math.max(y, 40), "Comprobante generado automaticamente por el sistema Flash Courier.");
            }

            doc.save(destino);
        }
    }

    private float campo(PDPageContentStream cs, float y, String etiqueta, String valor) throws IOException {
        cs.setFont(PDType1Font.HELVETICA_BOLD, 10);
        texto(cs, MARGEN_X, y, etiqueta);
        cs.setFont(PDType1Font.HELVETICA, 10);
        texto(cs, MARGEN_X + 140, y, (valor == null || valor.isEmpty()) ? "-" : valor);
        return y - 16;
    }

    private void texto(PDPageContentStream cs, float x, float y, String contenido) throws IOException {
        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.showText(contenido != null ? contenido : "-");
        cs.endText();
    }

    private float linea(PDPageContentStream cs, float y, float ancho) throws IOException {
        cs.setLineWidth(0.6f);
        cs.moveTo(MARGEN_X, y);
        cs.lineTo(MARGEN_X + ancho, y);
        cs.stroke();
        return y;
    }

    /** Parte un texto en varias lineas para que quepa en el ancho disponible, sin cortar palabras ni usar "...". */
    private List<String> ajustarTexto(String texto, PDFont fuente, float tamano, float anchoMaximo) throws IOException {
        List<String> lineas = new ArrayList<>();
        if (texto == null || texto.isEmpty()) {
            lineas.add("-");
            return lineas;
        }
        String[] palabras = texto.split(" ");
        StringBuilder actual = new StringBuilder();
        for (String palabra : palabras) {
            String candidata = actual.length() == 0 ? palabra : actual + " " + palabra;
            float ancho = fuente.getStringWidth(candidata) / 1000 * tamano;
            if (ancho > anchoMaximo && actual.length() > 0) {
                lineas.add(actual.toString());
                actual = new StringBuilder(palabra);
            } else {
                actual = new StringBuilder(candidata);
            }
        }
        if (actual.length() > 0) lineas.add(actual.toString());
        return lineas;
    }
}
