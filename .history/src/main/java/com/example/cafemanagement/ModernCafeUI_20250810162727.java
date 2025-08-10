package com.example.cafemanagement;

import com.formdev.flatlaf.FlatLightLaf;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ModernCafeUI extends JFrame {
    private JTextField searchField;
    private JComboBox<String> categoryCombo;
    private JPanel productsGrid;      // grid of product cards
    private JScrollPane gridScroll;
    private ProductDAO productDAO;

    public ModernCafeUI() {
        // install a modern look and feel
        FlatLightLaf.setup();
        this.productDAO = new ProductDAO();

        setTitle("★ Cafe Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 760);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createTopBar(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createCartPanel(), BorderLayout.EAST);

        // initial load
        reloadProducts(null, null);

        // padding
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(8, 8, 8, 8));
    }

    private JComponent createTopBar() {
        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setBorder(new EmptyBorder(8, 8, 8, 8));

        // title
        JLabel title = new JLabel("Cafe Manager");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        top.add(title, BorderLayout.WEST);

        // center: search + category
        JPanel center = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchField = new JTextField(30);
        searchField.setToolTipText("Search products by name...");
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> onSearch());
        center.add(searchField);
        center.add(searchBtn);

        categoryCombo = new JComboBox<>(new String[] {"All", "Coffee", "Snacks", "Dessert", "Beverage"});
        center.add(new JLabel("Category:"));
        center.add(categoryCombo);
        top.add(center, BorderLayout.CENTER);

        // right: Add Product button
        JButton add = new JButton("Add Product");
        add.addActionListener(e -> openAddProductDialog());
        top.add(add, BorderLayout.EAST);

        return top;
    }

    private JComponent createCenterPanel() {
        productsGrid = new JPanel();
        productsGrid.setLayout(new WrapLayout(FlowLayout.LEFT, 16, 16)); // WrapLayout helper below
        gridScroll = new JScrollPane(productsGrid);
        gridScroll.getVerticalScrollBar().setUnitIncrement(16);
        return gridScroll;
    }

    private JComponent createCartPanel() {
        // Placeholder cart panel (you can extend with cart functionality)
        JPanel cart = new JPanel(new BorderLayout(8,8));
        cart.setPreferredSize(new Dimension(320, 0));
        cart.setBorder(BorderFactory.createTitledBorder("Cart"));

        JLabel info = new JLabel("<html><b>Cart</b><br/>(Place order & billing later)</html>");
        info.setHorizontalAlignment(SwingConstants.CENTER);
        cart.add(info, BorderLayout.CENTER);

        return cart;
    }

    private void onSearch() {
        String q = searchField.getText().trim();
        String cat = (String) categoryCombo.getSelectedItem();
        if ("All".equals(cat)) cat = null;
        reloadProducts(q.isEmpty() ? null : q, cat);
    }

    private void reloadProducts(String q, String category) {
        productsGrid.removeAll();
        List<Product> list = productDAO.findAll();
        for (Product p : list) {
            if (q != null && !p.getName().toLowerCase().contains(q.toLowerCase())) continue;
            if (category != null && !category.equalsIgnoreCase(p.getCategory())) continue;
            productsGrid.add(createProductCard(p));
        }
        productsGrid.revalidate();
        productsGrid.repaint();
    }

    private JComponent createProductCard(Product p) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(220, 300));
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                new EmptyBorder(8,8,8,8)));

        // image area
        JLabel imgLabel = new JLabel();
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imgLabel.setPreferredSize(new Dimension(200, 160));
        ImageIcon icon = loadImageIcon(p.getImagePath(), 200, 160);
        if (icon != null) imgLabel.setIcon(icon);
        else imgLabel.setText("<html><center>No Image</center></html>");
        card.add(imgLabel, BorderLayout.NORTH);

        // name & price
        JPanel info = new JPanel(new BorderLayout());
        JLabel name = new JLabel("<html><b>" + p.getName() + "</b></html>");
        name.setFont(new Font("SansSerif", Font.BOLD, 14));
        info.add(name, BorderLayout.NORTH);

        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        JLabel price = new JLabel(nf.format(p.getPrice()));
        price.setFont(new Font("SansSerif", Font.PLAIN, 13));
        info.add(price, BorderLayout.WEST);

        JLabel cat = new JLabel(p.getCategory());
        cat.setHorizontalAlignment(SwingConstants.RIGHT);
        info.add(cat, BorderLayout.EAST);

        card.add(info, BorderLayout.CENTER);

        // action panel
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(e -> {
            // For now show toast; we will implement cart later
            JOptionPane.showMessageDialog(this, p.getName() + " added to cart (demo).");
        });
        JButton edit = new JButton("Edit");
        edit.addActionListener(e -> openEditDialog(p));
        JButton del = new JButton("Delete");
        del.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this, "Delete " + p.getName() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                productDAO.deleteById(p.getId());
                reloadProducts(null, null);
            }
        });
        actions.add(addBtn);
        actions.add(edit);
        actions.add(del);
        card.add(actions, BorderLayout.SOUTH);

        return card;
    }

    // small dialog to add product (name, price, category, imagePath)
    private void openAddProductDialog() {
        ProductFormDialog d = new ProductFormDialog(this, null);
        d.setVisible(true);
        if (d.isSaved()) {
            productDAO.insert(d.getProduct());
            reloadProducts(null, null);
        }
    }

    private void openEditDialog(Product p) {
        ProductFormDialog d = new ProductFormDialog(this, p);
        d.setVisible(true);
        if (d.isSaved()) {
            productDAO.update(d.getProduct());
            reloadProducts(null, null);
        }
    }

   private ImageIcon loadImageIcon(String imagePath, int w, int h) {
    if (imagePath == null || imagePath.isEmpty()) return null;
    try {
        // imagePath is like "images/cappuccino.jpg" relative to your working dir
        java.io.File imgFile = new java.io.File(imagePath);
        if (!imgFile.exists()) return null;

        BufferedImage img = ImageIO.read(imgFile);
        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    } catch (Exception ex) {
        ex.printStackTrace();
        return null;
    }
}


    // main
    public static void main(String[] args) {
        // init DB first
        MongoDBUtil.init("mongodb://localhost:27017", "cafe_db");
        SwingUtilities.invokeLater(() -> {
            ModernCafeUI ui = new ModernCafeUI();
            ui.setVisible(true);
        });
    }
}
