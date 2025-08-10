package com.example.cafemanagement;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            CafeUI ui = new CafeUI();
            ui.setVisible(true);
        });
    }
}
