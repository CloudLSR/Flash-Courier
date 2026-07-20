/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.flashcourier.mavenproject;

import com.flashcourier.mavenproject.formularios.FrmLogin;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author JoseLSR
 */
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new FrmLogin().setVisible(true));
    }
}
