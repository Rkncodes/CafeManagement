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
        FlatLightLaf.setup();
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
    title.setFont(new Font("SansSerif", Font.BOLD, 26));
    top.add(title, BorderLayout.WEST);

    JPanel center = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    searchField = new JTextField(30);
    searchField.setToolTipText("Search products...");
    JButton searchBtn = createStyledButton("Search");
    searchBtn.addActionListener(e -> onSearch());
    center.add(searchField);
    center.add(searchBtn);

    categoryCombo = new JComboBox<>(new String[]{"All", "Coffee", "Snacks", "Dessert", "Beverage"});
    center.add(new JLabel("Category:"));
    center.add(categoryCombo);

    top.add(center, BorderLayout.CENTER);

    JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

    JButton refreshBtn = createStyledButton("🔄 Refresh");
    refreshBtn.addActionListener(e -> reloadProducts(getCurrentSearchQuery(), getCurrentCategoryFilter()));
    rightPanel.add(refreshBtn);

    JButton add = createStyledButton("➕ Add Product");
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
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        cart.add(title, BorderLayout.NORTH);

        cartItemsPanel = new JPanel();
        cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(cartItemsPanel);
        scroll.setBorder(null);
        cart.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(10, 10));
        totalLabel = new JLabel("Total: ₹0.00");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        JButton checkoutBtn = createStyledButton("Checkout");
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

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(0, 123, 255));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 105, 217));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 123, 255));
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
        name.setFont(new Font("SansSerif", Font.BOLD, 16));
        info.add(name, BorderLayout.NORTH);

        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        JLabel price = new JLabel(nf.format(p.getPrice()));
        price.setFont(new Font("SansSerif", Font.BOLD, 15));
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
        JButton addBtn = createStyledButton("Add");
        addBtn.addActionListener(e -> {
            cartManager.addProduct(p);
            refreshCartUI();
        });
        JButton edit = createStyledButton("Edit");
        edit.addActionListener(e -> openEditDialog(p));
        JButton del = createStyledButton("Delete");
        del.setBackground(new Color(220, 53, 69));
        del.addActionListener(e -> {
    int ok = JOptionPane.showConfirmDialog(ModernCafeUI.this, "Delete " + p.getName() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
    if (ok == JOptionPane.YES_OPTION) {
        boolean success = productDAO.deleteById(p.getId());
        if (success) {
            SwingUtilities.invokeLater(() -> {
                reloadProducts(getCurrentSearchQuery(), getCurrentCategoryFilter());
            });
        } else {
            JOptionPane.showMessageDialog(ModernCafeUI.this, "Failed to delete product.");
        }
    }
});

        actions.add(addBtn);
        actions.add(edit);
        actions.add(del);
        card.add(actions, BorderLayout.SOUTH);

        return card;
    }

    private void refreshCartUI() {
        cartItemsPanel.removeAll();
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

        for (Map.Entry<Product, Integer> entry : cartManager.getItems().entrySet()) {
            Product product = entry.getKey();
            int qty = entry.getValue();

            JPanel itemRow = new JPanel(new BorderLayout(5, 5));

            // Left side: Product name
            JLabel nameLabel = new JLabel(product.getName());
            itemRow.add(nameLabel, BorderLayout.WEST);

            // Center: Quantity controls
            JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            JButton minusBtn = new JButton("➖");
            minusBtn.addActionListener(e -> {
                cartManager.updateQuantity(product, qty - 1);
                refreshCartUI();
            });
            JLabel qtyLabel = new JLabel(String.valueOf(qty));
            JButton plusBtn = new JButton("➕");
            plusBtn.addActionListener(e -> {
                cartManager.updateQuantity(product, qty + 1);
                refreshCartUI();
            });
            qtyPanel.add(minusBtn);
            qtyPanel.add(qtyLabel);
            qtyPanel.add(plusBtn);

            itemRow.add(qtyPanel, BorderLayout.CENTER);

            // Right side: Price + remove button
            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            JLabel priceLabel = new JLabel(nf.format(product.getPrice() * qty));
            JButton removeBtn = new JButton("❌");
            removeBtn.addActionListener(e -> {
                cartManager.removeProduct(product);
                refreshCartUI();
            });
            rightPanel.add(priceLabel);
            rightPanel.add(removeBtn);

            itemRow.add(rightPanel, BorderLayout.EAST);

            cartItemsPanel.add(itemRow);
        }

        totalLabel.setText("Total: " + nf.format(cartManager.getTotal()));
        cartItemsPanel.revalidate();
        cartItemsPanel.repaint();
    }

    // Updated openAddProductDialog with try-catch & uniqueness check
 private void openAddProductDialog() {
    ProductFormDialog d = new ProductFormDialog(this, null);
    d.setVisible(true);
    if (d.isSaved()) {
        try {
            Product inserted = productDAO.insert(d.getProduct());
            SwingUtilities.invokeLater(() -> {
                reloadProducts(getCurrentSearchQuery(), getCurrentCategoryFilter());
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving product: " + e.getMessage());
        }
    }
}


    // Updated openEditDialog with try-catch & uniqueness check
  private void openEditDialog(Product p) {
    System.out.println("Opening edit dialog for product: " + p.getName());
    ProductEditDialog editDialog = new ProductEditDialog(this, p);
    editDialog.setVisible(true);

    if (editDialog.isSaved()) {
        SwingUtilities.invokeLater(() -> {
            reloadProducts(getCurrentSearchQuery(), getCurrentCategoryFilter());
        });
    }
}


    private ImageIcon loadImageIcon(String imagePath, int w, int h) {
        if (imagePath == null || imagePath.isEmpty()) return null;
        try {
            File imgFile = new File(imagePath);
            if (!imgFile.exists()) return null;
            BufferedImage img = ImageIO.read(imgFile);
            Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private String getCurrentSearchQuery() {
        String q = searchField.getText().trim();
        return q.isEmpty() ? null : q;
    }

    private String getCurrentCategoryFilter() {
        String cat = (String) categoryCombo.getSelectedItem();
        return "All".equals(cat) ? null : cat;
    }
    // Add these new methods to your ModernCafeUI class

private void showCheckoutDialog() {
    if (cartManager.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Cart is empty!");
        return;
    }

    JPanel panel = new JPanel(new BorderLayout(10, 10));
    
    // Customer info form
    JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
    JTextField nameField = new JTextField();
    JTextField phoneField = new JTextField();
    JComboBox<String> paymentCombo = new JComboBox<>(new String[]{"Cash", "Card", "UPI", "Online"});
    
    formPanel.add(new JLabel("Customer Name:"));
    formPanel.add(nameField);
    formPanel.add(new JLabel("Phone Number:"));
    formPanel.add(phoneField);
    formPanel.add(new JLabel("Payment Method:"));
    formPanel.add(paymentCombo);
    
    panel.add(formPanel, BorderLayout.CENTER);
    
    // Order summary
    JTextArea summary = new JTextArea();
    summary.setEditable(false);
    summary.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    summary.setText(generateOrderSummary());
    panel.add(new JScrollPane(summary), BorderLayout.SOUTH);
    
    int result = JOptionPane.showConfirmDialog(
        this, 
        panel, 
        "Checkout - Confirm Order", 
        JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.PLAIN_MESSAGE
    );
    
    if (result == JOptionPane.OK_OPTION) {
        cartManager.setCustomerInfo(
            nameField.getText().trim(),
            phoneField.getText().trim(),
            (String) paymentCombo.getSelectedItem()
        );
        
        // Generate and show invoice
        String invoice = cartManager.generateInvoice();
        showInvoice(invoice);
        
        // Clear cart after successful checkout
        cartManager.clearCart();
        refreshCartUI();
    }
}

private String generateOrderSummary() {
    StringBuilder summary = new StringBuilder();
    NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    
    summary.append("---------- ORDER SUMMARY ----------\n");
    for (Map.Entry<Product, Integer> entry : cartManager.getItems().entrySet()) {
        Product p = entry.getKey();
        int qty = entry.getValue();
        summary.append(String.format("%-20s %2d x %s\n", 
            p.getName(), qty, nf.format(p.getPrice())));
    }
    summary.append("----------------------------------\n");
    summary.append(String.format("%-24s %10s\n", "Subtotal:", nf.format(cartManager.getTotal())));
    summary.append(String.format("%-24s %10s\n", "Tax (10%):", nf.format(cartManager.getTax())));
    summary.append(String.format("%-24s %10s\n", "TOTAL:", nf.format(cartManager.getGrandTotal())));
    
    return summary.toString();
}

private void showInvoice(String invoiceText) {
    JDialog invoiceDialog = new JDialog(this, "Invoice", true);
    invoiceDialog.setSize(400, 600);
    invoiceDialog.setLocationRelativeTo(this);
    
    JTextArea invoiceArea = new JTextArea(invoiceText);
    invoiceArea.setEditable(false);
    invoiceArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    
    JButton printBtn = new JButton("Print");
    printBtn.addActionListener(e -> {
        try {
            invoiceArea.print();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(invoiceDialog, "Error printing: " + ex.getMessage());
        }
    });
    
    JButton saveBtn = new JButton("Save as File");
    saveBtn.addActionListener(e -> saveInvoiceToFile(invoiceText));
    
    JButton closeBtn = new JButton("Close");
    closeBtn.addActionListener(e -> invoiceDialog.dispose());
    
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(printBtn);
    buttonPanel.add(saveBtn);
    buttonPanel.add(closeBtn);
    
    invoiceDialog.add(new JScrollPane(invoiceArea), BorderLayout.CENTER);
    invoiceDialog.add(buttonPanel, BorderLayout.SOUTH);
    invoiceDialog.setVisible(true);
}

private void saveInvoiceToFile(String invoiceText) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Save Invoice");
    fileChooser.setSelectedFile(new File("invoice_" + System.currentTimeMillis() + ".txt"));
    
    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        try (java.io.PrintWriter out = new java.io.PrintWriter(file)) {
            out.println(invoiceText);
            JOptionPane.showMessageDialog(this, "Invoice saved successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
        }
    }
}

// Update your checkout button action listener to use the new dialog
// Replace this in your createCartPanel() method:
checkoutBtn.addActionListener(e -> showCheckoutDialog());

    public static void main(String[] args) {
        MongoDBUtil.init("mongodb://localhost:27017", "cafe_db");
        SwingUtilities.invokeLater(() -> new ModernCafeUI().setVisible(true));
    }
}
