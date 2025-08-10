package com.example.cafemanagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CafeUI extends JFrame {
    private JTextField nameField, priceField, categoryField;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private ProductDAO productDAO;

    public CafeUI() {
        MongoDBUtil.init("mongodb://localhost:27017", "cafe_db");
        productDAO = new ProductDAO();

        setTitle("Cafe Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Form
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.add(new JLabel("Product Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Price:"));
        priceField = new JTextField();
        formPanel.add(priceField);

        formPanel.add(new JLabel("Category:"));
        categoryField = new JTextField();
        formPanel.add(categoryField);

        JButton saveButton = new JButton("Save Product");
        saveButton.addActionListener(e -> {
            saveProduct();
            loadProducts();
        });
        formPanel.add(saveButton);

        mainPanel.add(formPanel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Name", "Price", "Category"};
        tableModel = new DefaultTableModel(columnNames, 0);
        productTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Delete button
        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(e -> deleteSelectedProduct());
        mainPanel.add(deleteButton, BorderLayout.SOUTH);

        add(mainPanel);
        loadProducts();
    }

    private void saveProduct() {
        String name = nameField.getText();
        double price = Double.parseDouble(priceField.getText());
        String category = categoryField.getText();
        productDAO.saveProduct(new Product(name, price, category));

        nameField.setText("");
        priceField.setText("");
        categoryField.setText("");
    }

    private void loadProducts() {
        tableModel.setRowCount(0);
        for (Product p : productDAO.getAllProducts()) {
            tableModel.addRow(new Object[]{p.getName(), p.getPrice(), p.getCategory()});
        }
    }

    private void deleteSelectedProduct() {
        int row = productTable.getSelectedRow();
        if (row != -1) {
            String productName = (String) tableModel.getValueAt(row, 0);
            productDAO.deleteProduct(productName);
            loadProducts();
        }
    }
}
