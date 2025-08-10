package com.example.cafemanagement;

import javax.swing.*;
import java.awt.*;

public class CafeUI extends JFrame {
    private JTextField nameField, priceField, categoryField;
    private ProductDAO productDAO;

    public CafeUI() {
        // MongoDB init
        MongoDBUtil.init("mongodb://localhost:27017", "cafe_db");
        productDAO = new ProductDAO();

        setTitle("Cafe Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        panel.add(new JLabel("Product Name:"));
        nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Price:"));
        priceField = new JTextField();
        panel.add(priceField);

        panel.add(new JLabel("Category:"));
        categoryField = new JTextField();
        panel.add(categoryField);

        JButton saveButton = new JButton("Save Product");
        saveButton.addActionListener(e -> saveProduct());
        panel.add(saveButton);

        add(panel);
    }

    private void saveProduct() {
        try {
            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            String category = categoryField.getText();

            productDAO.addProduct(new Product(name, price, category));

            JOptionPane.showMessageDialog(this, "Product saved successfully!");
            nameField.setText("");
            priceField.setText("");
            categoryField.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
