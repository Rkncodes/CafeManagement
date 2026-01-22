package com.example.cafemanagement;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // 1️⃣ Connect to MongoDB
        MongoDBUtil.init("mongodb://localhost:27017", "cafe_db");
        System.out.println("✅ Connected to CafeManagementDB");

        // 2️⃣ Start the UI
        SwingUtilities.invokeLater(() -> {
            ModernCafeUI ui = new ModernCafeUI();
            ui.setVisible(true);
        });
    }
}
