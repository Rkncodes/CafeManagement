package com.example.cafemanagement;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Initialize MongoDB connection
        MongoDBUtil.init("mongodb://localhost:27017", "cafe_db");

        // Launch UI
        SwingUtilities.invokeLater(() -> {
            ModernCafeUI ui = new ModernCafeUI();
            ui.setVisible(true);
        });
    }
}
