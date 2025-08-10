package com.example.cafemanagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class ProductFormDialog extends JDialog {
    private JTextField nameField;
    private JTextField priceField;
    private JComboBox<String> categoryCombo;
    private JLabel dropLabel;
    private String savedImagePath; // relative path like "images/xxx.jpg"
    private boolean saved = false;
    private Product product;

    private final ProductDAO productDAO = new ProductDAO();

    public ProductFormDialog(Frame parent, Product p) {
        super(parent, true);
        this.product = p;

        setTitle(p == null ? "Add Product" : "Edit Product");
        setSize(400, 420);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        initUI();

        if (p != null) {
            populateFields(p);
        }
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
        dropLabel = new JLabel("Drop Image Here", SwingConstants.CENTER);
        dropLabel.setPreferredSize(new Dimension(200, 150));
        dropLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        dropLabel.setOpaque(true);
        dropLabel.setBackground(Color.WHITE);
        form.add(dropLabel);

        // Enable drag & drop for images
        new DropTarget(dropLabel, new DropTargetListener() {
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    dtde.acceptDrag(DnDConstants.ACTION_COPY);
                    dropLabel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                } else {
                    dtde.rejectDrag();
                }
            }

            @Override
            public void dragOver(DropTargetDragEvent dtde) {}

            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {}

            @Override
            public void dragExit(DropTargetEvent dte) {
                dropLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            }

            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        dtde.acceptDrop(DnDConstants.ACTION_COPY);
                        List<File> droppedFiles = (List<File>) dtde.getTransferable()
                                .getTransferData(DataFlavor.javaFileListFlavor);

                        if (!droppedFiles.isEmpty()) {
                            File file = droppedFiles.get(0);

                            // Ensure images folder exists
                            File imagesDir = new File("images");
                            if (!imagesDir.exists()) {
                                imagesDir.mkdirs();
                            }

                            // Create unique file name
                            String newFileName = System.currentTimeMillis() + "_" + file.getName();
                            File dest = new File(imagesDir, newFileName);

                            // Copy image
                            Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

                            // Save relative path
                            savedImagePath = "images/" + newFileName;

                            // Show preview
                            ImageIcon icon = new ImageIcon(savedImagePath);
                            Image scaled = icon.getImage().getScaledInstance(
                                    dropLabel.getWidth(), dropLabel.getHeight(),
                                    Image.SCALE_SMOOTH
                            );
                            dropLabel.setIcon(new ImageIcon(scaled));
                            dropLabel.setText(null);
                        }
                        dropLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                        dtde.dropComplete(true);
                    } else {
                        dtde.rejectDrop();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    dtde.dropComplete(false);
                }
            }
        });

        add(form, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> onSave());
        cancelBtn.addActionListener(e -> onCancel());

        buttons.add(saveBtn);
        buttons.add(cancelBtn);
        add(buttons, BorderLayout.SOUTH);
    }

    private void populateFields(Product p) {
        nameField.setText(p.getName());
        priceField.setText(String.valueOf(p.getPrice()));
        categoryCombo.setSelectedItem(p.getCategory());
        savedImagePath = p.getImagePath();

        if (savedImagePath != null && !savedImagePath.isEmpty()) {
            File imgFile = new File(savedImagePath);
            if (imgFile.exists()) {
                ImageIcon icon = new ImageIcon(savedImagePath);
                Image scaled = icon.getImage().getScaledInstance(
                        dropLabel.getWidth(), dropLabel.getHeight(),
                        Image.SCALE_SMOOTH
                );
                dropLabel.setIcon(new ImageIcon(scaled));
                dropLabel.setText(null);
            } else {
                dropLabel.setText("<html><center>No Image Found</center></html>");
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
            if (savedImagePath == null || savedImagePath.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please drag and drop an image.");
                return;
            }

            if (product == null) {
                product = new Product(null, name, price, category, savedImagePath);
                productDAO.insert(product);
            } else {
                product.setName(name);
                product.setPrice(price);
                product.setCategory(category);
                product.setImagePath(savedImagePath);
                productDAO.update(product);
            }

            saved = true;
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price must be a valid number.");
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
