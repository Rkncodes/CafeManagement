package com.example.cafemanagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ProductFormDialog extends JDialog {
    private JTextField nameField;
    private JTextField priceField;
    private JComboBox<String> categoryCombo;
    private JLabel dropLabel;
    private String savedImagePath;

    private boolean saved = false;
    private Product product;
    private ProductDAO productDAO;

    public ProductFormDialog(Frame owner, Product p) {
        super(owner, true);
        this.productDAO = new ProductDAO();
        this.product = p;

        setTitle(p == null ? "Add Product" : "Edit Product");
        setSize(400, 350);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(8, 8));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(8, 8, 8, 8));

        initUI();

        if (p != null) {
            nameField.setText(p.getName());
            priceField.setText(String.valueOf(p.getPrice()));
            categoryCombo.setSelectedItem(p.getCategory());
            savedImagePath = p.getImagePath();
            if (savedImagePath != null && !savedImagePath.isEmpty()) {
                dropLabel.setText("<html><center>Image:<br>" + savedImagePath + "</center></html>");
            }
        }
    }

    private void initUI() {
        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));

        nameField = new JTextField();
        priceField = new JTextField();
        categoryCombo = new JComboBox<>(new String[]{"Coffee", "Snacks", "Dessert", "Beverage"});

        // Drag & Drop Label
        dropLabel = new JLabel("Drop Image Here", SwingConstants.CENTER);
        dropLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        dropLabel.setPreferredSize(new Dimension(200, 150));

        // Enable Drag & Drop for image files
        dropLabel.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            @Override
            public boolean importData(TransferSupport support) {
                try {
                    java.util.List<File> files = (java.util.List<File>) support.getTransferable()
                            .getTransferData(DataFlavor.javaFileListFlavor);
                    if (!files.isEmpty()) {
                        File file = files.get(0);
                        File imagesDir = new File("images");
                        if (!imagesDir.exists()) imagesDir.mkdirs();

                        File dest = new File(imagesDir, file.getName());
                        Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

                        savedImagePath = "images/" + file.getName();
                        dropLabel.setText("<html><center>Image Added:<br>" + file.getName() + "</center></html>");
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Price:"));
        form.add(priceField);
        form.add(new JLabel("Category:"));
        form.add(categoryCombo);
        form.add(new JLabel("Image:"));
        form.add(dropLabel);

        add(form, BorderLayout.CENTER);

        // Buttons panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            try {
                double price = Double.parseDouble(priceField.getText());
                if (nameField.getText().isEmpty() || savedImagePath == null) {
                    JOptionPane.showMessageDialog(this, "Name and Image are required", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (product == null) {
                    product = new Product(null, nameField.getText(), price, (String) categoryCombo.getSelectedItem(), savedImagePath);
                    productDAO.insert(product);
                } else {
                    product.setName(nameField.getText());
                    product.setPrice(price);
                    product.setCategory((String) categoryCombo.getSelectedItem());
                    product.setImagePath(savedImagePath);
                    productDAO.update(product);
                }
                saved = true;
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Price must be a valid number", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> {
            saved = false;
            dispose();
        });

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        add(btnPanel, BorderLayout.SOUTH);
    }

    public boolean isSaved() {
        return saved;
    }

    public Product getProduct() {
        return product;
    }
}
