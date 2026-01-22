package com.example.cafemanagement;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ModernCafeUI ui = new ModernCafeUI();
            ui.setVisible(true);
        });
    }
}
