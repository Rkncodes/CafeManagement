package com.example.cafemanagement;

import javax.swing.*;
import java.awt.*;

public class ProductFormDialog extends JDialog {
    private JTextField nameF, priceF, categoryF, imageF;
    private boolean saved = false;
    private Product product;

    public ProductFormDialog(Window owner, Product existing) {
        super(owner, "Product", ModalityType.APPLICATION_MODAL);
        setSize(420, 260);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(8,8));

        JPanel form = new JPanel(new GridLayout(4,2,8,8));
        form.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        form.add(new JLabel("Name:"));
        nameF = new JTextField(); form.add(nameF);
        form.add(new JLabel("Price:"));
        priceF = new JTextField(); form.add(priceF);
        form.add(new JLabel("Category:"));
        categoryF = new JTextField(); form.add(categoryF);
        form.add(new JLabel("Image resource (e.g. images/espresso.jpg):"));
        imageF = new JTextField(); form.add(imageF);

        add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel();
        JButton save = new JButton("Save");
        save.addActionListener(e -> onSave());
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dispose());
        btns.add(save); btns.add(cancel);
        add(btns, BorderLayout.SOUTH);

        if (existing != null) {
            product = existing;
            nameF.setText(existing.getName());
            priceF.setText(String.valueOf(existing.getPrice()));
            categoryF.setText(existing.getCategory());
            imageF.setText(existing.getImagePath());
        } else {
            product = new Product();
        }
    }

    private void onSave() {
        try {
            String name = nameF.getText().trim();
            double price = Double.parseDouble(priceF.getText().trim());
            String cat = categoryF.getText().trim();
            String img = imageF.getText().trim();
            if (name.isEmpty()) { JOptionPane.showMessageDialog(this, "Name required"); return; }
            product.setName(name);
            product.setPrice(price);
            product.setCategory(cat.isEmpty() ? "General" : cat);
            product.setImagePath(img.isEmpty() ? null : img);
            saved = true;
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid price.");
        }
    }

    public boolean isSaved() { return saved; }
    public Product getProduct() { return product; }
}
