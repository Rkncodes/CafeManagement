package com.example.cafemanagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductPanel extends JPanel {
    private JTextField nameField, priceField, categoryField, stockField;
    private JTable table;
    private DefaultTableModel model;
    private ProductDAO dao;
    private String selectedProductId = null;

    public ProductPanel() {
        dao = new ProductDAO();
        setLayout(new BorderLayout(10,10));

        // form
        JPanel form = new JPanel(new GridLayout(5,2,8,8));
        form.setBorder(BorderFactory.createTitledBorder("Product Details"));
        form.add(new JLabel("Name:")); nameField = new JTextField(); form.add(nameField);
        form.add(new JLabel("Price:")); priceField = new JTextField(); form.add(priceField);
        form.add(new JLabel("Category:")); categoryField = new JTextField(); form.add(categoryField);
        form.add(new JLabel("Stock:")); stockField = new JTextField(); stockField.setText("0"); form.add(stockField);

        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> onSave());
        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> clearForm());
        form.add(saveBtn);
        form.add(clearBtn);

        add(form, BorderLayout.NORTH);

        // table
        String[] cols = {"ID","Name","Price","Category","Stock"};
        model = new DefaultTableModel(cols,0) {
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0); // hide id column visually
        table.getSelectionModel().addListSelectionListener(e -> onRowSelect());
        JScrollPane sp = new JScrollPane(table);
        add(sp, BorderLayout.CENTER);

        // actions panel
        JPanel actions = new JPanel();
        JButton refresh = new JButton("Refresh"); refresh.addActionListener(e -> loadProducts()); actions.add(refresh);
        JButton delete = new JButton("Delete"); delete.addActionListener(e -> onDelete()); actions.add(delete);
        add(actions, BorderLayout.SOUTH);

        loadProducts();
    }

    private void onSave() {
        try {
            String name = nameField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            String category = categoryField.getText().trim();
            int stock = Integer.parseInt(stockField.getText().trim());

            if (name.isEmpty()) { JOptionPane.showMessageDialog(this, "Name required"); return; }

            if (selectedProductId == null) {
                Product p = new Product(name, price, category, stock);
                dao.insert(p);
                JOptionPane.showMessageDialog(this, "Product added");
            } else {
                Product p = new Product(selectedProductId, name, price, category, stock);
                dao.update(p);
                JOptionPane.showMessageDialog(this, "Product updated");
            }
            clearForm();
            loadProducts();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number for price/stock");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void onRowSelect() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        selectedProductId = (String) model.getValueAt(row, 0);
        nameField.setText((String) model.getValueAt(row, 1));
        priceField.setText(String.valueOf(model.getValueAt(row, 2)));
        categoryField.setText((String) model.getValueAt(row, 3));
        stockField.setText(String.valueOf(model.getValueAt(row, 4)));
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a row first"); return; }
        String id = (String) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected product?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dao.delete(id);
            loadProducts();
            clearForm();
        }
    }

    private void loadProducts() {
        model.setRowCount(0);
        List<Product> list = dao.getAllProducts();
        for (Product p : list) {
            model.addRow(new Object[]{p.getId(), p.getName(), p.getPrice(), p.getCategory(), p.getStock()});
        }
    }

    private void clearForm() {
        selectedProductId = null;
        nameField.setText("");
        priceField.setText("");
        categoryField.setText("");
        stockField.setText("0");
        table.clearSelection();
    }
}
