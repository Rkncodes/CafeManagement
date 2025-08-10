package com.example.cafemanagement;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // init DB once for the app
        MongoDBUtil.init("mongodb://localhost:27017", "cafe_db");

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Cafe Management");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 650);
            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Products", new ProductPanel());
            tabs.addTab("Orders", new OrderPanel());
            frame.getContentPane().add(tabs);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        // Note: we don't call MongoDBUtil.close() here — app lifetime until window closes.
    }
}
