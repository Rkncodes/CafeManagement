package com.example.cafemanagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class ProductEditDialog extends JDialog {
    private JTextField nameField;
    private JTextField priceField;
    private JComboBox<String> categoryCombo;
    private JLabel imageLabel;
    private String savedImagePath;
    private boolean saved = false;
    private Product product;
    private final ProductDAO productDAO = new ProductDAO();

    public ProductEditDialog(Frame parent, Product product) {
        super(parent, true);
        this.product = product;

        setTitle("Edit Product");
        setSize(400, 420);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        initUI();

        // Defer populating fields to ensure components are sized
        SwingUtilities.invokeLater(() -> populateFields(product));
    }

    private void initUI() {
        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));

        form.add(new JLabel("Name:"));
        nameField = new JTextField();
        form.add(nameField);

        form.add(new JLabel("Price:"));
        priceField = new JTextField();
        form.add(priceField);

        form.add(new JLabel("Category:"));
        categoryCombo = new JComboBox<>(new String[]{"Coffee", "Snacks", "Dessert", "Beverage"});
        form.add(categoryCombo);

        form.add(new JLabel("Image:"));
        imageLabel = new JLabel("No Image", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(200, 150));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        form.add(imageLabel);

        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> onSave());
        cancelBtn.addActionListener(e -> onCancel());

        buttons.add(saveBtn);
        buttons.add(cancelBtn);

        add(buttons, BorderLayout.SOUTH);
    }

    private void populateFields(Product product) {
        nameField.setText(product.getName());
        priceField.setText(String.valueOf(product.getPrice()));
        categoryCombo.setSelectedItem(product.getCategory());
        savedImagePath = product.getImagePath();

        if (savedImagePath != null && !savedImagePath.isEmpty()) {
            File imgFile = new File(savedImagePath);
            if (imgFile.exists()) {
                ImageIcon icon = new ImageIcon(savedImagePath);
                Image scaled = icon.getImage().getScaledInstance(
                        imageLabel.getWidth(), imageLabel.getHeight(), Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaled));
                imageLabel.setText(null);
            } else {
                imageLabel.setText("<html><center>No Image Found</center></html>");
            }
        }
    }

    private void onSave() {
        try {
            String name = nameField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            String category = (String) categoryCombo.getSelectedItem();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name is required.");
                return;
            }

            product.setName(name);
            product.setPrice(price);
            product.setCategory(category);
            product.setImagePath(savedImagePath);

            boolean success = productDAO.update(product);
            if (!success) {
                JOptionPane.showMessageDialog(this, "Product with this name and category already exists!");
                return;
            }

            saved = true;
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price must be a valid number.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating product: " + ex.getMessage());
        }
    }

    private void onCancel() {
        saved = false;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public Product getProduct() {
        return product;
    }
}
