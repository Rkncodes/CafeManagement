package com.example.cafemanagement;

import com.formdev.flatlaf.FlatLightLaf;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ModernCafeUI extends JFrame {
    private JTextField searchField;
    private JComboBox<String> categoryCombo;
    private JPanel productsGrid;
    private JScrollPane gridScroll;
    private ProductDAO productDAO;

    // Cart support
    private CartManager cartManager;
    private JPanel cartItemsPanel;
    private JLabel totalLabel;

    public ModernCafeUI() {
        // Modern flat look and feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Failed to initialize LaF");
        }

        productDAO = new ProductDAO();
        cartManager = new CartManager();

        setTitle("★ Cafe Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(8, 8, 8, 8));

        add(createTopBar(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createCartPanel(), BorderLayout.EAST);

        reloadProducts(null, null);
    }

    private JComponent createTopBar() {
        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setBorder(new EmptyBorder(8, 8, 8, 8));

        JLabel title = new JLabel("☕ Cafe Manager");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        top.add(title, BorderLayout.WEST);

        JPanel center = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchField = new JTextField(30);
        searchField.setToolTipText("Search products...");
        JButton searchBtn = createModernButton("Search", new Color(0, 123, 255), new Color(0, 105, 217));
        searchBtn.addActionListener(e -> onSearch());
        center.add(searchField);
        center.add(searchBtn);

        categoryCombo = new JComboBox<>(new String[]{"All", "Coffee", "Snacks", "Dessert", "Beverage"});
        center.add(new JLabel("Category:"));
        center.add(categoryCombo);

        top.add(center, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

        JButton refreshBtn = createModernButton("🔄 Refresh", new Color(0, 123, 255), new Color(0, 105, 217));
        refreshBtn.addActionListener(e -> reloadProducts(getCurrentSearchQuery(), getCurrentCategoryFilter()));
        rightPanel.add(refreshBtn);

        JButton add = createModernButton("➕ Add Product", new Color(0, 123, 255), new Color(0, 105, 217));
        add.addActionListener(e -> openAddProductDialog());
        rightPanel.add(add);

        top.add(rightPanel, BorderLayout.EAST);

        return top;
    }

    private JComponent createCenterPanel() {
        productsGrid = new JPanel();
        productsGrid.setLayout(new WrapLayout(FlowLayout.LEFT, 20, 20));
        gridScroll = new JScrollPane(productsGrid);
        gridScroll.getVerticalScrollBar().setUnitIncrement(16);
        gridScroll.setBorder(BorderFactory.createEmptyBorder());
        return gridScroll;
    }

    private JComponent createCartPanel() {
        JPanel cart = new JPanel(new BorderLayout(10, 10));
        cart.setPreferredSize(new Dimension(350, 0));
        cart.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));
        cart.setBackground(new Color(250, 250, 250));

        JLabel title = new JLabel("🛒 Cart");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        cart.add(title, BorderLayout.NORTH);

        cartItemsPanel = new JPanel();
        cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(cartItemsPanel);
        scroll.setBorder(null);
        cart.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(10, 10));
        totalLabel = new JLabel("Total: ₹0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JButton checkoutBtn = createModernButton("Checkout", new Color(40, 167, 69), new Color(33, 136, 56));
        checkoutBtn.addActionListener(e -> {
            if (cartManager.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Cart is empty!");
                return;
            }
            JOptionPane.showMessageDialog(this, "Total: ₹" + cartManager.getTotal() + "\nThank you for your order!");
            cartManager.clearCart();
            refreshCartUI();
        });
        bottom.add(totalLabel, BorderLayout.WEST);
        bottom.add(checkoutBtn, BorderLayout.EAST);
        cart.add(bottom, BorderLayout.SOUTH);

        return cart;
    }

    // Modern reusable button factory method
    private JButton createModernButton(String text, Color bgColor, Color hoverColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
            }
        });
        return btn;
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
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(240, 320));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(8, 8, 8, 8)
        ));
        card.setBackground(Color.WHITE);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2, true));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));
            }
        });

        JLabel imgLabel = new JLabel();
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imgLabel.setPreferredSize(new Dimension(220, 160));
        ImageIcon icon = loadImageIcon(p.getImagePath(), 220, 160);
        if (icon != null) imgLabel.setIcon(icon);
        else imgLabel.setText("<html><center>No Image</center></html>");
        card.add(imgLabel, BorderLayout.NORTH);

        JPanel info = new JPanel(new BorderLayout(5, 5));
        info.setOpaque(false);
        JLabel name = new JLabel("<html><b>" + p.getName() + "</b></html>");
        name.setFont(new Font("Segoe UI", Font.BOLD, 16));
        info.add(name, BorderLayout.NORTH);

        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        JLabel price = new JLabel(nf.format(p.getPrice()));
        price.setFont(new Font("Segoe UI", Font.BOLD, 15));
        price.setForeground(new Color(0, 128, 0));
        info.add(price, BorderLayout.WEST);

        JLabel cat = new JLabel(p.getCategory());
        cat.setHorizontalAlignment(SwingConstants.RIGHT);
        cat.setOpaque(true);
        cat.setBackground(new Color(240, 240, 240));
        cat.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        info.add(cat, BorderLayout.EAST);

        card.add(info, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        actions.setOpaque(false);

        JButton addBtn = createModernButton("Add", new Color(0, 123, 255), new Color(0, 105, 217));
        addBtn.addActionListener(e -> {
            cartManager.addProduct(p);
            refreshCartUI();
        });
        actions.add(addBtn);

        JButton editBtn = createModernButton("Edit", new Color(255, 193, 7), new Color(255, 179, 0));
        editBtn.addActionListener(e -> openEditProductDialog(p));
        actions.add(editBtn);

        JButton delBtn = createModernButton("Delete", new Color(220, 53, 69), new Color(200, 35, 51));
        delBtn.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this,
                    "Delete product \"" + p.getName() + "\"?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                productDAO.deleteProduct(p.getId());
                reloadProducts(getCurrentSearchQuery(), getCurrentCategoryFilter());
                cartManager.removeProduct(p);
                refreshCartUI();
            }
        });
        actions.add(delBtn);

        card.add(actions, BorderLayout.SOUTH);

        return card;
    }

    private void refreshCartUI() {
        cartItemsPanel.removeAll();
        Map<Product, Integer> cartItems = cartManager.getCartItems();

        if (cartItems.isEmpty()) {
            JLabel empty = new JLabel("Cart is empty");
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            empty.setForeground(Color.GRAY);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            cartItemsPanel.add(empty);
        } else {
            for (Map.Entry<Product, Integer> e : cartItems.entrySet()) {
                JPanel row = new JPanel(new BorderLayout(5, 5));
                row.setMaximumSize(new Dimension(320, 40));

                JLabel name = new JLabel(e.getKey().getName() + " × " + e.getValue());
                name.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                row.add(name, BorderLayout.WEST);

                JButton removeBtn = createModernButton("Remove", new Color(220, 53, 69), new Color(200, 35, 51));
                removeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                removeBtn.setPreferredSize(new Dimension(80, 28));
                removeBtn.addActionListener(ev -> {
                    cartManager.removeProduct(e.getKey());
                    refreshCartUI();
                });
                row.add(removeBtn, BorderLayout.EAST);

                cartItemsPanel.add(row);
            }
        }

        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        totalLabel.setText("Total: " + nf.format(cartManager.getTotal()));

        cartItemsPanel.revalidate();
        cartItemsPanel.repaint();
    }

    // Helper to load image icon with size, returns null if not found
    private ImageIcon loadImageIcon(String path, int width, int height) {
        try {
            BufferedImage img = ImageIO.read(new File(path));
            Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            return null;
        }
    }

    // Open dialogs (implement your own)
    private void openAddProductDialog() {
        // Your implementation for adding new product
        JOptionPane.showMessageDialog(this, "Add Product dialog opened (implement yourself).");
    }

    private void openEditProductDialog(Product p) {
        // Your implementation for editing product
        JOptionPane.showMessageDialog(this, "Edit Product dialog opened for " + p.getName() + " (implement yourself).");
    }

    private String getCurrentSearchQuery() {
        String q = searchField.getText().trim();
        return q.isEmpty() ? null : q;
    }

    private String getCurrentCategoryFilter() {
        String cat = (String) categoryCombo.getSelectedItem();
        return "All".equals(cat) ? null : cat;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ModernCafeUI ui = new ModernCafeUI();
            ui.setVisible(true);
        });
    }
}
