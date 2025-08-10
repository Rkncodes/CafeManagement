package com.example.cafemanagement;

import com.formdev.flatlaf.FlatLightLaf;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ModernCafeUI extends JFrame {
    private final ProductDAO productDAO;
    private final CartManager cartManager;

    private JTextField searchField;
    private JComboBox<String> categoryCombo;
    private JPanel productsGrid;
    private JScrollPane gridScroll;

    // cart UI
    private JTable cartTable;
    private CartTableModel cartTableModel;
    private JLabel totalLabel;
    private NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public ModernCafeUI() {
        // Look & feel
        FlatLightLaf.setup();

        this.productDAO = new ProductDAO();
        this.cartManager = new CartManager();

        setTitle("★ Cafe Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 820);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        add(createTopBar(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createCartPanel(), BorderLayout.EAST);

        reloadProducts(null, null);
    }

    // ---------- Top bar ----------
    private JComponent createTopBar() {
        JPanel top = new JPanel(new BorderLayout(12, 6));
        top.setBorder(new EmptyBorder(6, 6, 6, 6));
        top.setBackground(new Color(250, 250, 250));

        JLabel title = new JLabel("☕ Cafe Manager");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        top.add(title, BorderLayout.WEST);

        JPanel center = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        center.setOpaque(false);
        searchField = new JTextField(26);
        searchField.setToolTipText("Search products...");
        JButton searchBtn = createStyledButton("Search");
        searchBtn.addActionListener(e -> onSearch());
        center.add(searchField);
        center.add(searchBtn);

        categoryCombo = new JComboBox<>(new String[]{"All", "Coffee", "Snacks", "Dessert", "Beverage"});
        center.add(new JLabel("Category:"));
        center.add(categoryCombo);
        top.add(center, BorderLayout.CENTER);

        JButton add = createPrimaryButton("➕ Add Product");
        add.addActionListener(e -> openAddProductDialog());
        top.add(add, BorderLayout.EAST);

        return top;
    }

    // ---------- Center (product grid) ----------
    private JComponent createCenterPanel() {
        productsGrid = new JPanel();
        productsGrid.setLayout(new WrapLayout(FlowLayout.LEFT, 18, 18)); // WrapLayout helper you already have
        productsGrid.setOpaque(false);

        gridScroll = new JScrollPane(productsGrid, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        gridScroll.getVerticalScrollBar().setUnitIncrement(16);
        gridScroll.setBorder(BorderFactory.createEmptyBorder());
        gridScroll.getViewport().setBackground(new Color(245, 245, 245));

        return gridScroll;
    }

    // ---------- Cart Panel ----------
    private JComponent createCartPanel() {
        JPanel cart = new JPanel(new BorderLayout(8, 8));
        cart.setPreferredSize(new Dimension(360, 0));
        cart.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(12, 12, 12, 12)
        ));
        cart.setBackground(new Color(255, 255, 255));

        JLabel heading = new JLabel("🧾 Order / Cart");
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));
        cart.add(heading, BorderLayout.NORTH);

        // Table
        cartTableModel = new CartTableModel();
        cartTable = new JTable(cartTableModel);
        cartTable.setRowHeight(32);
        cartTable.setFillsViewportHeight(true);

        // Qty column - spinner editor
        cartTable.getColumnModel().getColumn(1).setCellEditor(new SpinnerEditor(1, 100, 1));
        cartTable.getColumnModel().getColumn(1).setMaxWidth(80);
        cartTable.getColumnModel().getColumn(2).setMaxWidth(90);
        cartTable.getColumnModel().getColumn(3).setMaxWidth(90);
        cartTable.getColumnModel().getColumn(4).setMaxWidth(80);

        // Remove column - button renderer/editor
        cartTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer("Remove"));
        cartTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), label -> {
            // remove action
            int modelRow = cartTable.convertRowIndexToModel(cartTable.getEditingRow());
            Product p = cartTableModel.getProductAt(modelRow);
            cartManager.removeProduct(p);
            refreshCartUI();
        }));

        // Right align price columns
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        cartTable.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        cartTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        JScrollPane tableScroll = new JScrollPane(cartTable);
        tableScroll.setBorder(null);
        cart.add(tableScroll, BorderLayout.CENTER);

        // Bottom - totals + checkout
        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setOpaque(false);
        totalLabel = new JLabel("Total: " + nf.format(0.0));
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        bottom.add(totalLabel, BorderLayout.WEST);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        JButton clearBtn = createStyledButton("Clear");
        clearBtn.addActionListener(e -> {
            cartManager.clearCart();
            refreshCartUI();
        });
        JButton checkoutBtn = createPrimaryButton("Checkout");
        checkoutBtn.addActionListener(e -> doCheckout());
        btnRow.add(clearBtn);
        btnRow.add(checkoutBtn);
        bottom.add(btnRow, BorderLayout.EAST);

        cart.add(bottom, BorderLayout.SOUTH);

        return cart;
    }

    // ---------- UI helpers ----------
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(235, 235, 235));
        btn.setForeground(Color.DARK_GRAY);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(new Color(225, 225, 225)); }
            public void mouseExited(java.awt.event.MouseEvent evt)  { btn.setBackground(new Color(235, 235, 235)); }
        });
        return btn;
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(0, 123, 255));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(new Color(0, 105, 217)); }
            public void mouseExited(java.awt.event.MouseEvent evt)  { btn.setBackground(new Color(0, 123, 255)); }
        });
        return btn;
    }

    // ---------- Product loading / search ----------
    private void onSearch() {
        String q = searchField.getText().trim();
        String cat = (String) categoryCombo.getSelectedItem();
        if ("All".equals(cat)) cat = null;
        reloadProducts(q.isEmpty() ? null : q, cat);
    }

    private void reloadProducts(String q, String category) {
        productsGrid.removeAll();
        List<Product> list = productDAO.findAll(); // from your DAO
        for (Product p : list) {
            if (q != null && !p.getName().toLowerCase().contains(q.toLowerCase())) continue;
            if (category != null && !category.equalsIgnoreCase(p.getCategory())) continue;
            productsGrid.add(createProductCard(p));
        }
        productsGrid.revalidate();
        productsGrid.repaint();
    }

    // ---------- Product card ----------
    private JComponent createProductCard(Product p) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(240, 340)); // fixed size prevents layout jumps
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(8, 8, 8, 8)
        ));
        card.setBackground(Color.WHITE);

        // Image panel with fixed height so text never shifts
        JLabel imgLabel = new JLabel();
        imgLabel.setPreferredSize(new Dimension(220, 160));
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon icon = loadImageIcon(p.getImagePath(), 220, 160);
        if (icon != null) {
            imgLabel.setIcon(icon);
        } else {
            imgLabel.setText("<html><center style='color:#888;'>No Image</center></html>");
        }
        card.add(imgLabel, BorderLayout.NORTH);

        // Info
        JPanel info = new JPanel(new BorderLayout(6, 6));
        info.setOpaque(false);

        // Name (wraps if long)
        JLabel name = new JLabel("<html><div style='width:200px'><b>" + escapeHtml(p.getName()) + "</b></div></html>");
        name.setFont(new Font("SansSerif", Font.BOLD, 16));
        info.add(name, BorderLayout.NORTH);

        // Price + category row
        JPanel mid = new JPanel(new BorderLayout());
        mid.setOpaque(false);
        JLabel price = new JLabel(nf.format(p.getPrice()));
        price.setFont(new Font("SansSerif", Font.BOLD, 15));
        price.setForeground(new Color(0, 120, 40));
        mid.add(price, BorderLayout.WEST);

        JLabel cat = new JLabel(p.getCategory());
        cat.setOpaque(true);
        cat.setBackground(new Color(245, 245, 245));
        cat.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        mid.add(cat, BorderLayout.EAST);

        info.add(mid, BorderLayout.CENTER);

        card.add(info, BorderLayout.CENTER);

        // Buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        actions.setOpaque(false);
        JButton addBtn = createPrimaryButton("Add");
        addBtn.addActionListener(e -> {
            cartManager.addProduct(p);
            refreshCartUI();
        });

        JButton editBtn = createStyledButton("Edit");
        editBtn.addActionListener(e -> openEditDialog(p));

        JButton delBtn = createStyledButton("Delete");
        delBtn.setForeground(new Color(170, 20, 20));
        delBtn.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this, "Delete " + p.getName() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                productDAO.deleteById(p.getId());
                reloadProducts(null, null);
            }
        });

        actions.add(addBtn);
        actions.add(editBtn);
        actions.add(delBtn);
        card.add(actions, BorderLayout.SOUTH);

        return card;
    }

    // ---------- Image loader ----------
    private ImageIcon loadImageIcon(String imagePath, int w, int h) {
        if (imagePath == null || imagePath.isEmpty()) return null;
        try {
            File f = new File(imagePath);
            if (!f.exists()) return null;
            BufferedImage img = ImageIO.read(f);
            Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // ---------- Cart logic UI ----------
    private void refreshCartUI() {
        // build a stable list from CartManager
        cartTableModel.updateFromCart(cartManager.getItems());
        totalLabel.setText("Total: " + nf.format(cartManager.getTotal()));
    }

    private void doCheckout() {
        if (cartManager.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!", "Checkout", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Build bill text
        StringBuilder sb = new StringBuilder();
        sb.append("<html><h2 style='margin:0'>Cafe Bill</h2><hr>");
        sb.append("<table cellpadding='4'>");
        sb.append("<tr><th align='left'>Item</th><th>Qty</th><th>Price</th><th>Subtotal</th></tr>");
        for (Map.Entry<Product, Integer> e : cartManager.getItems().entrySet()) {
            Product p = e.getKey();
            int q = e.getValue();
            sb.append("<tr>")
                    .append("<td>").append(escapeHtml(p.getName())).append("</td>")
                    .append("<td align='center'>").append(q).append("</td>")
                    .append("<td align='right'>").append(nf.format(p.getPrice())).append("</td>")
                    .append("<td align='right'>").append(nf.format(p.getPrice() * q)).append("</td>")
                    .append("</tr>");
        }
        sb.append("</table><hr>");
        sb.append("<h3>Total: ").append(nf.format(cartManager.getTotal())).append("</h3>");
        sb.append("</html>");

        // Show bill in dialog (could be extended to print/save PDF)
        JLabel billLabel = new JLabel(sb.toString());
        billLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane s = new JScrollPane(billLabel);
        s.setPreferredSize(new Dimension(600, 400));
        int option = JOptionPane.showConfirmDialog(this, s, "Checkout - Bill Preview", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            // confirm order -> clear cart
            JOptionPane.showMessageDialog(this, "Order placed. Thank you!");
            cartManager.clearCart();
            refreshCartUI();
        }
    }

    // ---------- Dialogs ----------
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

    // ---------- Util ----------
    private static String escapeHtml(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // ---------- Main ----------
    public static void main(String[] args) {
        MongoDBUtil.init("mongodb://localhost:27017", "cafe_db");
        SwingUtilities.invokeLater(() -> {
            ModernCafeUI ui = new ModernCafeUI();
            ui.setVisible(true);
        });
    }

    // ---------- Cart Table Model & Helpers (inner classes) ----------

    private class CartTableModel extends AbstractTableModel {
        private final String[] cols = new String[]{"Item", "Qty", "Price", "Subtotal", ""};
        private final List<Product> products = new ArrayList<>();
        private final List<Integer> quantities = new ArrayList<>();

        public void updateFromCart(Map<Product, Integer> items) {
            products.clear();
            quantities.clear();
            items.forEach((p, q) -> { products.add(p); quantities.add(q); });
            fireTableDataChanged();
        }

        public Product getProductAt(int row) {
            return products.get(row);
        }

        @Override
        public int getRowCount() { return products.size(); }

        @Override
        public int getColumnCount() { return cols.length; }

        @Override
        public String getColumnName(int column) { return cols[column]; }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 1) return Integer.class;
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 1 || columnIndex == 4; // qty editable, remove button
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Product p = products.get(rowIndex);
            int qty = quantities.get(rowIndex);
            switch (columnIndex) {
                case 0: return p.getName();
                case 1: return qty;
                case 2: return nf.format(p.getPrice());
                case 3: return nf.format(p.getPrice() * qty);
                case 4: return "Remove";
                default: return "";
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 1) {
                int newQty = (aValue instanceof Number) ? ((Number) aValue).intValue() : Integer.parseInt(aValue.toString());
                if (newQty <= 0) {
                    Product p = products.get(rowIndex);
                    cartManager.removeProduct(p);
                } else {
                    Product p = products.get(rowIndex);
                    cartManager.updateQuantity(p, newQty);
                }
                updateFromCart(cartManager.getItems());
                totalLabel.setText("Total: " + nf.format(cartManager.getTotal()));
            }
        }
    }

    // Spinner editor for qty column
    private static class SpinnerEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor {
        private final JSpinner spinner;

        SpinnerEditor(int min, int max, int step) {
            SpinnerNumberModel model = new SpinnerNumberModel(min, min, max, step);
            spinner = new JSpinner(model);
            JComponent c = spinner.getEditor();
            c.setPreferredSize(new Dimension(60, 24));
        }

        @Override
        public Object getCellEditorValue() {
            return spinner.getValue();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            spinner.setValue(value instanceof Number ? ((Number) value).intValue() : Integer.parseInt(value.toString()));
            return spinner;
        }
    }

    // Button renderer for remove column
    private static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        ButtonRenderer(String text) {
            setText(text);
            setOpaque(true);
            setFont(new Font("SansSerif", Font.PLAIN, 12));
            setBackground(new Color(235, 75, 75));
            setForeground(Color.WHITE);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // Button editor that runs a callback
    private static class ButtonEditor extends DefaultCellEditor {
        private final JButton button = new JButton();
        private final Callback callback;
        private boolean isPushed;

        interface Callback { void onClick(); }

        ButtonEditor(JCheckBox checkBox, Callback cb) {
            super(checkBox);
            this.callback = cb;
            button.setOpaque(true);
            button.setBackground(new Color(220, 60, 60));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("SansSerif", Font.PLAIN, 12));
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            button.setText(value == null ? "" : value.toString());
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                SwingUtilities.invokeLater(callback::onClick);
            }
            isPushed = false;
            return "";
        }
    }
}
